/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivesocket.transport.websocket.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.reactivesocket.ConnectionSetupHandler;
import io.reactivesocket.DefaultReactiveSocket;
import io.reactivesocket.Frame;
import io.reactivesocket.LeaseGovernor;
import io.reactivesocket.ReactiveSocket;
import io.reactivesocket.transport.tcp.MutableDirectByteBuf;
import io.reactivesocket.util.Unsafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactiveSocketServerHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveSocketServerHandler.class);

    private ConnectionSetupHandler setupHandler;
    private LeaseGovernor leaseGovernor;
    private ServerWebSocketDuplexConnection connection;

    protected ReactiveSocketServerHandler(ConnectionSetupHandler setupHandler, LeaseGovernor leaseGovernor) {
        this.setupHandler = setupHandler;
        this.leaseGovernor = leaseGovernor;
    }

    public static ReactiveSocketServerHandler create(ConnectionSetupHandler setupHandler) {
        return create(setupHandler, LeaseGovernor.UNLIMITED_LEASE_GOVERNOR);
    }

    public static ReactiveSocketServerHandler create(ConnectionSetupHandler setupHandler, LeaseGovernor leaseGovernor) {
        return new ReactiveSocketServerHandler(setupHandler, leaseGovernor);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connection = new ServerWebSocketDuplexConnection(ctx);
        ReactiveSocket reactiveSocket =
            DefaultReactiveSocket.fromServerConnection(connection, setupHandler, leaseGovernor, Throwable::printStackTrace);
        // Note: No blocking code here (still it should be refactored)
        Unsafe.startAndWait(reactiveSocket);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf content = msg.content();
        MutableDirectByteBuf mutableDirectByteBuf = new MutableDirectByteBuf(content);
        Frame from = Frame.from(mutableDirectByteBuf, 0, mutableDirectByteBuf.capacity());

        if (connection != null) {
            connection.getSubscribers().forEach(o -> o.onNext(from));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        logger.error("caught an unhandled exception", cause);
    }
}
