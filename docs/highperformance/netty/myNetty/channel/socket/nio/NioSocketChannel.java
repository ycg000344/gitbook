package io.xuqi.myNetty.channel.socket.nio;
import io.xuqi.myNetty.channel.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

//负责接收客户端发来的数据
public class NioSocketChannel extends AbstractNioChannel{
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    NioSocketChannel(Channel parent, SocketChannel socket){
        super(parent,socket, SelectionKey.OP_READ);
    }

    @Override
    protected SocketChannel javaChannel() {
        return (SocketChannel) super.javaChannel();
    }

    @Override
    public Object doReadMessages() {
        try {
            buffer.clear();
            javaChannel().read(buffer);
            buffer.flip();
            if(buffer.limit() > 0){
                byte[] result = new byte[buffer.limit()];
                buffer.get(result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
