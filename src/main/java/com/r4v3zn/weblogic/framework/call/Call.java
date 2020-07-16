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

package com.r4v3zn.weblogic.framework.call;

import com.r4v3zn.weblogic.framework.entity.ContextPojo;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;
import com.r4v3zn.weblogic.framework.vuls.VulTest;
/**
 * Title: Call
 * Desc: Call
 * Date:2020/4/4 23:18
 * @author 0nise
 * @version 1.0.0
 */
public interface Call {

    // Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    /**
     * 执行回显方案前置
     *
     * @param param 利用参数
     * @param vulTest 漏洞对象
     * @param gadgetClazz
     * @param contextPojo 链接对象
     * @return
     */
    ContextPojo executeCall(GadgetParam param, VulTest vulTest, final Class<? extends ObjectGadget<?>> gadgetClazz, ContextPojo contextPojo);
}
