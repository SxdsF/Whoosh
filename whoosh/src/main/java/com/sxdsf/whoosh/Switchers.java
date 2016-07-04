package com.sxdsf.whoosh;

import android.os.Looper;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * com.sxdsf.whoosh.Switchers
 *
 * @author 孙博闻
 * @date 2016/5/18 13:09
 * @desc Switcher的集合
 */
class Switchers {

    /**
     * 主线程的切换者
     */
    private final Switcher MAIN;
    /**
     * 发送者线程的切换者
     */
    private final Switcher POSTING;
    /**
     * 后台线程的切换者
     */
    private final Switcher BACKGROUND;
    /**
     * 异步线程的切换者
     */
    private final Switcher ASYNC;

    /**
     * 单一实例
     */
    private static final Switchers INSTANCE = new Switchers();

    private static final String TAG = "Switchers";

    private Switchers() {
        MAIN = new MainThreadSwitcher();
        POSTING = new PostingThreadSwitcher();

        //存储要执行任务的阻塞队列
        BlockingQueue<MessageHandler.Temp> taskQueue = new LinkedBlockingQueue<>();
        BACKGROUND = new BackgroundThreadSwitcher(taskQueue);
        ASYNC = new AsyncThreadSwitcher(taskQueue);

        //用于在后台和异步执行的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new Task(taskQueue));
        executorService.execute(new Task(taskQueue));
    }

    /**
     * 返回一个主线程的切换者
     *
     * @return
     */
    static Switcher mainThread() {
        return INSTANCE.MAIN;
    }

    /**
     * 返回一个发送线程的切换者
     *
     * @return
     */
    static Switcher postingThread() {
        return INSTANCE.POSTING;
    }

    /**
     * 返回一个后台线程的切换者
     *
     * @return
     */
    static Switcher backgroundThread() {
        return INSTANCE.BACKGROUND;
    }

    /**
     * 返回一个异步线程的切换者
     *
     * @return
     */
    static Switcher asyncThread() {
        return INSTANCE.ASYNC;
    }

    /**
     * 主线程的切换类
     */
    private static class MainThreadSwitcher implements Switcher {
        private final MessageHandler mMessageHandler;

        public MainThreadSwitcher() {
            mMessageHandler = new MessageHandler(Looper.getMainLooper());
        }

        @Override
        public <T> Carrier<? super T> switches(Carrier<? super T> carrier) {
            return new MainSwitchCarrier<>(carrier, mMessageHandler);
        }
    }

    /**
     * 发送者线程的切换类
     */
    private static class PostingThreadSwitcher implements Switcher {

        @Override
        public <T> Carrier<? super T> switches(Carrier<? super T> carrier) {
            return carrier;
        }
    }

    /**
     * 后台线程的切换类
     */
    private static class BackgroundThreadSwitcher implements Switcher {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public BackgroundThreadSwitcher(BlockingQueue<MessageHandler.Temp> taskQueue) {
            mTaskQueue = taskQueue;
        }

        @Override
        public <T> Carrier<? super T> switches(Carrier<? super T> carrier) {
            return new BackgroundSwitchCarrier<>(carrier, mTaskQueue);
        }
    }

    /**
     * 异步线程的切换者
     */
    private static class AsyncThreadSwitcher implements Switcher {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public AsyncThreadSwitcher(BlockingQueue<MessageHandler.Temp> taskQueue) {
            mTaskQueue = taskQueue;
        }

        @Override
        public <T> Carrier<? super T> switches(Carrier<? super T> carrier) {
            return new AsyncSwitchCarrier<>(carrier, mTaskQueue);
        }
    }

    /**
     * 线程变换基本处理的类
     *
     * @param <T>
     */
    private static abstract class SwitchCarrier<T> implements Carrier<T> {

        protected Carrier<? super T> mRawCarrier;

        public SwitchCarrier(Carrier<? super T> rawCarrier) {
            mRawCarrier = rawCarrier;
        }
    }

    /**
     * 主线程的处理
     *
     * @param <T>
     */
    private static class MainSwitchCarrier<T> extends SwitchCarrier<T> {

        private MessageHandler mMessageHandler;

        public MainSwitchCarrier(Carrier<? super T> rawCarrier, MessageHandler messageHandler) {
            super(rawCarrier);
            mMessageHandler = messageHandler;
        }

        @Override
        public void onReceive(T content) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mRawCarrier.onReceive(content);
            } else {
                MessageHandler.Temp temp = new MessageHandler.Temp();
                temp.mCarrier = mRawCarrier;
                temp.mMessage = content;
                mMessageHandler.enQueue(temp);
                if (!mMessageHandler.isRunning()) {
                    mMessageHandler.sendMessage(mMessageHandler.obtainMessage());
                }
            }
        }
    }

    /**
     * 后台线程的处理
     *
     * @param <T>
     */
    private static class BackgroundSwitchCarrier<T> extends SwitchCarrier<T> {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public BackgroundSwitchCarrier(Carrier<? super T> rawCarrier, BlockingQueue<MessageHandler.Temp> taskQueue) {
            super(rawCarrier);
            mTaskQueue = taskQueue;
        }

        @Override
        public void onReceive(T content) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                try {
                    MessageHandler.Temp temp = new MessageHandler.Temp<>();
                    temp.mCarrier = mRawCarrier;
                    temp.mMessage = content;
                    mTaskQueue.put(temp);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                mRawCarrier.onReceive(content);
            }
        }
    }

    /**
     * 异步线程的处理
     *
     * @param <T>
     */
    private static class AsyncSwitchCarrier<T> extends SwitchCarrier<T> {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public AsyncSwitchCarrier(Carrier<? super T> rawCarrier, BlockingQueue<MessageHandler.Temp> taskQueue) {
            super(rawCarrier);
            mTaskQueue = taskQueue;
        }

        @Override
        public void onReceive(T content) {
            try {
                MessageHandler.Temp temp = new MessageHandler.Temp<>();
                temp.mCarrier = mRawCarrier;
                temp.mMessage = content;
                mTaskQueue.put(temp);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * 异步执行的队列
     */
    private static class Task implements Runnable {

        private final BlockingQueue<MessageHandler.Temp> mQueue;

        private Task(BlockingQueue<MessageHandler.Temp> queue) {
            mQueue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    MessageHandler.Temp temp = mQueue.take();
                    if (temp == null || temp.mCarrier == null || temp.mMessage == null) {
                        continue;
                    }
                    temp.mCarrier.onReceive(temp.mMessage);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }
}
