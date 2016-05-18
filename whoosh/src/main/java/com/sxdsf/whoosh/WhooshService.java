package com.sxdsf.whoosh;

import android.support.annotation.NonNull;

import com.sxdsf.whoosh.info.Topic;

/**
 * WhooshService
 *
 * @author sunbowen
 * @date 2016/5/17-14:58
 * @desc 消息服务的service类
 */
public interface WhooshService<T> {

	void initialize();

	boolean isInitialized();

	T register(@NonNull Topic topic, Filter... filters);

	void unRegister(@NonNull Topic topic, T listener);

	Producer createProducer(@NonNull Topic topic);
}
