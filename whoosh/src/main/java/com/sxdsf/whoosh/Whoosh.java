package com.sxdsf.whoosh;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.sxdsf.whoosh.Whoosh
 *
 * @author 孙博闻
 * @date 2016/5/18 15:09
 * @desc 消息服务的主类
 */
public class Whoosh<T> {

    /**
     * 结果适配器
     */
    private final Adapter<T> mAdapter;
    /**
     * 话题和消息监听者的存储单元
     */
    public final StorageUnit<T> mStorageUnit;
    /**
     * 用于类似activity中传值所保存东西的地方
     */
    private final Map<UUID, Message> mThingsMapper = new ConcurrentHashMap<>();
    /**
     * 服务是否启动的标识
     */
    private final AtomicBoolean mIsInit = new AtomicBoolean(false);
    /**
     * 锁，用于保证一个线程拿到锁后，一次性把所有监听者遍历完
     */
    private final Lock mLock = new ReentrantLock(true);

    private Whoosh(Adapter<T> adapter) {
        mAdapter = adapter;
        mStorageUnit = new StorageUnit<>(mAdapter);
    }

    /**
     * 创建一个服务，使用传入的适配器
     *
     * @param adapter 适配器
     * @param <T>
     * @return
     */
    public static <T> Whoosh<T> create(Adapter<T> adapter) {
        return new Whoosh<>(adapter);
    }

    /**
     * 创建一个服务，使用默认的适配器
     *
     * @return
     */
    public static Whoosh<Listener<Message>> create() {
        return create(new Adapter.DefaultAdapter());
    }

    private static final String TAG = "Whoosh";

    /**
     * 初始化方法
     */
    public void initialize() {
        if (mIsInit.compareAndSet(false, true)) {
            Log.v(TAG, "Whoosh is initialized");
        }
    }

    /**
     * 判断是否初始化方法
     *
     * @return
     */
    public boolean isInitialized() {
        return mIsInit.get();
    }

    /**
     * 对于某一个目的地消息的发送方法
     *
     * @param destination 目的地
     * @param message     消息
     */
    public void post(@NonNull Destination destination, @NonNull Message message) {
        this.mThingsMapper.put(destination.getUniqueId(), message);
    }

    /**
     * 对于某一个目的地的接收，默认是获取后并移除
     *
     * @param destination 目的地
     * @return
     */
    public Message receive(@NonNull Destination destination) {
        return receive(destination, true);
    }

    /**
     * 对于某一个目的地消息的接收
     *
     * @param destination 目的地
     * @param remove      取走后是否移除
     * @return
     */
    public Message receive(@NonNull Destination destination, boolean remove) {
        Message content;
        synchronized (mThingsMapper) {
            content = mThingsMapper.get(destination.getUniqueId());
            if (remove) {
                mThingsMapper.remove(destination.getUniqueId());
            }
        }
        return content;
    }

    /**
     * 注册一个话题，并返回一个监听者
     *
     * @param topic   话题
     * @param filters 消息过滤器
     * @return
     */
    public T register(@NonNull Topic topic, Filter... filters) {
        List<Filter> filterList = null;
        if (filters != null && filters.length > 0) {
            filterList = Arrays.asList(filters);
        }
        Theme<Message> theme = Theme.create(filterList);
        mStorageUnit.add(topic, theme);
        return mAdapter.adapt(theme);
    }

    /**
     * 把一个监听者和一个话题解绑
     *
     * @param topic    话题
     * @param listener 监听者
     */
    public void unRegister(@NonNull Topic topic, T listener) {
        mStorageUnit.remove(topic, listener);
    }

    /**
     * 基于某一个话题，发送一个消息
     *
     * @param topic   话题
     * @param message 消息
     */
    public void send(@NonNull Topic topic, @NonNull final Message message) {
        mLock.lock();
        try {
            List<Theme<Message>> themes = mStorageUnit.get(topic);
            if (themes == null) {
                return;
            }
            for (final Theme<Message> theme : themes) {
                if (theme == null) {
                    continue;
                }
                boolean isThroughFilters = true;
                //如果是空消息，一个过滤器都不会走
                if (!message.mIsEmptyMessage) {
                    List<Filter> filters = theme.getFilters();
                    if (filters != null) {
                        for (Filter filter : filters) {
                            if (filter == null) {
                                continue;
                            }
                            isThroughFilters = isThroughFilters && filter.filter(message);
                        }
                    }
                }
                if (isThroughFilters) {
                    theme.onReceive(message);
                }
            }
        } finally {
            mLock.unlock();
        }
    }
}
