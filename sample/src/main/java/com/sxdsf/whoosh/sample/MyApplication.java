package com.sxdsf.whoosh.sample;

import android.app.Application;

import com.sxdsf.whoosh.Whoosh;

/**
 * MyApplication
 *
 * @author sunbowen
 * @date 2016/5/18-20:12
 * @desc 自己实现的Application
 */
public class MyApplication extends Application {

	/** 消息服务的实例 */
	public static final Whoosh WHOOSH = Whoosh.create();

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化消息服务
		if (!WHOOSH.isInitialized()) {
			WHOOSH.initialize();
		}
	}
}
