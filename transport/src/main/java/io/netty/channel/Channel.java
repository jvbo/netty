/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeMap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


/**
 * A nexus to a network socket or a component which is capable of I/O
 * operations such as read, write, connect, and bind.
 * <p>
 * A channel provides a user:
 * <ul>
 * <li>the current state of the channel (e.g. is it open? is it connected?),</li>
 * <li>the {@linkplain ChannelConfig configuration parameters} of the channel (e.g. receive buffer size),</li>
 * <li>the I/O operations that the channel supports (e.g. read, write, connect, and bind), and</li>
 * <li>the {@link ChannelPipeline} which handles all I/O events and requests
 *     associated with the channel.</li>
 * </ul>
 *
 * <h3>All I/O operations are asynchronous.</h3>
 * <p>
 * All I/O operations in Netty are asynchronous.  It means any I/O calls will
 * return immediately with no guarantee that the requested I/O operation has
 * been completed at the end of the call.  Instead, you will be returned with
 * a {@link ChannelFuture} instance which will notify you when the requested I/O
 * operation has succeeded, failed, or canceled.
 *
 * <h3>Channels are hierarchical</h3>
 * <p>
 * A {@link Channel} can have a {@linkplain #parent() parent} depending on
 * how it was created.  For instance, a {@link SocketChannel}, that was accepted
 * by {@link ServerSocketChannel}, will return the {@link ServerSocketChannel}
 * as its parent on {@link #parent()}.
 * <p>
 * The semantics of the hierarchical structure depends on the transport
 * implementation where the {@link Channel} belongs to.  For example, you could
 * write a new {@link Channel} implementation that creates the sub-channels that
 * share one socket connection, as <a href="http://beepcore.org/">BEEP</a> and
 * <a href="http://en.wikipedia.org/wiki/Secure_Shell">SSH</a> do.
 *
 * <h3>Downcast to access transport-specific operations</h3>
 * <p>
 * Some transports exposes additional operations that is specific to the
 * transport.  Down-cast the {@link Channel} to sub-type to invoke such
 * operations.  For example, with the old I/O datagram transport, multicast
 * join / leave operations are provided by {@link DatagramChannel}.
 *
 * <h3>Release resources</h3>
 * <p>
 * It is important to call {@link #close()} or {@link #close(ChannelPromise)} to release all
 * resources once you are done with the {@link Channel}. This ensures all resources are
 * released in a proper way, i.e. filehandles.
 */

/**
 * TODO 是Netty网络操作抽象类,聚合了一组功能,
 * 包括但不限于网络的读,写,客户端发起连接,主动关闭连接,链路关闭,获取通信双方的网络地址等;
 *
 * 代表一个实体(如一个硬件设备,一个文件,一个网络套接字或者一个能够执行一个或者多个不同的I/O操作的程序组件)的开放连接,如读操作和写操作;
 * Channel可以看作是传入(入站)或者传出(出站)数据的载体;因此,它可以被打开或者被关闭,连接或者断开连接;
 *
 * Socket;
 *
 * 基本的I/O操作(bind(), connect(), read(), write())依赖于底层网络传输所提供的原语;
 *
 * 有一系列实现类,是拥有许多预定义的,专门化实现的广泛类层次结构的根;
 */
