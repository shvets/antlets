package org.shvets.antlet.starter;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.shvets.util.FileUtil;
import org.shvets.antlet.launcher.Launcher;

/**
 * This class is used for starting Jar file as executable.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class JarStarter extends GenericStarter {
  /** The location of ant project file inside jar. */
  public static final String DEFAULT_ANT_PROJECT_LOCATION = "META-INF/default.ant";

  private String mainClassName;

  /**
   * "Start" method.
   *
   * @param args   command line arguments
   * @param loader the class loader
   */
  public void start(final String[] args, final ClassLoader loader)
         throws Throwable {
    final String jarFileName = args[0];

    final Manifest manifest = FileUtil.getManifest(args[0]);

    if(manifest != null) {
      final Attributes mainAttributes = manifest.getMainAttributes();

      mainClassName = mainAttributes.getValue(Attributes.Name.MAIN_CLASS);

      if(mainClassName == null) {
        mainClassName = mainAttributes.getValue("MIDlet-Name");
      }
    }

    final File buildFile = getProjectFile(jarFileName);

    if (buildFile != null) {
      final org.shvets.antlet.starter.Starter starter = new AntStarter();

      System.setProperty("jar.file", jarFileName);
      System.setProperty(Launcher.MAIN_CLASS_PROPERTY, mainClassName);

      final String[] newArgs = new String[args.length + 2];

      System.arraycopy(args, 0, newArgs, 2, args.length);

      newArgs[0] = "-f";
      newArgs[1] = buildFile.getPath();
      newArgs[2] = "-Dbasedir=" + new File(jarFileName).getParent();

      starter.start(newArgs, loader);

      buildFile.delete();
    }
    else {
      super.start(args, loader);
    }
  }

  /**
   * Gets the Ant project file located inside jar file.
   *
   * @param jarFileName the name of jar file
   * @return Ant project file
   * @throws IOException I/O Exception
   */
  private File getProjectFile(final String jarFileName) throws IOException {
    File projectFile = null;

    final JarFile jarFile = new JarFile(jarFileName);

    final ZipEntry zipEntry = jarFile.getEntry(DEFAULT_ANT_PROJECT_LOCATION);

    if (zipEntry != null) {
      projectFile = 
        FileUtil.copyToTempFile(jarFile.getInputStream(zipEntry), "default-", ".ant");
    }

    return projectFile;
  }

  /**
   * Gets the name of the main class.
   *
   * @param args   command line arguments
   * @return the name of the main class
   */
  public final String getMainClassName(final String[] args) {
    return mainClassName;
  }

}
