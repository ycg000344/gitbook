public class StackConfinement {
    public static void main(String[] args){
        int a=1;
        Thread thread = new Thread(() ->
            System.out.println("子线程访问 a:" + a)
        );
        thread.start();
        System.out.println("主线程访问 a:" + a);
    }
}
