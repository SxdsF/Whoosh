package com.sxdsf.whoosh;

/**
 * com.sxdsf.whoosh.ListenerShip
 *
 * @author 孙博闻
 * @date 2016/7/6 23:32
 * @desc 监听者监听后返回的接口
 */
public interface ListenerShip {

    /**
     * 取消监听
     */
    void unListen();

    /**
     * 是否没有监听
     *
     * @return
     */
    boolean isUnListened();
}
