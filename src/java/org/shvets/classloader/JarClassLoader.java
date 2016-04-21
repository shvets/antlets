// JarClassLoader.java

package org.shvets.classloader;

import org.shvets.util.FileUtil;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.io.*;
import java.net.URL;

/**
 * This is executable jar-based class loader.
 * @deprecated
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class JarClassLoader extends GenericClassLoader {

  private final Map jarsMap = new HashMap();

  /**
   * Creates jar class loader.
   *
   * @param jars the array of jars.
   * @throws IOException I/O Exception
   */
  public JarClassLoader(final URL[] jars) throws IOException {
    for (int i = 0; i < jars.length; i++) {
      final URL url = jars[i];

      final String name = url.getFile().substring(1);

      if (new File(name).isFile()) {
        parseClassPathAttribute(name);
      }
    }
  }

  /**
   * Creates jar class loader.
   *
   * @param filesList the list of jars.
   * @throws IOException I/O Exception
   */
  public JarClassLoader(final List filesList) throws IOException {
    for (int i = 0; i < filesList.size(); i++) {
      final String name = (String) filesList.get(i);

      parseClassPathAttribute(name);
    }
  }

  /**
   * Parses class path attribute from MANIFEST.MF file.
   *
   * @param jarName the jar name
   * @throws IOException I/O Exception
   */
  private void parseClassPathAttribute(final String jarName) throws IOException {
    final JarFile jarFile = new JarFile(jarName);

    final JarFile jf = (JarFile) jarsMap.get(jarFile);

    if (jf != null && jf.getName().equals(jarFile.getName())) {
      return;
    }

    jarsMap.put(jarFile, new ArrayList());

    final Manifest manifest = getManifest(jarFile);

    final Attributes mainAttributes = manifest.getMainAttributes();

    String mainClass = mainAttributes.getValue(Attributes.Name.MAIN_CLASS);

    if (mainClass != null) {
      final String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);

      collectJars(jarFile, classPath);
    }
    else {
      mainClass = mainAttributes.getValue("MIDlet-Name");

      if (mainClass != null) {
        final String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);

        collectJars(jarFile, classPath);
      }
    }
  }

  /**
   * Discovers jars in Class-Path attribute.
   *
   * @param jarFile   the jar file
   * @param classPath the class path attribute
   */
  private void collectJars(final JarFile jarFile, final String classPath) {
    if (classPath != null) {
      final StringTokenizer st = new StringTokenizer(classPath);

      while (st.hasMoreTokens()) {
        final String jarName = st.nextToken();

        final ZipEntry zipEntry = jarFile.getEntry(jarName);

        if (zipEntry != null) {
          final List list = (List) jarsMap.get(jarFile);

          list.add(jarName);
        }
      }
    }
  }

  /**
   * Gets the manifest object from jar file.
   *
   * @return the manifest object
   * @throws IOException I/O Exception
   */
  private Manifest getManifest(final JarFile jarFile) throws IOException {
    Manifest manifest = null;

    ZipEntry zipEntry = jarFile.getEntry("META-INF/MANIFEST.MF");

    if (zipEntry == null) {
      zipEntry = jarFile.getEntry("meta-inf/manifest.mf");
    }

    if (zipEntry != null) {
      final InputStream is = jarFile.getInputStream(zipEntry);

      manifest = new Manifest(is);

      is.close();
    }

    return manifest;
  }

  /**
   * Gets the resource.
   *
   * @param name the resource name
   * @return the resource in the form of the stream
   */
  public InputStream getResourceAsStream(String name) {
    name = name.replace('\\', '/');

    if (name.startsWith("/")) {
      name = name.substring(1);
    }

    InputStream is = super.getResourceAsStream(name);

    if (is == null) {
      is = getSystemResourceAsStream(name);

      if (is == null) {
        final Iterator iterator = jarsMap.keySet().iterator();

        while (iterator.hasNext()) {
          final JarFile jarFile = (JarFile) iterator.next();

          is = getResourceAsStream(jarFile, name);

          if (is != null) {
            break;
          }
        }
      }
    }

    return is;
  }

  private InputStream getResourceAsStream(final JarFile jarFile, final String name) {
    InputStream is = null;
    try {
      final ZipEntry zipEntry = jarFile.getEntry(name);

      if (zipEntry != null) {
        is = jarFile.getInputStream(zipEntry);
      }
    }
    catch (IOException e) {
      is = null;
    }

    return is;
  }


  /**
   * Load bytes that represents the class.
   *
   * @param name the class name
   * @return bytes for the class
   */
  public byte[] loadClassBytes(final String name) {
    byte[] buffer = null;

    final Iterator iterator = jarsMap.keySet().iterator();

    while (iterator.hasNext()) {
      final JarFile jarFile = (JarFile) iterator.next();

      buffer = loadClassBytes(jarFile, name);

      if (buffer != null) {
        break;
      }
    }

    return buffer;
  }

  /**
   * Loads bytes for the class from jar file.
   *
   * @param jarFile the jar file
   * @param className the class name
   * @return the class in form of the array of bytes
   */
  public byte[] loadClassBytes(final JarFile jarFile, final String className) {
    byte[] buffer;

    try {
      buffer = loadFile(jarFile, className);
    }
    catch (Exception e) {
      buffer = null;
    }

    if (buffer == null) {
      final List cpEntries = (List) jarsMap.get(jarFile);

      for (int i = 0; i < cpEntries.size(); i++) {
        try {
          buffer = loadFile(jarFile, (String) cpEntries.get(i), className);
        }
        catch (Exception e) {
          e.printStackTrace();
          //buffer = null;
        }

        if (buffer != null) {
          break;
        }
      }
    }

    return buffer;
  }

  public byte[] loadFile(final JarFile jarFile, final String name) throws IOException {
    final ZipEntry zipEntry = jarFile.getEntry(name.replace('.', '/').concat(".class"));

    if (zipEntry != null) {
      return FileUtil.getStreamAsBytes(jarFile.getInputStream(zipEntry));
    }

    return null;
  }

  public byte[] loadFile(final JarFile jarFile, final String jarName, final String className)
          throws IOException {
    final ZipEntry zipEntry = jarFile.getEntry(jarName);

    if (zipEntry != null) {
      final String newClassName = className.replace('.', '/') + ".class";

      final InputStream is = jarFile.getInputStream(zipEntry);
      final ZipInputStream zin = new ZipInputStream(is);

      while (true) {
        final ZipEntry zipEntry2 = zin.getNextEntry();

        if (zipEntry2 == null) {
          break;
        }

        final String entryName = zipEntry2.getName();

        if (entryName.equals(newClassName)) {
          return FileUtil.getStreamAsBytes(zin);
        }

        zin.closeEntry();
      }

      zin.close();
    }

    return null;
  }

}
