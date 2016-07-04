package com.sxdsf.whoosh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicInteger;

public class EventBusActivity extends AppCompatActivity {

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        for (int i = 0; i < 100; i++) {
            EventReceiver eventReceiver = new EventReceiver(i);
        }

        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("第" + finalI + "个线程发送" + System.currentTimeMillis() + Thread.currentThread());
                    EventBus.getDefault().post("第" + finalI + "个线程发送");
                }
            }).start();
        }
    }

    private static class EventReceiver {

        private int i;

        public EventReceiver(int i) {
            this.i = i;
            EventBus.getDefault().register(this);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(String msg) {
            System.out.println("一共调用了" + count.incrementAndGet() + "第" + i + "回调" + msg + System.currentTimeMillis() + Thread.currentThread());
        }
    }
}
