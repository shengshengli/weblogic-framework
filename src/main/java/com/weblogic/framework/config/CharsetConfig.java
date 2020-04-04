package com.weblogic.framework.config;

/**
 * Title: CharsetConfig
 * Desc: CharsetConfig
 * Date:2020/4/4 23:43
 * @version 1.0.0
 */
public class CharsetConfig {

    /**
     * 私有化构造
     */
    private CharsetConfig(){}

    /**
     * 获取编码
     */
    public static String defaultCharsetName = getCharsetName();

    /**
     * 获取编码
     * @return
     */
    public static String getCharsetName(){
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().contains("win")){
            return  "GBK";
        }else{
            return  "UTF-8";
        }
    }
}
