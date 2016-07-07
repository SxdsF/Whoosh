package com.sxdsf.whoosh.info;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * com.sxdsf.whoosh.info.WhooshMessage
 *
 * @author 孙博闻
 * @date 2015/12/17 0:41
 * @desc Whoosh服务实现的消息类
 */
public class WhooshMessage<R> extends Message {

    /**
     * 内容
     */
    private R mContent;
    //    /**
    //     * 此消息对应的话题
    //     */
    //    public Topic mTopic;
    /**
     * 标识content是否被修改过
     */
    private boolean mIsAltered;
    /**
     * 前几次的值
     */
    private List<R> mPreContentList;
    /**
     * 保护此消息内容的读写锁
     */
    private ReadWriteLock mRwl = new ReentrantReadWriteLock(true);

    WhooshMessage(R content) {
        super();
        mContent = content;
    }

    /**
     * 改变内容的方法
     *
     * @param content 要改变的内容
     */
    public void alterContent(R content) {
        mRwl.writeLock().lock();
        try {
            if (mPreContentList == null) {
                mPreContentList = new LinkedList<>();
            }
            mPreContentList.add(mContent);
            mContent = content;
            mIsAltered = true;
        } finally {
            mRwl.writeLock().unlock();
        }

    }

    /**
     * 判断内容是否被修改过
     *
     * @return
     */
    public boolean isAltered() {
        boolean result = false;
        mRwl.readLock().lock();
        try {
            result = mIsAltered;
        } finally {
            mRwl.readLock().unlock();
        }
        return result;
    }

    /**
     * 获取前面修改值的list
     *
     * @return
     */
    public List<R> getPreContentList() {
        List<R> list = null;
        mRwl.readLock().lock();
        try {
            list = mPreContentList;
        } finally {
            mRwl.readLock().unlock();
        }
        return list;
    }

    @Override
    public <T> T checkAndGet(Class<T> cls) {
        T t = null;
        mRwl.readLock().lock();
        try {
            if (cls != null && mContent != null && cls == mContent.getClass()) {
                t = cls.cast(mContent);
            }
        } finally {
            mRwl.readLock().unlock();
        }
        return t;
    }

    //    /**
    //     * 从Message拷贝出一个WhooshMessage
    //     *
    //     * @param topic   消息对应的话题
    //     * @param message 消息
    //     * @return
    //     */
    //    public static WhooshMessage copyFromMessage(@NonNull Topic topic, @NonNull Message message) {
    //        WhooshMessage whooshMessage = WhooshMessage.class.cast(message);
    //        whooshMessage.mTopic = topic;
    //        return whooshMessage;
    //    }

    /**
     * 将一个message转换为whooshMessage
     *
     * @param message 消息
     * @return
     */
    public static WhooshMessage convert(@NonNull Message message) {
        return WhooshMessage.class.cast(message);
    }
}
