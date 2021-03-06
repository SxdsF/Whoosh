package com.sxdsf.whoosh;

import android.support.annotation.NonNull;

import com.sxdsf.echo.Caster;
import com.sxdsf.echo.OnCast;
import com.sxdsf.echo.Receiver;
import com.sxdsf.echo.Switcher;
import com.sxdsf.whoosh.exception.WhooshException;
import com.sxdsf.whoosh.info.Message;
import com.sxdsf.whoosh.info.Topic;

import java.util.Arrays;
import java.util.List;

/**
 * com.sxdsf.whoosh.Listener
 *
 * @author 孙博闻
 * @date 2016/7/7 8:41
 * @desc 消息的监听者
 */
public class Listener extends Caster<Message> implements Carrier, ListenerShip, Comparable<Listener> {

    /**
     * 默认的优先级
     */
    private static final int DEFAULT_PRIORITY = 0;

    /**
     * 当前listener执行的线程，默认是发送者线程
     */
    ThreadMode mThreadMode = ThreadMode.POSTING;
    /**
     * 当前listener的优先级，默认是0
     */
    int mPriority = DEFAULT_PRIORITY;
    /**
     * 当前listener的过滤器
     */
    List<Filter> mFilters;
    /**
     * 当前listener关心的主题
     */
    Topic mTopic;
    /**
     * 当前listener要注册到的服务
     */
    Whoosh mWhoosh;
    /**
     * 打印日志的拦截器，默认是系统提供的日志拦截器（什么都不做）
     */
    LogInterceptor mLogInterceptor = new DefaultLogInterceptor();
    /**
     * 传递到此listener时会不会中断消息的派发，默认是false
     */
    boolean mInterruptDelivery = false;

    private final ListenerOnCast mLoc;

    Listener(OnCast<Message> onCast, ListenerOnCast loc) {
        super(onCast);
        mLoc = loc;
    }

    /**
     * 创建一个Listener
     *
     * @return
     */
    public static Listener create() {
        ListenerOnCast loc = new ListenerOnCast();
        return new Listener(loc, loc);
    }

    /**
     * 创建一个listener
     *
     * @param topic  关心的话题
     * @param whoosh whoosh服务
     * @return
     */
    public static Listener create(@NonNull Topic topic, @NonNull Whoosh whoosh) {
        ListenerOnCast loc = new ListenerOnCast();
        Listener listener = new Listener(loc, loc);
        listener.mTopic = topic;
        listener.mWhoosh = whoosh;
        return listener;
    }

    @Override
    public void onReceive(Message content) {
        mLogInterceptor.preReceive(mTopic, this, content);
        //如果设置了传递到此listener中断消息派发，则把消息设置为被废弃的
        if (mInterruptDelivery) {
            content.setIsAbandoned(true);
        }
        mLoc.mRawCarrier.onReceive(content);
    }

    /**
     * 设置监听者的过滤器
     *
     * @param filters 过滤器
     * @return
     */
    public Listener filters(Filter... filters) {
        if (filters != null && filters.length > 0) {
            mFilters = Arrays.asList(filters);
        }
        return this;
    }

    /**
     * 传入打印log的拦截器
     *
     * @param logInterceptor 打印log的拦截器
     * @return
     */
    public Listener log(@NonNull LogInterceptor logInterceptor) {
        mLogInterceptor = logInterceptor;
        return this;
    }

    /**
     * 传递到此listener时会不会中断消息的派发
     *
     * @param interruptDelivery 是否中断派发
     * @return
     */
    public Listener interruptDelivery(boolean interruptDelivery) {
        mInterruptDelivery = interruptDelivery;
        return this;
    }

    /**
     * 设置监听者的优先级
     *
     * @param priority 优先级
     * @return
     */
    public Listener priority(int priority) {
        mPriority = priority;
        return this;
    }

    /**
     * 做一个统一的处理
     *
     * @param converter 做统一处理的类
     * @return
     */
    public Listener unify(@NonNull Converter converter) {
        return (Listener) super.convert(converter);
    }

    /**
     * 表示此listener关心哪个话题
     *
     * @param topic 话题
     * @return
     */
    public Listener careAbout(@NonNull Topic topic) {
        mTopic = topic;
        return this;
    }

    /**
     * 表示把此listener注册到哪个服务里
     *
     * @param whoosh
     * @return
     */
    public Listener listenIn(@NonNull Whoosh whoosh) {
        mWhoosh = whoosh;
        return this;
    }

    /**
     * 监听者在哪个线程监听
     *
     * @param threadMode 线程模式
     * @return
     */
    public Listener listenOn(ThreadMode threadMode) {
        mThreadMode = threadMode;
        Switcher<Message> switcher;
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
        return (Listener) super.receiveOn(switcher);
    }

    /**
     * 监听者的监听方法
     *
     * @param carrier 消息的载体
     */
    public ListenerShip listen(@NonNull Carrier carrier) {
        super.cast(carrier);
        if (mWhoosh != null && mTopic != null) {
            mLogInterceptor.preListen(mTopic, this);
            if (isUnListened()) {
                mWhoosh.register(mTopic, this);
            } else {
                //如果已经注册过，再注册会发一个异常到异常话题
                mWhoosh.createProducer(mWhoosh.WhooshException).send(Message.create(new WhooshException("Listener", "listen", "this listener is registered")));
            }
            mLogInterceptor.afterListen(mTopic, this);
        }
        return this;
    }

    /**
     * 适配方法，将此listener根据适配器适配成任何结果
     *
     * @param adapter 适配器
     * @param <T>
     * @return
     */
    public <T> T adaptTo(@NonNull Adapter<Listener, T> adapter) {
        return adapter.adapt(this);
    }

    @Override
    public void unListen() {
        if (mWhoosh != null && mTopic != null) {
            mLogInterceptor.preUnListen(mTopic, this);
            mWhoosh.unRegister(mTopic, this);
            mLogInterceptor.afterUnListen(mTopic, this);
        }
    }

    @Override
    public boolean isUnListened() {
        boolean result = false;
        if (mWhoosh != null && mTopic != null) {
            result = !mWhoosh.isRegistered(mTopic, this);
        }
        return result;
    }

    @Override
    public int compareTo(@NonNull Listener another) {
        if (mPriority > another.mPriority) {
            return 1;
        }

        if (mPriority == another.mPriority) {
            return 0;
        }

        if (mPriority < another.mPriority) {
            return -1;
        }
        return 0;
    }

    /**
     * 系统默认的拦截器
     */
    private static class DefaultLogInterceptor implements LogInterceptor {

        @Override
        public void preListen(Topic topic, Listener listener) {
            //do nothing
        }

        @Override
        public void afterListen(Topic topic, Listener listener) {
            //do nothing
        }

        @Override
        public void preReceive(Topic topic, Listener listener, Message message) {
            //do nothing
        }

        @Override
        public void preUnListen(Topic topic, Listener listener) {
            //do nothing
        }

        @Override
        public void afterUnListen(Topic topic, Listener listener) {
            //do nothing
        }
    }

    private static class ListenerOnCast implements OnCast<Message> {

        Carrier mRawCarrier;

        @Override
        public void call(Receiver<Message> tReceiver) {
            mRawCarrier = (Carrier) tReceiver;
        }
    }
}
