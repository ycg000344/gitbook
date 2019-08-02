import java.util.Random;

public class AutoResetEvent {
    public static void main(String[] args) throws Exception{
        Random random = new Random();
        AutoResetEvent event = new AutoResetEvent(false);
        Thread consumerThread = new Thread(() -> {
            try {
                //随机等待一段时间后去买包子
                Thread.sleep(random.nextInt(5000));
                System.out.println("1、去买包子");
                event.waitOne();
                System.out.println("2、买到包子，回家");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        consumerThread.start();
        // 随机等待一段时间后，生产一个包子
        Thread.sleep(random.nextInt(5000));
        System.out.println("3、通知消费者");
        event.set();

    }

    private final Object monitor = new Object();
    private volatile boolean isOpen = false;

    public AutoResetEvent(boolean open)
    {
        isOpen = open;
    }

    public void waitOne() throws InterruptedException
    {
        synchronized (monitor) {
            while (!isOpen) {
                monitor.wait();
            }
            isOpen = false;
        }
    }

    public void waitOne(long timeout) throws InterruptedException
    {
        synchronized (monitor) {
            long t = System.currentTimeMillis();
            while (!isOpen) {
                monitor.wait(timeout);
                // 检查时间是否足够,防止伪唤醒
                if (System.currentTimeMillis() - t >= timeout)
                    break;
            }
            isOpen = false;
        }
    }

    public void set()
    {
        synchronized (monitor) {
            isOpen = true;
            monitor.notify();
        }
    }

    public void reset()
    {
        isOpen = false;
    }
}
