// ByteArrayClassLoader.java

package org.shvets.classloader;

/**
 * This is the class loader from byte array.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */
public abstract class ByteArrayClassLoader extends GenericClassLoader {

  private byte[] classBytes;

  /**
   * Creates new byte array class loader.
   */
  public ByteArrayClassLoader() {
  }

  /**
   * Creates new byte array class loader.
   *
   * @param classBytes the list of class bytes
   */
  public ByteArrayClassLoader(final byte[] classBytes) {
    this.classBytes = classBytes;
  }

  /**
   * Load bytes that represents the class.
   *
   * @param name the class name
   * @return bytes for the class
   */
  protected byte[] loadClassBytes(final String name) {
    return classBytes;
  }

  /**
   * Sets the list of class bytes.
   *
   * @param classBytes the list of class bytes
   */
  public final void setByteArray(final byte[] classBytes) {
    this.classBytes = classBytes;
  }

}
