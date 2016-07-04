package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;

import java.util.ArrayList;
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
class StorageUnit<T> {
    /**
     * 保存话题和监听者的map
     */
    private final Map<UUID, List<Theme<Message>>> mThemesMapper = new ConcurrentHashMap<>();
    /**
     * 当前的结果适配器
     */
    private final Adapter<T> mAdapter;
    /**
     * 读写话题和监听者map的锁
     */
    private final ReadWriteLock mRwl = new ReentrantReadWriteLock(true);

    StorageUnit(Adapter<T> adapter) {
        mAdapter = adapter;
    }

    /**
     * 添加一个监听者
     *
     * @param topic 要监听的话题
     * @param theme 监听者
     */
    void add(Topic topic, Theme<Message> theme) {
        if (topic == null || theme == null) {
            return;
        }
        mRwl.writeLock().lock();
        try {
            List<Theme<Message>> themes = mThemesMapper.get(topic.getUniqueId());
            if (themes == null) {
                themes = new ArrayList<>();
                mThemesMapper.put(topic.getUniqueId(), themes);
            }
            themes.add(theme);
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
    List<Theme<Message>> get(Topic topic) {
        List<Theme<Message>> themes = null;
        if (topic != null) {
            themes = mThemesMapper.get(topic.getUniqueId());
        }
        return themes;
    }

    /**
     * 删除某一个话题下的某个监听者
     *
     * @param topic    话题
     * @param listener 监听者
     */
    void remove(Topic topic, T listener) {
        if (topic == null || listener == null) {
            return;
        }
        mRwl.writeLock().lock();
        try {
            List<Theme<Message>> themes = mThemesMapper.get(topic.getUniqueId());
            if (themes != null) {
                themes.remove(Theme.class.cast(mAdapter.reverseAdapt(listener)));
                if (themes.isEmpty()) {
                    mThemesMapper.remove(topic.getUniqueId());
                }
            }
        } finally {
            mRwl.writeLock().unlock();
        }
    }
}
