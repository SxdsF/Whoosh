package com.sxdsf.whoosh.info;

/**
 * Information
 *
 * @author sunbowen
 * @date 2016/5/16-15:32
 * @desc 信息接口
 */
interface Information {

    <T> T checkAndGet(Class<T> cls);
}
