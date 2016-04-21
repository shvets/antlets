package org.shvets.classloader;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.net.URL;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedActionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.Resource;

import org.shvets.util.FileUtil;

/**
 * This class loader is used to load classes from jar files
 * and jar files inside other jar file.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class JarUrlClassLoader extends UrlClassLoader {
  private final Map jarsMap = new HashMap();

  /**
   * Creates new class loader.
   *
   * @param urls the list of URLs
   */
  public JarUrlClassLoader(String name, final URL[] urls) {
    super(name, urls);

    for(int i = 0; i < urls.length; i++) {
      final String fileName = urls[i].getFile().substring(1);

      final File file = new File(fileName);

      if(file.exists() && file.isFile()) {
        try {
          parseClassPathAttribute(fileName);
        }
        catch(IOException e) {
          throw new IllegalArgumentException(e.toString());
        }
      }
    }

    Runtime.getRuntime().addShutdownHook(createShutdownHook());
  }

  /**
   * Removes temporar libraries. If the jar file has another jar
   * files, they wull be unzipped temporary and at the end - deleted.
   *
   * @return the shutdown hook
   */
  private Thread createShutdownHook() {
    return new Thread() {
      public void run() {
        final Iterator iterator = jarsMap.keySet().iterator();

        while(iterator.hasNext()) {
          final JarFile jarFile = (JarFile) iterator.next();

          final List values = (List) jarsMap.get(jarFile);

          for(int i = 0; i < values.size(); i++) {
            final File file = (File) values.get(i);

            file.delete();
          }
        }
      }
    };
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

    if(jf != null && jf.getName().equals(jarFile.getName())) {
      return;
    }

    jarsMap.put(jarFile, new ArrayList());

    final Manifest manifest = FileUtil.getManifest(jarFile);

    if(manifest != null) {
      final Attributes mainAttributes = manifest.getMainAttributes();

      String mainClass = mainAttributes.getValue(Attributes.Name.MAIN_CLASS);

      if(mainClass != null) {
        final String classPath =
                mainAttributes.getValue(Attributes.Name.CLASS_PATH);

        collectJars(jarFile, classPath);
      }
      else {
        mainClass = mainAttributes.getValue("MIDlet-Name");

        if(mainClass != null) {
          final String classPath =
                  mainAttributes.getValue(Attributes.Name.CLASS_PATH);

          collectJars(jarFile, classPath);
        }
      }
    }
  }

  /**
   * Discovers jars in Class-Path attribute.
   *
   * @param jarFile the jar file
   * @param classPath the class path attribute
   */
  private void collectJars(final JarFile jarFile, final String classPath) {
    if(classPath != null) {
      final StringTokenizer st = new StringTokenizer(classPath);

      while(st.hasMoreTokens()) {
        final String jarName = st.nextToken();

        final ZipEntry zipEntry = jarFile.getEntry(jarName);

        if(zipEntry != null) {
          final List list = (List) jarsMap.get(jarFile);

          try {
            final File file = FileUtil.copyToTempFile(jarFile.getInputStream(zipEntry), "jar-", ".tmp");

            list.add(file);
          }
          catch(IOException e) {
            throw new IllegalArgumentException(e.toString());
          }
        }
      }
    }
  }

