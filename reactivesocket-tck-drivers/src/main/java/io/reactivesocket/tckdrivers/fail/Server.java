package io.reactivesocket.tckdrivers.fail;

import io.reactivesocket.RequestHandler;
import io.reactivesocket.exceptions.Exceptions;
import io.reactivesocket.transport.tcp.server.TcpReactiveSocketServer;
import io.reactivesocket.util.PayloadImpl;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) {

        TcpReactiveSocketServer.create(4567)
                .start((setupPayload, reactiveSocket) -> {

                    return new RequestHandler.Builder().withFireAndForget(p -> s -> {
                    })
                            .withRequestResponse(payload -> s -> {

                            })
                            .withRequestStream(payload -> s -> {
                                try {
                                    Thread.sleep(3000);
                                } catch (Exception e) {

                                }

                                s.onSubscribe(new Subscription() {

                                    @Override
                                    public void request(long n) {

                                        s.onNext(new PayloadImpl("a", "b"));
                                    }

                                    @Override
                                    public void cancel() {
                                        System.out.println("canceled");
                                    }
                                });
                            })
                            .withRequestSubscription(payload -> s -> {
                            })
                            .withRequestChannel(payloadPublisher -> s -> {

                            })
                            .build();
                }).awaitShutdown();


    }

}
