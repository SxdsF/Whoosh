package com.sxdsf.whoosh.sample;

import android.app.Application;

import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.Whoosh;
import com.sxdsf.whoosh.info.Message;

/**
 * MyApplication
 *
 * @author sunbowen
 * @date 2016/5/18-20:12
 * @desc 自己实现的Application
 */
public class MyApplication extends Application {

	public static final Whoosh<Listener<Message>> WHOOSH = Whoosh.create();

	@Override
	public void onCreate() {
		super.onCreate();
		if (!WHOOSH.isInitialized()) {
			WHOOSH.initialize();
		}
	}
}
