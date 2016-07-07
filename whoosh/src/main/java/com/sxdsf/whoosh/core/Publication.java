package com.sxdsf.whoosh.core;

/**
 * com.sxdsf.whoosh.core.Publication
 *
 * @author 孙博闻
 * @date 2016/5/17 14:53
 * @desc 继承Publisher实现Carrier
 */
public abstract class Publication<T> extends Publisher<T> implements Carrier<T> {

    private PublicationOnPublish<T> mPop;

    protected Publication(OnPublish<T> onPublish, PublicationOnPublish<T> pop) {
        super(onPublish);
        mPop = pop;
    }

    @Override
    public void onReceive(T content) {
        mPop.mRawCarrier.onReceive(content);
    }

    public static class PublicationOnPublish<T> implements OnPublish<T> {

        Carrier<? super T> mRawCarrier;

        @Override
        public void call(Carrier<? super T> carrier) {
            mRawCarrier = carrier;
        }
    }
}
