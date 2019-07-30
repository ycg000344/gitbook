> 转载 <https://github.com/xuqifzz>

# 类加载机制

本课老师提到类被卸载需要满足两个条件
1. 该Class所有实例都已被GC
2. 加载该类的ClassLoader实例已经被GC

而<<深入理解Java虚拟机>>一书中提到,需要满足3个条件,多出的一个条件是
 > 该类对应的Class对象没有在任何地方被引用

我经过测试发现, 这个条件是多余的, 因为如果不满足该条件的话,该类的ClassLoader不可能会被GC

本节课老师讲解了类加载机制, Java可以让不同的类加载器加载同一位置的类多次。

我觉得这种做法很奇妙,但随即想到一个问题: 如果类A实现了接口B, 而A和B分别由不同的类加载器加载, 那么A instanceof B成立吗?

## 测试代码
* 首先新建两个文件夹,分别命名为`1`和`2`,
* 将`ClassLoaderTest.java`和`MyInterface1.java`放到文件夹`1`中;
* 将`MyClass1.java`和`MyInterface1.java`放到文件夹`2`中;
* 注意`MyInterface1.java`目前同时存在于`1`和`2 `中

MyInterface1.java
```java
public interface MyInterface1 {
    void show();
}
```

MyClass1.java
```java
public class MyClass1 implements MyInterface1 {
	@Override
    public void show() {
        System.out.println("MyClass1 show!");
    }
}
```

ClassLoaderTest.java
```java
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
```

## 测试1-子加载器的类实现父加载器的接口

在上面的代码中,`urlClassLoader`是`appClassLoader`的子加载器, `MyInterface1`由父加载器加载, `MyClass1`由子加载器加载。

```
# 在1中执行
javac ClassLoaderTest.java
# 在2中执行
javac MyClass1.java
# 在1中执行
java ClassLoaderTest
```

执行结果:
```
o is instanceof MyInterface1
MyClass1 show!
```

可以看到, 结果很正常, 这里`MyClass1`和`ClassLoaderTest`引用的`MyInterface1` 都是由`appClassLoader`加载的, 所以一切很和谐。

## 测试2-子加载器的类实现兄弟加载器的接口
将`ClassLoaderTest`的代码做如下修改

```java
//将如下代码
URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{classUrl}, appClassLoader);
//改为
URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{classUrl}, extClassLoader);

```

这样一来, `appClassLoader`和`urlClassLoader`成了兄弟关系, 这哥两都是`extClassLoader`的子加载器

重新编译`ClassLoaderTest`后再次运行,结果如下:
```
o is not instanceof MyInterface1
```

这里`MyClass`引用的`MyInterface1`来自于`urlClassLoader`, 而`ClassLoaderTest`引用的`MyInterface1`来自于`appClassLoader`,这两个`MyInterface1`是不同的, `MyClass`并非`appClassLoader`里的`MyInterface1`的实例。

## 测试3-子加载器实现的接口与父加载器的接口签名相同,但方法更多

这里先回滚测试2的修改,让`urlClassLoader`仍作为`appClassLoader`的子加载器。

然后修改`2`文件夹里的`MyClass1.java`和`MyInterface1.java`, 为`MyInterface1`增加一个`show2`方法,并实现它
```java
public interface MyInterface1 {
    void show();
	void show2();
}

public class MyClass1 implements MyInterface1 {
	@Override
    public void show() {
        System.out.println("MyClass1 show!");
    }
	@Override
    public void show2() {
        System.out.println("MyClass1 show2!");
    }
}
```

在`1`里重新编译`ClassLoaderTest`, 在`2`里重新编译`MyClass1`,再运行`ClassLoaderTest`,结果如下:
```
o is instanceof MyInterface1
MyClass1 show!
```

这结果居然成功了, MyClass1实现的明明不是`appClassLoader`里的`MyInterface1`, 但因为两个`MyInterface1`的`show`方法签名一致,居然也能成功调用

## 测试4 子加载器实现的接口与父加载器的接口签名不同

这次干脆删掉`2`文件夹里的`MyInterface1`的`show`方法, 只保留`show2`方法, 而`MyCalss1`里仍保留`show`方法,但去掉`@Override`注解,代码如下:

```java
public interface MyInterface1 {
	void show2();
}

public class MyClass1 implements MyInterface1 {
    public void show() {
        System.out.println("MyClass1 show!");
    }
	@Override
    public void show2() {
        System.out.println("MyClass1 show2!");
    }
}
```

这样一来`1`和`2`里的`MyInterface1`仅仅只是名称相同了,重新编译运行后,发现仍然可以正常工作:
```
o is instanceof MyInterface1
MyClass1 show!
```

## 测试5 子类不提供与父加载器的接口的方法

这一次把`MyCalss1`中的`show`方法删掉,如下:

```java
public class MyClass1 implements MyInterface1 {
	@Override
    public void show2() {
        System.out.println("MyClass1 show2!");
    }
}
```

重新编译运行后,结果如下:
```
o is instanceof MyInterface1
Exception in thread "main" java.lang.AbstractMethodError: MyClass1.show()V
        at ClassLoaderTest.main(ClassLoaderTest.java:20)
```

终于出问题了, 因为`MyClass1`压根就没有实现`show`方法,这说明Java在调用方法的时候,引用是什么类型的并不重要,它查找方法就是靠方法签名,找得到就能执行, 找不到就抛异常。

