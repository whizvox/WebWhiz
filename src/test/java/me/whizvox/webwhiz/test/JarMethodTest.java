package me.whizvox.webwhiz.test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.concurrent.Executors;

public class JarMethodTest {

    public static void main(String[] args) throws Exception {

        URL url1 = new File("C:\\Users\\Neil\\dev\\TESTING\\Test2.jar").toURI().toURL();
        //URL url2 = new File("C:\\Users\\Neil\\dev\\TESTING\\Test2.jar").toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[] {url1}, ClassLoader.getSystemClassLoader());
        Enumeration<URL> resources = loader.getResources("Printer.class");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            System.out.println(url.getPath());
        }
        Class clazz = Class.forName("Printer", true, loader);
        String str = "Hello world!";
        Method printMethod = clazz.getMethod("print", String.class);
        Object inst = clazz.newInstance();
        printMethod.invoke(inst, str);
        loader.close();

        System.out.println("end");

    }

}
