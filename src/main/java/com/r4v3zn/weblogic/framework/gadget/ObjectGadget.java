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

package com.r4v3zn.weblogic.framework.gadget;


import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import org.reflections.Reflections;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;

/**
 * Title: ObjectPayload
 * Desc: ObjectPayload
 * Date:2020/3/29 0:35
 * @version 1.0.0
 */
@SuppressWarnings ( "rawtypes" )
public interface ObjectGadget<T> {

    /**
     * 获取序列化 payload (Runtime)
     * @param command 执行的命令
     * @return 序列结果
     * @throws Exception
     */
    T getObject(String command, URLClassLoader urlClassLoader) throws Exception;

    /**
     * 获取序列化 payload
     * @param codeByte 需要序列化的字节码
     * @param bootArgs 执行中参数
     * @param className 反射的 class name
     * @return 序列结果
     * @throws Exception
     */
    T getObject(final byte[] codeByte, final String[] bootArgs, String className, URLClassLoader urlClassLoader)throws Exception;

    /**
     * 获取序列化 payload
     * @param param 参数
     * @return
     * @throws Exception
     */
    T getObject(GadgetParam param) throws Exception;

    /**
     * 生成文件文件写入payload
     * @param param 参数
     * @return
     * @throws Exception
     */
    T getWriteFileObject(GadgetParam param) throws Exception;

    /**
     * 加载文件
     * @param param Exception
     * @return
     * @throws Exception
     */
    T getLoadFileObject(GadgetParam param) throws Exception;

    public static class Utils {

        // get payload classes by classpath scanning
        public static Set<Class<? extends ObjectGadget>> getPayloadClasses () {
            final Reflections reflections = new Reflections(ObjectGadget.class.getPackage().getName());
            final Set<Class<? extends ObjectGadget>> payloadTypes = reflections.getSubTypesOf(ObjectGadget.class);
            for ( Iterator<Class<? extends ObjectGadget>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
                Class<? extends ObjectGadget> pc = iterator.next();
                if ( pc.isInterface() || Modifier.isAbstract(pc.getModifiers()) ) {
                    iterator.remove();
                }
            }
            return payloadTypes;
        }
    }
}
