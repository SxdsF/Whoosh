package com.sxdsf.whoosh.core;

/**
 * com.sxdsf.whoosh.core.Switcher
 *
 * @author 孙博闻
 * @date 2016/5/18 10:58
 * @desc 用于线程切换
 */
public interface Switcher {

    /**
     * 切换方法
     *
     * @param carrier 被切换的内容载体
     * @return
     */
    <T> Carrier<? super T> switches(Carrier<? super T> carrier);
}
