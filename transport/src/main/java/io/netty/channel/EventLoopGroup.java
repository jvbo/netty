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

import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Special {@link EventExecutorGroup} which allows registering {@link Channel}s that get
 * processed for later selection during the event loop.
 *
 */
public interface EventLoopGroup extends EventExecutorGroup {

	/**
	 * Netty线程模型
	 * 1. Reactor单线程模型: 是指所有的I/O操作都在同一个NIO线程上完成,NIO线程职责如下:
	 * 		a. 作为NIO服务端,接收客户端的tcp连接;
	 * 		b. 作为NIO客户端,向服务端发起tcp连接;
	 * 		c. 读取通信对端的请求或者应答消息;
	 * 		d. 向通信对端发送消息请求或者应答消息;
	 * 	由于Reactor模式使用的是异步非阻塞I/O,所有的I/O操作都不会导致阻塞,理论上一个线程可以独立处理所有I/O相关的操作;
	 * 	从架构层面看,一个NIO线程确实可以完成其承担的职责;例如通过Acceptor类接收客户端的tcp连接请求消息,当链路建立成功之后,
	 * 	通过Dispatch将对应的ByteBuffer派发到指定的Handler上,进行消息解码;用户线程消息解码后通过NIO线程将消息发送给客户端;
	 *
	 * 2. Reactor多线程模型: 与Reactor单线程模型最大的区别就是一组NIO线程来处理I/O操作;特点如下:
	 * 		a. 有专门一个NIO线程--Acceptor线程用于监听服务端,接收客户端的tcp连接请求;
	 * 		b. 网络I/O操作--读,写等由一个NIO线程池负责,线程池可以采用标准的jdk线程池实现,它包含一个任务队列和N个可用的线程,由这些NIO线程负责消息的读取,编码,解码和发送;
	 * 		c. 一个NIO线程可以同时处理N条链路,但是一个链路只对应一个NIO线程,防止发生并发操作问题;
	 *
	 * 3. 主从Reactor多线程模型: 主从Reactor多线程模型的特点是,服务端用于接收客户端连接的不再是一个单独的NIO线程,
	 * 而是一个独立的NIO线程池;Acceptor接收到客户端tcp连接请求并处理完成后(可能包含接入认证等),
	 * 将新创建的SocketChannel注册到I/O线程池(sub reactor线程池)的某个I/O线程上,
	 * 由它负责SocketChannel的读写和编解码工作;Acceptor线程池仅仅用于客户端的登录,握手和安全认证,一旦链路建立成功,
	 * 就将链路注册到后端subReactor线程池的I/O线程上,由I/O线程负责后续的I/O操作;
	 */

    /**
     * Return the next {@link EventLoop} to use
     */
    @Override
    EventLoop next();

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The returned {@link ChannelFuture}
     * will get notified once the registration was complete.
     */
    ChannelFuture register(Channel channel);

    /**
     * Register a {@link Channel} with this {@link EventLoop} using a {@link ChannelFuture}. The passed
     * {@link ChannelFuture} will get notified once the registration was complete and also will get returned.
     */
    ChannelFuture register(ChannelPromise promise);

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The passed {@link ChannelFuture}
     * will get notified once the registration was complete and also will get returned.
     *
     * @deprecated Use {@link #register(ChannelPromise)} instead.
     */
    @Deprecated
    ChannelFuture register(Channel channel, ChannelPromise promise);
}
