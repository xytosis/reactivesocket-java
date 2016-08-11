package io.reactivesocket.tckdrivers.fail;

import io.netty.buffer.ByteBuf;
import io.netty.handler.logging.LogLevel;
import io.reactivesocket.ConnectionSetupPayload;
import io.reactivesocket.ReactiveSocket;
import io.reactivesocket.tckdrivers.client.JavaClientDriver;
import io.reactivesocket.tckdrivers.client.JavaTCPClient;
import io.reactivesocket.transport.tcp.client.TcpReactiveSocketConnector;
import io.reactivex.netty.protocol.tcp.client.TcpClient;

import java.net.*;
import java.util.function.Function;

import static rx.RxReactiveStreams.toObservable;

public class Client {

    private static URI uri;

    public static void main(String[] args)
            throws MalformedURLException, URISyntaxException {

        try {
            setURI(new URI("tcp://" + "localhost" + ":" + 4567 + "/rs"));


            ClientActor actor = new ClientActor();
            /*actor.act();
            actor.act();
            actor.act();*/

            actor.act2StreamFail();
            actor.act2StreamFail();
            actor.act2StreamFail();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setURI(URI uri2) {
        uri = uri2;
    }

    /**
     * A function that creates a ReactiveSocket on a new TCP connection.
     * @return a ReactiveSocket
     */
    public static ReactiveSocket createClient() {
        ConnectionSetupPayload setupPayload = ConnectionSetupPayload.create("", "");

        if ("tcp".equals(uri.getScheme())) {
            Function<SocketAddress, TcpClient<ByteBuf, ByteBuf>> clientFactory =
                    socketAddress -> TcpClient.newClient(socketAddress);

            return toObservable(
                    TcpReactiveSocketConnector.create(setupPayload, Throwable::printStackTrace, clientFactory)
                            .connect(new InetSocketAddress(uri.getHost(), uri.getPort()))).toSingle()
                    .toBlocking()
                    .value();
        }
        else {
            throw new UnsupportedOperationException("uri unsupported: " + uri);
        }
    }

}
