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

package com.weblogic.framework.gadget.impl;

import com.weblogic.framework.entity.GadgetParam;
import com.weblogic.framework.gadget.ObjectGadget;
import java.net.URLClassLoader;
import java.util.Queue;

/**
 * Title: CommonsCollections2Gadget
 * Desc: CommonsCollections2 Gadget
 * Gadget chain:
 *     ObjectInputStream.readObject()
 *         PriorityQueue.readObject()
 *             ...
 *                 TransformingComparator.compare()
 *                     InvokerTransformer.transform()
 *                         Method.invoke()
 *                             Runtime.exec()
 * Date: 2020/4/19 15:44
 * @version 1.0.0
 */
public class CommonsCollections2Gadget implements ObjectGadget<Queue<Object>> {

    @Override
    public Queue<Object> getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        return null;
    }

    @Override
    public Queue<Object> getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
        return null;
    }

    @Override
    public Queue<Object> getObject(GadgetParam param) throws Exception {
        param.getCodeByte();
        param.getBootArgs();
        param.getClassName();
        param.getUrlClassLoader();
        return null;
    }

}