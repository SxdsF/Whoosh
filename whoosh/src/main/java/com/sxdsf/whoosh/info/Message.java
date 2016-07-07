package com.sxdsf.whoosh.info;

import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger mMessageId = new AtomicInteger();
    /**
     * 时间戳
     */
    public final long mTimestamp;
    /**
     * 优先级
     */
    private final AtomicInteger mPriority = new AtomicInteger(NORM_PRIORITY);
    /**
     * 是否是空消息
     */
    private final AtomicBoolean mIsEmptyMessage = new AtomicBoolean(false);
    /**
     * 是否被发送过
     */
    private final AtomicBoolean mIsSent = new AtomicBoolean(false);
    /**
     * 是否被消费
     */
    private final AtomicBoolean mIsConsumed = new AtomicBoolean(false);

    protected Message() {
        this.mTimestamp = System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NonNull Message another) {
        int result = 0;
        if (mPriority.get() < another.mPriority.get()) {
            result = -1;
        } else if (mPriority.get() > another.mPriority.get()) {
            result = 1;
        }
        return result;
    }

    /**
     * 获取消息Id
     *
     * @return
     */
    public int getMessageId() {
        return mMessageId.get();
    }

    /**
     * 设置消息Id
     *
     * @param messageId 消息Id
     */
    public void setMessageId(int messageId) {
        mMessageId.set(messageId);
    }

    /**
     * 获取消息优先级
     *
     * @return
     */
    public int getPriority() {
        return mPriority.get();
    }

    /**
     * 设置消息优先级
     *
     * @param priority
     */
    public void setPriority(int priority) {
        mPriority.set(priority);
    }

    /**
     * 判断是否是空消息
     *
     * @return
     */
    public boolean isEmptyMessage() {
        return mIsEmptyMessage.get();
    }

    /**
     * 判断是否被消费过
     *
     * @return
     */
    public boolean isConsumed() {
        return mIsConsumed.get();
    }

    /**
     * 设置是否被消费过得标记
     *
     * @param isConsumed 是否被消费过
     */
    public void setIsConsumed(boolean isConsumed) {
        mIsConsumed.set(isConsumed);
    }

    /**
     * 判断此消息是否被发送过，如果没有被发送过就会立马被设置为被发送过
     *
     * @return
     */
    public boolean isSent() {
        return mIsSent.getAndSet(true);
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
        message.mIsEmptyMessage.set(true);
        return message;
    }
}
