package com.sxdsf.whoosh;

import java.util.List;

/**
 * com.sxdsf.whoosh.Theme
 *
 * @author 孙博闻
 * @date 2016/5/17 14:53
 * @desc 继承Listener实现Carrier
 */
class Theme<T> extends Listener<T> implements Carrier<T> {

    private ThemeOnListen<T> mTol;
    private final List<Filter> mFilters;

    /**
     * 获取当前监听者下的filters
     *
     * @return
     */
    List<Filter> getFilters() {
        return mFilters;
    }

    Theme(OnListen<T> onListen, ThemeOnListen<T> tol, List<Filter> filters) {
        super(onListen);
        mTol = tol;
        mFilters = filters;
    }

    @Override
    public void onReceive(T content) {
        mTol.mRawCarrier.onReceive(content);
    }

    /**
     * 创建一个Theme
     *
     * @param filters 过滤器的集合
     * @param <T>
     * @return
     */
    static <T> Theme<T> create(List<Filter> filters) {
        ThemeOnListen<T> tol = new ThemeOnListen<>();
        return new Theme<>(tol, tol, filters);
    }

    private static class ThemeOnListen<T> implements OnListen<T> {

        Carrier<? super T> mRawCarrier;

        @Override
        public void call(Carrier<? super T> carrier) {
            mRawCarrier = carrier;
        }
    }
}
