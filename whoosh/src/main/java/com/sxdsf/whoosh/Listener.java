package com.sxdsf.whoosh;

/**
 * com.sxdsf.whoosh.Listener
 *
 * @author 孙博闻
 * @date 2016/7/1 10:18
 * @desc 消息的监听者
 */
public class Listener<T> {

    final OnListen<T> mOnListen;

    /**
     * 当前listener执行的线程，默认是发送者线程
     */
    ThreadMode mThreadMode = ThreadMode.POSTING;

    protected Listener(OnListen<T> onListen) {
        this.mOnListen = onListen;
    }

    public static <T> Listener<T> create(OnListen<T> onListen) {
        return new Listener<>(onListen);
    }

    /**
     * 监听者的监听方法
     *
     * @param carrier 消息的载体
     */
    public void listen(Carrier<T> carrier) {
        mOnListen.call(carrier);
    }

    /**
     * 监听者在哪个线程监听
     *
     * @param threadMode 线程模式
     * @return
     */
    public Listener<T> listenOn(ThreadMode threadMode) {
        mThreadMode = threadMode;
        Switcher switcher;
        switch (threadMode) {
            case MAIN:
                switcher = Switchers.mainThread();
                break;
            case BACKGROUND:
                switcher = Switchers.backgroundThread();
                break;
            case ASYNC:
                switcher = Switchers.asyncThread();
                break;
            case POSTING:
            default:
                switcher = Switchers.postingThread();
                break;
        }
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
            return this.mSwitcher.switches(carrier);
        }
    }

    private static class OnListenLift<T> implements Listener.OnListen<T> {

        private Listener.OnListen<T> mParent;
        private Listener.Alter<T> mAlter;

        public OnListenLift(Listener.OnListen<T> parent, Listener.Alter<T> alter) {
            mParent = parent;
            mAlter = alter;
        }

        @Override
        public void call(Carrier<? super T> rCarrier) {
            Carrier<? super T> c = mAlter.call(rCarrier);
            mParent.call(c);
        }
    }
}
