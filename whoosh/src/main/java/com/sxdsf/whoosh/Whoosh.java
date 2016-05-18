package com.sxdsf.whoosh;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Whoosh
 *
 * @author sunbowen
 * @date 2016/5/18-15:09
 * @desc 消息服务的Builder类
 */
public class Whoosh<T> {
	private Adapter<T> mAdapter;

	private Whoosh(Adapter<T> adapter) {
		this.mAdapter = adapter;
	}

	public static <T> Whoosh<T> create(Adapter<T> adapter) {
		return new Whoosh<>(adapter);
	}

	public static Whoosh<Listener<Message>> create() {
		return create(new Adapter.DefaultAdapter());
	}

	private final Map<UUID, Message> themesMapper = new ConcurrentHashMap<>();
	private final StorageUnit mStorageUnit = new StorageUnit();
	private final BlockingQueue<WhooshMessage> messageQueue = new PriorityBlockingQueue<>();
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final AtomicBoolean isInit = new AtomicBoolean(false);

	private static final String TAG = "Whoosh";

	/**
	 * 初始化方法
	 */
	public void initialize() {
		if (this.isInit.compareAndSet(false, true)) {
			this.executorService.execute(new Task());
		}
	}

	/**
	 * 判断是否初始化方法
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return this.isInit.get();
	}

	/**
	 * 对于某一个目的地消息的发送方法
	 * 
	 * @param destination
	 *            目的地
	 * @param message
	 *            消息
	 */
	public void post(@NonNull Destination destination, @NonNull Message message) {
		this.themesMapper.put(destination.getUniqueId(), message);
	}

	/**
	 * 对于某一个目的地消息的接收
	 * 
	 * @param destination
	 *            目的地
	 * @return
	 */
	public Message receive(@NonNull Destination destination) {
		Message content;
		synchronized (this.themesMapper) {
			content = this.themesMapper.get(destination.getUniqueId());
			this.themesMapper.remove(destination.getUniqueId());
		}
		return content;
	}

	/**
	 * 注册一个话题，并返回一个监听者
	 * 
	 * @param topic
	 *            话题
	 * @param filters
	 *            消息过滤器
	 * @return
	 */
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

	/**
	 * 把一个监听者和一个话题解绑
	 * 
	 * @param topic
	 *            话题
	 * @param listener
	 *            监听者
	 */
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

	/**
	 * 针对一个话题生成一个消息产生者
	 * 
	 * @param topic
	 *            话题
	 * @return
	 */
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

	private static class StorageUnit {
		public final Map<UUID, List<Theme<Message>>> themesMapper = new ConcurrentHashMap<>();
		public final Map<Theme<Message>, List<Filter>> filtersMapper = new ConcurrentHashMap<>();
	}
}
