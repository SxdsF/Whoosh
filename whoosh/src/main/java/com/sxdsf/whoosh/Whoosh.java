package com.sxdsf.whoosh;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sxdsf.whoosh.info.Destination;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;
import com.sxdsf.whoosh.info.WhooshTopic;

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
public class Whoosh {

    public final Topic WhooshException = new WhooshTopic("WhooshException");
    /**
     * 话题和消息监听者的存储单元
     */
    public final StorageUnit mStorageUnit = new StorageUnit();
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

    private Whoosh() {
    }

    /**
     * 创建一个服务，使用默认的适配器
     *
     * @return
     */
    public static Whoosh create() {
        return new Whoosh();
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
     * 将一个监听者注册到一个话题
     *
     * @param topic 话题
     * @return
     */
    void register(@NonNull Topic topic, @NonNull Listener listener) {
        mStorageUnit.add(topic, listener);
    }

    /**
     * 把一个监听者和一个话题解绑
     *
     * @param topic    话题
     * @param listener 监听者
     */
    void unRegister(@NonNull Topic topic, @NonNull Listener listener) {
        mStorageUnit.remove(topic, listener);
    }

    /**
     * 根据话题判断监听者是否已经注册
     *
     * @param topic    话题
     * @param listener 监听者
     * @return
     */
    boolean isRegistered(@NonNull Topic topic, @NonNull Listener listener) {
        return mStorageUnit.contains(topic, listener);
    }

    /**
     * 创建一个消息的生产者
     *
     * @param topic 话题
     * @return
     */
    public Producer createProducer(@NonNull Topic topic) {
        return new Producer(topic, mStorageUnit, mLock);
    }
}
