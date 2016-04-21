package org.shvets.antlet.launcher;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.tools.ant.launch.Locator;

import org.shvets.classloader.JarUrlClassLoader;
import org.shvets.antlet.starter.*;
import org.shvets.antlet.starter.Starter;

/**
 * This is the generic implementation of the launcher
 * for any Java program.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public class GenericLauncher implements Launcher {

  /**
   * Launches the class.
   *
   * @param args command line arguments
   */
  public void launch(final String[] args) throws Throwable {
    final String className = getMainClassName();

    if (className == null) {
      throw new IllegalArgumentException("Class name is missed.");
    }

    final List libPaths = new ArrayList();

    final String[] newArgs = removeLibParameters(args, libPaths);

    final List jars = searchJars(newArgs, libPaths);

    final URL[] urlJars = new URL[jars.size()];

    for (int i = 0; i < jars.size(); i++) {
      urlJars[i] = (URL) jars.get(i);
    }

    final ClassLoader loader = getMainClassLoader(urlJars);

    Thread.currentThread().setContextClassLoader(loader);

    invoke(className, newArgs, loader);
  }

  /**
   * Gets the name of the main class.
   *
   * @return the name of the main class
   */
  public String getMainClassName() {
    return System.getProperty(MAIN_CLASS_PROPERTY);
  }

  /**
   * Gets the class loader for the main class.
   *
   * @return the class loader for the main class
   */
  public ClassLoader getMainClassLoader(URL[] urlJars) {
    return new JarUrlClassLoader("$basic-root$", urlJars);
  }

  /**
   * Prepares new command line without "-lib" parameter pairs.
   *
   * @param args     original arguments
   * @param libPaths the list of library paths
   * @return modified arguments
   */
  private String[] removeLibParameters(final String[] args,
                                       final List libPaths) {
    int cnt = 0;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-lib")) {
        if (i < args.length - 1) {
          cnt += 2;
        }
        else {
          cnt += 1;
        }
      }
    }

    final String[] newArgs = new String[args.length - cnt];

    for (int i = 0, j = 0; i < args.length; i++) {
      final String arg = args[i];

      if (arg.equals("-lib")) {
        if (i < args.length - 1) {
          ++i;

          final String libPath = args[i];

          if (!libPaths.contains(libPath)) {
            libPaths.add(libPath);
          }
        }
      }
      else {
        newArgs[j++] = arg;
      }
    }

    return newArgs;
  }

  /**
   * Looks for jars to be added to the class loader.
   *
   * @param args     command line arguments
   * @param libPaths the list of library paths
   * @return the list of jars to be added to the class loader
   */
  protected List searchJars(final String[] args, final List libPaths)
            throws MalformedURLException {
    final List libPathURLs = new ArrayList();

    final Iterator iterator = libPaths.iterator();

    while (iterator.hasNext()) {
      final String libPath = (String) iterator.next();

      final StringTokenizer myTokenizer =
            new StringTokenizer(libPath, System.getProperty("path.separator"));

      while (myTokenizer.hasMoreElements()) {
        final String elementName = myTokenizer.nextToken();
        final File element = new File(elementName);

        if (elementName.indexOf("%") != -1 && !element.exists()) {
          continue;
        }

        if (element.isDirectory()) {
          // add any jars in the directory
          final URL[] dirURLs = Locator.getLocationURLs(element);

          for (int j = 0; j < dirURLs.length; ++j) {
            final URL dirURL = dirURLs[j];

            if (!libPathURLs.contains(dirURL)) {
              libPathURLs.add(dirURL);
            }
          }
        }

        final URL libPathURL = element.toURL();

        if (!libPathURLs.contains(libPathURL)) {
          libPathURLs.add(libPathURL);
        }
      }
    }

    final URL[] libJars = (URL[]) libPathURLs.toArray(new URL[0]);

    final File sourceJar = Locator.getClassSource(getClass());

    final File jarDir = sourceJar.getParentFile();

    final URL[] systemJars = Locator.getLocationURLs(jarDir);

    int numJars = libJars.length + systemJars.length;

    final File toolsJar = Locator.getToolsJar();

    if (toolsJar != null) {
      numJars++;
    }

    final URL[] urlJars = new URL[numJars];

    System.arraycopy(libJars, 0, urlJars, 0, libJars.length);
    System.arraycopy(systemJars, 0, urlJars, libJars.length, systemJars.length);

    if (toolsJar != null) {
      urlJars[urlJars.length - 1] = toolsJar.toURL();
    }

    final List jars = new ArrayList();

    for (int i = 0; i < urlJars.length; i++) {
      jars.add(urlJars[i]);
    }

    return jars;
  }

  /**
   * Invokes the class with the help of specified loader.
   *
   * @param className the class name to be started
   * @param args      the list of arguments the class will be invoked with
   * @param loader    the class loader
   */
  private void invoke(final String className, final String[] args,
                      final ClassLoader loader)
          throws Throwable {
    final Class mainClass = loader.loadClass(className);

    if (Starter.class.isAssignableFrom(mainClass)) {
      final Starter starter = (org.shvets.antlet.starter.Starter) mainClass.newInstance();

      starter.start(args, loader);
    }
    else {
      final Method method = getMainMethod(mainClass);

      if(method != null) {
        final Object starter = mainClass.newInstance();

        method.invoke(starter, new Object[]{args});
      }
      else {
        throw new IllegalArgumentException("public static void main(String[] argv) method is missed.");
      }
    }
  }

  /**
   * Searches for the "main" method.
   *
   * @param clazz the class where we are searching for "main" methd
   * @return the "main" method
   */
  public static Method getMainMethod(final Class clazz) {
    final Method[] methods = clazz.getMethods();

    for(int i = 0; i < methods.length; i++) {
      final Method method = methods[i];

      if(method.getName().equals(MAIN_METHOD_NAME_PROPERTY)) {
        final int modifiers = method.getModifiers();

        final boolean correctSignature =
                Modifier.isStatic(modifiers) &&
                Modifier.isPublic(modifiers) &&
                method.getReturnType() == Void.TYPE;

        if(correctSignature) {
          final Class[] paramTypes = method.getParameterTypes();

          final boolean correctParameters =
                  paramTypes.length == 1 &&
                  paramTypes[0] == (java.lang.String[].class);

          if(correctParameters) {
            return method;
          }
        }
      }
    }

    return null;
  }

  /**
   * Entry point for starting the program.
   *
   * @param args command line arguments
   */
  public static void main(final String[] args) throws Throwable {
    final Launcher launcher = new GenericLauncher();

    launcher.launch(args);
  }

}
