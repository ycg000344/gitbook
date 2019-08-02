import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

/***
 * 启动20个线程,进行大量运算,观察其线程状态
 * 为了识别这些线程,也为了不让机器卡死,所以把这些线程的优先级设置到了最低
 */
public class ThreadStateTest {
    public static void main(String[] args) throws Exception{
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        //输出进程ID,供其他工具使用
        System.out.println(runtimeMXBean.getName());
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(()->{
                Thread currentThread = Thread.currentThread();
                System.out.println(currentThread.getName() + ":"  + currentThread.getId());
                long j=0;
                while (j!=-1) j++; //约等于死循环
            });
            thread.setName(String.format("工作线程-%d",i));
            thread.setPriority(Thread.MIN_PRIORITY); //设置线程优先级为最低
            list.add(thread);
            thread.start();

        }
        Thread.sleep(5000);
        //输出线程状态
        list.forEach( thread -> System.out.println(thread.getName() + ":" + thread.getState().toString()));
        Thread.sleep(20000); //手工操作其他工具需要时间
        System.out.println("game over");
        System.exit(0);
    }
}
