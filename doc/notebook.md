### Netty架构
#### Netty逻辑架构
	Netty采用了典型的三层架构进行设计和开发;
##### Reactor通信调度层;
	> 由一系列辅助类完成,包括Reactor线程NioEventLoop及其父类,
	NioSocketChannel/NioServerSocketChannel及其父类,
	ByteBuf及其衍生出来的各种Buffer,Unsafe及其衍生出的各种内部类等;
	该层主要职责就是监听网络的读写和连接操作,
	负责将网络层的数据读取到内存缓冲区中,
	然后触发各种网络事件,
	例如连接创建,连接激活,读事件,写事件等,将这些事件触发到Pipeline中,
	由Pipeline管理的职责链来进行后续的处理;
##### 职责链ChannelPipeline;
	> 负责事件在职责链中的有序传播,同时负责动态地编排职责链;
	职责链可以选择监听和处理自己关心的事件,
	它可以拦截处理和向后/向前传播事件;
	不同应用的Handler节点的功能也不同,
	通常情况下,往往会开发编解码Handler用于消息的编解码,
	它可以将外部的协议消息转换成内部的POJO对象,
	这样上层业务只需要关心处理业务逻辑即可,
    不需要感知底层的协议差异和线程模型差异,实现了架构层面的分层隔离;
##### 业务逻辑编排层(Service ChannelHandler);
	> 业务逻辑编排层通常由两类: 
        > * 纯粹的业务逻辑编排;
        > * 其他的应用层协议插件,
        用于特定协议相关的会话和链路管理,
        例如CMPP协议,用于管理和中国移动短信系统的对接;
    
> 架构的不同层面,需要关心和处理的对象都不同,通常情况下,对于业务开发者,只需要关心职责链的拦截和业务Handler的编排,因为应用层协议栈往往是一次开发,到处运行,实际上对于业务开发者来说,只需要关心服务层的业务逻辑开发即可;各种应用协议以插件的形式提供,只有协议开发人员需要关注协议插件,对于其他业务开发人员来说,只需要关心业务逻辑定制即可;这种分层的架构设计理念实现了NIO框架各层之间的解耦,便于上层业务协议栈的开发和业务逻辑的定制;

#### 关键架构质量属性
##### 高性能
1. 影响最终产品的性能因素非常多;
    + 软件因素;
        - 架构不合理导致的性能问题;
        - 编码实现不合理导致的性能问题,例如锁的不恰当使用导致性能瓶颈;
    + 硬件因素;
        - 服务器硬件配置太低导致的性能问题;
        - 带宽,磁盘的IOPS等限制导致的I/O操作性能差;
        - 测试环境被共用导致被测试的软件产品受到影响;

2. 性能是设计出来的,而不是测试出来的,Netty架构设计的高性能:
    + 1. 采用异步非阻塞的I/O类库,基于Reactor模式实现,解决了传统同步阻塞I/O模式下一个服务端无法平滑地处理线性增长的客户端的问题;
    + 2. TCP接收和发送缓冲区使用直接内存代替堆内存,避免了内存复制,提升了I/O读取和写入的性能;
    + 3. 支持通过内存池的方式循环利用ByteBuf,避免了频繁创建和销毁ByteBuf带来的性能损耗;
    + 4. 可配置的I/O线程数,TCP参数等,为不同的用户场景提供定制化的调优参数,满足不同的性能场景;
    + 5. 采用环形数组缓冲区实现无锁化并发编程,代替传统的线程安全容器或者锁;
    + 6. 合理使用线程安全容器,原子类等,提升系统的并发处理能力;
    + 7. 关键资源的处理使用单线程串行化的方式,避免多线程并发访问带来的锁竞争和额外的cpu资源消耗问题;
    + 8. 通过引用计数器及时的释放不再引用的对象,细粒度的内存管理降低了GC的频率,减少了频繁GC带来的时延增大和CPU损耗;
    

##### 可靠性
1. 链路有效性检测;链路空闲检测机制:
    + 读空闲超时机制: 当连续周期T没有消息可读时,触发超时Handler,用户可以基于读空闲超时发送心跳消息,进行链路检测;如果连续N个周期仍然没有读取到心跳消息,可以主动关闭链路;
    + 写空闲超时机制:当连续周期T没有消息要发送时,触发超时Handler,用户可以基于写空闲超时发送心跳消息,进行链路检测;如果连续N个周期仍然没有接收到对方的心跳消息,可以主动关闭链路;
2. 内存保护机制;Netty提供多种机制对内存进行保护:
    + 通过对象引用计数器对Netty的ByteBuf等内置对象进行细粒度的内存申请和释放,对非法的对象引用进行检测和保护;
    + 通过内存池来重用ByteBuf,节省内存;
    + 可设置的内存容量上限,包括ByteBuf,线程池线程数等;
3. 优雅停机;
    + 相比于Netty的早起版本,Netty5.0版本的优雅退出功能做的更加完善;
    优雅停机功能指的是当系统退出时,JVM通过注册的ShutdownHook拦截到退出信号量,然后执行退出操作,释放相关模块的资源占用,
    将缓冲区的消息处理完成或者清空,将待刷新的数据持久化到磁盘或者数据库中,等到资源回收和缓冲区消息处理完成之后再退出;优雅停机往往需要设置个最大超时时间T,如果达到T后系统仍然没有退出,则通过kill -9 pid强杀当前的进程;

##### 可定制性
1. 责任链模式: ChannelPipeline基于责任链模式开发,便于业务逻辑的拦截,定制和扩展;
2. 基于接口的开发: 关键的类库都提供了接口或者抽象类,如果Netty自身的实现无法满足用户的需求,可以由用户自定义实现相关接口;
3. 提供了大量工厂类,通过重载这些工厂类可以按需创建出用户实现的对象;
4. 提供了大量的系统参数供用户按需设置,增强系统的场景定制性;

##### 可扩展性
1. 基于Netty的基础NIO框架,可以方便地进行应用层协议定制,例如HTTP协议栈,Thrift协议栈,FTP协议栈等;
这些扩展不需要修改Netty源码,直接基于Netty的二进制类库即可实现协议的扩展和定制;目前业界有大量的基于Netty框架开发的协议,例如基于Netty的HTTP协议,Dubbo协议,RocketMQ内部私有协议等;