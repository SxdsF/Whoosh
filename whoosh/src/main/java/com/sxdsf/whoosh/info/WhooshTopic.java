package com.sxdsf.whoosh.info;

/**
 * com.sxdsf.whoosh.info.WhooshTopic
 *
 * @author 孙博闻
 * @date 2015/12/17 0:38
 * @desc Whoosh服务实现的话题类
 */
public class WhooshTopic extends WhooshDestination implements Topic {

    public WhooshTopic(String name) {
        super(name);
    }

    @Override
    public String getTopicName() {
        return mName;
    }

    @Override
    public String toString() {
        return getTopicName();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this == object) {
            return true;
        }

        if (!(object instanceof WhooshTopic)) {
            return false;
        }

        return mUuid.equals(((WhooshTopic) object).mUuid);
    }
}
