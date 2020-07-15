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

import com.r4v3zn.weblogic.framework.enmus.CallEnum;
import lombok.Data;

/**
 * Title: VulCheckParam
 * Desc: 漏洞验证参数实体类
 * Date: 2020/4/19 17:24
 *
 * @version 1.0.0
 */
@Data
public class VulCheckParam {
    /**
     * JNDI Url
     */
    private String jndiUrl;

    /**
     * javascript.jar 文件 URL
     */
    private String javascriptUrl;

    /**
     * 版本
     */
    private String version;

    /**
     * 编码
     */
    private String charsetName;

    /**
     * 回调实体类名称
     */
    private String callName;

    /**
     * 协议名称
     */
    private String protocol;

    /**
     * 回显方案
     */
    private CallEnum call;
}
