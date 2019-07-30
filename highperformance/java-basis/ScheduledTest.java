import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledTest {
    static Runnable getRunnable(String name,long sleep){
        return ()->{
            try {
                Thread.sleep(sleep);
                System.out.println(name +  "被执行，现在时间：" + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }
    public static void main(String[] args) throws Exception{
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        threadPoolExecutor.scheduleWithFixedDelay(getRunnable("任务-1", 2000), 100, 1000, TimeUnit.MILLISECONDS);
        threadPoolExecutor.scheduleWithFixedDelay(getRunnable("任务-2", 2000), 200, 1000, TimeUnit.MILLISECONDS);
        threadPoolExecutor.scheduleWithFixedDelay(getRunnable("任务-3", 2000), 300, 1000, TimeUnit.MILLISECONDS);
    }
}
