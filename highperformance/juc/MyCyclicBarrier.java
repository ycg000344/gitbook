

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class MyCyclicBarrier {
    private volatile int state;
    private volatile int generation;
    private final int parties;
    private final Runnable barrierAction;
    private final List<Thread> waiters = new LinkedList<>();


    public MyCyclicBarrier(int parties, Runnable barrierAction){
        this.state = this.parties = parties;
        this.barrierAction = barrierAction;
        generation = 0;

    }

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
    private  Thread[] getAndClearWaitingThreads(){
        Thread[] threads = new Thread[waiters.size()];
        waiters.toArray(threads);
        waiters.clear();
        return threads;
    }

    private void signalAll(Thread[] threads){
        if(barrierAction != null){
            barrierAction.run();
        }
        Arrays.stream(threads).forEach(thread -> LockSupport.unpark(thread));
    }

    static class Snapshot{
        public final int state;
        public final int generation;
        public final Thread[] threads;
        Snapshot(int state,int generation,Thread[] threads){
            this.state = state;
            this.generation = generation;
            this.threads = threads;
        }


    }

}
