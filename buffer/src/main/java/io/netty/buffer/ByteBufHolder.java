/*
 * Copyright 2013 The Netty Project
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
package io.netty.buffer;

import io.netty.util.ReferenceCounted;

/**
 * A packet which is send or receive.
 */

/**
 * TODO 是ByteBuf的容器,在Netty中非常有用,
 * 例如http协议的请求消息和应答消息都可以携带消息体,
 * 这个消息体在NIO ByteBuffer中就是个ByteBuffer对象,
 * 在Netty中就是ByteBuf对象;由于不同的协议消息体可以包含不同的协议字段和功能,
 * 因此,需要对ByteBuf进行包装和抽象,不同的子类可以有不同的是实现;
 * 为了满足这些定制化的需求,Netty抽象出了ByteBufHolder对象,它包含了一个ByteBuf,
 * 另外还提供了一些其他实用的方法,使用者继承ByteBufHolder接口后可以按需封装自己的实现;
 */
public interface ByteBufHolder extends ReferenceCounted {

    /**
     * Return the data which is held by this {@link ByteBufHolder}.
     */
    ByteBuf content();

    /**
     * Creates a deep copy of this {@link ByteBufHolder}.
     */
    ByteBufHolder copy();

    /**
     * Duplicates this {@link ByteBufHolder}. Be aware that this will not automatically call {@link #retain()}.
     */
    ByteBufHolder duplicate();

    /**
     * Duplicates this {@link ByteBufHolder}. This method returns a retained duplicate unlike {@link #duplicate()}.
     *
     * @see ByteBuf#retainedDuplicate()
     */
    ByteBufHolder retainedDuplicate();

    /**
     * Returns a new {@link ByteBufHolder} which contains the specified {@code content}.
     */
    ByteBufHolder replace(ByteBuf content);

    @Override
    ByteBufHolder retain();

    @Override
    ByteBufHolder retain(int increment);

    @Override
    ByteBufHolder touch();

    @Override
    ByteBufHolder touch(Object hint);
}
