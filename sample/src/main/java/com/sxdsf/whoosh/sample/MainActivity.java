package com.sxdsf.whoosh.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Filters;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshTopic;

public class MainActivity extends AppCompatActivity {

	public static final Topic MainTopic = new WhooshTopic("main");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Listener<Message> listener = MyApplication.WHOOSH.register(MainTopic, Filters.klass(String.class));
		listener.listen(new Carrier<Message>() {
			@Override
			public void onReceive(Message content) {
				System.out.println("MainActivity" + content.checkAndGet(String.class));
			}
		});

		MyApplication.WHOOSH.post(SecondActivity.SecondDestination, Message.create("测试"));

		Intent intent = new Intent();
		intent.setClass(this, SecondActivity.class);
		this.startActivity(intent);
	}
}
