package com.sxdsf.whoosh;

/**
 * com.sxdsf.whoosh.Adapter
 *
 * @author 孙博闻
 * @date 2016/7/1 10:18
 * @desc 返回结果适配类
 */
public interface Adapter<T, R extends Listener> {

    /**
     * 把系统默认的适配成T
     *
     * @param listener 系统默认的监听者
     * @return
     */
    T adapt(R listener);
}
