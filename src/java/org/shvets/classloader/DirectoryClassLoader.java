// DirectoryClassLoader.java

package org.shvets.classloader;

import org.shvets.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;

/**
 * This is directory-based class loader.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public final class DirectoryClassLoader extends ByteArrayClassLoader {

  private final String dirName;

  /**
   * Creates new class loader for the directory.
   *
   * @param dirName the directory name
   */
  public DirectoryClassLoader(final String dirName) {
    this.dirName = dirName;
  }

  /**
   * Load bytes that represents the class.
   *
   * @param name the class name
   * @return bytes for the class
   */
  protected byte[] loadClassBytes(final String name) {
    final String fileName = dirName + File.separator + toClassFileName(name);

    final File file = new File(fileName);

    try {
      final FileInputStream fis = new FileInputStream(file);

      return FileUtil.getStreamAsBytes(fis);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Gets the directory name.
   *
   * @return the directory name
   */
  public String getDirName() {
    return dirName;
  }

}
