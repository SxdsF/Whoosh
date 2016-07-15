package com.sxdsf.whoosh.adapter;

import com.sxdsf.whoosh.Adapter;
import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.info.Message;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * RxJavaAdapter
 *
 * @author sunbowen
 * @date 2016/5/18-0:33
 * @desc 针对于RxJava的转换
 */
public class RxJavaAdapter implements Adapter<Listener, Observable<Message>> {

    @Override
    public Observable<Message> adapt(Listener publisher) {
        return Observable.create(new CallOnSubscribe(publisher));
    }

    static final class CallOnSubscribe implements Observable.OnSubscribe<Message> {
        private final Listener mOriginalPublisher;

        CallOnSubscribe(Listener originalPublisher) {
            mOriginalPublisher = originalPublisher;
        }

        @Override
        public void call(final Subscriber<? super Message> subscriber) {
            synchronized (mOriginalPublisher) {
                RequestArbiter requestArbiter = new RequestArbiter(mOriginalPublisher);
                mOriginalPublisher.listen(new Carrier() {
                    @Override
                    public void onReceive(Message content) {
                        subscriber.onNext(content);
                    }
                });
                subscriber.add(requestArbiter);
            }
        }
    }

    static final class RequestArbiter implements Subscription {
        private final Listener mPublisher;

        RequestArbiter(Listener publisher) {
            mPublisher = publisher;
        }

        @Override
        public void unsubscribe() {
            mPublisher.unListen();
        }

        @Override
        public boolean isUnsubscribed() {
            return mPublisher.isUnListened();
        }
    }
}
