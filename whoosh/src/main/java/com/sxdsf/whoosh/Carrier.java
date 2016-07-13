package com.sxdsf.whoosh;

import com.sxdsf.echo.Receiver;
import com.sxdsf.whoosh.info.Message;

/**
 * com.sxdsf.whoosh.Carrier
 *
 * @author sunbowen
 * @date 2016/5/16-17:07
 * @desc 内容的载体
 */
public interface Carrier extends Receiver<Message> {

    /**
     * 有消息时回调此方法
     *
     * @param content 消息
     */
    void onReceive(Message content);
}
