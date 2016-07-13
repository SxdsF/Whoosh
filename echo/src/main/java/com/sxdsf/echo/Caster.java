package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Caster
 *
 * @author 孙博闻
 * @date 2016/7/12 13:58
 * @desc 发声者
 */
public class Caster<T extends Voice> {

    protected OnCast<T> mOnCast;

    /**
     * 发送方法
     *
     * @param receiver 接收者
     */
    public void cast(Receiver<T> receiver) {
        mOnCast.call(receiver);
    }

    /**
     * 指定接收者的线程
     *
     * @param switcher 执行的线程切换
     * @return
     */
    public Caster<T> receiveOn(Switcher<T> switcher) {
        mOnCast = new OnReceiveSwitch<>(mOnCast, new SwitchAlter<>(switcher));
        return this;
    }

    /**
     * 转换方法
     *
     * @param converter 执行转换方法的转化者
     * @param <R>
     * @return
     */
    public <R extends Voice> Caster<R> convert(Converter<T, R> converter) {
        return converter.call(this);
    }

    /**
     * 类型转换方法
     *
     * @param cls 要转换的类型
     * @param <K>
     * @return
     */
    public <K extends Caster<? extends Voice>> K classCast(Class<K> cls) {
        return cls.cast(this);
    }

    protected Caster(OnCast<T> onCast) {
        mOnCast = onCast;
    }

    public static <T extends Voice> Caster create(OnCast<T> onCast) {
        return new Caster<>(onCast);
    }

    private static class SwitchAlter<T extends Voice> implements Alter<T> {

        private Switcher<T> mSwitcher;

        public SwitchAlter(Switcher<T> switcher) {
            mSwitcher = switcher;
        }

        @Override
        public Receiver<T> call(Receiver<T> tReceiver) {
            return mSwitcher.switches(tReceiver);
        }
    }

    private static class OnReceiveSwitch<T extends Voice> implements OnCast<T> {

        private OnCast<T> mParent;
        private Alter<T> mAlter;

        public OnReceiveSwitch(OnCast<T> parent, Alter<T> alter) {
            mParent = parent;
            mAlter = alter;
        }

        @Override
        public void call(Receiver<T> tReceiver) {
            mParent.call(mAlter.call(tReceiver));
        }
    }
}
