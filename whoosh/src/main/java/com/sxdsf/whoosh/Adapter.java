package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;

/**
 * Alter
 *
 * @author sunbowen
 * @date 2016/5/17-15:07
 * @desc 返回结果适配类
 */
public interface Adapter<T> {

	T adapt(Listener<Message> listener);

	Listener<Message> reverseAdapt(T t);

	class DefaultAdapter implements Adapter<Listener<Message>> {

		@Override
		public Listener<Message> adapt(Listener<Message> listener) {
			return listener;
		}

		@Override
		public Listener<Message> reverseAdapt(Listener<Message> messageListener) {
			return messageListener;
		}
	}
}
