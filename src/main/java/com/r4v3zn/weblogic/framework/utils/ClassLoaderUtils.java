/*
 * Copyright (c) 2020. r4v3zn.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r4v3zn.weblogic.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Title: ClassLoaderUtils
 * Desc: class loader utils
 * Date:2020/4/4 20:02
 * @author 0nise
 * @version 1.0.0
 */
public class ClassLoaderUtils {

   static Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

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
        version = version.replace(".0.0", ".0");
        String basePath = System.getProperty("user.dir")+ File.separator+"lib"+File.separator;
        URL[] urls = new URL[jarNames.length];
        for (int i = 0; i < jarNames.length; i++) {
            String jarName = jarNames[i];
            String path = basePath + version+"/"+ jarName;
            log.info("[*] load class "+jarName+" version --> "+version);
            urls[i] = new URL("file:"+path);
        }
        return new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
    }
}
