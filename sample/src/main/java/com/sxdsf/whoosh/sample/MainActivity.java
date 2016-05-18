package com.sxdsf.whoosh.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Filters;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.Producer;
import com.sxdsf.whoosh.Whoosh;
import com.sxdsf.whoosh.WhooshService;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshTopic;

public class MainActivity extends AppCompatActivity {

	private WhooshService<Listener<Message>> service = Whoosh.build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (!service.isInitialized()) {
			service.initialize();
		}

		Topic topic = new WhooshTopic("test");
		Listener<Message> listener = service.register(topic, Filters.klass(String.class));
		listener.listen(new Carrier<Message>() {
			@Override
			public void onReceive(Message content) {
				System.out.println(Thread.currentThread());
			}
		});
		Producer producer = service.createProducer(topic);
		producer.send(Message.create("测试"));
	}
}
