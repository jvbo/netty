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

/**
 * {@link ChannelHandler} which adds callbacks for state changes. This allows the user
 * to hook in to state changes easily.
 */

/**
 * TODO 对从客户端发往服务器的报文进行处理,一般用来执行解码,读取客户端数据,进行业务处理等;
 * 按注册的先后顺序执行;
 */
public interface ChannelInboundHandler extends ChannelHandler {

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered with its {@link EventLoop}
     */
	/**
	 * TODO 当Channel已经注册到它的EventLoop并且能够处理I/O时被调用;
	 * @param ctx
	 * @throws Exception
	 */
	void channelRegistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was unregistered from its {@link EventLoop}
     */
	/**
	 * TODO 当Channel从它的EventLoop注销并且无法处理任何I/O时被调用;
	 * @param ctx
	 * @throws Exception
	 */
	void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} is now active
     */
	/**
	 * TODO 当Channel处于活动状态(已经连接到远程节点)时调用,Channel已经连接/绑定并且可以接受和发送数据了;
	 * @param ctx
	 * @throws Exception
	 */
	void channelActive(ChannelHandlerContext ctx) throws Exception;

    /**
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered is now inactive and reached its
     * end of lifetime.
     */
	/**
	 * TODO 当Channel离开活动状态并且不再连接它的远程节点时被调用;
	 * @param ctx
	 * @throws Exception
	 */
	void channelInactive(ChannelHandlerContext ctx) throws Exception;

    /**
     * Invoked when the current {@link Channel} has read a message from the peer.
     */
	/**
	 * TODO 当从Channel读取数据时被调用;
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    /**
     * Invoked when the last message read by the current read operation has been consumed by
     * {@link #channelRead(ChannelHandlerContext, Object)}.  If {@link ChannelOption#AUTO_READ} is off, no further
     * attempt to read an inbound data from the current {@link Channel} will be made until
     * {@link ChannelHandlerContext#read()} is called.
     */
	/**
	 * TODO 当Channel上的一个读操作完成时被调用;
	 * @param ctx
	 * @throws Exception
	 */
	void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if an user event was triggered.
     */
	/**
	 * TODO 当ChannelInboundHandler.fireUserEventTriggered()被调用时被调用,因为POJO被传经了ChannelPipeline;
	 * @param ctx
	 * @param evt
	 * @throws Exception
	 */
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * Gets called once the writable state of a {@link Channel} changed. You can check the state with
     * {@link Channel#isWritable()}.
     */
	/**
	 * TODO 当Channel的可写状态发生改变时被调用;用户可以确保写操作不会完成的太快(以避免OOM)
	 * 或者可以在Channel变为再次可写时恢复写入;可以通过调用Channel的isWriteable()来检测Channel的可写性;
	 * 与可写性相关的阈值可以通过Channel.config().setWriteHighWaterMark()和Channel.config().setWriteLowWaterMark()方法设置;
	 * @param ctx
	 * @throws Exception
	 */
	void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if a {@link Throwable} was thrown.
     */
    @Override
    @SuppressWarnings("deprecation")
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
