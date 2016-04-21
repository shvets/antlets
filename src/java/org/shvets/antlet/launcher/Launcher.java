package org.shvets.antlet.launcher;

/**
 * This is the launcher for any Java program.
 * It will look for the "launch" method and invoke it.
 * If this method does not exist, the launcher will try to look
 * for the "main" method - if it exists - invokes it.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public interface Launcher {
  /** Main class property. */
  String MAIN_CLASS_PROPERTY = "main.class";

  /** Main method name property. */
  String MAIN_METHOD_NAME_PROPERTY = "main";

  /**
   * Launches the program.
   *
   * @param args command line arguments
   */
  void launch(String[] args) throws Throwable;

}
