package io.xuqi.application;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 6666));
        while (!socketChannel.finishConnect()) {
            // 没连接上,则一直等待
            Thread.yield();
        }
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("请输入：");
            // 发送内容
            String msg = scanner.nextLine();
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        }


    }
}
