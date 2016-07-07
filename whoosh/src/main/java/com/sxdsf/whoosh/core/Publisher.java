package com.sxdsf.whoosh.core;

/**
 * com.sxdsf.whoosh.core.Publisher
 *
 * @author 孙博闻
 * @date 2016/7/1 10:18
 * @desc 内容的发布者
 */
public abstract class Publisher<T> {

    /**
     * 当前publisher的触发器
     */
    protected OnPublish<T> mOnPublish;

    Publisher(OnPublish<T> onPublish) {
        this.mOnPublish = onPublish;
    }

    /**
     * 发布方法，将内容发送到载体上
     *
     * @param carrier 内容的载体
     */
    protected void publish(Carrier<T> carrier) {
        mOnPublish.call(carrier);
    }

    public interface OnPublish<T> extends Action<Carrier<? super T>> {
    }

    public interface Alter<T> extends Function<Carrier<? super T>, Carrier<? super T>> {
    }
}
