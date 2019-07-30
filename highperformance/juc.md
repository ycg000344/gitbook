# AQS (AbstractQueuedSynchronizer) 抽象队列同步器

## code

```java
/**
 * @ClassName: MyAqs
 * @Description: 手写aqs
 * @Date: 2019/1/11 16:15
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
@Getter
@Setter
@ToString
public abstract class MyAqs {
    // acquire、 acquireShared ： 定义了资源争用的逻辑，如果没拿到，则等待。
    // tryAcquire、 tryAcquireShared ： 实际执行占用资源的操作，如何判定一个由使用者具体去实现。
    // release、 releaseShared ： 定义释放资源的逻辑，释放之后，通知后续节点进行争抢。
    // tryRelease、 tryReleaseShared： 实际执行资源释放的操作，具体的AQS使用者去实现。

    private volatile AtomicReference<Thread> owner = new AtomicReference();

    private volatile LinkedBlockingDeque<Thread> waiters = new LinkedBlockingDeque<>();

    private volatile AtomicInteger state = new AtomicInteger(0);

    public void releaseShared() {
        if (tryReleaseShared()) {
            waiters.forEach(e -> LockSupport.unpark(e));
        }
    }
    public void acquireShared() {
        boolean addQueue = true;
        while (tryAcquireShared() < 0) {
            if (addQueue) {
                waiters.offer(Thread.currentThread());
                addQueue = false;
            } else {
                LockSupport.park();
            }
        }
        waiters.remove(Thread.currentThread());
    }
    protected abstract boolean tryReleaseShared();
    protected abstract int tryAcquireShared();
    public void acquire() {
        boolean addQueue = true;
        while (!tryAcquire()) {
            if (addQueue) {
                waiters.offer(Thread.currentThread());
                addQueue = false;
            } else {
                LockSupport.park();
            }
        }
        waiters.remove(Thread.currentThread());
    }
    public void release() {
        if (tryRelease()) {
            waiters.forEach(e -> LockSupport.unpark(e));
        }
    }
    protected abstract boolean tryRelease();
    protected abstract boolean tryAcquire();
}

```

## implements

### semaphore

又称"信号量"，控制多个线程争抢许可。

`acquire` 获取一个许可，没有则等待

`release` 颁发一个许可。

典型的使用场景：代码并发处理限流

### ReentrantLock

### ReentrantReadWriteLock

### CountDownLatch

---

# ForkJoinPool

默认情况下，并行线程数量等于可用处理器的数量；

ForkJoinPool与其他类型的ExecutorService的区别主要在于它使用了工作窃取：
池中的所有线程都试图查找和执行提交给池的任务和/或其他活动任务创建的任务(如果不存在工作，则最终阻塞等待工作)。

## CODE

```java
/**
 * 并行调用http接口
 */
@Service
public class UserServiceForkJoin {
    // 本质是一个线程池,默认的线程数量:CPU的核数
    ForkJoinPool forkJoinPool = new ForkJoinPool(10, ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null, true);
    @Autowired
    private RestTemplate restTemplate;
    /**
     * 查询多个系统的数据，合并返回
     */
    public Object getUserInfo(String userId) throws ExecutionException, InterruptedException {
        // 其他例子, 查数据库的多个表数据,分多次查询
        // fork/join
        // forkJoinPool.submit()
        ArrayList<String> urls = new ArrayList<>();
        urls.add("http://www.tony.com/userinfo-api/get?userId=" + userId);
        urls.add("http://www.tony.com/integral-api/get?userId=" + userId);

        HttpJsonRequest httpJsonRequest = new HttpJsonRequest(restTemplate, urls, 0, urls.size() - 1);
        ForkJoinTask<JSONObject> forkJoinTask = forkJoinPool.submit(httpJsonRequest);

        JSONObject result = forkJoinTask.get();
        return result;
    }
}
// 任务
class HttpJsonRequest extends RecursiveTask<JSONObject> {
    RestTemplate restTemplate;
    ArrayList<String> urls;
    int start;
    int end;
    HttpJsonRequest(RestTemplate restTemplate, ArrayList<String> urls, int start, int end) {
        this.restTemplate = restTemplate;
        this.urls = urls;
        this.start = start;
        this.end = end;
    }
    // 就是实际去执行的一个方法入口(任务拆分)
    @Override
    protected JSONObject compute() {
        int count = end - start; // 代表当前这个task需要处理多少数据
        // 自行根据业务场景去判断是否是大任务,是否需要拆分
        if (count == 0) {
            String url = urls.get(start);
            // TODO 如果只有一个接口调用,立刻调用
            long userinfoTime = System.currentTimeMillis();
            String response = restTemplate.getForObject(url, String.class);
            JSONObject value = JSONObject.parseObject(response);
            System.out.println(Thread.currentThread() + " 接口调用完毕" + (System.currentTimeMillis() - userinfoTime) + " #" + url);
            return value;
        } else { // 如果是多个接口调用,拆分成子任务  7,8,   9,10
            System.out.println(Thread.currentThread() + "任务拆分一次");
            int x = (start + end) / 2;
            HttpJsonRequest httpJsonRequest = new HttpJsonRequest(restTemplate, urls, start, x);// 负责处理哪一部分?
            httpJsonRequest.fork();
            HttpJsonRequest httpJsonRequest1 = new HttpJsonRequest(restTemplate, urls, x + 1, end);// 负责处理哪一部分?
            httpJsonRequest1.fork();

            // join获取处理结果
            JSONObject result = new JSONObject();
            result.putAll(httpJsonRequest.join());
            result.putAll(httpJsonRequest1.join());
            return result;
        }
    }
}

```

