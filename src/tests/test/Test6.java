package test;

import org.shvets.classloader.JarClassLoader;

import java.util.List;
import java.util.ArrayList;

public class Test6 {
    public static void main(String[] args) throws Exception {
      List filesList = new ArrayList();

      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\CafeBabe.jar");
      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\lib\\starters.jar");
      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\lib\\launchers.jar");

      JarClassLoader loader = new JarClassLoader(filesList);

      byte[] buffer = loader.loadClassBytes("org.shvets.antlet.starter.JarStarter");

      System.out.println("buffer.length " + buffer.length);
    }
}
