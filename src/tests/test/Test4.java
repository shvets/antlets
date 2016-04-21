package test;

import org.shvets.classloader.JarClassLoader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

public class Test4 {
    public static void main(String[] args) throws Exception {
      List filesList = new ArrayList();

      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\CafeBabe.jar");

      JarClassLoader loader = new JarClassLoader(filesList);

      //Class clazz = loader.loadClass("javax.activation.CommandInfo", true);
      Class clazz = loader.loadClass("org.shvets.mdi.MDIDesktopPane", true);

      System.out.println("class " + clazz.getName());

      Method methods[] = clazz.getDeclaredMethods();

      for(int i=0; i < methods.length; i++) {
        String methodName = methods[i].getName();
        System.out.println(methodName);
      }

      System.exit(0);
    }
}
