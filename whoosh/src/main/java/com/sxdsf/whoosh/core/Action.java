package com.sxdsf.whoosh.core;

/**
 * com.sxdsf.whoosh.core.Action
 *
 * @author 孙博闻
 * @date 2016/7/7 17:43
 * @desc 功能性接口
 */
interface Action<T> {
    void call(T t);
}
