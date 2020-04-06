package com.weblogic.framework.entity;

/**
 * Title: MyException
 * Desc: 自定义异常
 * Date:2020/3/24 0:09
 * @version 1.0.0
 */
public class MyException extends RuntimeException {

    public MyException(String msg){
        super(msg);
    }
}
