package com.sxdsf.whoosh;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Producer
 *
 * @author sunbowen
 * @date 2016/5/18-15:15
 * @desc 消息的产生者
 */
public class Producer {

	private final Topic topic;
	private final BlockingQueue<WhooshMessage> messageQueue;

	protected Producer(Topic topic, BlockingQueue<WhooshMessage> messageQueue) {
		this.topic = topic;
		this.messageQueue = messageQueue;
	}

	private static final String TAG = "Producer";

	/**
	 * 发送一个消息
	 * 
	 * @param message
	 *            消息
	 */
	public void send(@NonNull Message message) {
		try {
			WhooshMessage tm = WhooshMessage.copyFromMessage(message);
			tm.topic = this.topic;
			this.messageQueue.put(tm);
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
