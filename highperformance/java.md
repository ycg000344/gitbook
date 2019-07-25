# Java高性能专题预备

## Java程序运行原理分析

## Demo

```java
/**
 * @ClassName:Demo1
 * @Description: 使用Javap 将编译后的16进制文件转化为可读的文件
 * @Date: 2019/1/5 13:25
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class Demo1 {
    public int x;
    public int sum(int a, int b) {
        return a + b;
    }
    public static void main(String[] args) {
        Demo1 demo1 = new Demo1();
        demo1.x = 3;
        int y = 2;
        int z = demo1.sum(demo1.x, y);
        System.out.println("person age is " + z);
    }
}
```

### 编译Java文件后查看内容

```shel
    javac Demo.java
    javap -v Demo.class > demo.txt
```

### 默认的构造函数

```java
public application_1.chapter_00.Demo1();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 11: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lapplication/chapter_00/Demo1;
```

* 因为代码中没有显示定义Demo1的构造函数,因此javac生成了一个默认的无参构造函数
* 这个构造函数因为存在一个隐含的this参数,所以locals以及args_size都为1 
* 因为使用this参数调用了父类的初始化方法(`<init>`),所以需要将this先入栈`aload_0`,然后在用`invokespecial #1`进行调用,这就是stack=1的原因.
* `invokespecial #1`表示调用常量表中的第1项,即:
```java
    #1 = Methodref          #14.#37        // java/lang/Object."<init>":()V
```
* 此处引用的是一个方法引用,再顺腾摸瓜,可以找到相关的所有内容
```java
  #14 = Class              #50            // java/lang/Object
  #15 = Utf8               x
  #16 = Utf8               I
  #17 = Utf8               <init>
  #18 = Utf8               ()V
  #19 = Utf8               Code
  #20 = Utf8               LineNumberTable
  #21 = Utf8               LocalVariableTable
  #22 = Utf8               this
  #23 = Utf8               Lapplication/chapter_00/Demo1;
  #24 = Utf8               sum
  #25 = Utf8               (II)I
  #26 = Utf8               a
  #27 = Utf8               b
  #28 = Utf8               main
  #29 = Utf8               ([Ljava/lang/String;)V
  #30 = Utf8               args
  #31 = Utf8               [Ljava/lang/String;
  #32 = Utf8               demo1
  #33 = Utf8               y
  #34 = Utf8               z
  #35 = Utf8               SourceFile
  #36 = Utf8               Demo1.java
  #37 = NameAndType        #17:#18        // "<init>":()V
```



---

## Java 内存结构

待去 [oracle](docs.oracle.com) 和 [openjdk](openjdk.com) 获取具体的说明文档 

### 线程共享区

#### 方法区

#### 堆内存

### 线程私有区

#### 本地方法栈

#### 虚拟机栈

#### 程序计数器

---

## 线程状态

```
    Thread.State{
        NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED 
    }
```

---

## 线程终止

* stop: 可能会导致线程安全问题,不建议使用
* interrupt: JDK比较推荐的方式
* 标志位: 通过判断标志位主动退出线程

### CODE

```java
/**
 * @ClassName:Demo3
 * @Description: 线程stop强制性中止，破坏线程安全的示例
 * @Date: 2019/1/5 13:50
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class Demo3 {
    public static void main(String[] args) throws InterruptedException {
        StopThread thread = new StopThread();
        thread.start();
        // 休眠1秒，确保i变量自增成功
        Thread.sleep(1000);
        // 暂停线程
        //  thread.stop(); // 错误的终止
        thread.interrupt(); // 正确终止
        while (thread.isAlive()) {
            // 确保线程已经终止
        } // 输出结果
        thread.print();
    }
}
```

---

## CPU缓存和内存屏障

* CPU缓存 - 多级缓存
* 缓存同步协议 - MESI协议
* 最终一致性
* 运行时指令重排
* 数据不一致问题
* 乱序执行 - as-if-serial语义
* 内存屏障 - 写内存屏障和读内存屏障

---

## 线程通信

* suspend/resume
* wait/notify
* park/unpark


* 是否容易写出死锁代码
* 加锁与解锁顺序问题
* 其他问题,比如同步代码块,许可叠加问题

### CODE

