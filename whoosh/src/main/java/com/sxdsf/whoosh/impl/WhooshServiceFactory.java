package com.sxdsf.whoosh.impl;

import com.sxdsf.whoosh.Adapter;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.WhooshService;
import com.sxdsf.whoosh.info.Message;

/**
 * WhooshServiceFactory
 *
 * @author sunbowen
 * @date 2016/5/18-15:03
 * @desc 消息服务的工厂类
 */
public class WhooshServiceFactory {

	public static <T> WhooshService<T> create(Adapter<T> adapter) {
		return new WhooshServiceImpl<>(adapter);
	}

	public static WhooshService<Listener<Message>> create() {
		return new WhooshServiceImpl<>(new Adapter.DefaultAdapter());
	}
}
