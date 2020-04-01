package com.r4v3zn.weblogic.tools;

import org.omg.CORBA.portable.Delegate;
import weblogic.cluster.singleton.ClusterMasterRemote;
import weblogic.iiop.*;

import java.lang.reflect.Field;

/**
 * Title: Test
 * Descrption: TODO
 * Date:2020/4/1 13:46
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
//        ReplyMessage rsp = new ReplyMessage();
//        Field endPointField = RequestMessage.class.getDeclaredField("endPoint");
//        // EndPointImpl c IIOPConnection type
//        Field cField = EndPointImpl.class.getDeclaredField("c");
//        EndPointImpl impl = new EndPointImpl();
//        // c Value
//        Connection connection =  impl.getConnection();
//        IOR remoteCodeBase = aa.getRemoteCodeBase();
//        Field iopProfileField = IOR.class.getDeclaredField("iopProfile");
//        iopProfileField.setAccessible(true);
//        Field profilesField = IOR.class.getDeclaredField("profiles");
//        profilesField.setAccessible(true);
//        //
//        IOPProfile iopProfile = remoteCodeBase.getProfile();
//        // host
//        Field hostField = IOPProfile.class.getDeclaredField("host");
//        hostField.setAccessible(true);
//        hostField.set(iopProfile, "host");
//        // port
//        Field portField = IOPProfile.class.getDeclaredField("port");
//        portField.setAccessible(true);
//        portField.set(iopProfile, "port");
//        iopProfileField.set(remoteCodeBase, iopProfile);
//        profilesField.set(remoteCodeBase, new Profile[]{iopProfile});
//        Field remoteCodeBaseField = Connection.class.getDeclaredField("remoteCodeBase");
//        remoteCodeBaseField.setAccessible(true);
//        remoteCodeBaseField.set(connection,remoteCodeBase);

    }
}
