package com.sxdsf.whoosh;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.sxdsf.whoosh.core.Carrier;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.sxdsf.whoosh.MessageHandler
 *
 * @author 孙博闻
 * @date 2016/7/4 14:01
 * @desc 用于处理消息的handler，可以把消息发到不同的线程
 */
public class MessageHandler extends Handler {
    /**
     * handler里等待锁的时间，同时也是在一次handler里处理的最大时间
     */
    private static final int MAX_HANDLE_TIME = 10;
    /**
     * 内部实现的一个阻塞的消息队列
     */
    private final Queue<Temp> mQueue = new LinkedList<>();
    /**
     * 用于实现阻塞消息队列的锁
     */
    private final Lock mLock = new ReentrantLock(true);
    /**
     * handleMessage是否在运行的标识
     */
    private volatile boolean mIsRunning = false;

    private static final String TAG = "MessageHandler";

    MessageHandler(Looper looper) {
        super(looper);
    }

    /**
     * 返回handleMessage是否在运行
     *
     * @return
     */
    boolean isRunning() {
        return mIsRunning;
    }

    /**
     * 将一个消息入队，会阻塞的
     *
     * @param temp 要入队的
     */
    void enQueue(Temp temp) {
        mLock.lock();
        try {
            mQueue.offer(temp);
        } finally {
            mLock.unlock();
        }
    }

    /**
     * handleMessage从阻塞的队列里拿消息，默认会等待一个最大时长
     *
     * @return
     */
    private Temp poll() {
        Temp temp = null;
        try {
            if (mLock.tryLock(MAX_HANDLE_TIME, TimeUnit.MILLISECONDS)) {
                try {
                    temp = mQueue.poll();
                } finally {
                    mLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Log.v(TAG, e.getMessage());
        }
        return temp;
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        // TODO Auto-generated method stub
        //这里做的处理是，只要handleMessage一执行，就将标识位置为true，
        //并且循环的从阻塞队列里取消息出来执行，如果取出来的是null，
        //那就跳出这个循环，并将标识位置为false，如果handleMessage运行
        //超过了最大时长，就跳出，并发一个消息到looper以保证此handleMessage
        //继续执行
        mIsRunning = true;
        long startTime = SystemClock.uptimeMillis();
        while (true) {
            Temp temp = poll();
            if (temp == null) {
                mIsRunning = false;
                return;
            }
            if (temp.mCarrier == null || temp.mMessage == null) {
                continue;
            }
            temp.mCarrier.onReceive(temp.mMessage);
            if (SystemClock.uptimeMillis() - startTime >= MAX_HANDLE_TIME) {
                sendMessage(obtainMessage());
                mIsRunning = false;
                return;
            }
        }
    }

    public static class Temp<T> {
        public Carrier<T> mCarrier;
        public T mMessage;
    }
}
