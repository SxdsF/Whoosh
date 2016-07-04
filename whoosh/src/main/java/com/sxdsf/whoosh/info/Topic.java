package com.sxdsf.whoosh.info;

/**
 * com.sxdsf.whoosh.info.Topic
 *
 * @author 孙博闻
 * @date 2015/12/17 23:49
 * @desc 话题接口，继承自目的地接口
 */
public interface Topic extends Destination {

    /**
     * 返回话题的名称
     *
     * @return
     */
    String getTopicName();
}
