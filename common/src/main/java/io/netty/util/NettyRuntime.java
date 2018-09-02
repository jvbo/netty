/*
 * Copyright 2017 The Netty Project
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

package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.Locale;

/**
 * A utility class for wrapping calls to {@link Runtime}.
 */
public final class NettyRuntime {

	/**
	 * 不选择java原生NIO编程的原因:
	 * 1. NIO类库和api繁杂,使用麻烦,你需要熟练掌握Selector,ServerSocketChannel,SocketChannel,ByteBuffer等;
	 * 2. 需要具备其他的额外技能做铺垫,例如熟悉多线程编程;以为NIO编程涉及到Reactor模式,必须对多线程和网络编程非常熟悉才能编写出高质量的NIO程序;
	 * 3. 可靠性能力补齐,工作量和难度都非常大;例如客户端面临断线重连,网络闪断,半包读写,失败缓存,网络拥赛和异常码流的处理等问题,
	 * NIO编程的特点是功能开发相对容易,但是可靠性能力补齐的工作量和难度都非常大;
	 * 4. jdk NIO的bug,例如epoll bug,会导致Selector空轮询,最终导致cpu100%;
	 *
	 * 为什么选择Netty:
	 * 1. api使用简单,开发门槛低;
	 * 2. 功能强大,预置了多种编码功能,支持多种主流协议;
	 * 3. 定制能力强,可以通过ChannelHandler对通信框架进行灵活的扩展;
	 * 4. 性能高,通过与其他业界主流的NIO框架对比,Netty综合性能最优;
	 * 5. 成熟,稳定,Netty修复了已经发现的所有jdk nio bug,业务开发人员不需要再为NIO的bug而烦恼;
	 * 6. 经历了大规模的商业应用考验,质量得到验证,在互联网,大数据,网络游戏,企业应用,电信软件等众多行业得到成功商用,
	 * 证明了它已经完全能够满足不同行业的商业应用了;
	 */

    /**
     * Holder class for available processors to enable testing.
     */
    static class AvailableProcessorsHolder {

        private int availableProcessors;

        /**
         * Set the number of available processors.
         *
         * @param availableProcessors the number of available processors
         * @throws IllegalArgumentException if the specified number of available processors is non-positive
         * @throws IllegalStateException    if the number of available processors is already configured
         */
        synchronized void setAvailableProcessors(final int availableProcessors) {
            ObjectUtil.checkPositive(availableProcessors, "availableProcessors");
            if (this.availableProcessors != 0) {
                final String message = String.format(
                        Locale.ROOT,
                        "availableProcessors is already set to [%d], rejecting [%d]",
                        this.availableProcessors,
                        availableProcessors);
                throw new IllegalStateException(message);
            }
            this.availableProcessors = availableProcessors;
        }

        /**
         * Get the configured number of available processors. The default is {@link Runtime#availableProcessors()}.
         * This can be overridden by setting the system property "io.netty.availableProcessors" or by invoking
         * {@link #setAvailableProcessors(int)} before any calls to this method.
         *
         * @return the configured number of available processors
         */
        @SuppressForbidden(reason = "to obtain default number of available processors")
        synchronized int availableProcessors() {
            if (this.availableProcessors == 0) {
                final int availableProcessors =
                        SystemPropertyUtil.getInt(
                                "io.netty.availableProcessors",
                                Runtime.getRuntime().availableProcessors());
                setAvailableProcessors(availableProcessors);
            }
            return this.availableProcessors;
        }
    }

    private static final AvailableProcessorsHolder holder = new AvailableProcessorsHolder();

    /**
     * Set the number of available processors.
     *
     * @param availableProcessors the number of available processors
     * @throws IllegalArgumentException if the specified number of available processors is non-positive
     * @throws IllegalStateException    if the number of available processors is already configured
     */
    @SuppressWarnings("unused,WeakerAccess") // this method is part of the public API
    public static void setAvailableProcessors(final int availableProcessors) {
        holder.setAvailableProcessors(availableProcessors);
    }

    /**
     * Get the configured number of available processors. The default is {@link Runtime#availableProcessors()}. This
     * can be overridden by setting the system property "io.netty.availableProcessors" or by invoking
     * {@link #setAvailableProcessors(int)} before any calls to this method.
     *
     * @return the configured number of available processors
     */
    public static int availableProcessors() {
        return holder.availableProcessors();
    }

    /**
     * No public constructor to prevent instances from being created.
     */
    private NettyRuntime() {
    }
}
