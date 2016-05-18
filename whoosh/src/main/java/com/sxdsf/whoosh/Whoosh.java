package com.sxdsf.whoosh;

import android.support.annotation.NonNull;

import com.sxdsf.whoosh.impl.WhooshServiceFactory;
import com.sxdsf.whoosh.info.Message;

/**
 * Whoosh
 *
 * @author sunbowen
 * @date 2016/5/18-15:09
 * @desc 消息服务的Builder类
 */
public class Whoosh {

	public static <T> WhooshService<T> build(@NonNull Adapter<T> adapter) {
		return WhooshServiceFactory.create(adapter);
	}

	public static WhooshService<Listener<Message>> build() {
		return WhooshServiceFactory.create();
	}
}
