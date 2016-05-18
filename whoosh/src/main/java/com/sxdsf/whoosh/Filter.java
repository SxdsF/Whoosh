package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;

/**
 * Filter
 *
 * @author sunbowen
 * @date 2016/5/18-15:11
 * @desc 消息的过滤类
 */
public interface Filter {
	boolean filter(Message message);
}
