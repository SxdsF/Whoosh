package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Alter
 *
 * @author 孙博闻
 * @date 2016/7/12 15:18
 * @desc 用于接收者的变换
 */
public interface Alter<T extends Voice> extends Action2<Receiver<T>, Receiver<T>> {
}
