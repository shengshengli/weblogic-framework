package com.weblogic.framework.utils;

import com.weblogic.framework.entity.MyException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Title: ClassLoaderUtils
 * Desc: class loader utils
 * Date:2020/4/4 20:02
 * @version 1.0.0
 */
public class ClassLoaderUtils {

    /**
     * 私有化构造防止被实例化
     */
    private ClassLoaderUtils(){}

    /**
     * 自定义加载 jar
     * @param version weblogic 版本
     * @param jarNames 需要加载的名称
     * @return
     */
    public static URLClassLoader loadJar(String version, String... jarNames) throws MalformedURLException {
        if(jarNames.length == 0){
            throw new MyException("jar名称不能为空");
        }
        version = version.replace(".0.0", ".0");
        String basePath = ClassLoaderUtils.class.getResource("/lib/").getPath();
        URL[] urls = new URL[jarNames.length];
        for (int i = 0; i < jarNames.length; i++) {
            String jarName = jarNames[i];
            String path = basePath + version+"/"+ jarName;
            System.out.println("[*] load class "+jarName+" version --> "+version);
            urls[i] = new URL("file:"+path);
        }
        return new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
    }
}