```java
/**
 * @ClassName:Demo6
 * @Description: 线程通信
 * 1. suspend/resume
 * 2. wait/notify
 * 3. park/unpark
 * @Date: 2019/1/5 13:56
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class Demo6 {
    /**
     * 包子店
     */
    public static Object baozidian = null;

    /**
     * 正常的suspend/resume
     */
    public void suspendResumeTest() throws Exception {
        // 启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null) { // 如果没包子，则进入等待
                System.out.println("1、进入等待");
                Thread.currentThread().suspend();
            }
            System.out.println("2、买到包子，回家");
        });
        consumerThread.start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        consumerThread.resume();
        System.out.println("3、通知消费者");
    }
    /**
     * 死锁的suspend/resume。 suspend并不会像wait一样释放锁，故此容易写出死锁代码
     */
    public void suspendResumeDeadLockTest() throws Exception {
        // 启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null) { // 如果没包子，则进入等待
                System.out.println("1、进入等待");
                // 当前线程拿到锁，然后挂起
                synchronized (this) {
                    Thread.currentThread().suspend();
                }
            }
            System.out.println("2、买到包子，回家");
        });
        consumerThread.start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        // 争取到锁以后，再恢复consumerThread
        synchronized (this) {
            consumerThread.resume();
        }
        System.out.println("3、通知消费者");
    }
    /**
     * 导致程序永久挂起的suspend/resume
     */
    public void suspendResumeDeadLockTest2() throws Exception {
        // 启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null) {
                System.out.println("1、没包子，进入等待");
                try { // 为这个线程加上一点延时
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 这里的挂起执行在resume后面
                Thread.currentThread().suspend();
            }
            System.out.println("2、买到包子，回家");
        });
        consumerThread.start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        consumerThread.resume();
        System.out.println("3、通知消费者");
        consumerThread.join();
    }
    /**
     * 正常的wait/notify
     */
    public void waitNotifyTest() throws Exception {
        // 启动线程
        new Thread(() -> {
            synchronized (this) {
                while (baozidian == null) { // 如果没包子，则进入等待
                    try {
                        System.out.println("1、进入等待");
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("2、买到包子，回家");
        }).start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        synchronized (this) {
            this.notifyAll();
            System.out.println("3、通知消费者");
        }
    }
    /**
     * 会导致程序永久等待的wait/notify
     */
    public void waitNotifyDeadLockTest() throws Exception {
        // 启动线程
        new Thread(() -> {
            if (baozidian == null) { // 如果没包子，则进入等待
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                synchronized (this) {
                    try {
                        System.out.println("1、进入等待");
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("2、买到包子，回家");
        }).start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        synchronized (this) {
            this.notifyAll();
            System.out.println("3、通知消费者");
        }
    }
    /**
     * 正常的park/unpark
     */
    public void parkUnparkTest() throws Exception {
        // 启动线程
        Thread consumerThread = new Thread(() -> {
            while (baozidian == null) { // 如果没包子，则进入等待
                System.out.println("1、进入等待");
                LockSupport.park();
            }
            System.out.println("2、买到包子，回家");
        });
        consumerThread.start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        LockSupport.unpark(consumerThread);
        System.out.println("3、通知消费者");
    }
    /**
     * 死锁的park/unpark
     */
    public void parkUnparkDeadLockTest() throws Exception {
        // 启动线程
        Thread consumerThread = new Thread(() -> {
            if (baozidian == null) { // 如果没包子，则进入等待
                System.out.println("1、进入等待");
                // 当前线程拿到锁，然后挂起
                synchronized (this) {
                    LockSupport.park();
                }
            }
            System.out.println("2、买到包子，回家");
        });
        consumerThread.start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        // 争取到锁以后，再恢复consumerThread
        synchronized (this) {
            LockSupport.unpark(consumerThread);
        }
        System.out.println("3、通知消费者");
    }
    public static void main(String[] args) throws Exception {
        // 对调用顺序有要求，也要开发自己注意锁的释放。这个被弃用的API， 容易死锁，也容易导致永久挂起。
//		 new Demo6().suspendResumeTest();
//		 new Demo6().suspendResumeDeadLockTest();
//		 new Demo6().suspendResumeDeadLockTest2();

        // wait/notify要求再同步关键字里面使用，免去了死锁的困扰，但是一定要先调用wait，再调用notify，否则永久等待了
        // new Demo6().waitNotifyTest();
//		 new Demo6().waitNotifyDeadLockTest();

        // park/unpark没有顺序要求，但是park并不会释放锁，所有再同步代码中使用要注意
//		 new Demo6().parkUnparkTest();
        new Demo6().parkUnparkDeadLockTest();

    }
}
```

