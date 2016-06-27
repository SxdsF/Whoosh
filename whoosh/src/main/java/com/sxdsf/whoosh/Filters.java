package com.sxdsf.whoosh;

import com.sxdsf.whoosh.info.Message;

/**
 * Filters
 *
 * @author sunbowen
 * @date 2016/5/18-15:26
 * @desc Filter的集合
 */
public class Filters {

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
    static class ClassFilter implements Filter {

        private final Class<?> cls;

        public ClassFilter(Class<?> cls) {
            this.cls = cls;
        }

        @Override
        public boolean filter(Message message) {
            boolean result = false;
            if (message != null) {
                if (message.checkAndGet(this.cls) != null) {
                    result = true;
                }
            }
            return result;
        }
    }

    /**
     * 是否消费过的过滤器
     */
    static class IsConsumedFilter implements Filter {

        @Override
        public boolean filter(Message message) {
            boolean result = false;
            if (message != null) {
                result = !message.isConsumed;
            }
            return result;
        }
    }

    /**
     * 消息Id的过滤器
     */
    static class MessageIdFilter implements Filter {

        private final int mId;

        public MessageIdFilter(int id) {
            this.mId = id;
        }

        @Override
        public boolean filter(Message message) {
            boolean result = false;
            if (message != null) {
                if (this.mId == message.messageId) {
                    result = true;
                }
            }
            return result;
        }
    }
}
