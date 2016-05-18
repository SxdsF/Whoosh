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

	/**
	 * 把系统默认的适配成T
	 * 
	 * @param listener
	 *            系统默认的监听者
	 * @return
	 */
	T adapt(Listener<Message> listener);

	/**
	 * 把T适配成系统默认的
	 * 
	 * @param t
	 *            T
	 * @return
	 */
	Listener<Message> reverseAdapt(T t);

	/**
	 * 默认的Adapter，维持使用系统的类型
	 */
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
