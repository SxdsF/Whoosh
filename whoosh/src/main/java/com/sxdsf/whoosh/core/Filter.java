package com.sxdsf.whoosh.core;

import com.sxdsf.whoosh.info.Message;

/**
 * com.sxdsf.whoosh.core.Filter
 *
 * @author 孙博闻
 * @date 2016/7/1 10:18
 * @desc 消息的过滤器
 */
public interface Filter {

    /**
     * 判断是否要过滤此消息
     *
     * @param message 要过滤的消息
     * @return
     */
    boolean filter(Message message);
}