/* slow way
  private List jars = new ArrayList();

  protected void parseClassPathAttribute_old(String jarName) throws IOException {
    final Manifest manifest = FileUtil.getManifest(jarName);

    final Attributes mainAttributes = manifest.getMainAttributes();

    String mainClass = mainAttributes.getValue(Attributes.Name.MAIN_CLASS);

    if(mainClass != null) {
      final String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);

      collectJars0(jarName, classPath);
    }
    else {
      mainClass = mainAttributes.getValue("MIDlet-Name");

      if(mainClass != null) {
        final String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);

        collectJars0(jarName, classPath);
      }
    }
  }

  private void collectJars0(final String jarName, final String classPath)
          throws IOException {
    if(classPath != null) {
      final StringTokenizer st = new StringTokenizer(classPath);

      final JarFile jarFile = new JarFile(jarName);

      while(st.hasMoreTokens()) {
        final String jarNameI = st.nextToken();

        File jarFileI = new File(jarNameI);

        if(jarFileI.exists()) {
          addURL(jarFileI.toURL());

          jars.add(jarFileI);
        }
        else {
          final ZipEntry zipEntry = jarFile.getEntry(jarNameI);

          if(zipEntry != null) {
            File file = FileUtil.copyToTempFile(jarFile.getInputStream(zipEntry), "jar-", ".tmp");

            file.deleteOnExit();

            addURL(file.toURL());

            jars.add(file);

            //URL url = new URL("jar:file:" + jarFileName.replace('\\', '/') + "!/" + jarName);

            //jars.add(url);
          }
        }
      }

      jarFile.close();
    }
  }
*/

  /**
   * Finds and loads the class with the specified name from the URL search
   * path. Any URLs referring to JAR files are loaded and opened as needed
   * until the class is found.
   *
   * @param name the name of the class
   * @return the resulting class
   * @throws ClassNotFoundException if the class could not be found
   */
  protected Class findClass(final String name)
          throws ClassNotFoundException {
    try {
      return super.findClass(name);
    }
    catch(ClassNotFoundException e) {
      ; // supress messages
    }

    final AccessControlContext acc = AccessController.getContext();

    try {
      return (Class)
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
          public Object run() throws ClassNotFoundException {
            final Resource res = getResourceFromJar(name);

            if(res != null) {
              return defineClass(name, res);
            }
            else {
              throw new ClassNotFoundException(name);
            }
          }
        }, acc);
    }
    catch(PrivilegedActionException e) {
      throw (ClassNotFoundException)e.getException();
    }
  }

  final class Pair {
    final InputStream is;
    final long size;

    Pair(final InputStream is, final long size) {
      this.is = is;
      this.size = size;
    }
  }

  /**
   * Looks for the resource inside the jar file.
   *
   * @param name the resource name
   * @return the resource
   */
  private Resource getResourceFromJar(final String name) {
    final String path = name.replace('.', '/').concat(".class");

    final Map resourceMap = new HashMap();

    Pair pair = null;

    final Iterator iterator = jarsMap.keySet().iterator();

    outer: {
      while(iterator.hasNext()) {
        final JarFile jarFile = (JarFile) iterator.next();

        final List values = (List) jarsMap.get(jarFile);

        for(int i = 0; i < values.size(); i++) {
          final File file = (File) values.get(i);

          try {
            pair = loadResource(file, path);
          }
          catch(Exception e) {
            e.printStackTrace();
            pair = null;
          }

          if(pair != null) {
            resourceMap.put("input.stream", pair.is);
            resourceMap.put("content.length", new Integer((int) pair.size));

            resourceMap.put("name", file.getName());
            resourceMap.put("url", getURLs()[0]);
            resourceMap.put("code.source.url", getURLs()[0]);

            break outer;
          }
        }
      }
    }

    if(pair == null) {
      return null;
    }

    final Resource resource = new Resource() {
      public int getContentLength() {
        final int contentLength =
            ((Integer)resourceMap.get("content.length")).intValue();

        return contentLength;
      }

      public InputStream getInputStream() {
        return (InputStream) resourceMap.get("input.stream");
      }

      public String getName() {
        return (String) resourceMap.get("name");
      }

      public URL getCodeSourceURL() {
        return (URL) resourceMap.get("code.source.url");
      }

      public URL getURL() {
        return (URL) resourceMap.get("url");
      }
    };

    return resource;
  }

  /**
   * Loads the file from jar file.
   *
   * @param file the file
   * @param name resource name
   * @return the pair that represents the resource
   * @throws IOException
   */
  public Pair loadResource(final File file, final String name)
          throws IOException {
    final JarFile jarFile = new JarFile(file.getPath());

    final ZipEntry zipEntry = jarFile.getEntry(name);

    if(zipEntry != null) {
      return new Pair(jarFile.getInputStream(zipEntry), zipEntry.getSize());
    }

    return null;
  }

  /**
   * This is the hook to get access to the private method
   * of the parent class.
   */
  private Class defineClass(final String name, final Resource res) {
    try {
      Class[] parameterTypes = new Class[] { String.class, Resource.class };

      Method method = getClass().getSuperclass().getDeclaredMethod("defineClass", parameterTypes);

      method.setAccessible(true);

      return (Class)method.invoke(this, new Object[] { name, res });
    }
    catch(NoSuchMethodException e) {
      throw new ClassFormatError();
    }
    catch(IllegalAccessException e) {
      throw new ClassFormatError();
    }
    catch(InvocationTargetException e) {
      throw new ClassFormatError();
    }
  }

}
