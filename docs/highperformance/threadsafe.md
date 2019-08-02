# 线程安全问题

## 可见性问题

### code

```java
/**
 * @ClassName: VisibilityDemo
 * @Description: 线程安全
 * 测试代码 将运行模式设置为-server， 变成死循环   。 没加默认就是client模式，就是正常（可见性问题）
 * 通过设置JVM的参数，打印出jit编译的内容 （这里说的编译非class文件），通过可视化工具jitwatch进行查看
 * -server -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:+LogCompilation -XX:LogFile=jit.log
 * 关闭jit优化-Djava.compiler=NONE
 * @Date: 2019/1/5 14:19
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class VisibilityDemo {
    private boolean flag = true;
    public static void main(String[] args) throws InterruptedException {
        VisibilityDemo demo1 = new VisibilityDemo();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                // class ->  运行时jit编译  -> 汇编指令 -> 重排序
                while (demo1.flag) { // 指令重排序
                    i++;
                }
                System.out.println(i);
            }
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(2);
        // 设置is为false，使上面的线程结束while循环
        demo1.flag = false;
        System.out.println("被置为false了.");
    }
}
```

### 使用 `volatile` 关键字解决可见性问题以及阻止指令重排序

#### CODE

```java
/**
 * @ClassName: VisibilityDemo2
 * @Description: volatile 状态标识 (不用缓存)
 * @Date: 2019/1/5 14:38
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class VisibilityDemo2 {
    // 状态标识 (不用缓存)
    private volatile boolean flag = true;
    // 源码 -> 字节码class
    // JVM 转化为 操作系统能够执行的代码 （JIT Just In Time Compiler 编译器 ）（JVM  --  client   ， --server）
    public static void main(String[] args) throws InterruptedException {
        VisibilityDemo2 demo1 = new VisibilityDemo2();
        new Thread(new Runnable() {
            public void run() {
                int i = 0;
                while (demo1.flag) {
                    i++;
                }
                System.out.println(i);
            }
        }).start();
        TimeUnit.SECONDS.sleep(2);
        // 设置is为false，使上面的线程结束while循环
        demo1.flag = false;
        System.out.println("被置为false了.");
    }
}

```

# 锁的概念和synchronized关键字的实现

## Java中锁的概念

1. 自旋锁
2. 悲观锁
3. 乐观锁
4. 独享锁（write）
5. 共享锁（read）
6. 可重入、不可重入锁
7. 公平锁、非公平锁

## 同步关键字synchronized

### 特性

1. 悲观锁
2. 可重入
3. 独享

### 锁的范围

1. 类锁
2. 对象锁
3. 锁粗化
4. 锁消失

### 原理分析

偏向锁 ---> 轻量级锁 ---> 重量级锁

[Synchronization and Object Locking](https://wiki.openjdk.java.net/display/HotSpot/Synchronization)


# Lock 接口

## ReentrantLock

可重入锁；独享锁；支持公平、非公平锁两种模式；

`
lock的次数 == unlock的次数，否则会造成死锁
`

## ReentrantReadWriteLock

维护一对关联锁，一个用于读操作，另外一个用于写操作。

读锁可以由多个线程同时持有，写锁则是排他。

**锁降级**指的是*写锁*降级成为*读锁*。把持住当前拥有的写锁的同时，再获取读锁，随后释放写锁的过程。

写锁是线程独占，读锁则是共享，所以写 -> 读 是升级。（读->写，是不能实现的）