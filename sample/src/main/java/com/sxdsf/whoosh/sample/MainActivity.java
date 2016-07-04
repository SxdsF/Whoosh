package com.sxdsf.whoosh.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Filters;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.ThreadMode;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshTopic;

public class MainActivity extends AppCompatActivity {

    /**
     * 话题
     */
    public static final Topic MainTopic = new WhooshTopic("main");

    /**
     * 基于话题的监听者
     */
    private Listener<Message> mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 根据话题生成一个监听者
        mListener = MyApplication.WHOOSH.register(MainTopic, Filters.klass(String.class));
        // 监听者监听，其中Carrier作为消息的载体，收到消息后会执行onReceive方法
        mListener.listenOn(ThreadMode.MAIN).listen(new Carrier<Message>() {
            @Override
            public void onReceive(Message content) {
                System.out.println("MainActivity" + content.checkAndGet(String.class));
            }
        });

        findViewById(R.id.whoosh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //两个activity之间传递任意格式的数据
                MyApplication.WHOOSH.post(WhooshActivity.SecondDestination, Message.create("测试"));
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WhooshActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.event_bus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EventBusActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在onDestroy时解绑监听者
        MyApplication.WHOOSH.unRegister(MainTopic, mListener);
    }
}
