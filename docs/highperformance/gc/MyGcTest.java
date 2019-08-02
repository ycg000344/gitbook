import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.*;

public class MyGcTest {
	//java -Xms50M -Xmx50M -Xmn30M -XX:SurvivorRatio=1 -XX:+UseSerialGC -XX:+PrintGCDetails MyGcTest > myLog.txt
    private static final int Unit = 1024 * 1024;
    private static Queue<Object> holder = new LinkedList<>();
    public static void main(String[] args) throws Exception {

        long i = 1;
        try {
            for (; ; i++) {
                byte[] bigObject = new byte[Unit];//本程序的GC基本上都是由本行代码触发
				
                register(bigObject);	  //使用PhantomReference跟踪bigObject的释放情况,并调用checkGC()
                if (i % 4 == 0) {
                    holder.add(bigObject);
                }
                if (i %5 == 0) {
                    holder.poll();
                }  
				Thread.sleep(30);				
            }
        } catch (OutOfMemoryError error) {
            System.out.println("OutOfMemoryError: 一共创建bigObject:" + (i-1) + "个,GC次数:" + gcCount);
            System.out.println("总共成功回收bigObject:" + gcObjectCount + ",堆中还剩下:" + phantomSet.size());
        }

    }

    private static HashSet<PhantomReference<Object>> phantomSet = new HashSet<>();
    private static ReferenceQueue<Object> phantomReferenceQueue = new ReferenceQueue<>();//
    private static int gcCount=0;
    private static int gcObjectCount=0;
    
    static void register(byte[] bigObject){		
        phantomSet.add(new PhantomReference<>(bigObject,phantomReferenceQueue));
        checkGC();
		System.out.print(".");  //打一个点表示成功创建一个bigObject
    }
    
    static void checkGC(){
        int count = 0;
        Reference<?> reference = null;
        while ((reference = phantomReferenceQueue.poll()) != null) {
            count++;
            phantomSet.remove(reference);
        }
        if(count > 0){
            System.out.println("下次GC将回收bigObject:" + count );
            gcCount++;
            gcObjectCount += count;
        }
    }

}
