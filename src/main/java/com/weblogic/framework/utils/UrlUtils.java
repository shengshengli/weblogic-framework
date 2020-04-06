package com.weblogic.framework.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import com.weblogic.framework.entity.MyException;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: UrlUtils
 * Desc: UrlUtils
 * Date:2020/4/5 3:07
 * @version 1.0.0
 */
public class UrlUtils {
    /**
     * 私有化构造
     */
    private UrlUtils(){}

    /**
     * check url 是否合法
     * @param url
     */
    public static String checkUrl(String url)throws Exception{
        if(isBlank(url)){
            throw new MyException("URL 不能为空");
        }
        if(!url.startsWith("http")){
            url = "http://"+url;
        }
        try{
            HttpUtil.get(url,5);
        }catch (Exception e){
            if(e instanceof IORuntimeException){
                throw new MyException("URL 无法访问");
            }
        }
        return url;
    }
}
