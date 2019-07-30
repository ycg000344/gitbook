package io.xuqi.myNetty.channel;

public interface ChannelHandler {
    //没错,在我的实现里,ChannelHandler只有一个方法
    void channelRead(Object msg);
}
