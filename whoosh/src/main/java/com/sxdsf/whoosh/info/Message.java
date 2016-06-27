package com.sxdsf.whoosh.info;

import android.support.annotation.NonNull;

/**
 * Created by sunbowen on 2015/12/17.
 */
public abstract class Message implements Information, Comparable<Message> {

    /**
     * 消息id
     */
    public int messageId;
    /**
     * 时间戳
     */
    public final long timestamp;
    /**
     * 优先级
     */
    public int priority = NORM_PRIORITY;
    /**
     * 是否是空消息
     */
    public boolean isEmptyMessage;
    /**
     * 是否被消费
     */
    public boolean isConsumed;

    public final static int MIN_PRIORITY = 1;
    public final static int NORM_PRIORITY = 5;
    public final static int MAX_PRIORITY = 10;

    protected Message() {
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public int compareTo(Message another) {
        int result = 0;
        if (another != null) {
            if (this.priority < another.priority) {
                result = -1;
            } else if (this.priority > another.priority) {
                result = 1;
            }
        }
        return result;
    }

    public static <T> Message create(@NonNull T content) {
        return new WhooshMessage<>(content);
    }

    public static Message createEmptyMessage() {
        Message message = new WhooshMessage<>(null);
        message.isEmptyMessage = true;
        return message;
    }
}
