package com.sxdsf.whoosh;

/**
 * com.sxdsf.whoosh.ThreadMode
 *
 * @author 孙博闻
 * @date 2016/6/27 20:40
 * @desc 线程的模式
 */
public enum ThreadMode {
    /**
     * 主线程
     */
    MAIN,
    /**
     * 发送者线程
     */
    POSTING,
    /**
     * 后台线程，如果发送者线程是主线程就会启动一个后台线程来运行，如果发送者线程不是主线程，就会在发送者线程执行
     */
    BACKGROUND,
    /**
     * 异步线程，会找一个既不是发送者线程，也不是主线程的线程来执行
     */
    ASYNC
}
