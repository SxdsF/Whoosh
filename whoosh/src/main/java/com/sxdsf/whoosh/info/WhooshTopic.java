package com.sxdsf.whoosh.info;

/**
 * Created by sunbowen on 2015/12/17.
 */
public class WhooshTopic extends WhooshDestination implements Topic, Comparable<WhooshTopic> {

    public WhooshTopic(String physicalName) {
        super(physicalName);
    }

    @Override
    public String getTopicName() {
        return this.getPhysicalName();
    }

    @Override
    public String toString() {
        return this.getTopicName();
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

        return this.getUuid().equals(((WhooshTopic) object).getUuid());
    }

    @Override
    public int compareTo(WhooshTopic another) {
        if (another == this) {
            return 0;
        }

        return this.getUuid().compareTo(another.getUuid());
    }
}
