package io.xuqi.myNetty.channel.nio;

import io.xuqi.myNetty.channel.Channel;
import io.xuqi.myNetty.channel.EventLoop;
import io.xuqi.myNetty.channel.socket.nio.AbstractNioChannel;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class NioEventLoop implements EventLoop, Runnable {
    private Selector selector;
    NioEventLoop() {
        try {
            selector =  Selector.open();
            //Netty里面是等有任务以后才初始化线程,我这里管不了这么多了,在构造函数直接启动线程
            Thread t= new Thread(this);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EventLoop next() {
        return this;
    }

    @Override
    public void register(Channel channel) {
        channel.register(this);
    }

    public Selector unwrappedSelector() {
        return selector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int readyChannels = selector.select(512);
                processSelectedKeysPlain(selector.selectedKeys());
                Thread.yield();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys){
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        for (;;) {
            final SelectionKey k = i.next();
            final AbstractNioChannel a = (AbstractNioChannel)k.attachment();
            i.remove();
            a.read();
            if (!i.hasNext()) {
                break;
            }
        }
    }
}
