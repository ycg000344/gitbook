# Java8 - 异步编程

> 摘抄
>
> https://juejin.im/post/5d4c1bfef265da03be48c623



```java
public class AsyncDemo {

    private final String format = "【%s】-【%s】: this is a log";
    private final String format1 = "当前线程为：【%s】，接收到异步线程的返回值为：(%s)";

    private String doSomeThing(String method) {
        String format = String.format(this.format, method, Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    /**
     * runAsync
     * <p>
     * 可以在后台执行异步计算，但是没有返回值
     * <p>
     * print:
     * <p>
     * 【runAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log
     */
    @Test
    public void runAsync() {
        CompletableFuture.runAsync(() -> doSomeThing("runAsync"));
    }

    /**
     * supplyAsync
     * <p>
     * 传入任何，获取返回值
     * <p>
     * print:
     * <p>
     * 【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     */
    @Test
    public void supplyAsync() {
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync"));
        try {
            System.out.println(String.format(format1, Thread.currentThread().getName(), supplyAsync.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // TODO: 2019/8/21 顺序执行任务

    /**
     * thenApply
     * <p>
     * 可以接口上一个异步任务的返回值，自身有返回值
     * <p>
     * print:
     * <p>
     * 【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【ForkJoinPool.commonPool-worker-1】，接收到异步线程的返回值为：(【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     * <p>
     * 【thenApply】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(【thenApply】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     */
    @Test
    public void thenApply() {
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync"));
        CompletableFuture<String> thenApply = supplyAsync.thenApply(s -> {
            System.out.println(String.format(format1, Thread.currentThread().getName(), s));
            return doSomeThing("thenApply");
        });
        try {
            System.out.println(String.format(format1, Thread.currentThread().getName(), thenApply.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * thenAccept
     * <p>
     * 可以接口上一个异步任务的返回值, 但自身无返回值
     * <p>
     * print:
     * <p>
     * 【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     */
    @Test
    public void thenAccept() {
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync"));
        supplyAsync.thenAccept(s -> System.out.println(String.format(format1, Thread.currentThread().getName(), s)));
    }

    /**
     * thenRun
     * <p>
     * 不可以接口上一个异步任务的返回值, 而且自身无返回值
     * <p>
     * print:
     * <p>
     * 【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【ForkJoinPool.commonPool-worker-1】，接收到异步线程的返回值为：()
     */
    @Test
    public void thenRun() {
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync"));
        supplyAsync.thenRun(() -> System.out.println(String.format(format1, Thread.currentThread().getName(), "")));
    }

    /**
     * thenApplyAsync
     * <p>
     * 功能效果类似 <code>thenApply</code>  但又有区别
     * <p>
     * 区别主要体现在 <strong>线程</strong> 方面
     * <p>
     * <strong>thenApply</strong>： 如果 supplyAsync 线程执行的速度特别快，则直接使用 主线程， 否则采用 ForkJoinPool 线程池
     * <p>
     * <strong>thenApplyAsync</strong>： 无论 supplyAsync 线程执行的速度如何，都采用  ForkJoinPool 线程池
     * <p>
     * print：
     * <p>
     * 【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【ForkJoinPool.commonPool-worker-1】，接收到异步线程的返回值为：(【supplyAsync】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     * <p>
     * 【thenApply】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(【thenApply】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     */
    @Test
    public void thenApplyAsync() {
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync"));
        CompletableFuture<String> thenApply = supplyAsync.thenApplyAsync(s -> {
            System.out.println(String.format(format1, Thread.currentThread().getName(), s));
            return doSomeThing("thenApply");
        });
        try {
            System.out.println(String.format(format1, Thread.currentThread().getName(), thenApply.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // TODO: 2019/8/21 组合任务

    /**
     * thenCompose
     * <p>
     * 当第一个任务完成后才进行第二个任务
     * <p>
     * print:
     * <p>
     * 【supplyAsync1】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(【supplyAsync1】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     * <p>
     * 【supplyAsync2】-【ForkJoinPool.commonPool-worker-1】: this is a log
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(【supplyAsync2】-【ForkJoinPool.commonPool-worker-1】: this is a log)
     */
    @Test
    public void thenCompose() {
        CompletableFuture<String> supplyAsync1 = CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync1"));
        CompletableFuture<String> supplyAsync2 = supplyAsync1.thenCompose(s -> {
            System.out.println(String.format(format1, Thread.currentThread().getName(), s));
            return CompletableFuture.supplyAsync(() -> doSomeThing("supplyAsync2"));
        });
        try {
            System.out.println(String.format(format1, Thread.currentThread().getName(), supplyAsync2.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * thenCombine
     * <p>
     * 多个任务全部完成才会执行后续动作
     * <p>
     * print
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(500)
     */
    @Test
    public void thenCombine() {
        CompletableFuture<Integer> supplyAsync200 = CompletableFuture.supplyAsync(() -> 200);
        CompletableFuture<Integer> supplyAsync300 = CompletableFuture.supplyAsync(() -> 300);

        CompletableFuture<Integer> thenCombine = supplyAsync200.thenCombine(supplyAsync300, (x, y) -> x + y);
        try {
            System.out.println(String.format(format1, Thread.currentThread().getName(), thenCombine.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * allOf
     * <p>
     * 等待所有任务完成才会执行后续动作
     * <p>
     * print
     * <p>
     * [1, 2, 3, 4, 5]
     */
    @Test
    public void allOf() {
        CompletableFuture<Integer> s1 = CompletableFuture.supplyAsync(() -> 1);
        CompletableFuture<Integer> s2 = CompletableFuture.supplyAsync(() -> 2);
        CompletableFuture<Integer> s3 = CompletableFuture.supplyAsync(() -> 3);
        CompletableFuture<Integer> s4 = CompletableFuture.supplyAsync(() -> 4);
        CompletableFuture<Integer> s5 = CompletableFuture.supplyAsync(() -> 5);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(s1, s2, s3, s4, s5);

        allOf.thenApply(v -> Stream.of(s1, s2, s3, s4, s5)
                .map(CompletableFuture::join)
                .collect(Collectors.toList()))
                .thenAccept(System.out::println);
    }

    /**
     * anyOf
     * <p>
     * 只要其中一个完成任务就继续执行后续动作，其余任务不再执行
     * <p>
     * print
     * <p>
     * 当前线程为：【main】，接收到异步线程的返回值为：(1)
     */
    @Test
    public void anyOf() {
        CompletableFuture<Integer> s1 = CompletableFuture.supplyAsync(() -> 1);
        CompletableFuture<Integer> s2 = CompletableFuture.supplyAsync(() -> 2);
        CompletableFuture<Integer> s3 = CompletableFuture.supplyAsync(() -> 3);
        CompletableFuture<Integer> s4 = CompletableFuture.supplyAsync(() -> 4);
        CompletableFuture<Integer> s5 = CompletableFuture.supplyAsync(() -> 5);

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(s1, s2, s3, s4, s5);
        try {
            System.out.println(String.format(format1, Thread.currentThread().getName(), anyOf.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // TODO: 2019/8/21 异常处理

    /**
     * exceptionally
     * <p>
     * 捕获异常，获取异常信息并设定出现异常时的返回值
     * <p>
     * print
     * <p>
     * java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     * <p>
     * some error
     * <p>
     * runAsync
     * <p>
     */
    @Test
    public void exceptionally() {
        CompletableFuture.supplyAsync(() -> {
            int i = 10 / 0;
            return "success";
        }).exceptionally(e -> {
            System.out.println(e);
            return "some error";
        }).thenAccept(s -> System.out.println(s));
        CompletableFuture.runAsync(() -> System.out.println("runAsync"));
    }

    /**
     * handle
     * <p>
     * 与exceptionally 不同的地方是无论是否发生异常，都会被调用
     * <p>
     * print
     * <p>
     * 有异常时：
     * <p>
     * throwable：  java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     * <p>
     * response：   null
     * <p>
     * 无异常时：
     * <p>
     * throwable：  null
     * <p>
     * response：   success
     */
    @Test
    public void handle() {
        // 有异常
        CompletableFuture.supplyAsync(() -> {
            int i = 10 / 0;
            return "success";
        }).handle((response, throwable) -> {
            System.out.println("有异常时：");
            System.out.println("throwable：  " + throwable);
            System.out.println("response：   " + response);
            return response;
        });
        // 无异常
        CompletableFuture.supplyAsync(() -> "success").handle((response, throwable) -> {
            System.out.println("无异常时：");
            System.out.println("throwable：  " + throwable);
            System.out.println("response：   " + response);
            return response;
        });

    }

}

```

