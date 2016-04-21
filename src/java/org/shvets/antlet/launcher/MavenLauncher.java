package org.shvets.antlet.launcher;

import java.io.File;
import java.io.FileInputStream;

import com.werken.forehead.Forehead;

/**
 * This is the implementation of the launcher for Maven.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public class MavenLauncher implements Launcher {

  /**
   * "start" method.
   *
   * @param args   command line arguments
   */
  public void launch(final String[] args)
         throws Throwable {
    Forehead starter = Forehead.getInstance();

    String confFileName = System.getProperty("forehead.conf.file");

    File confFile = new File(confFileName);

    starter.config(new FileInputStream(confFile));

    starter.run(args);
  }

  /**
   * Entry point for starting the program.
   *
   * @param args command line arguments
   */
  public static void main(final String[] args) throws Throwable {
    final Launcher launcher = new MavenLauncher();

    launcher.launch(args);
  }

}
