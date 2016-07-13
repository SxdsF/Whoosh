package com.sxdsf.whoosh;

import com.sxdsf.echo.Caster;
import com.sxdsf.whoosh.info.Message;

/**
 * com.sxdsf.whoosh.Converter
 *
 * @author 孙博闻
 * @date 2016/7/13 10:51
 * @desc listener整体的变化
 */
public abstract class Converter implements com.sxdsf.echo.Converter<Message, Message> {

    @Override
    public Caster<Message> call(Caster<Message> messageCaster) {
        return this.convert(messageCaster.classCast(Listener.class));
    }

    public abstract Listener convert(Listener listener);
}
