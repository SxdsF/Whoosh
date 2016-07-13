package com.sxdsf.whoosh;

import android.support.annotation.NonNull;

import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * com.sxdsf.whoosh.Producer
 *
 * @author 孙博闻
 * @date 2016/7/8 11:31
 * @desc 消息的生产者
 */
public class Producer {

    /**
     * 当前发送者要发送消息的话题
     */
    private final Topic mTopic;
    /**
     * 存储监听者的存储单元
     */
    private final StorageUnit mStorageUnit;
    /**
     * 锁
     */
    private final Lock mLock;

    /**
     * 接收回复的消息
     */
    private Carrier mReplyMessageCarrier;

    Producer(Topic topic, StorageUnit storageUnit, Lock lock) {
        mTopic = topic;
        mStorageUnit = storageUnit;
        mLock = lock;
    }

    /**
     * 设置接收回复消息的消息承载者
     *
     * @param carrier 消息承载者
     * @return
     */
    public Producer setReply(Carrier carrier) {
        mReplyMessageCarrier = carrier;
        return this;
    }

    /**
     * 回复消息
     *
     * @param message 消息
     */
    public void reply(@NonNull Message message) {
        if (!message.isSent()) {
            if (mReplyMessageCarrier != null) {
                mReplyMessageCarrier.onReceive(message);
            }
        }
    }

    /**
     * 发送一个消息
     *
     * @param message 消息
     */
    public void send(@NonNull Message message) {
        //如果没有被发送过才发送
        if (!message.isSent()) {
            message.setProducer(this);
            mLock.lock();
            try {
                List<Listener> publications = mStorageUnit.get(mTopic);
                if (publications == null) {
                    return;
                }
                for (final Listener publication : publications) {
                    //如果消息没有被废弃就不再派发了
                    if (message.isAbandoned()) {
                        break;
                    }
                    if (publication == null) {
                        continue;
                    }
                    boolean isThroughFilters = true;
                    List<Filter> filters = publication.mFilters;
                    if (filters != null) {
                        for (Filter filter : filters) {
                            if (filter == null) {
                                continue;
                            }
                            isThroughFilters = isThroughFilters && filter.filter(message);
                        }
                    }
                    if (isThroughFilters) {
                        publication.onReceive(message);
                        //只要调用过onReceive就把消息设置为已经消费过
                        message.setIsConsumed(true);
                    }
                }
            } finally {
                mLock.unlock();
            }
        }
    }
}
