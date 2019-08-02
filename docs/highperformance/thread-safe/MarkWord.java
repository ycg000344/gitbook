import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class MarkWord {
    static Unsafe unsafe;
    static {
        try {
            // 反射技术获取unsafe值
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{

        if(!System.getProperty("sun.arch.data.model").equals("64")){
            System.out.println("该Demo只支持64位jdk");
            return;
        }

        Object o = new Object();

        System.out.println("===初始化 ===");
        showMarkWord(o);
        synchronized (o){
            System.out.println("===主线程同步块1 ===");
            showMarkWord(o);
        }

        Thread t= new Thread(()->{
            synchronized (o){
                System.out.println("===子线程同步块1 ===");
                showMarkWord(o);
            }
            sleep(1000);
            synchronized (o){
                System.out.println("===子线程同步块2 ===");
                showMarkWord(o);
            }
        });
        t.start();
        sleep(500);
        synchronized (o){
            System.out.println("===主线程同步块2开始 ===");
            showMarkWord(o);
            sleep(2000);
            System.out.println("===主线程同步块2结束 ===");
            showMarkWord(o);
        }
        System.gc();
        sleep(3000);
        System.out.println("=== 结束 ===");
        showMarkWord(o);


    }
    //  http://hg.openjdk.java.net/jdk8u/jdk8u/hotspot/file/9ce27f0a4683/src/share/vm/oops/markOop.hpp
    //  32 bits:
    //  --------
    //  hash:25 ------------>| age:4    biased_lock:1 lock:2 (normal object)
    //  JavaThread*:23 epoch:2 age:4    biased_lock:1 lock:2 (biased object)
    //  size:32 ------------------------------------------>| (CMS free block)
    //  PromotedObject*:29 ---------->| promo_bits:3 ----->| (CMS promoted object)
    //
    //  64 bits:
    //  --------
    //  unused:25 hash:31 -->| unused:1   age:4    biased_lock:1 lock:2 (normal object)
    //  JavaThread*:54 epoch:2 unused:1   age:4    biased_lock:1 lock:2 (biased object)
    //  PromotedObject*:61 --------------------->| promo_bits:3 ----->| (CMS promoted object)
    //  size:64 ----------------------------------------------------->| (CMS free block)
    static void showMarkWord(Object o){
        long markWord =unsafe.getLong(o,0);
        int tag = (int)getSubLong(markWord,62,2);
        switch (tag){
            case 0:showLightWeightLocked(markWord);break;
            case 1:showUnlocked(markWord);break;
            case 2:showHeavyWeightLocked(markWord);break;
            case 3:showMarkForGC(markWord);break;

        }
    }
    static void sleep(int m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    static String subString(String s,int start,int length){
        return s.substring(start,start+length);
    }
    static void showUnlocked(long markWord){
        System.out.print("lock:Unlocked,");
        long biasedLock = getSubLong(markWord,61,1);
        System.out.print("biasedLock:" + biasedLock);
        if(biasedLock == 1){
            long thread = getSubLong(markWord,0,54);
            long epoch = getSubLong(markWord,54,2);
            System.out.print(",thread:" + thread + ",epoch:" + epoch);
        }
        long age = getSubLong(markWord,57,4);
        System.out.println(",age:" + age);


    }
    static void showLightWeightLocked(long markWord){
        System.out.println("lock:LightWeightLocked");

    }
    static void showHeavyWeightLocked(long markWord){
        System.out.println("lock:HeavyWeightLocked");

    }
    static void showMarkForGC(long markWord){
        System.out.println("lock:MarkForGC");
    }

    static long getSubLong(long n,int start, int length){
        int curIndex = 63 - start;
        int curLength = length - 1;
        long result = 0;
        while (curLength >= 0){
            result += ((n >> curIndex) & 1) << curLength;
            curIndex--;
            curLength--;
        }
        return result;

    }

    static String getBitString(long n){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(n);
        return getBitString(byteBuffer.array());
    }
    static String getBitString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--)
                sb.append((bytes[i] >> j) & 1);
        }
        return  sb.toString();
    }

}