public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {

    /**
     * Channel是Netty抽象出来的网络I/O读写相关接口,为什么不使用JDK NIO原生的Channel而要另起炉灶呢,
     * 主要原因如下:
     * 1. jdk的SocketChannel和ServerSocketChannel没有同意的Channel接口供业务开发者调用,对于用户而言,没有统一的操作视图,
     * 使用起来不方便;
     * 2. jdk的SocketChannel和ServerSocketChannel的主要职责就是网络I/O操作,由于它们是SPI类接口,由具体的虚拟机厂家来提供,
     * 所以通过继承SPI功能类来扩展其功能的难度很大;直接实现ServerSocketChannel和SocketChannel抽象类,其工作量和重新开发一个新的Channel功能类是差不多的;
     * 3. Netty的Channel需要跟Netty的整体架构融合在一起,例如I/O模型,基于ChannelPipeline的定制模型,已经基于元数据描述配置化的tcp参数等,
     * 这些jdk的SocketChannel和ServerSocketChannel都没有提供,需要重新封装;
     * 4. 自定义的Channel,功能实现更加灵活;
     *
     *
     * Channel设计理念:
     * 1. 在Channel接口层,采用Facade模式进行统一封装,将网络I/O操作,网络I/O相关联的其他操作封装起来,统一对外提供;
     * 2. Channel接口的定义尽量大而全,为SocketChannel和ServerSocketChannel提供统一的视图,由不同的子类实现不同的功能,
     * 公共功能在抽象父类中实现,最大程度上实现功能和接口的重用;
     * 3. 具体实现采用聚合而非包含的方式,将相关的功能类聚合在Channel中,由Channel统一负责分配和调度;功能实现更加灵活;
     */

    /**
     * Returns the globally unique identifier of this {@link Channel}.
     */
    /**
     * TODO 获取Channel标识的id(),返回ChannelId对象,ChannelId是Channel的唯一标识;
     * @return
     */
    ChannelId id();

    /**
     * Return the {@link EventLoop} this {@link Channel} was registered to.
     */
    /**
     * TODO Channel需要注册到EventLoop的多路复用器上,用于处理I/O事件,通过eventLoop()方法可以获取到Channel注册的EventLoop;
     * EventLoop本质上就是处理网络读写事件的Reactor线程,在Netty中,它不仅仅用来处理网络事件,也可以用来执行定时任务和
     * 用户自定义NioTask等任务;
     * @return
     */
    EventLoop eventLoop();

    /**
     * Returns the parent of this channel.
     *
     * @return the parent channel.
     *         {@code null} if this channel does not have a parent channel.
     */
    /**
     * TODO 对于服务端Channel而言,它的父Channel为空;
     * 对于客户端Channel,它的父Channel就是创建它的ServerSocketChannel;
     * @return
     */
    Channel parent();

    /**
     * Returns the configuration of this channel.
     */
    /**
     * TODO 获取当前Channel的配置信息,例如CONNECT_TIMEOUT_MILLIS
     * @return
     */
    ChannelConfig config();

    /**
     * Returns {@code true} if the {@link Channel} is open and may get active later
     */
    /**
     * TODO 判断当前Channel是否已经打开;
     * @return
     */
    boolean isOpen();

    /**
     * Returns {@code true} if the {@link Channel} is registered with an {@link EventLoop}.
     */
    /**
     * TODO 判断当前Channel是否已经注册到EventLoop上;
     * @return
     */
    boolean isRegistered();

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     */
    /**
     * TODO 判断当前Channel是否处于激活状态;
     * @return
     */
    boolean isActive();

    /**
     * Return the {@link ChannelMetadata} of the {@link Channel} which describe the nature of the {@link Channel}.
     */
    /**
     * TODO 获取当前Channel的元数据描述信息,包括TCP参数配置等;
     * 当创建Socket的时候需要指定TCP参数,例如接收和发送的TCP缓冲区大小,TCP的超时时间,
     * 是否重用地址等等,在Netty中,每个Channel对应一个物理连接,每个连接都有自己的TCP参数配置;
     * 所以,Channel会聚合一个ChannelMetadata用来对TCP参数提供元数据描述信息,通过metadata()就可以获取当前Channel的TCP参数配置;
     * @return
     */
    ChannelMetadata metadata();

    /**
     * Returns the local address where this channel is bound to.  The returned
     * {@link SocketAddress} is supposed to be down-cast into more concrete
     * type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the local address of this channel.
     *         {@code null} if this channel is not bound.
     */
    /**
     * TODO 获取当前Channel的本地绑定地址;
     * @return
     */
    SocketAddress localAddress();

    /**
     * Returns the remote address where this channel is connected to.  The
     * returned {@link SocketAddress} is supposed to be down-cast into more
     * concrete type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the remote address of this channel.
     *         {@code null} if this channel is not connected.
     *         If this channel is not connected but it can receive messages
     *         from arbitrary remote addresses (e.g. {@link DatagramChannel},
     *         use {@link DatagramPacket#recipient()} to determine
     *         the origination of the received message as this method will
     *         return {@code null}.
     */
    /**
     * TODO 获取当前Channel通信的远程Socket地址;
     * @return
     */
    SocketAddress remoteAddress();

    /**
     * Returns the {@link ChannelFuture} which will be notified when this
     * channel is closed.  This method always returns the same future instance.
     */
    ChannelFuture closeFuture();

    /**
     * Returns {@code true} if and only if the I/O thread will perform the
     * requested write operation immediately.  Any write requests made when
     * this method returns {@code false} are queued until the I/O thread is
     * ready to process the queued write requests.
     */
    boolean isWritable();

    /**
     * Get how many bytes can be written until {@link #isWritable()} returns {@code false}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code false} then 0.
     */
    long bytesBeforeUnwritable();

    /**
     * Get how many bytes must be drained from underlying buffers until {@link #isWritable()} returns {@code true}.
     * This quantity will always be non-negative. If {@link #isWritable()} is {@code true} then 0.
     */
    long bytesBeforeWritable();

    /**
     * Returns an <em>internal-use-only</em> object that provides unsafe operations.
     */
    Unsafe unsafe();

    /**
     * Return the assigned {@link ChannelPipeline}.
     */
    ChannelPipeline pipeline();

    /**
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     */
    ByteBufAllocator alloc();

    /**
     * TODO 从当前的Channel中读取数据到第一个inbound缓冲区中,如果数据被成功读取,
     * 触发ChannelHandler.channelRead(ChannelHandlerContext, Object)事件,
     * 读取操作api操作完成后,紧接着会触发ChannelHandler.channelReadComplate(ChannelHandlerContext)事件,
     * 这样业务ChannelHandler可以决定是否需要继续读取数据;如果已经有读操作请求被挂起,则后续的读操作会被忽略;
     * @return
     */
    @Override
    Channel read();

    /**
     * TODO 将之前写入到发送环形数组中的消息全部写入到目标Channel中,发送给通信对方;
     * @return
     */
    @Override
    Channel flush();

    /**
     * <em>Unsafe</em> operations that should <em>never</em> be called from user-code. These methods
     * are only provided to implement the actual transport, and must be invoked from an I/O thread except for the
     * following methods:
     * <ul>
     *   <li>{@link #localAddress()}</li>
     *   <li>{@link #remoteAddress()}</li>
     *   <li>{@link #closeForcibly()}</li>
     *   <li>{@link #register(EventLoop, ChannelPromise)}</li>
     *   <li>{@link #deregister(ChannelPromise)}</li>
     *   <li>{@link #voidPromise()}</li>
     * </ul>
     */
    // TODO Channel接口的辅助接口;
    interface Unsafe {

        /**
         * Return the assigned {@link RecvByteBufAllocator.Handle} which will be used to allocate {@link ByteBuf}'s when
         * receiving data.
         */
        RecvByteBufAllocator.Handle recvBufAllocHandle();

        /**
         * Return the {@link SocketAddress} to which is bound local or
         * {@code null} if none.
         */
        SocketAddress localAddress();

        /**
         * Return the {@link SocketAddress} to which is bound remote or
         * {@code null} if none is bound yet.
         */
        SocketAddress remoteAddress();

        /**
         * Register the {@link Channel} of the {@link ChannelPromise} and notify
         * the {@link ChannelFuture} once the registration was complete.
         */
        void register(EventLoop eventLoop, ChannelPromise promise);

        /**
         * Bind the {@link SocketAddress} to the {@link Channel} of the {@link ChannelPromise} and notify
         * it once its done.
         */
        /**
         * TODO ChannelPromise用于写入操作结果;
         * @param localAddress
         * @param promise
         */
        void bind(SocketAddress localAddress, ChannelPromise promise);

        /**
         * Connect the {@link Channel} of the given {@link ChannelFuture} with the given remote {@link SocketAddress}.
         * If a specific local {@link SocketAddress} should be used it need to be given as argument. Otherwise just
         * pass {@code null} to it.
         *
         * The {@link ChannelPromise} will get notified once the connect operation was complete.
         */
        /**
         * TODO 客户端使用指定的服务端地址remoteAdress发起连接请求,如果连接因为应答超时而失败,
         * ChannelFuture中的操作结果就是ConnectTimeoutException异常,如果连接被拒绝,操作结果为ConnectException;
         * 该方法会级联触发ChannelHandler.connect(ChannelHandlerContext, SocketAddress, SocketAdress, ChannelPromise);
         * @param remoteAddress
         * @param localAddress
         * @param promise
         */
        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        /**
         * Disconnect the {@link Channel} of the {@link ChannelFuture} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        /**
         * TODO 请求断开与远程通信对端的连接并使用ChannelPromise来获取操作结果的通知信息,
         * 该方法会级联触发ChannelHandler.disconnect(ChannelHandlerContext, ChannelPromise)事件;
         * @param promise
         */
        void disconnect(ChannelPromise promise);

        /**
         * Close the {@link Channel} of the {@link ChannelPromise} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        /**
         * TODO 主动关闭当前连接,通过ChannelPromise设置操作结果并进行结果通知,无论操作是否成功,
         * 都可以通过ChannelPromise获取操作结果;概操作会触发ChannelPipeline中所有ChannelHandler的
         * ChannelHandler.close(ChannelHandlerContext, ChannelPromise)事件;
         * @param promise
         */
        void close(ChannelPromise promise);

        /**
         * Closes the {@link Channel} immediately without firing any events.  Probably only useful
         * when registration attempt failed.
         */
        void closeForcibly();

        /**
         * Deregister the {@link Channel} of the {@link ChannelPromise} from {@link EventLoop} and notify the
         * {@link ChannelPromise} once the operation was complete.
         */
        void deregister(ChannelPromise promise);

        /**
         * Schedules a read operation that fills the inbound buffer of the first {@link ChannelInboundHandler} in the
         * {@link ChannelPipeline}.  If there's already a pending read operation, this method does nothing.
         */
        void beginRead();

        /**
         * Schedules a write operation.
         */
        /**
         * TODO 请求将当前的msg通过ChannelPipeline写入到目标Channel中,
         * 注意write操作只是将消息存入到消息发送环形数组中,并没有真正被发送,
         * 只有调用flush操作才会被写入到Channel中,发送给对方,ChannelPromise参数负责设置写入操作的结果;
         * @param msg
         * @param promise
         */
        void write(Object msg, ChannelPromise promise);

        /**
         * Flush out all write operations scheduled via {@link #write(Object, ChannelPromise)}.
         */
        void flush();

        /**
         * Return a special ChannelPromise which can be reused and passed to the operations in {@link Unsafe}.
         * It will never be notified of a success or error and so is only a placeholder for operations
         * that take a {@link ChannelPromise} as argument but for which you not want to get notified.
         */
        ChannelPromise voidPromise();

        /**
         * Returns the {@link ChannelOutboundBuffer} of the {@link Channel} where the pending write requests are stored.
         */
        ChannelOutboundBuffer outboundBuffer();
    }
}
