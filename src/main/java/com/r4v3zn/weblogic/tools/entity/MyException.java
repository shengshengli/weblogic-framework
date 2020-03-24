package com.r4v3zn.weblogic.tools.entity;

/**
 * Title: MyException
 * Desc: 自定义异常
 * Date:2020/3/24 0:09
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
public class MyException extends RuntimeException {

    public MyException(String msg){
        super(msg);
    }
}