---

## 线程封闭之ThreadLocal和栈封闭

### 线程封闭

就是将不需要在线程间共享的数据封闭在线程中,从而杜绝线程安全问题.

ThreadLocal可以理解为JVM维护了一个Map<Thread,T>,每个线程要用这个T的时候,用当前线程去Map里取.

### 栈封闭

1. 局部变量的固有属性之一就是封闭在线程中
2. 他们位于执行线程的栈中,其他线程无法访问这个栈

### CODE

```java
/**
 * @ClassName:Demo7
 * @Description: 线程封闭
 * @Date: 2019/1/5 14:06
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class Demo7 {
    /**
     * threadLocal变量，每个线程都有一个副本，互不干扰
     */
    public static ThreadLocal<String> value = new ThreadLocal<>();
    /**
     * threadlocal测试
     *
     * @throws Exception
     */
    public void threadLocalTest() throws Exception {
        // threadlocal线程封闭示例
        value.set("这是主线程设置的123"); // 主线程设置值
        String v = value.get();
        System.out.println("线程1执行之前，主线程取到的值：" + v);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String v = value.get();
                System.out.println("线程1取到的值：" + v);
                // 设置 threadLocal
                value.set("这是线程1设置的456");

                v = value.get();
                System.out.println("重新设置之后，线程1取到的值：" + v);
                System.out.println("线程1执行结束");
            }
        }).start();
        Thread.sleep(5000L); // 等待所有线程执行结束
        v = value.get();
        System.out.println("线程1执行之后，主线程取到的值：" + v);
    }
    public static void main(String[] args) throws Exception {
        new Demo7().threadLocalTest();
    }
}

```

---

## 线程池应用及实现原理剖析

### CODE

