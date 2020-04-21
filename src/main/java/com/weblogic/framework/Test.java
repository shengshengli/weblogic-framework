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

package com.weblogic.framework;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.util.ClassLoader;
import javassist.ClassPool;
import javassist.CtClass;
import weblogic.cluster.singleton.ClusterMasterRemote;
import java.rmi.Remote;
import static com.weblogic.framework.utils.CallUtils.CALL_MAP;

/**
 * Title: Test
 * Desc: TODO
 * Date:2020/4/9 17:57
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String callName = ClusterMasterRemote.class.getSimpleName();
        Class<? extends Remote> callClazz = CALL_MAP.get(callName);
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =  pool.get(callClazz.getName());
        if(ctClass.isFrozen()){
            ctClass.defrost();
        }
        ctClass.setName(callClazz.getSimpleName());
        byte[] bytes = ctClass.toBytecode();
        String result = Utility.encode(bytes, true);
        System.out.println(result);
        result = "$$BCEL$$"+result;


        ClassLoader classLoader = new ClassLoader();
//        Class clazz1 = classLoader.getParent().loadClass("weblogic.cluster.singleton.ClusterMasterRemote");
//        Class clazz1 = classLoader.loadClass("weblogic.cluster.singleton.ClusterMasterRemote");
//        Class clazz = classLoader.loadClass("$$BCEL$$"+result);
        // $$BCEL$$$
//        System.out.println(clazz.getName());
    }
}
