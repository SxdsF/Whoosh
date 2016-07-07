package com.sxdsf.whoosh.core;

/**
 * com.sxdsf.whoosh.core.Carrier
 *
 * @author sunbowen
 * @date 2016/5/16-17:07
 * @desc 内容的载体
 */
public interface Carrier<T> {

    /**
     * 有消息时回调此方法
     *
     * @param content 消息
     */
    void onReceive(T content);
}
