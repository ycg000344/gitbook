package io.xuqi.myNetty.channel;

import java.net.SocketAddress;

//我这里没有unsafe,所以把unsafe的功能整进Channel了
public interface Channel {
    void register(EventLoop eventLoop);
    void bind(SocketAddress localAddress);
    Object doReadMessages();
    EventLoop eventLoop();

    void setHandler(ChannelHandler handler);//这个方法是我自己发明的
}
