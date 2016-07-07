package com.sxdsf.whoosh.exception;

/**
 * com.sxdsf.whoosh.exception.WhooshException
 *
 * @author 孙博闻
 * @date 2016/7/7 12:03
 * @desc whoosh服务抛出的异常
 */
public class WhooshException extends Exception {

    private String mClassName;
    private String mMethod;

    public WhooshException(String detailMessage) {
        this(detailMessage, null, null);
    }

    public WhooshException(String detailMessage, String className, String method) {
        super(detailMessage);
        mClassName = className;
        mMethod = method;
    }

    @Override
    public String getMessage() {
        return "className=" + mClassName + ",method=" + mMethod + "," + getMessage();
    }
}
