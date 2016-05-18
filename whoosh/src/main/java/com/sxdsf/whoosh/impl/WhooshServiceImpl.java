package com.sxdsf.whoosh.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sxdsf.whoosh.Adapter;
import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Filter;
import com.sxdsf.whoosh.Filters;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.Producer;
import com.sxdsf.whoosh.Switchers;
import com.sxdsf.whoosh.Theme;
import com.sxdsf.whoosh.WhooshService;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WhooshServiceImpl
 *
 * @author sunbowen
 * @date 2016/5/17-15:38
 * @desc 消息服务的实现
 */
public class WhooshServiceImpl<T> implements WhooshService<T> {

	private Adapter<T> mAdapter;

	protected WhooshServiceImpl(Adapter<T> adapter) {
		this.mAdapter = adapter;
	}

	private final StorageUnit mStorageUnit = new StorageUnit();
	private final BlockingQueue<WhooshMessage> messageQueue = new PriorityBlockingQueue<>();
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final AtomicBoolean isInit = new AtomicBoolean(false);

	private static final String TAG = "WhooshService";

	@Override
	public void initialize() {
		if (this.isInit.compareAndSet(false, true)) {
			this.executorService.execute(new Task());
		}
	}

	@Override
	public boolean isInitialized() {
		return this.isInit.get();
	}

	@Override
	public T register(@NonNull Topic topic, Filter... filters) {
		Theme<Message> theme = Theme.create();
		synchronized (this.mStorageUnit) {
			List<Theme<Message>> themes = this.mStorageUnit.themesMapper.get(topic.getUniqueId());
			if (themes == null) {
				themes = new ArrayList<>();
				this.mStorageUnit.themesMapper.put(topic.getUniqueId(), themes);
			}
			List<Filter> filterList = new ArrayList<>();
			filterList.add(Filters.isConsumed());
			if (filters != null && filters.length > 0) {
				filterList.addAll(Arrays.asList(filters));
			}
			this.mStorageUnit.filtersMapper.put(theme, filterList);
			themes.add(theme);
		}
		return this.mAdapter.adapt(theme);
	}

	@Override
	public void unRegister(@NonNull Topic topic, T listener) {
		synchronized (this.mStorageUnit) {
			List<Theme<Message>> themes = this.mStorageUnit.themesMapper.get(topic.getUniqueId());
			if (themes != null) {
				themes.remove(Theme.class.cast(this.mAdapter.reverseAdapt(listener)));
				if (themes.isEmpty()) {
					this.mStorageUnit.themesMapper.remove(topic.getUniqueId());
				}
			}
			this.mStorageUnit.filtersMapper.remove(Theme.class.cast(this.mAdapter.reverseAdapt(listener)));
		}
	}

	@Override
	public Producer createProducer(@NonNull Topic topic) {
		return new Producer(topic, this.messageQueue);
	}

	private class Task implements Runnable {
		@Override
		public void run() {
			try {
				final WhooshMessage message = messageQueue.take();
				if (message != null && message.topic != null) {
					Topic topic = message.topic;
					synchronized (mStorageUnit) {
						List<Theme<Message>> themes = mStorageUnit.themesMapper.get(topic.getUniqueId());
						if (themes != null && !themes.isEmpty()) {
							for (final Theme<Message> theme : themes) {
								if (theme != null) {
									boolean flag = true;
									List<Filter> filters = mStorageUnit.filtersMapper.get(theme);
									if (!message.isEmptyMessage) {
										if (filters != null) {
											for (Filter filter : filters) {
												if (filter != null) {
													flag = filter.filter(message);
												}
											}
										}
									}
									if (flag) {
										Listener.from(message).listenOn(Switchers.mainThread())
												.listen(new Carrier<WhooshMessage>() {
													@Override
													public void onReceive(WhooshMessage content) {
														theme.onReceive(message);
													}
												});
									}
								}
							}
						}
					}
				}
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}
