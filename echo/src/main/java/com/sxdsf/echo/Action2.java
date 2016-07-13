package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Action2
 *
 * @author 孙博闻
 * @date 2016/7/12 14:52
 * @desc 有两个泛型的调用
 */
public interface Action2<T, R> {

    R call(T t);
}
