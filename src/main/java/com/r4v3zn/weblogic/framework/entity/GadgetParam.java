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

package com.r4v3zn.weblogic.framework.entity;

import java.net.URLClassLoader;

/**
 * Title: GadgetParam
 * Desc: GadgetParam
 * Date: 2020/4/19 18:15
 *
 * @version 1.0.0
 */
public class GadgetParam {
    /**
     * 字节码
     */
    private byte[] codeByte;

    /**
     * 命令执行参数
     */
    private String[] bootArgs;

    /**
     * 反射生成的 class Name
     */
    private String className;

    /**
     * urlClassLoader
     */
    private URLClassLoader urlClassLoader;

    /**
     * JNDI URL
     */
    private String jndiUrl;

    public GadgetParam() {
    }

    public byte[] getCodeByte() {
        return this.codeByte;
    }

    public String[] getBootArgs() {
        return this.bootArgs;
    }

    public String getClassName() {
        return this.className;
    }

    public URLClassLoader getUrlClassLoader() {
        return this.urlClassLoader;
    }

    public String getJndiUrl() {
        return this.jndiUrl;
    }

    public void setCodeByte(byte[] codeByte) {
        this.codeByte = codeByte;
    }

    public void setBootArgs(String[] bootArgs) {
        this.bootArgs = bootArgs;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setUrlClassLoader(URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }

    public void setJndiUrl(String jndiUrl) {
        this.jndiUrl = jndiUrl;
    }
}
