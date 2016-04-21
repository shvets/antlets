package org.shvets.antlet.launcher;

import java.io.File;
import java.util.List;
import java.net.MalformedURLException;

/**
 * This is the launcher for jar files.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class JarLauncher extends GenericLauncher {

  /**
   * Looks for jars to be added to the class loader.
   * Adds the main jar file to the list of libraries to be added
   * to the class loader
   *
   * @param args     command line arguments
   * @param libPaths the list of library paths
   * @return the list of jars to be added to the class loader
   */
  protected List searchJars(final String[] args, final List libPaths)
          throws MalformedURLException {
    final List jars = super.searchJars(args, libPaths);

    final String jarFileName = args[0];

    if (jarFileName != null) {
      jars.add(new File(jarFileName).toURL());
    }

    return jars;
  }

  /**
   * Entry point for starting Jar file.
   *
   * @param args command line arguments
   */
  public static void main(final String[] args) throws Throwable {
    final Launcher launcher = new JarLauncher();

    launcher.launch(args);
  }

}
