package io.xuqi.myNetty.channel.socket.nio;

import io.xuqi.myNetty.channel.Channel;
import io.xuqi.myNetty.channel.ChannelHandler;
import io.xuqi.myNetty.channel.EventLoop;
import io.xuqi.myNetty.channel.nio.NioEventLoop;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;

//AbstractNioChannel负责NioServerSocketChannel以及NioSocketChannel的公共操作
public abstract class AbstractNioChannel implements Channel {
    private  NioEventLoop eventLoop;  //关联的eventLoop
    protected Channel parent;  //父Channel 暂时没用
    private final SelectableChannel ch;  //Nio的Channel
    protected final int readInterestOp;  //关注的操作
    protected ChannelHandler handler;    //这是我发明的handler
    @Override
    public void setHandler(ChannelHandler handler){
        this.handler = handler;
    }

    //构造函数,接收parent, Nio的Channel以及关注的OP
    protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        this.ch = ch;
        this.readInterestOp = readInterestOp;
        try {
            ch.configureBlocking(false);
        } catch (Throwable e) {

        }
    }
    @Override
    public void register(EventLoop eventLoop){
        this.eventLoop = (NioEventLoop)eventLoop;
        doRegister();

    }
    private void doRegister(){
        try {
            //channel向Selector注册就在此处
            javaChannel().register(eventLoop.unwrappedSelector(), readInterestOp,this);
            //因为此时Selector可能正在select,所以唤醒它一下,重新进行select
            eventLoop.unwrappedSelector().wakeup();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
    @Override
    public NioEventLoop eventLoop() {
        return eventLoop;
    }
    @Override
    public void bind(SocketAddress localAddress) {}
    protected SelectableChannel javaChannel() {
        return ch;
    }
    public void read(){
        Object msg = doReadMessages();
        if(msg != null && handler != null){
            handler.channelRead(msg);
        }

    }

}
