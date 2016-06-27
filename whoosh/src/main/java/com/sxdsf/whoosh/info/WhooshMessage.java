package com.sxdsf.whoosh.info;

import android.support.annotation.NonNull;

/**
 * Created by sunbowen on 2015/12/17.
 */
public class WhooshMessage<R> extends Message {

    /**
     * 内容
     */
    private R content;
    public Topic topic;

    public WhooshMessage(R content) {
        this(content, null);
    }

    public WhooshMessage(R content, Topic topic) {
        super();
        this.content = content;
        this.topic = topic;
    }

    @Override
    public <T> T checkAndGet(Class<T> cls) {
        T t = null;
        if (cls != null && this.content != null && cls == this.content.getClass()) {
            t = cls.cast(this.content);
        }
        return t;
    }

    public static WhooshMessage copyFromMessage(@NonNull Message message) {
        return WhooshMessage.class.cast(message);
    }
}
