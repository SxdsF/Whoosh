package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Topic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * com.sxdsf.whoosh.StorageUnit
 *
 * @author 孙博闻
 * @date 2016/7/1 10:18
 * @desc 存储单元
 */
class StorageUnit {
    /**
     * 保存话题和监听者的map
     */
    private final Map<UUID, List<Listener>> mListenersMapper = new ConcurrentHashMap<>();
    /**
     * 读写话题和监听者map的锁
     */
    private final ReadWriteLock mRwl = new ReentrantReadWriteLock(true);

    StorageUnit() {
    }

    /**
     * 添加一个监听者
     *
     * @param topic       要监听的话题
     * @param publication 监听者
     */
    void add(Topic topic, Listener publication) {
        if (topic == null || publication == null) {
            return;
        }
        mRwl.writeLock().lock();
        try {
            List<Listener> publications = mListenersMapper.get(topic.getUniqueId());
            if (publications == null) {
                publications = new LinkedList<>();
                mListenersMapper.put(topic.getUniqueId(), publications);
            }
            publications.add(publication);
            //优先级处理
            Collections.sort(publications);
        } finally {
            mRwl.writeLock().unlock();
        }
    }

    /**
     * 返回话题下的所有监听者
     *
     * @param topic 话题
     * @return
     */
    List<Listener> get(Topic topic) {
        List<Listener> publications = null;
        if (topic != null) {
            publications = mListenersMapper.get(topic.getUniqueId());
        }
        return publications;
    }

    /**
     * 删除某一个话题下的某个监听者
     *
     * @param topic    话题
     * @param listener 监听者
     */
    void remove(Topic topic, Listener listener) {
        if (topic == null || listener == null) {
            return;
        }
        mRwl.writeLock().lock();
        try {
            List<Listener> publications = mListenersMapper.get(topic.getUniqueId());
            if (publications != null) {
                publications.remove(listener);
                if (publications.isEmpty()) {
                    mListenersMapper.remove(topic.getUniqueId());
                }
            }
        } finally {
            mRwl.writeLock().unlock();
        }
    }

    /**
     * 检查listener是否已经注册过
     *
     * @param topic
     * @param listener
     * @return
     */
    boolean contains(Topic topic, Listener listener) {
        boolean result = false;
        mRwl.readLock().lock();
        try {
            List<Listener> publications = mListenersMapper.get(topic.getUniqueId());
            if (publications != null) {
                result = publications.contains(listener);
            }
        } finally {
            mRwl.readLock().unlock();
        }
        return result;
    }
}
