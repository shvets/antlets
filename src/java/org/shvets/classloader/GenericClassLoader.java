package org.shvets.classloader;

import java.util.Hashtable;
import java.io.File;

/**
 * This is the generic class loader.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public abstract class GenericClassLoader extends ClassLoader {
  private final Hashtable cache = new Hashtable();

  /**
   * Load bytes that represents the class.
   *
   * @param name the class name
   * @return bytes for the class
   */
  protected abstract byte[] loadClassBytes(String name);

  /**
   * Loads the class.
   *
   * @param className the class className
   * @return the class object
   */
  public final synchronized Class loadClass(final String className)
          throws ClassNotFoundException {
    return loadClass(className, true);
  }

  /**
   * Loads the class.
   *
   * @param className the class className
   * @param resolveIt resolve it flag
   * @return the class object
   */
  public final synchronized Class loadClass(final String className, final boolean resolveIt)
          throws ClassNotFoundException {
    // Check the cache of classes
    Class result = (Class) cache.get(className);

    if (result == null) {
      // Check with the primordial class loader
      try {
        // Try to get the class from the system class loader.
        result = findSystemClass(className);
      }
      catch (ClassNotFoundException e) {
        // It was not a system class so try getting it via our own method
        result = null;
      }
      catch (Error t) {
        result = null;
      }

      if (result == null) {
        if (className.startsWith("java.lang")) {
          ; // ignore it
        }
        else {
          // Check if the class was already loaded
          try {
            result = findLoadedClass(className);
          }
          catch (Error t) {
            result = null;
          }

          // Still no class found?  Try our class loader...
          if (result == null) {
            try {
              // Try to load it from the added class paths
              final byte[] classData = loadClassBytes(className);

              if (classData == null) {
                throw new ClassNotFoundException(className);
              }

              // Define it (parse the class file)

              try {
                result = defineClass(className, classData, 0, classData.length);
              }
              catch (Throwable t) {
              }
            }
            catch (Exception e) {
              System.out.println("className " + className);
              e.printStackTrace();

              throw new ClassFormatError(className);
            }

            // Add the class to the cache
            if (result != null) {
              cache.put(className, result);
            }
          }
        }
      }
    }

    if (result != null && resolveIt) {
      resolveClass(result);
    }

    return result;
  }

  /**
   * Converts the name to class file name.
   *
   * @param name
   * @return class file name
   */
  protected final String toClassFileName(final String name) {
    String className = name.replace('.', File.separatorChar);

    className = className.replace('/', File.separatorChar);

    className = className + ".class";

    return className;
  }

}
