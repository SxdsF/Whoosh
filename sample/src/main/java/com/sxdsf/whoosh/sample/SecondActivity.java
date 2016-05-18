package com.sxdsf.whoosh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.whoosh.Producer;
import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.WhooshDestination;

public class SecondActivity extends AppCompatActivity {

	public static final Destination SecondDestination = new WhooshDestination("second");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		Producer producer = MyApplication.WHOOSH.createProducer(MainActivity.MainTopic);
		producer.send(Message.create("测试"));

		System.out.println("SecondActivity" + MyApplication.WHOOSH.receive(SecondDestination).checkAndGet(String.class));
	}
}
