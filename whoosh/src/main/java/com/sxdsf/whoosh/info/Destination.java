package com.sxdsf.whoosh.info;

import java.util.UUID;

/**
 * com.sxdsf.whoosh.info.Destination
 *
 * @author 孙博闻
 * @date 2015/12/17 23:39
 * @desc 目的地接口
 */
public interface Destination {

    /**
     * 返回目的地的名称
     *
     * @return
     */
    String getDestinationName();

    /**
     * 返回目的地的唯一Id
     *
     * @return
     */
    UUID getUniqueId();
}
