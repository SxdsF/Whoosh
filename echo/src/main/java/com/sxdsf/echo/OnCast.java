package com.sxdsf.echo;

/**
 * com.sxdsf.echo.OnCast
 *
 * @author 孙博闻
 * @date 2016/7/12 14:53
 * @desc 当发送的时候调用接口
 */
public interface OnCast<T extends Voice> extends Action1<Receiver<T>> {
}
