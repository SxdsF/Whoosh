package com.sxdsf.whoosh;

/**
 * Carrier
 *
 * @author sunbowen
 * @date 2016/5/16-17:07
 * @desc 消息的载体
 */
public interface Carrier<T> {

	/**
	 * 有消息时回调此方法
	 * 
	 * @param content
	 *            消息
	 */
	void onReceive(T content);
}
