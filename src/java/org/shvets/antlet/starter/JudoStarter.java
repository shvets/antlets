package org.shvets.antlet.starter;

/**
 * This is the launcher for Judo files.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class JudoStarter extends GenericStarter {

  /**
   * Gets the name of the main class.
   *
   * @param args   command line arguments
   * @return the name of the main class
   */
  public String getMainClassName(String[] args) {
    return "com.judoscript.JudoEngine";
  }

  /**
   * Gets the name of the main method.
   *
   * @return the name of the main method
   */
  public String getMainMethodName() {
    return "runScript";
  }

  /**
   * Gets the main method parameter types.
   *
   * @return the main method parameter types
   */
  public Class getMainMethodParameterTypes() {
    return String.class;
  }

  /**
   * Gets the main method parameters.
   *
   * @param args command line arguments
   * @return the main method parameters
   */
  public Object getMainMethodParameters(String[] args) {
    return args[0];
  }

}
