package org.shvets.tools.ant.taskdefs;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import org.shvets.classloader.UrlClassLoader;

/**
 * Adds new libraries to the current class loader.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class AddToClassLoaderTask extends Task {
  //Example:
  // <addtoclassloader>
  //  <path>
  //    <fileset dir="lib">
  //      <include name="**/*.jar"/>
  //    </fileset>
  //  </path>
  // </addtoclassloader>
  //

  /** The path with libraries to be added. */
  private Path path;

  /**
   * Sets the path.
   *
   * @param s the path
   */
  public void setPath(Path s) {
    createPath();

    path.append(s);
  }

  /**
   * Creates a nested path element.
   *
   * @return the path
   */
  public Path createPath() {
    if(path == null) {
      path = new Path(getProject());
    }

    return path;
  }

  /**
   * Adds a reference to a PATH defined elsewhere.
   *
   * @param reference the reference to path
   */
  public void setPathRef(Reference reference) {
    createPath();

    path.setRefid(reference);
  }

  /**
   * The method executing the task.
   */
  public void execute() throws BuildException {
    if(path == null) {
      throw new BuildException("Path attribute must be set!", getLocation());
    }

    final String [] list = path.list();

    if(list.length > 0) {
      final UrlClassLoader classLoader =
         (UrlClassLoader)getProject().getCoreLoader();

      for(int i=0; i < list.length; i++) {
        final File file =new File(list[i]);

        if(file.exists()) {
          try {
            classLoader.addURL(file.toURL());
          }
          catch(MalformedURLException e) {
            throw new IllegalArgumentException(e.toString());
          }
        }
      }
    }
  }

}
