package com.sxdsf.whoosh.adapter;

import com.sxdsf.whoosh.Adapter;
import com.sxdsf.whoosh.Carrier;
import com.sxdsf.whoosh.Listener;
import com.sxdsf.whoosh.info.Message;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * RxJavaAdapter
 *
 * @author sunbowen
 * @date 2016/5/18-0:33
 * @desc 针对于RxJava的转换
 */
public class RxJavaAdapter implements Adapter<Observable<Message>> {

	@Override
	public Observable<Message> adapt(final Listener<Message> listener) {
		return Observable.create(new Observable.OnSubscribe<Message>() {
			@Override
			public void call(final Subscriber<? super Message> subscriber) {
				listener.listen(new Carrier<Message>() {
					@Override
					public void onReceive(Message content) {
						subscriber.onNext(content);
					}
				});
			}
		});
	}

	@Override
	public Listener<Message> reverseAdapt(final Observable<Message> messageObservable) {
		return Listener.create(new Listener.OnListen<Message>() {
			@Override
			public void call(final Carrier<? super Message> carrier) {
				messageObservable.subscribe(new Action1<Message>() {
					@Override
					public void call(Message message) {
						carrier.onReceive(message);
					}
				});
			}
		});
	}
}
