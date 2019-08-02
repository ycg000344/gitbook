package io.xuqi.myNetty.channel;


public interface EventLoopGroup {
    EventLoop next();
    void register(Channel channel);
}
