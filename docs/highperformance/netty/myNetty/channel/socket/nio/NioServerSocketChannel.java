package io.xuqi.myNetty.channel.socket.nio;


import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

//负责监听端口,接受连接
public class NioServerSocketChannel extends AbstractNioChannel {
    public NioServerSocketChannel() throws IOException {
        super(null, ServerSocketChannel.open(), SelectionKey.OP_ACCEPT);
    }

    @Override
    public void bind(SocketAddress localAddress) {
        try {
            //bind操作
            javaChannel().bind(localAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object doReadMessages() {
        try {
            //accept操作
            SocketChannel ch = javaChannel().accept();
            if(ch != null){
                return  new NioSocketChannel(this, ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ServerSocketChannel javaChannel() {
        return (ServerSocketChannel) super.javaChannel();
    }

}
