package com.sxdsf.whoosh.info;

/**
 * com.sxdsf.whoosh.info.Information
 *
 * @author 孙博闻
 * @date 2016/5/16 15:32
 * @desc 信息接口
 */
interface Information {

    /**
     * 检查类型并返回相应信息
     *
     * @param cls 检查的类型
     * @param <T>
     * @return
     */
    <T> T checkAndGet(Class<T> cls);
}