```java
/**
 * @ClassName:Demo9
 * @Description: 线程池的使用
 * @Date: 2019/1/5 14:08
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class Demo9 {
    /**
     * 测试： 提交15个执行时间需要3秒的任务,看线程池的状况
     *
     * @param threadPoolExecutor 传入不同的线程池，看不同的结果
     * @throws Exception
     */
    public void testCommon(ThreadPoolExecutor threadPoolExecutor) throws Exception {
        // 测试： 提交15个执行时间需要3秒的任务，看超过大小的2个，对应的处理情况
        for (int i = 0; i < 15; i++) {
            int n = i;
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("开始执行：" + n);
                        Thread.sleep(3000L);
                        System.err.println("执行结束:" + n);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("任务提交成功 :" + i);
        }
        // 查看线程数量，查看队列等待数量
        Thread.sleep(500L);
        System.out.println("当前线程池线程数量为：" + threadPoolExecutor.getPoolSize());
        System.out.println("当前线程池等待的数量为：" + threadPoolExecutor.getQueue().size());
        // 等待15秒，查看线程数量和队列数量（理论上，会被超出核心线程数量的线程自动销毁）
        Thread.sleep(15000L);
        System.out.println("当前线程池线程数量为：" + threadPoolExecutor.getPoolSize());
        System.out.println("当前线程池等待的数量为：" + threadPoolExecutor.getQueue().size());
    }
    /**
     * 1、线程池信息： 核心线程数量5，最大数量10，无界队列，超出核心线程数量的线程存活时间：5秒， 指定拒绝策略的
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest1() throws Exception {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        testCommon(threadPoolExecutor);
        // 预计结果：线程池线程数量为：5,超出数量的任务，其他的进入队列中等待被执行
    }
    /**
     * 2、 线程池信息： 核心线程数量5，最大数量10，队列大小3，超出核心线程数量的线程存活时间：5秒， 指定拒绝策略的
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest2() throws Exception {
        // 创建一个 核心线程数量为5，最大数量为10,等待队列最大是3 的线程池，也就是最大容纳13个任务。
        // 默认的策略是抛出RejectedExecutionException异常，java.util.concurrent.ThreadPoolExecutor.AbortPolicy
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.err.println("有任务被拒绝执行了");
            }
        });
        testCommon(threadPoolExecutor);
        // 预计结果：
        // 1、 5个任务直接分配线程开始执行
        // 2、 3个任务进入等待队列
        // 3、 队列不够用，临时加开5个线程来执行任务(5秒没活干就销毁)
        // 4、 队列和线程池都满了，剩下2个任务，没资源了，被拒绝执行。
        // 5、 任务执行，5秒后，如果无任务可执行，销毁临时创建的5个线程
    }
    /**
     * 3、 线程池信息： 核心线程数量5，最大数量5，无界队列，超出核心线程数量的线程存活时间：5秒
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest3() throws Exception {
        // 和Executors.newFixedThreadPool(int nThreads)一样的
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        testCommon(threadPoolExecutor);
        // 预计结：线程池线程数量为：5，超出数量的任务，其他的进入队列中等待被执行
    }
    /**
     * 4、 线程池信息：
     * 核心线程数量0，最大数量Integer.MAX_VALUE，SynchronousQueue队列，超出核心线程数量的线程存活时间：60秒
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest4() throws Exception {
        // SynchronousQueue，实际上它不是一个真正的队列，因为它不会为队列中元素维护存储空间。与其他队列不同的是，它维护一组线程，这些线程在等待着把元素加入或移出队列。
        // 在使用SynchronousQueue作为工作队列的前提下，客户端代码向线程池提交任务时，
        // 而线程池中又没有空闲的线程能够从SynchronousQueue队列实例中取一个任务，
        // 那么相应的offer方法调用就会失败（即任务没有被存入工作队列）。
        // 此时，ThreadPoolExecutor会新建一个新的工作者线程用于对这个入队列失败的任务进行处理（假设此时线程池的大小还未达到其最大线程池大小maximumPoolSize）。
        // 和Executors.newCachedThreadPool()一样的
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        testCommon(threadPoolExecutor);
        // 预计结果：
        // 1、 线程池线程数量为：15，超出数量的任务，其他的进入队列中等待被执行
        // 2、 所有任务执行结束，60秒后，如果无任务可执行，所有线程全部被销毁，池的大小恢复为0
        Thread.sleep(60000L);
        System.out.println("60秒后，再看线程池中的数量：" + threadPoolExecutor.getPoolSize());
    }
    /**
     * 5、 定时执行线程池信息：3秒后执行，一次性任务，到点就执行 <br/>
     * 核心线程数量5，最大数量Integer.MAX_VALUE，DelayedWorkQueue延时队列，超出核心线程数量的线程存活时间：0秒
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest5() throws Exception {
        // 和Executors.newScheduledThreadPool()一样的
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        threadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务被执行，现在时间：" + System.currentTimeMillis());
            }
        }, 3000, TimeUnit.MILLISECONDS);
        System.out.println(
                "定时任务，提交成功，时间是：" + System.currentTimeMillis() + ", 当前线程池中线程数量：" + threadPoolExecutor.getPoolSize());
        // 预计结果：任务在3秒后被执行一次
    }
    /**
     * 6、 定时执行线程池信息：线程固定数量5 ，<br/>
     * 核心线程数量5，最大数量Integer.MAX_VALUE，DelayedWorkQueue延时队列，超出核心线程数量的线程存活时间：0秒
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest6() throws Exception {
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        // 周期性执行某一个任务，线程池提供了两种调度方式，这里单独演示一下。测试场景一样。
        // 测试场景：提交的任务需要3秒才能执行完毕。看两种不同调度方式的区别
        // 效果1： 提交后，2秒后开始第一次执行，之后每间隔1秒，固定执行一次(如果发现上次执行还未完毕，则等待完毕，完毕后立刻执行)。
        // 也就是说这个代码中是，3秒钟执行一次（计算方式：每次执行三秒，间隔时间1秒，执行结束后马上开始下一次执行，无需等待）
        threadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务-1 被执行，现在时间：" + System.currentTimeMillis());
            }
        }, 2000, 1000, TimeUnit.MILLISECONDS);
        // 效果2：提交后，2秒后开始第一次执行，之后每间隔1秒，固定执行一次(如果发现上次执行还未完毕，则等待完毕，等上一次执行完毕后再开始计时，等待1秒)。
        // 也就是说这个代码钟的效果看到的是：4秒执行一次。 （计算方式：每次执行3秒，间隔时间1秒，执行完以后再等待1秒，所以是 3+1）
        threadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务-2 被执行，现在时间：" + System.currentTimeMillis());
            }
        }, 2000, 1000, TimeUnit.MILLISECONDS);
    }
    /**
     * 7、 终止线程：线程池信息： 核心线程数量5，最大数量10，队列大小3，超出核心线程数量的线程存活时间：5秒， 指定拒绝策略的
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest7() throws Exception {
        // 创建一个 核心线程数量为5，最大数量为10,等待队列最大是3 的线程池，也就是最大容纳13个任务。
        // 默认的策略是抛出RejectedExecutionException异常，java.util.concurrent.ThreadPoolExecutor.AbortPolicy
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.err.println("有任务被拒绝执行了");
            }
        });
        // 测试： 提交15个执行时间需要3秒的任务，看超过大小的2个，对应的处理情况
        for (int i = 0; i < 15; i++) {
            int n = i;
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("开始执行：" + n);
                        Thread.sleep(3000L);
                        System.err.println("执行结束:" + n);
                    } catch (InterruptedException e) {
                        System.out.println("异常：" + e.getMessage());
                    }
                }
            });
            System.out.println("任务提交成功 :" + i);
        }
        // 1秒后终止线程池
        Thread.sleep(1000L);
        threadPoolExecutor.shutdown();
        // 再次提交提示失败
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("追加一个任务");
            }
        });
        // 结果分析
        // 1、 10个任务被执行，3个任务进入队列等待，2个任务被拒绝执行
        // 2、调用shutdown后，不接收新的任务，等待13任务执行结束
        // 3、 追加的任务在线程池关闭后，无法再提交，会被拒绝执行
    }
    /**
     * 8、 立刻终止线程：线程池信息： 核心线程数量5，最大数量10，队列大小3，超出核心线程数量的线程存活时间：5秒， 指定拒绝策略的
     *
     * @throws Exception
     */
    private void threadPoolExecutorTest8() throws Exception {
        // 创建一个 核心线程数量为5，最大数量为10,等待队列最大是3 的线程池，也就是最大容纳13个任务。
        // 默认的策略是抛出RejectedExecutionException异常，java.util.concurrent.ThreadPoolExecutor.AbortPolicy
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.err.println("有任务被拒绝执行了");
            }
        });
        // 测试： 提交15个执行时间需要3秒的任务，看超过大小的2个，对应的处理情况
        for (int i = 0; i < 15; i++) {
            int n = i;
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("开始执行：" + n);
                        Thread.sleep(3000L);
                        System.err.println("执行结束:" + n);
                    } catch (InterruptedException e) {
                        System.out.println("异常：" + e.getMessage());
                    }
                }
            });
            System.out.println("任务提交成功 :" + i);
        }
        // 1秒后终止线程池
        Thread.sleep(1000L);
        List<Runnable> shutdownNow = threadPoolExecutor.shutdownNow();
        // 再次提交提示失败
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("追加一个任务");
            }
        });
        System.out.println("未结束的任务有：" + shutdownNow.size());

        // 结果分析
        // 1、 10个任务被执行，3个任务进入队列等待，2个任务被拒绝执行
        // 2、调用shutdownnow后，队列中的3个线程不再执行，10个线程被终止
        // 3、 追加的任务在线程池关闭后，无法再提交，会被拒绝执行
    }

    public static void main(String[] args) throws Exception {
//		new Demo9().threadPoolExecutorTest1();
//		new Demo9().threadPoolExecutorTest2();
//		new Demo9().threadPoolExecutorTest3();
//		new Demo9().threadPoolExecutorTest4();
//		new Demo9().threadPoolExecutorTest5();
//		new Demo9().threadPoolExecutorTest6();
//		new Demo9().threadPoolExecutorTest7();
        new Demo9().threadPoolExecutorTest8();
    }
}

```

