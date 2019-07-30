import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderTest {
    public static void main(String[] args) throws Exception {

        //get Application  ClassLoader
        ClassLoader appClassLoader = ClassLoaderTest.class.getClassLoader();

        //get ExtClassLoader
        ClassLoader extClassLoader = appClassLoader.loadClass("com.sun.nio.zipfs.ZipCoder").getClassLoader();

        URL classUrl = new URL("file:" + System.getProperty("user.dir") + "/../2/"); //class path
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{classUrl}, appClassLoader);
        Class clazz = urlClassLoader.loadClass("MyClass1");
        Object o = clazz.newInstance();
        if(o instanceof MyInterface1){
            System.out.println("o is instanceof MyInterface1");
            MyInterface1 myInterface1 = (MyInterface1)o;
            myInterface1.show();
        }else {
            System.out.println("o is not instanceof MyInterface1");
        }

    }
}
