package com.sxdsf.whoosh;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.SoftReference;

/**
 * Switcher
 *
 * @author sunbowen
 * @date 2016/5/18-10:58
 * @desc 用于线程切换
 */
class Switcher {

    public <T> Carrier<? super T> switchIt(Carrier<? super T> carrier) {
        return new SwitchCarrier<>(carrier);
    }

    private static class SwitchCarrier<T> implements Carrier<T> {

        private Carrier<T> rawCarrier;
        private MessageHandler<T> mMessageHandler;

        public SwitchCarrier(Carrier<T> rawCarrier) {
            this.rawCarrier = rawCarrier;
            mMessageHandler = new MessageHandler<>(this, Looper.getMainLooper());
        }

        @Override
        public void onReceive(T content) {
            android.os.Message message = mMessageHandler.obtainMessage();
            message.obj = content;
            mMessageHandler.sendMessage(message);
        }

        private static class MessageHandler<T> extends Handler {
            private SoftReference<SwitchCarrier<T>> callbackReference;

            public MessageHandler(SwitchCarrier<T> callback, Looper looper) {
                super(looper);
                this.callbackReference = new SoftReference<>(callback);
            }

            @Override
            public void handleMessage(android.os.Message msg) {
                // TODO Auto-generated method stub
                if (this.callbackReference != null) {
                    SwitchCarrier<T> callback = this.callbackReference.get();
                    if (callback != null) {
                        callback.handleMessage(msg);
                    }
                }
            }
        }

        private void handleMessage(android.os.Message msg) {
            if (msg != null) {
                rawCarrier.onReceive((T) msg.obj);
            }
        }
    }
}
