package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Cast
 *
 * @author 孙博闻
 * @date 2016/7/12 15:59
 * @desc 表明发声者和接收者的一种关系
 */
public interface Cast {

    /**
     * 是否在解除接收的状态
     *
     * @return
     */
    boolean isUnReceived();

    /**
     * 解除接收
     */
    void unReceive();
}
