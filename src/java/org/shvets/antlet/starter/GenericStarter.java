package org.shvets.antlet.starter;

import java.lang.reflect.Method;

import org.shvets.antlet.launcher.Launcher;

/**
 * This class is used for starting the class with given class loader.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public abstract class GenericStarter implements Starter {

  /**
   * "start" method.
   *
   * @param args   command line arguments
   * @param loader the class loader
   */
  public void start(final String[] args, final ClassLoader loader)
         throws Throwable {
    final String className = getMainClassName(args);

    if(className == null) {
      throw new IllegalArgumentException("Main class name is missed.");
    }

    final Class mainClass = loader.loadClass(className);

    final Object starter = mainClass.newInstance();

    final Method method =
          mainClass.getMethod(getMainMethodName(),
                              new Class[] { getMainMethodParameterTypes() });

    method.invoke(starter, new Object[] { getMainMethodParameters(args) });
  }

  /**
   * Gets the name of the main method.
   *
   * @return the name of the main method
   */
  public String getMainMethodName() {
    return Launcher.MAIN_METHOD_NAME_PROPERTY;
  }

  /**
   * Gets the main method parameter types.
   *
   * @return the main method parameter types
   */
  public Class getMainMethodParameterTypes() {
    return String[].class;
  }

  /**
   * Gets the main method parameters.
   *
   * @param args command line arguments
   * @return the main method parameters
   */
  public Object getMainMethodParameters(String[] args) {
    String[] newArgs = new String[args.length - 1];

    System.arraycopy(args, 1, newArgs, 0, newArgs.length);

    return newArgs;
  }

}
