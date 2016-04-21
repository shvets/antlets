package org.shvets.util;

import java.io.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;
import java.util.jar.Manifest;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * This is the class for holding file utils.
 *
 * @version 1.0 05/14/2004
 * @author Alexander Shvets
 */

public final class FileUtil {

  /**
   * Copies the stream to the temporary file.
   *
   * @param is the input stream
   * @param file the file
   * @return the temporary file
   * @throws IOException I/O Exception
   */
  public static File copyToFile(final InputStream is, final File file)
          throws IOException {
    final FileOutputStream fos = new FileOutputStream(file);

    ReadableByteChannel source = null;
    FileChannel target = null;

    try {
      source = Channels.newChannel(is);
      target = fos.getChannel();

      final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

      while (source.read(buffer) != -1) {
        // prepare the buffer to be drained
        buffer.flip();
        // make sure the buffer was fully drained.
        while (buffer.hasRemaining()) {
          target.write(buffer);
        }

        // make the buffer empty, ready for filling
        buffer.clear();
      }
    }
    finally {
      if (source != null) {
        source.close();
      }

      if (target != null) {
        target.close();
      }
    }

    return file;
  }

  /**
   * Copies the stream to the temporary file.
   *
   * @param is     the input stream
   * @param prefix the prefix part of the file name
   * @param suffix the suffix part of the file name
   * @return the temporary file
   * @throws java.io.IOException I/O Exception
   */
  public static File copyToTempFile(final InputStream is, final String prefix, final String suffix)
          throws IOException {
    return copyToFile(is, File.createTempFile(prefix, suffix));
  }

  /**
   * Gets the manifest object from jar file.
   *
   * @param jarFileName the jar file name
   * @return the manifest object
   * @throws IOException I/O Exception
   */
  public static Manifest getManifest(final String jarFileName) throws IOException {
    final JarFile jarFile = new JarFile(jarFileName);

    final Manifest manifest = getManifest(jarFile);

    jarFile.close();

    return manifest;
  }

  /**
   * Gets the manifest object from jar file.
   *
   * @param jarFile the jar file
   * @return the manifest object
   * @throws IOException I/O Exception
   */
  public static Manifest getManifest(final JarFile jarFile) throws IOException {
    Manifest manifest = null;

    ZipEntry zipEntry = jarFile.getEntry("META-INF/MANIFEST.MF");

    if (zipEntry == null) {
      zipEntry = jarFile.getEntry("meta-inf/manifest.mf");
    }

    if (zipEntry != null) {
      final InputStream is = jarFile.getInputStream(zipEntry);

      manifest = new Manifest(is);

      is.close();
    }

    return manifest;
  }

  /**
   * Converst input stream into the byte array.
   *
   * @param is the input stream
   * @return the byte array
   */
  public static byte[] getStreamAsBytes(final InputStream is) throws IOException {
    final BufferedInputStream bis = new BufferedInputStream(is);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    final byte[] buffer = new byte[2048];

    while (true) {
      final int n = bis.read(buffer);

      if (n == -1) {
        break;
      }

      baos.write(buffer, 0, n);
    }

    bis.close();
    baos.close();

    return baos.toByteArray();
  }

}
