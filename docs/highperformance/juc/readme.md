> 转载 <https://github.com/xuqifzz>

# AQS抽象队列同步器详解

这节课老师主要讲解了AQS抽象队列同步器的概念,实现原理;其应用场景主要是实现其他线程同步工具.

最后老师留下了作业,实现自己的`CyclicBarrier`

下面是老师初始化`CyclicBarrier`的代码片段
```java
// 每当有4个线程处于await状态的时候，则会触发barrierAction执行
CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {
    @Override
    public void run() {
        // 这是每满足4次数据库操作，就触发一次批量执行
        System.out.println("有4个线程执行了，开始批量插入： " + Thread.currentThread());
        for (int i = 0; i < 4; i++) {
            System.out.println(sqls.poll());
        }
    }
})
```

看到这里,我就已经产生了以下几点疑惑

* 构造函数支持一个`Runnable`类型的回调,会在满足指定数量(这里是4)的线程调用`await`后触发,那个这个回调是在哪个线程执行的呢?是`CyclicBarrier`内部新建了一个线程吗?
* 这个回调函数具体是何时执行呢? 是在栅栏打开之前就执行完毕,还是在栅栏打开后,与调用`await`的线程并发执行?
* 这个`CyclicBarrier`功能看上去与`CountDownLatch`差不多,那为何在jdk中`CountDownLatch`采用AQS实现,而CyclicBarrier却没有呢?

我看了一下jdk中`CyclicBarrier`的代码,下面是其中的一部分

```java
//await调用了dowait,下面是dowait部分代码
int index = --count;
if (index == 0) {  // tripped
    boolean ranAction = false;
    try {
        final Runnable command = barrierCommand;
        if (command != null)
            command.run();  //运行回调
        ranAction = true;
        nextGeneration(); //这里唤醒了所有挂起的线程
        return 0;
    } finally {
        if (!ranAction)
            breakBarrier();
    }
}
```

看了这段后,能够回答前两个问题了,`CountDownLatch`并没有创建线程运行回调函数,而是借用了最后一个调用await的线程运行的,另外,由于上面整片代码都是在锁内运行的,所以`CountDownLatch`是在运行完回调后再打开栅栏的.

下面就是自己实现一个`MyCountDownLatch`了,首先我考虑参考老师的[CDLdemo](CDLdemo.java),利用[AQSdemo](AQSdemo.java)来实现

刚一动手,就发现问题了,在[CDLdemo](CDLdemo.java)中,`countDown`和`await`是独立的两个方法,可以跟[AQSdemo](AQSdemo.java)的`releaseShared`和`acquireShared`,重复调用而`CountDownLatch`只有一个`await`,相当于同时调用`countDown`和`await`.

由于`CountDownLatch`是一次性的,所以可以通过`state == 0`来判断是否可以参与倒计时的线程能否继续运行,因为一旦条件满足,就一直是满足的了,不会又变为不满足状态,因此这样判断是可行的.

而`CountDownLatch`是需要重复利用的,所以想重用[AQSdemo](AQSdemo.java)就比较困难了,这里我使用一个快照的方式控制这个state,保留住state=0及其当时情况的快照,下面是主要代码的片段

```java
public int await(){
    Snapshot snapshot = generateSnapshot();
    if(snapshot.state ==0){
        // 参照CyclicBarrier的逻辑,将运行回调和唤醒线程放到同步代码中去
        // signalAll(snapshot.threads);
    }else {
        //挂起线程,并防止伪唤醒,通过代数来判断
        while (snapshot.generation == generation){
            LockSupport.park();
        }
    }
    return snapshot.generation;

}

//在同步块内生成当前快照
private synchronized Snapshot generateSnapshot(){
    int currentState = --state;
    Thread[] threads = null;
    int currentGeneration = generation;
    if(state == 0){
        state = parties;
        threads = getAndClearWaitingThreads();
        //增加代数
        generation++;
        //如果把signalAll拿到外面执行的话,可让回调和栅栏线程并行执行
        signalAll(threads);
    }else {
        waiters.add(Thread.currentThread());
    }
    return new Snapshot(currentState,currentGeneration,threads);

}
```

由于这个实现没有异常处理以及定时功能,所以代码比较简单
* [完整代码:MyCyclicBarrier](MyCyclicBarrier.java)
* [Tony老师提供的测试代码](MyCyclicBarrierTest.java)






