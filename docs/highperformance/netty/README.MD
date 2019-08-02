> 转载 <https://github.com/xuqifzz>

# Netty线程模型

注意: 本节内容已经重新整理成专题`从零开始实现Netty极简版`, 可以在[笔记首页](../../../README.MD)看到

本节课老师简要介绍了一下Netty的使用方法,这节课老师的语速略快,对着Netty的源码一顿操作,我早已晕头转向.


醒过神来后,总结了一下,这节课主要讲解了如下的一些Netty概念
* EventLoopGroup
* EventLoop
* NioEventLoopGroup
* NioEventLoop
* Channel
* NioServerSocketChannel
* NioServerSocketChannel

由于Netty是的实现要考虑很多因素,包括健壮性,性能,易用性,易扩展性等等,所以Netty的源码错综复杂,直接看源码很容易迷失.

我觉得光是看的话肯定很难理解,不如走个捷径,自己对照着Netty的源码把上面提到的这个概念实现一遍, 把错误处理,异步处理等等能去掉的全部去掉,留下一个骨架, 这样一来,在实现的过程中肯定能轻松的理解Netty的工作流程了.

我的这个实现会基本遵照netty的逻辑,把对应的操作尽量放到它应该在的地方.完整代码在`myNetty`文件夹下

首先定义接口
```java
public interface EventLoopGroup {
    EventLoop next();
    void register(Channel channel);
}

public interface EventLoop extends EventLoopGroup {

}

//我这里没有unsafe,所以把unsafe的功能整进Channel了
public interface Channel {
    void register(EventLoop eventLoop);
    void bind(SocketAddress localAddress);
    Object doReadMessages();
    EventLoop eventLoop();

    void setHandler(ChannelHandler handler); //这个方法是我自己发明的
}

public interface ChannelHandler {
    //没错,在我的实现里,ChannelHandler只有一个方法
    void channelRead(Object msg);
}

```
上面就是所有接口定义了, 比起netty复杂到突破天际的接口定义,我定义的接口就是这么简洁,优雅-.-

接着是实现`NioEventLoopGroup`,这个逻辑很简单,就是初始化指定数目的`NioEventLoop`,并实现一个选择算法

[完整代码:NioEventLoopGroup.java](myNetty/channel/nio/NioEventLoopGroup.java)
```java
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

```

下面是`NioEventLoop`,`NioEventLoop`的实现相对来说复杂一些,它负责创建选择器,以及进行select循环

[完整代码:NioEventLoop.java](myNetty/channel/nio/NioEventLoop.java)
```java
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

```

接着就是要实现`NioServerSocketChannel`以及`NioSocketChannel`了,因为这两个类比较相似,为了避免重复代码,所以提取了一个抽象基类`AbstractNioChannel`

[完整代码:AbstractNioChannel.java](myNetty/channel/socket/nio/AbstractNioChannel.java)
```java
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
```

`NioServerSocketChannel`实际上就是Nio的`ServerSocketChannel`的封装,主要负责监听端口,接受连接

[完整代码:NioServerSocketChannel.java](myNetty/channel/socket/nio/NioServerSocketChannel.java)
```java
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
```
`NioSocketChannel`实际上就是Nio的`SocketChannel`的封装,主要负责接收数据

[完整代码:NioSocketChannel.java](myNetty/channel/socket/nio/NioSocketChannel.java)

```java
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
```

最后要实现的就是启动器`ServerBootstrap`了,这个类的主要工作就是负责组装以上实现的内容

[完整代码:ServerBootstrap.java](myNetty/bootstrap/ServerBootstrap.java)

