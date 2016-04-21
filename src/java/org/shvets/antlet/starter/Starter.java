package org.shvets.antlet.starter;

/**
 * This class is used for starting the class with 
 * the given class loader.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public interface Starter {

  /**
   * "Start" method.
   *
   * @param args   command line arguments
   * @param loader the class loader
   */
  void start(String[] args, ClassLoader loader) throws Throwable;

  /**
   * Gets the name of the main class.
   *
   * @param args   command line arguments
   * @return the name of the main class
   */
  String getMainClassName(String[] args);

  /**
   * Gets the name of the main method.
   *
   * @return the name of the main method
   */
  String getMainMethodName();

  /**
   * Gets the main method parameter types.
   *
   * @return the main method parameter types
   */
  Class getMainMethodParameterTypes();

  /**
   * Gets the main method parameters.
   *
   * @param args command line arguments
   * @return the main method parameters
   */
  Object getMainMethodParameters(String[] args);

}
