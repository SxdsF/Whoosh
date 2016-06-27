package com.sxdsf.whoosh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Filter;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.Producer;
import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshDestination;
import com.sxdsf.whoosh.info.WhooshTopic;

public class WhooshActivity extends AppCompatActivity {

	/** 目的地，接收从上一个activity传来的信息，用作地址标识 */
	public static final Destination SecondDestination = new WhooshDestination("second");

	public static final Topic TEST = new WhooshTopic("test");
	private Thread mThread;

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
				.println("WhooshActivity" + MyApplication.WHOOSH.receive(SecondDestination).checkAndGet(String.class));

		for (int i = 0; i < 100; i++) {
			Listener<Message> listener = MyApplication.WHOOSH.register(TEST, (Filter[]) null);
			final int finalI = i;
			listener.listen(new Carrier<Message>() {
				@Override
				public void onReceive(Message content) {
					System.out.println("第" + finalI + "个接收到消息为---" + content.checkAndGet(String.class) + "---，时间为"
							+ System.currentTimeMillis());
				}
			});
		}

		for (int i = 0; i < 100; i++) {
			final int finalI = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Producer producer = MyApplication.WHOOSH.createProducer(TEST);
					producer.send(Message.create("第" + finalI + "个线程发送"));
				}
			}).start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
