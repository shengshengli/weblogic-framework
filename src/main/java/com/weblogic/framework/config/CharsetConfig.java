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
