package test;

import org.shvets.classloader.DirectoryClassLoader;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: Apr 30, 2004
 * Time: 7:55:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test3 {
    public static void main(String[] args) throws Exception {
      DirectoryClassLoader loader = new DirectoryClassLoader("classes");

      Class clazz = loader.loadClass("org.shvets.antlet.launcher.AntLauncher", true);
      System.out.println("class " + clazz.getName());

      Method methods[] = clazz.getDeclaredMethods();

      for(int i=0; i < methods.length; i++) {
        String methodName = methods[i].getName();
        System.out.println(methodName);
      }
    }
}
