package com.sxdsf.whoosh.info;

import android.support.annotation.NonNull;

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
    private final R mContent;
    /**
     * 此消息对应的话题
     */
    public Topic mTopic;

    WhooshMessage(R content) {
        super();
        mContent = content;
    }

    @Override
    public <T> T checkAndGet(Class<T> cls) {
        T t = null;
        if (cls != null && mContent != null && cls == mContent.getClass()) {
            t = cls.cast(mContent);
        }
        return t;
    }

    /**
     * 从Message拷贝出一个WhooshMessage
     *
     * @param topic   消息对应的话题
     * @param message 消息
     * @return
     */
    public static WhooshMessage copyFromMessage(@NonNull Topic topic, @NonNull Message message) {
        WhooshMessage whooshMessage = WhooshMessage.class.cast(message);
        whooshMessage.mTopic = topic;
        return whooshMessage;
    }
}
