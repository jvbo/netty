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

import io.netty.util.concurrent.OrderedEventExecutor;

/**
 * Will handle all the I/O operations for a {@link Channel} once registered.
 *
 * One {@link EventLoop} instance will usually handle more than one {@link Channel} but this may depend on
 * implementation details and internals.
 *
 */

/**
 * TODO 控制流,多线程处理,并发;
 * 定义了Netty的核心抽象,用于处理连接的生命周期中所发生的事件;
 */
public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
	/**
	 * Channel,EventLoop,Thread,EventLoopGroup之间的关系;
	 * 1. 一个EventLoopGroup包含一个或者多个EventLoop;
	 * 2. 一个EventLoop在它的生命周期内只和一个Thread绑定;
	 * 3. 所有由EventLoop处理的I/O事件都将在它专有的Thread上被处理;
	 * 4. 一个Channel在它的生命周期内只注册于一个EventLoop;
	 * 5. 一个EventLoop可能会被分配给一个或多个Channel;
	 * 在这种设计中,一个给定Channel的I/O操作都是由相同的Thread执行的,实际上消除了对同步的需要;
	 */

    @Override
    EventLoopGroup parent();
}
