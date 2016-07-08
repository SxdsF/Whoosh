package com.sxdsf.whoosh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.whoosh.Converter;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.LogInterceptor;
import com.sxdsf.whoosh.ThreadMode;
import com.sxdsf.whoosh.core.Carrier;
import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshDestination;
import com.sxdsf.whoosh.info.WhooshTopic;

import java.util.concurrent.atomic.AtomicInteger;

public class WhooshActivity extends AppCompatActivity {

    /**
     * 目的地，接收从上一个activity传来的信息，用作地址标识
     */
    public static final Destination SecondDestination = new WhooshDestination("second");

    public static final Topic TEST = new WhooshTopic("test");
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 发送一个消息
        MyApplication.WHOOSH.send(MainActivity.MainTopic, Message.create("测试"));

        //接收从上一个activity传过来的信息
        System.out
                .println("WhooshActivity" + MyApplication.WHOOSH.receive(SecondDestination).checkAndGet(String.class));

        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            Listener.
                    create().
                    priority(finalI).
                    unify(new Converter() {
                        @Override
                        public Listener call(Listener listener) {
                            return listener.
                                    listenOn(ThreadMode.MAIN).
                                    careAbout(TEST).
                                    listenIn(MyApplication.WHOOSH).
                                    log(new LogInterceptor() {
                                        @Override
                                        public void preListen(Topic topic, Listener listener) {
                                            System.out.println("preListen" + topic + listener);
                                        }

                                        @Override
                                        public void afterListen(Topic topic, Listener listener) {
                                            System.out.println("afterListen" + topic + listener);
                                        }

                                        @Override
                                        public void preReceive(Topic topic, Listener listener, Message message) {
                                            System.out.println("preReceive" + topic + listener + message);
                                        }

                                        @Override
                                        public void preUnListen(Topic topic, Listener listener) {
                                            System.out.println("preUnListen" + topic + listener);
                                        }

                                        @Override
                                        public void afterUnListen(Topic topic, Listener listener) {
                                            System.out.println("afterUnListen" + topic + listener);
                                        }
                                    });
                        }
                    }).
                    listen(new Carrier<Message>() {
                        @Override
                        public void onReceive(Message content) {
                            System.out.println("一共调用了" + count.incrementAndGet() + "第" + finalI + "回调" + content.checkAndGet(String.class) + System.currentTimeMillis() + Thread.currentThread());
                        }
                    });
        }

        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("第" + finalI + "个线程发送" + System.currentTimeMillis() + Thread.currentThread());
                    MyApplication.WHOOSH.send(TEST, Message.create("第" + finalI + "个线程发送"));
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
