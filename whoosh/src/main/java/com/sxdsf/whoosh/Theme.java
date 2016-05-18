package com.sxdsf.whoosh;

/**
 * Theme
 *
 * @author sunbowen
 * @date 2016/5/17-14:53
 * @desc 继承Listener实现Carrier
 */
public class Theme<T> extends Listener<T> implements Carrier<T> {

	private ThemeOnListen<T> mTol;

	protected Theme(OnListen<T> onListen, ThemeOnListen<T> tol) {
		super(onListen);
		mTol = tol;
	}

	@Override
	public void onReceive(T content) {
		mTol.mRawCarrier.onReceive(content);
	}

	public static <T> Theme<T> create() {
		ThemeOnListen<T> tol = new ThemeOnListen<>();
		return new Theme<>(tol, tol);
	}

	private static class ThemeOnListen<T> implements OnListen<T> {

		Carrier<? super T> mRawCarrier;

		@Override
		public void call(Carrier<? super T> carrier) {
			mRawCarrier = carrier;
		}
	}
}