```java
//服务端启动器
public class ServerBootstrap {
    private  Class<? extends Channel> channelClass;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private ChannelHandler childHandler;

    public void bind(int port){
        Channel channel = initAndRegister();
        channel.bind(new InetSocketAddress(port));
    }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        this.parentGroup = parentGroup;
        this.childGroup = childGroup;
        return this;
    }
    public ServerBootstrap channel(Class<? extends Channel> channelClass) {
        this.channelClass = channelClass;
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler childHandler){
        this.childHandler = childHandler;
        return this;
    }

    private Channel initAndRegister(){
        try {
            //创建NioServerSocketChannel
            Channel channel = channelClass.newInstance();
            init(channel);
            //注册到主EventLoopGroup
            parentGroup.register(channel);
            return channel;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void init(Channel channel){
        channel.setHandler(new ServerBootstrapAcceptor(childGroup,childHandler));

    }

    private static class ServerBootstrapAcceptor implements ChannelHandler{
        private final EventLoopGroup childGroup;
        private final ChannelHandler childHandler;
        ServerBootstrapAcceptor(EventLoopGroup childGroup,ChannelHandler childHandler){
            this.childGroup = childGroup;
            this.childHandler = childHandler;
        }
        @Override
        public void channelRead(Object msg) {
            final Channel child = (Channel)msg;
            child.setHandler(childHandler);
            //在此处将接收到的NioSocketChannel注册到childGroup
            childGroup.register(child);
        }
    }
}
```

到此为止,这个极度缩水版的netty便已经实现完成了,在netty中,bind和accept都是在Channel的pipeline中完成的,但我这个实现并没有pipeline,所以直接放在的Channel中,如果时间充裕的话,将来我会把这些特性都加入进来

以下是测试方法 [完整代码:Server.java](Server.java)
```java
ChannelHandler handler = (o) -> {
    byte[] bytes = (byte[])o;
    String msg = new String(bytes);
    System.out.println("接收到消息:" + msg);
};
EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup(1);
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(handler);
b.bind(6666);
```

以上的测试监听了6666端口,并将接收到的文本消息打印到控制台

[用于测试的客户端:Client.java](Client.java)

## 责任链设计模式
本节讲述了Netty中的责任链设计模式, 本人在MyNetty中也已经实现责任链模式, 有[专门的笔记](../../../myNetty/section-3.MD)进行介绍,这里不再重复。
本次笔记的主题是责任链模式的应用.

### 业务场景一: 支持多态的编程语言的设计

在设计一门需要支持多态的编程语言时候,子类可以继承父类的方法, 也可以覆盖(override)父类的方法.

在多级继承关系中就形成了责任链, 当调用子类没有实现的方法时, 就会尝试调用父类的对应方法, 如果父类也没有实现, 那就会去调用父类的父类的对应方法, 以此类推。

设计思路:

如果设计的语言是动态类型的, 类似于Lua或Javascript之类的, 可以在运行时改变对象基类的, 通常采取原型继承的模式,即通常对象都关联了一个原型对象(在javascript中是prototype,Lua中是metatable), 当用户尝试获取某个对象的属性或是调用某个方法, 而当前对象没有实现时, 就会去查找它的原型对象的相应属性或方法, 如果再找不到, 就去找原型的对象的原型对象, 以此类推。在这种设计中,原型链越长, 查找效率就可能越低, 时间复杂度为O(n) 。

如果是设计的语言是静态编译型的,类似于(C++/Java)之类的, 这种类型的语言不允许在运行时改变对象的父类, 可以在编译时进行优化, 为每一个类都分配一张方法表, 方法表中记录了最终实现的方法的地址, 这这种设计中, 查找效率不受继承链长度的影响, 时间复杂度为O(1) 。

### 业务场景二: UI框架中事件响应机制的设计

在UI框架的设计中, 采用责任链的方式进行事件传播几乎是通行做法, 在UI界面中, 控件之间是存在嵌套关系的。 
以为HTML文档为例, 控件(这里是标签)可能就存在如下的嵌套关系 `html->body->div1->div2->p`, 而此时如果用户点击了`p`标签,那么该用户点击事件应该在`html-body-div1-div2-p`, 这条链路上传播, 至于是从左向右(父->子), 还是从右向左(子->父),那就得看具体需求了。

设计思路:
以事件冒泡为例(子->父), 每个控件内部都记录父控件的引用, 当某个控件触发一个事件时, 他可以自行处理并结束事件传播; 也可以自己不处理, 转交给父控件处理; 甚至可以自己处理后再交给父控件处理。

