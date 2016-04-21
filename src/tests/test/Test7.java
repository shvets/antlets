package test;

import org.shvets.classloader.JarClassLoader;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.InputStream;

public class Test7 {
    public static void main(String[] args) throws Exception {
      List filesList = new ArrayList();

      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\CafeBabe.jar");
      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\lib\\starters.jar");
      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\lib\\launchers.jar");
      filesList.add("D:\\Work\\Antlets\\main\\v1.1\\lib\\bsf-2.3.0-rc1.jar");

      JarClassLoader loader = new JarClassLoader(filesList);

      InputStream is = loader.getResourceAsStream("org/apache/bsf/Languages.properties");

      Properties props = new Properties ();

      props.load(is);

      System.out.println("props " + props);
    }
}
