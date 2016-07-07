package com.sxdsf.whoosh;

import com.sxdsf.whoosh.core.Filter;
import com.sxdsf.whoosh.info.Message;

/**
 * com.sxdsf.whoosh.Filters
 *
 * @author 孙博闻
 * @date 2016/7/1 10:18
 * @desc 消息的过滤器的集合，里面是系统提供的几个过滤器
 */
public class Filters {

    /**
     * 是否消费过的过滤器
     */
    private final Filter isConsumed;

    private static final Filters INSTANCE = new Filters();

    private Filters() {
        isConsumed = new IsConsumedFilter();
    }

    /**
     * 返回一个针对消息体类型的过滤器
     *
     * @param cls 用于适配比对的类型
     * @return
     */
    public static Filter klass(Class<?> cls) {
        return new ClassFilter(cls);
    }

    /**
     * 返回一个是不是消费过的过滤器
     *
     * @return
     */
    public static Filter isConsumed() {
        return INSTANCE.isConsumed;
    }

    /**
     * 返回一个消息Id的过滤器
     *
     * @param id 要比对的Id
     * @return
     */
    public static Filter messageId(int id) {
        return new MessageIdFilter(id);
    }

    /**
     * 消息体类型的过滤器
     */
    private static class ClassFilter implements Filter {

        private final Class<?> mCls;

        public ClassFilter(Class<?> cls) {
            mCls = cls;
        }

        @Override
        public boolean filter(Message message) {
            boolean result = false;
            if (message != null) {
                if (message.checkAndGet(mCls) != null || message.isEmptyMessage()) {
                    result = true;
                }
            }
            return result;
        }
    }

    /**
     * 是否消费过的过滤器
     */
    private static class IsConsumedFilter implements Filter {

        @Override
        public boolean filter(Message message) {
            boolean result = false;
            if (message != null) {
                result = !message.isConsumed();
            }
            return result;
        }
    }

    /**
     * 消息Id的过滤器
     */
    private static class MessageIdFilter implements Filter {

        private final int mId;

        public MessageIdFilter(int id) {
            mId = id;
        }

        @Override
        public boolean filter(Message message) {
            boolean result = false;
            if (message != null) {
                if (mId == message.getMessageId()) {
                    result = true;
                }
            }
            return result;
        }
    }
}
