package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;

/**
 * Filter
 *
 * @author sunbowen
 * @date 2016/5/18-15:11
 * @desc 消息的过滤类
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
