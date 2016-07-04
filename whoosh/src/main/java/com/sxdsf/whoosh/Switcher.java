package com.sxdsf.whoosh;

/**
 * com.sxdsf.whoosh.Switcher
 *
 * @author 孙博闻
 * @date 2016/5/18 10:58
 * @desc 用于线程切换
 */
interface Switcher {

    /**
     * 切换方法
     *
     * @param carrier 被切换的消息承载者
     * @param <T>
     * @return
     */
    <T> Carrier<? super T> switches(Carrier<? super T> carrier);
}
