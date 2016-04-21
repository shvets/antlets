package org.shvets.classloader;

import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * This class is used to have an ability to add new libraries
 * to current loader.
 *
 * @version 1.0 05/22/2004
 * @author Alexander Shvets
 */public class UrlClassLoader extends URLClassLoader {

  /** The name of this loader. */
  private String name;

  /**
   * Creates new URL class loader.
   *
   * @param name The name of this loader
   * @param urls the arry of urls
   * @param parent The parent loader.
   */
  public UrlClassLoader(String name, URL[] urls, ClassLoader parent) {
    super(urls, parent);

    this.name = name;
  }

  /**
   * Creates new URL class loader.
   *
   * @param name The name of this loader.
   * @param parent The parent loader.
   */
  public UrlClassLoader(String name, ClassLoader parent) {
    this(name, new URL[0], parent);
  }

  /**
   * Creates new URL class loader.
   *
   * @param name The name of this loader
   * @param urls the arry of urls
   */
  public UrlClassLoader(String name, URL[] urls) {
    super(urls);

    this.name = name;
  }

  /**
   * Adds new url to the classloader.
   *
   * @param url the URL to be added
   */
  public void addURL(final URL url) {
    super.addURL(url);
  }

  /**
   * Adds all libraries from specified directory.
   *
   * @param repository the directory with libraries
   */
  public void addRepository(final File repository) {
    if(repository.exists() && repository.isDirectory()) {
      final File[] jars = repository.listFiles();

      for(int i = 0; i < jars.length; i++) {
        final String name = jars[i].getName().toLowerCase();

        if(name.endsWith(".jar") || name.endsWith(".zip")) {
          try {
            final URL url = jars[i].toURL();
            addURL(url);
          }
          catch(MalformedURLException e) {
            throw new IllegalArgumentException(e.toString());
          }
        }
      }
    }
  }

  public String toString() {
    return "UrlClassLoader {\n  name=\"" + name + "\"\n}";
  }

}
