package com.sxdsf.whoosh;

/**
 * Listener
 *
 * @author sunbowen
 * @date 2016/5/16-17:05
 * @desc 消息的监听者
 */
public class Listener<T> {

	final OnListen<T> mOnListen;

	protected Listener(OnListen<T> onListen) {
		this.mOnListen = onListen;
	}

	public static <T> Listener<T> create(OnListen<T> onListen) {
		return new Listener<>(onListen);
	}

	/**
	 * 监听者的监听方法
	 * 
	 * @param carrier
	 *            消息的载体
	 */
	public void listen(Carrier<T> carrier) {
		mOnListen.call(carrier);
	}

	public static <T> Listener<T> from(final T t) {
		return Listener.create(new OnListen<T>() {
			@Override
			public void call(Carrier<? super T> carrier) {
				carrier.onReceive(t);
			}
		});
	}

	public Listener<T> listenOn(Switcher switcher) {
		return new Listener<>(new OnListenLift<>(mOnListen, new SwitchAlter<T>(switcher)));
	}

	public interface OnListen<T> extends Action<Carrier<? super T>> {
	}

	interface Action<R> {
		void call(R t);
	}

	public interface Alter<T> extends Function<Carrier<? super T>, Carrier<? super T>> {
	}

	interface Function<K, V> {
		V call(K t);
	}

	private static class SwitchAlter<T> implements Listener.Alter<T> {

		private Switcher mSwitcher;

		public SwitchAlter(Switcher switcher) {
			mSwitcher = switcher;
		}

		@Override
		public Carrier<? super T> call(Carrier<? super T> carrier) {
			return this.mSwitcher.switchIt(carrier);
		}
	}

	private static class OnListenLift<T> implements Listener.OnListen<T> {

		private Listener.OnListen<T> parent;

		private Listener.Alter<T> alter;

		public OnListenLift(Listener.OnListen<T> parent, Listener.Alter<T> alter) {
			this.parent = parent;
			this.alter = alter;
		}

		@Override
		public void call(Carrier<? super T> rCarrier) {
			Carrier<? super T> c = this.alter.call(rCarrier);
			this.parent.call(c);
		}
	}
}
