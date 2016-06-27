package com.sxdsf.whoosh;

/**
 * Switchers
 *
 * @author sunbowen
 * @date 2016/5/18-13:09
 * @desc Switcher的集合
 */
public class Switchers {

    private Switcher MAIN_THREAD;

    private static final Switchers INSTANCE = new Switchers();

    private Switchers() {
        MAIN_THREAD = new Switcher();
    }

    public static Switcher mainThread() {
        return INSTANCE.MAIN_THREAD;
    }
}
