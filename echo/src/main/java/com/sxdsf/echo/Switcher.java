package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Switcher
 *
 * @author 孙博闻
 * @date 2016/7/12 15:06
 * @desc 用于线程切换
 */
public interface Switcher<T extends Voice> {

    /**
     * 切换方法
     *
     * @param receiver 被切换的接收者
     * @return
     */
    Receiver<T> switches(Receiver<T> receiver);
}
