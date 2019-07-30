package io.xuqi.myNetty.channel.nio;

import io.xuqi.myNetty.channel.Channel;
import io.xuqi.myNetty.channel.EventLoop;
import io.xuqi.myNetty.channel.EventLoopGroup;

public class NioEventLoopGroup implements EventLoopGroup {
    private final int nThreads;
    NioEventLoop[] eventLoops;
    int curIndex = 0;
    public NioEventLoopGroup(int nThreads) {
        this.nThreads = nThreads;
        initEventLoops();
    }
    private void initEventLoops(){
        eventLoops = new NioEventLoop[nThreads];
        //直接初始化所有的子NioEventLoop
        for (int i=0;i<nThreads;i++)
            eventLoops[i] = new NioEventLoop();
    }
    @Override
    public void register(Channel channel) {
         next().register(channel);
    }
    @Override
    //实现选择算法
    public EventLoop next() {
        return eventLoops[curIndex++ % nThreads];
    }
}
