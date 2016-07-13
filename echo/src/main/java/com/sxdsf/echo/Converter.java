package com.sxdsf.echo;

/**
 * com.sxdsf.echo.Converter
 *
 * @author 孙博闻
 * @date 2016/7/12 15:16
 * @desc caster的整体变化
 */
public interface Converter<T extends Voice, R extends Voice> extends Action2<Caster<T>, Caster<R>> {
}
