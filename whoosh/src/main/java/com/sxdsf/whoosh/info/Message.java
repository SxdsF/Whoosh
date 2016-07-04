package com.sxdsf.whoosh.info;

import android.support.annotation.NonNull;

/**
 * com.sxdsf.whoosh.info.Message
 *
 * @author 孙博闻
 * @date 2015/12/17 23:41
 * @desc 消息类，继承自信息接口
 */
public abstract class Message implements Information, Comparable<Message> {

    /**
     * 最小优先级
     */
    public final static int MIN_PRIORITY = 1;
    /**
     * 正常优先级
     */
    public final static int NORM_PRIORITY = 5;
    /**
     * 最高优先级
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * 消息id
     */
    public int mMessageId;
    /**
     * 时间戳
     */
    public final long mTimestamp;
    /**
     * 优先级
     */
    public int mPriority = NORM_PRIORITY;
    /**
     * 是否是空消息
     */
    public boolean mIsEmptyMessage;
    /**
     * 是否被消费
     */
    public boolean mIsConsumed;

    protected Message() {
        this.mTimestamp = System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NonNull Message another) {
        int result = 0;
        if (mPriority < another.mPriority) {
            result = -1;
        } else if (mPriority > another.mPriority) {
            result = 1;
        }
        return result;
    }

    /**
     * 创建一个消息
     *
     * @param content 消息的内容
     * @param <T>
     * @return
     */
    public static <T> Message create(@NonNull T content) {
        return new WhooshMessage<>(content);
    }

    /**
     * 创建一个空消息，内容为null
     *
     * @return
     */
    public static Message createEmptyMessage() {
        Message message = new WhooshMessage<>(null);
        message.mIsEmptyMessage = true;
        return message;
    }
}
