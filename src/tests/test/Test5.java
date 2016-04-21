package test;

import org.shvets.classloader.JarClassLoader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class Test5 {
    public static void main(String[] args) throws Exception {
      List filesList = new ArrayList();

      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\CafeBabe.jar");

      JarClassLoader loader = new JarClassLoader(filesList);
      JarFile jarFile = new JarFile("D:\\Work\\Antlets\\main\\v1.1\\CafeBabe.jar");

      byte[] buffer = loader.loadFile(jarFile, "lib/activation.jar", "javax.activation.CommandInfo");

      System.out.println("buffer.length " + buffer.length);
    }
}
