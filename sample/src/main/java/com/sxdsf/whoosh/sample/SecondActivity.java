package com.sxdsf.whoosh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.whoosh.Producer;
import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.WhooshDestination;

public class SecondActivity extends AppCompatActivity {

	/** 目的地，接收从上一个activity传来的信息，用作地址标识 */
	public static final Destination SecondDestination = new WhooshDestination("second");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		// 根据话题创建消息的发送者
		Producer producer = MyApplication.WHOOSH.createProducer(MainActivity.MainTopic);
		// 发送一个消息
		producer.send(Message.create("测试"));

		// 接收从上一个activity传过来的信息
		System.out
				.println("SecondActivity" + MyApplication.WHOOSH.receive(SecondDestination).checkAndGet(String.class));
	}
}