## static代码块研究

Java中的static代码块在同一类加载器下保证最多只运行一次, 那Java究竟是如何保证的呢?

会不会是所有类的static块都放到同一个线程里去运行呢?

测试代码:

```java

public class StaticClass1 {
    static {
        System.out.println(Thread.currentThread().getName() +  ": StaticClass1 static");
   }
}

public class StaticClass2 {
    static {
        System.out.println(Thread.currentThread().getName() +  ": StaticClass2 static");
    }
}


public class StaticTest {
    static void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Thread thread1 = new Thread(()->{
            new StaticClass1();
        });
        Thread thread2 = new Thread(()->{
            new StaticClass2();
        });
        thread1.setName("thread-1");
        thread2.setName("thread-2");
        thread1.start();
        thread2.start();
    }
}

```
运行结果:

```
thread-1: StaticClass1 static
thread-2: StaticClass2 static
```

显然不是,static代码块就是在调用线程中运行的。

## 一个类的static块会阻塞另一个类的static块吗

问题又来了, 既然Java要保证static代码块只会运行一次, 那会不会把所有类的static块都进行同步,串行运行呢?

修改`StaticClass1`和`StaticClass2`代码:
```java
public class StaticClass1 {
    static {
        System.out.println(Thread.currentThread().getName() +  ": StaticClass1 static");
        StaticTest.sleep(1000);
        System.out.println(Thread.currentThread().getName() +  ": sleep end");
    }
}

public class StaticClass2 {
    static {
        System.out.println(Thread.currentThread().getName() +  ": StaticClass2 static");
        StaticTest.sleep(1000);
        System.out.println(Thread.currentThread().getName() +  ": sleep end");
    }
}

```

重新运行`StaticTest`,结果如下:
```
thread-2: StaticClass2 static
thread-1: StaticClass1 static
thread-2: sleep end
thread-1: sleep end
```

显然也不是,不同类的static代码块是并行运行的, Java并未加入同步机制。


## Java是如何保证同一个类的static只执行一次的

再研究一下Java是如何保证同一个类的static只执行一次的, 代码改写如下:
```java
public class StaticClass1 {
    static {
        System.out.println(Thread.currentThread().getName() +  ": StaticClass1 static");
        StaticTest.sleep(10000);    //将睡眠时间延长到10秒
        System.out.println(Thread.currentThread().getName() +  ": sleep end");
    }
}

//StaticTest的main方法
public static void main(String[] args){
    Thread thread1 = new Thread(()->{
        new StaticClass1();
        System.out.println(Thread.currentThread().getName() + " end");
    });
    Thread thread2 = new Thread(()->{
        sleep(500);
        new StaticClass1();
        System.out.println(Thread.currentThread().getName() + " end");
    });
    thread1.setName("thread-1");
    thread2.setName("thread-2");
    thread1.start();
    thread2.start();
    sleep(2000);
    System.out.println("thread-1 state: " + thread1.getState());
    System.out.println("thread-2 state: " + thread2.getState());
}
```

运行结果如下:
```
thread-1: StaticClass1 static
thread-1 state: TIMED_WAITING
thread-2 state: RUNNABLE
thread-1: sleep end
thread-1 end
thread-2 end
```

其中static块在thread-1中运行, static块中调用了sleep, 所以thread-1的状态为TIMED_WAITING一点也不奇怪。

奇怪的是thread-2的状态居然是`RUNNABLE`(我以为应该是`BLOCKED`), 也就是说thread-2是以一种特别的机制(比如自旋锁之类的)在等待其他线程static的执行完成, 至于具体是什么机制, 我目前还没查到。

# JVM 内置工具

本节老师一口气介绍了很多JVM内置工具, 我在这里整理一下

注意:标记为`*`的工具表示试验工具,Oracle不提供技术支持


| 工具名称 | 描述  |  范例 |
| ------- | ----- | ----- |
| [javap](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javap.html) | 反汇编classs文件 |  `javap -v -p xxx.class > out.txt` |
| [jps](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jps.html) * | 列出系统上检测到的JVM |  `jps -q -mlvV` |
| [jstat](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html) * | 监视JVM的统计信息 |  `jstat -gc pid` |
| [jcmd](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jcmd.html) | 向正在运行的JVM发出诊断命令(可以替代jps) |  `jcmd pid GC.heap_info` |
| [jinfo](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jinfo.html) * | 查看或设置JVM参数 |  `jinfo pid` |
| [jmap](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jmap.html) * | 打印出Java进程中对象的情况。或者将JVM中的堆以二进制输出成文件 |  `jmap -dump:live,format=b,file=heap.bin pid` |
| [jhat](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jhat.html) * | 分析Java堆,将堆中对象以html展示,支持对象查询语言OQL |  `jhat -port 8080 heap.bin` |
| [jstack](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstack.html) * | 打印出给定Java进程或core file或远程调是服务的Java栈信息 |  `jstack -F pid > jstack.log` |
| [jconsole](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jconsole.html)  | 图形化的方式管理Java应用程序 |  GUI界面,双击运行 |
| [jvisialVM](https://docs.oracle.com/javase/8/docs/technotes/guides/visualvm/index.html)  | 更加强大的图形化的方式管理Java应用程序 |  GUI界面,双击运行 |

