package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;

/**
 * com.sxdsf.whoosh.LogInterceptor
 *
 * @author 孙博闻
 * @date 2016/7/7 17:58
 * @desc 打印日志的拦截器
 */
public interface LogInterceptor {

    /**
     * 在监听前
     *
     * @param topic    关心的话题
     * @param listener 监听者
     */
    void preListen(Topic topic, Listener listener);

    /**
     * 在监听后
     *
     * @param topic    关心的话题
     * @param listener 监听者
     */
    void afterListen(Topic topic, Listener listener);

    /**
     * 在接收前
     *
     * @param topic    关心的话题
     * @param listener 监听者
     * @param message  消息
     */
    void preReceive(Topic topic, Listener listener, Message message);

    /**
     * 在解监听前
     *
     * @param topic    关心的话题
     * @param listener 监听者
     */
    void preUnListen(Topic topic, Listener listener);

    /**
     * 在解监听后
     *
     * @param topic    关心的话题
     * @param listener 监听者
     */
    void afterUnListen(Topic topic, Listener listener);
}
