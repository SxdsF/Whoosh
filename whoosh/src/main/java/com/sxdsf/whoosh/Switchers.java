package com.sxdsf.whoosh;

import android.os.Looper;
import android.util.Log;

import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;
import com.sxdsf.whoosh.info.Message;

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
    private final Switcher<Message> MAIN;
    /**
     * 发送者线程的切换者
     */
    private final Switcher<Message> POSTING;
    /**
     * 后台线程的切换者
     */
    private final Switcher<Message> BACKGROUND;
    /**
     * 异步线程的切换者
     */
    private final Switcher<Message> ASYNC;

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
    static Switcher<Message> mainThread() {
        return INSTANCE.MAIN;
    }

    /**
     * 返回一个发送线程的切换者
     *
     * @return
     */
    static Switcher<Message> postingThread() {
        return INSTANCE.POSTING;
    }

    /**
     * 返回一个后台线程的切换者
     *
     * @return
     */
    static Switcher<Message> backgroundThread() {
        return INSTANCE.BACKGROUND;
    }

    /**
     * 返回一个异步线程的切换者
     *
     * @return
     */
    static Switcher<Message> asyncThread() {
        return INSTANCE.ASYNC;
    }

    /**
     * 主线程的切换类
     */
    private static class MainThreadSwitcher implements Switcher<Message> {
        private final MessageHandler mMessageHandler;

        public MainThreadSwitcher() {
            mMessageHandler = new MessageHandler(Looper.getMainLooper());
        }

        @Override
        public Receiver<Message> switches(Receiver<Message> receiver) {
            return new MainSwitchCarrier(receiver, mMessageHandler);
        }
    }

    /**
     * 发送者线程的切换类
     */
    private static class PostingThreadSwitcher implements Switcher<Message> {

        @Override
        public Receiver<Message> switches(Receiver<Message> receiver) {
            return receiver;
        }
    }

    /**
     * 后台线程的切换类
     */
    private static class BackgroundThreadSwitcher implements Switcher<Message> {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public BackgroundThreadSwitcher(BlockingQueue<MessageHandler.Temp> taskQueue) {
            mTaskQueue = taskQueue;
        }

        @Override
        public Receiver<Message> switches(Receiver<Message> receiver) {
            return new BackgroundSwitchCarrier(receiver, mTaskQueue);
        }
    }

    /**
     * 异步线程的切换者
     */
    private static class AsyncThreadSwitcher implements Switcher<Message> {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public AsyncThreadSwitcher(BlockingQueue<MessageHandler.Temp> taskQueue) {
            mTaskQueue = taskQueue;
        }

        @Override
        public Receiver<Message> switches(Receiver<Message> receiver) {
            return new AsyncSwitchCarrier(receiver, mTaskQueue);
        }
    }

    /**
     * 线程变换基本处理的类
     */
    private static abstract class SwitchCarrier implements Carrier {

        protected Carrier mRawCarrier;

        public SwitchCarrier(Receiver<Message> rawCarrier) {
            mRawCarrier = (Carrier) rawCarrier;
        }
    }

    /**
     * 主线程的处理
     */
    private static class MainSwitchCarrier extends SwitchCarrier {

        private MessageHandler mMessageHandler;

        public MainSwitchCarrier(Receiver<Message> rawCarrier, MessageHandler messageHandler) {
            super(rawCarrier);
            mMessageHandler = messageHandler;
        }

        @Override
        public void onReceive(Message content) {
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
     */
    private static class BackgroundSwitchCarrier extends SwitchCarrier {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public BackgroundSwitchCarrier(Receiver<Message> rawCarrier, BlockingQueue<MessageHandler.Temp> taskQueue) {
            super(rawCarrier);
            mTaskQueue = taskQueue;
        }

        @Override
        public void onReceive(Message content) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                try {
                    MessageHandler.Temp temp = new MessageHandler.Temp();
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
     */
    private static class AsyncSwitchCarrier extends SwitchCarrier {

        private final BlockingQueue<MessageHandler.Temp> mTaskQueue;

        public AsyncSwitchCarrier(Receiver<Message> rawCarrier, BlockingQueue<MessageHandler.Temp> taskQueue) {
            super(rawCarrier);
            mTaskQueue = taskQueue;
        }

        @Override
        public void onReceive(Message content) {
            try {
                MessageHandler.Temp temp = new MessageHandler.Temp();
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
