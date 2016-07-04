package com.sxdsf.whoosh.info;

import java.util.UUID;

/**
 * com.sxdsf.whoosh.info.WhooshDestination
 *
 * @author 孙博闻
 * @date 2015/12/17 0:32
 * @desc Whoosh服务实现的目的地类
 */
public class WhooshDestination implements Destination {

    /**
     * 名称
     */
    public final String mName;
    /**
     * 唯一Id
     */
    public final UUID mUuid;

    public WhooshDestination(String name) {
        mName = name;
        mUuid = UUID.randomUUID();
    }

    @Override
    public String getDestinationName() {
        return mName;
    }

    @Override
    public UUID getUniqueId() {
        return mUuid;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this == object) {
            return true;
        }

        if (!(object instanceof WhooshDestination)) {
            return false;
        }

        return mUuid.equals(((WhooshDestination) object).mUuid);
    }

}
