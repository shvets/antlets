package test;

import java.io.File;
import java.net.URL;

import org.shvets.classloader.JarUrlClassLoader;
import org.shvets.classloader.UrlClassLoader;

public class Test1 {

  public static void main(String[] argv) throws Throwable {
    String jarFileName = "d:/Env/Antlets/repository/CafeBabe/CafeBabe.jar";

    File file = new File(jarFileName);

    URL[] urls = new URL[] {file.toURL()};

    UrlClassLoader loader = new JarUrlClassLoader(urls);

    //Class c = loader.loadClass("javax.activation.CommandInfo");
    Class c = loader.loadClass("org.shvets.mdi.MDIDesktopPane");

    System.out.println("className " + c.getName());
  }

}
