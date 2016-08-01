package io.reactivesocket.tckdrivers.fail;

import io.reactivesocket.Payload;
import io.reactivesocket.ReactiveSocket;
import io.reactivesocket.tckdrivers.common.TestSubscriber;
import io.reactivesocket.util.PayloadImpl;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class ClientActor {

    public void act() {
        ReactiveSocket r = Client.createClient();
        TestSubscriber<Payload> ts = new TestSubscriber<>(0L);
        Publisher<Payload> pub = r.requestStream(new PayloadImpl("a", "b"));
        pub.subscribe(ts);
        ts.request(1);
        ts.cancel();
        ts.assertNoErrors();
        ts.assertNoErrors();
        ts.assertNoValues();
        TestSubscriber<Payload> ts2 = new TestSubscriber<>(0L);
        ReactiveSocket r2 = Client.createClient();
        Publisher<Payload> pub2 = r2.requestStream(new PayloadImpl("a", "b"));
        pub2.subscribe(ts2);
        ts2.request(1);
        ts2.cancel();
        ts2.assertNoErrors();
        ts2.assertNoErrors();
        ts2.assertNoValues();
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("interrupted");
        }
    }

    private interface MyRunnable extends Runnable {
        void start();
        void join();
    }


    private class RequestResponseThread implements MyRunnable {

        private Thread t;

        public RequestResponseThread() {
            this.t = new Thread(this);
        }

        @Override
        public void run() {
            ReactiveSocket sock = Client.createClient();
            Publisher<Payload> pub = sock.requestResponse(new PayloadImpl("a", "b"));
            TestSubscriber<Payload> ts = new TestSubscriber<>(0L);
            pub.subscribe(ts);
            ts.request(1);
        }

        @Override
        public void start() {
            this.t.run();
        }

        @Override
        public void join() {
            try {
                this.t.join();
            } catch (Exception e) {

            }
        }
    }

    private class RequestStreamThread implements MyRunnable {

        private Thread t;

        public RequestStreamThread() {
            this.t = new Thread(this);
        }

        @Override
        public void run() {
            ReactiveSocket sock = Client.createClient();
            Publisher<Payload> pub = sock.requestStream(new PayloadImpl("a", "b"));
            TestSubscriber<Payload> ts = new TestSubscriber<>(0L);
            pub.subscribe(ts);
            ts.request(10);
        }

        @Override
        public void start() {
            this.t.run();
        }

        @Override
        public void join() {
            try {
                this.t.join();
            } catch (Exception e) {

            }
        }
    }

    private class RequestSubscriptionThread implements MyRunnable {

        private Thread t;

        public RequestSubscriptionThread() {
            this.t = new Thread(this);
        }

        @Override
        public void run() {
            ReactiveSocket sock = Client.createClient();
            Publisher<Payload> pub = sock.requestSubscription(new PayloadImpl("a", "b"));
            TestSubscriber<Payload> ts = new TestSubscriber<>(0L);
            pub.subscribe(ts);
            ts.request(10);
        }

        @Override
        public void start() {
            this.t.run();
        }

        @Override
        public void join() {
            try {
                this.t.join();
            } catch (Exception e) {

            }
        }
    }

    private class FireAndForgetThread implements MyRunnable {

        private Thread t;

        public FireAndForgetThread() {
            this.t = new Thread(this);
        }

        @Override
        public void run() {
            ReactiveSocket sock = Client.createClient();
            Publisher<Void> pub = sock.fireAndForget(new PayloadImpl("a", "b"));
            TestSubscriber<Void> ts = new TestSubscriber<>(0L);
            pub.subscribe(ts);
            ts.request(10);
        }

        @Override
        public void start() {
            this.t.run();
        }
        @Override
        public void join() {
            try {
                this.t.join();
            } catch (Exception e) {

            }
        }
    }

    private class RequestChannelThread implements MyRunnable {

        private Thread t;

        public RequestChannelThread() {
            this.t = new Thread(this);
        }

        @Override
        public void run() {
            ReactiveSocket sock = Client.createClient();
            Publisher<Payload> pub = sock.requestChannel(new Publisher<Payload>() {
                @Override
                public void subscribe(Subscriber<? super Payload> s) {
                    s.onNext(new PayloadImpl("a", "b"));
                }
            });
            TestSubscriber<Payload> ts = new TestSubscriber<>(0L);
            pub.subscribe(ts);
            ts.request(10);
        }

        @Override
        public void start() {
            this.t.run();
        }

        @Override
        public void join() {
            try {
                this.t.join();
            } catch (Exception e) {

            }
        }
    }

}
