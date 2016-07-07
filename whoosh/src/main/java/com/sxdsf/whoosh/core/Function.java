package com.sxdsf.whoosh.core;

/**
 * com.sxdsf.whoosh.core.Function
 *
 * @author 孙博闻
 * @date 2016/7/7 17:42
 * @desc 功能性接口
 */
public interface Function<T, R> {
    R call(T t);
}
