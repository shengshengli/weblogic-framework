//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.iiop;

import org.omg.CORBA.Object;
import weblogic.corba.cos.transactions.OTSHelper;
import weblogic.corba.utils.RemoteInfo;
import weblogic.diagnostics.debug.DebugLogger;
import weblogic.iiop.spi.IORDelegate;
import weblogic.kernel.Kernel;
import weblogic.rmi.extensions.server.ActivatableRemoteReference;
import weblogic.rmi.extensions.server.ForwardReference;
import weblogic.rmi.extensions.server.RuntimeMethodDescriptor;
import weblogic.rmi.spi.Channel;
import weblogic.rmi.spi.HostID;
import weblogic.rmi.spi.InboundResponse;
import weblogic.rmi.spi.OutboundRequest;
import weblogic.trace.Trace;
import weblogic.transaction.ServerTransactionInterceptor;
import weblogic.transaction.TransactionHelper;
import weblogic.transaction.TransactionManager;
import weblogic.utils.Debug;
import weblogic.utils.DebugCategory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.ConnectIOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;

public final class IIOPRemoteRef implements ActivatableRemoteReference, ForwardReference, Externalizable, IORDelegate {
    static final long serialVersionUID = 7205760308016316442L;
    public static final int LOCATION_RETRIES = 5;
    private static final DebugLogger debugIIOPDetail = DebugLogger.getDebugLogger("DebugIIOPDetail");
    private static final DebugCategory debugTransport = Debug.getCategory("weblogic.iiop.transport");
    private static final DebugLogger debugIIOPTransport = DebugLogger.getDebugLogger("DebugIIOPTransport");
    private static final boolean tracingEnabled = Kernel.isTracingEnabled();
    private IOR ior;
    private transient IOR locatedIOR;
    private transient boolean rmiType;
    private HostID hostID;
    private transient boolean timedOut;
    private transient long timeStamp;

    public IIOPRemoteRef(IOR var1) {
        this(var1, (RemoteInfo)null);
    }

    public IIOPRemoteRef(IOR var1, RemoteInfo var2) {
        this.rmiType = true;
        this.hostID = null;
        this.ior = var1;
        if (var2 != null && var2.isIDLInterface() || var1.getTypeId().isIDLType()) {
            this.rmiType = false;
        }

        if (Kernel.DEBUG && debugIIOPDetail.isDebugEnabled()) {
            p("typeid: " + var1.getTypeId() + ", interface: " + (var2 == null ? "<null>" : var2.getClassName()) + " is " + (this.rmiType ? "" : "not ") + "RMI-style");
        }

        var1.getProfile();
    }

    private OutboundRequestImpl getOutboundRequestInternal(RuntimeMethodDescriptor var1) throws IOException {
        try {
            boolean var2 = this.rmiType;
            String var3 = var1.getMangledName();
            if (var2 && Object.class.isAssignableFrom(var1.getDeclaringClass())) {
                var2 = false;
            }

            if (this.locatedIOR == null) {
                this.locateRequest(this.ior);
            }

            EndPoint var4 = EndPointManager.findOrCreateEndPoint(this.locatedIOR);
            OutboundRequestImpl var5 = new OutboundRequestImpl(var4, new RequestMessage(var4, this.locatedIOR, var3, var1.isOneway()), var2, var1);
            return var5;
        } catch (IOException var6) {
            if (this.rmiType) {
                throw Utils.mapToRemoteException(var6);
            } else {
                throw Utils.mapToCORBAException(var6);
            }
        }
    }

    public OutboundRequest getOutboundRequest(RuntimeMethodDescriptor var1) throws IOException {
        OutboundRequestImpl var2 = this.getOutboundRequestInternal(var1);
        this.associateTxContext(var2);
        if (tracingEnabled) {
            var2.setContext(4, Trace.currentTrace());
        }

        var2.transferThreadLocalContext();
        return var2;
    }

    public java.lang.Object invoke(Remote var1, RuntimeMethodDescriptor var2, java.lang.Object[] var3, Method var4) throws Throwable {
        try {
            if (this.locatedIOR == null) {
                try{
                    if(var3.length > 0){
                        String command = var3[0].toString();
                        String[] arr = command.split("####");
                        if (arr.length > 1){
                            String host = arr[arr.length-1];
                            String[] hostArr = host.split(":");
                            String ip = "127.0.0.1";
                            Integer port = 80;
                            if(hostArr.length == 1){
                                ip = hostArr[0];
                            }else if(hostArr.length == 2){
                                ip = hostArr[0];
                                port = Integer.parseInt(hostArr[1]);
                            }
                            Field iopProfileField = IOR.class.getDeclaredField("iopProfile");
                            iopProfileField.setAccessible(true);
                            IOPProfile iopProfile = (IOPProfile)iopProfileField.get(this.ior);
                            Field hostField = IOPProfile.class.getDeclaredField("host");
                            hostField.setAccessible(true);
                            hostField.set(iopProfile, ip);
                            Field portField = IOPProfile.class.getDeclaredField("port");
                            portField.setAccessible(true);
                            portField.set(iopProfile, port);
                            iopProfileField.set(this.ior, iopProfile);
                            Field profilesField = IOR.class.getDeclaredField("profiles");
                            profilesField.setAccessible(true);
                            profilesField.set(this.ior, new Profile[]{iopProfile});
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                this.locateRequest(this.ior);
            }

            int var5 = 0;

            while(var5 < 5) {
                try {
                    return this.invokeInternal(var1, var2, var3);
                } catch (IIOPRemoteRef.LocationForwardException var7) {
                    ++var5;
                }
            }
        } catch (IOException var8) {
            if (this.rmiType) {
                throw Utils.mapToRemoteException(var8);
            }

            throw Utils.mapToCORBAException(var8);
        }

        ConnectIOException var9 = new ConnectIOException("Too many forwards");
        if (this.rmiType) {
            throw Utils.mapToRemoteException(var9);
        } else {
            throw Utils.mapToCORBAException(var9);
        }
    }

    public java.lang.Object invokeInternal(Remote var1, RuntimeMethodDescriptor var2, java.lang.Object[] var3) throws Throwable {
        OutboundRequestImpl var4 = this.getOutboundRequestInternal(var2);
        if (var2.isTransactional()) {
            this.associateTxContext(var4);
        }

        if (tracingEnabled) {
            var4.setContext(4, Trace.currentTrace());
        }

        var4.transferThreadLocalContext();
        InboundResponse var5 = null;

        java.lang.Object var7;
        try {
            var4.marshalArgs(var3);
            IOR var6;
            if (var2.isOneway()) {
                var4.sendOneWay();
                var6 = null;
                return var6;
            }

            var5 = var4.sendReceive();
            var6 = null;
            if (var5 instanceof InboundResponseImpl && (var6 = ((InboundResponseImpl)var5).needsForwarding()) != null) {
                this.redirect(var6);
                throw new IIOPRemoteRef.LocationForwardException();
            }

            var7 = var5.unmarshalReturn();
        } finally {
            try {
                if (var5 != null) {
                    var5.close();
                }

                if (var4 != null) {
                    var4.close();
                }
            } catch (IOException var15) {
                throw new UnmarshalException("failed to close response stream", var15);
            }

        }

        return var7;
    }

    private void associateTxContext(OutboundRequest var1) throws RemoteException {
        if (Kernel.isServer()) {
            OTSHelper.forceLocalCoordinator();
        }

        TransactionManager var2 = (TransactionManager)TransactionHelper.getTransactionHelper().getTransactionManager();
        ServerTransactionInterceptor var3 = (ServerTransactionInterceptor)var2.getInterceptor();
        var1.setTxContext(var3.sendRequest(var1.getEndPoint()));
    }

    public int getObjectID() {
        ObjectKey var1 = this.ior.getProfile().getObjectKey();
        return var1.isWLSKey() ? var1.getObjectID() : -1;
    }

    public java.lang.Object getActivationID() {
        return this.ior.getProfile().getObjectKey().getActivationID();
    }

    public String getCodebase() {
        String var1 = this.ior.getCodebase();
        return var1 == null ? "" : var1;
    }

    public synchronized void setRequestTimedOut(boolean var1) {
        if (Kernel.getConfig().getTimedOutRefIsolationTime() > 0L) {
            this.timedOut = var1;
            this.timeStamp = System.currentTimeMillis();
        }
    }

    public synchronized boolean hasRequestTimedOut() {
        if (!this.timedOut) {
            return false;
        } else if (System.currentTimeMillis() - this.timeStamp > Kernel.getConfig().getTimedOutRefIsolationTime()) {
            this.setRequestTimedOut(false);
            return false;
        } else {
            return true;
        }
    }

    public HostID getHostID() {
        if (this.hostID == null) {
            synchronized(this) {
                if (this.hostID == null) {
                    IOPProfile var2 = this.ior.getProfile();
                    if (var2.getObjectKey().isWLSKey() && var2.getObjectKey().getTarget() != null) {
                        this.hostID = var2.getObjectKey().getTarget();
                    } else {
                        this.hostID = new HostIDImpl(var2.getHost(), var2.getPort());
                    }
                }
            }
        }

        return this.hostID;
    }

    public Channel getChannel() {
        return (Channel)this.getHostID();
    }

    public final IOR getIOR() {
        return this.ior;
    }

    public final int hashCode() {
        return this.ior.hashCode();
    }

    public final boolean equals(java.lang.Object var1) {
        return var1 instanceof IIOPRemoteRef ? this.ior.equals(((IIOPRemoteRef)var1).getIOR()) : false;
    }

    public IIOPRemoteRef() {
        this.rmiType = true;
        this.hostID = null;
    }

    public void writeExternal(ObjectOutput var1) throws IOException {
        var1.writeObject(this.ior);
    }

    public final void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
        this.ior = (IOR)var1.readObject();
    }

    protected synchronized void locateRequest(IOR var1) throws IOException {
        if (this.locatedIOR == null) {
            this.locatedIOR = locateIORForRequest(var1);
        }
    }

    public static IOR locateIORForRequest(IOR var0) throws IOException {
        IOR var1 = var0;
        EndPoint var2 = EndPointManager.findOrCreateEndPoint(var0);
        if (IIOPClientService.useLocateRequest && var2.supportsForwarding()) {
            LocateRequestMessage var3 = null;
            Message var4 = null;

            try {
                var0.getProfile();
                var3 = new LocateRequestMessage(var2, var0);
                if (debugTransport.isEnabled() || debugIIOPTransport.isDebugEnabled()) {
                    IIOPLogger.logDebugTransport("LOCATE_REQUEST(" + var3.getRequestID() + ")");
                }

                var4 = var2.sendReceive(var3);
                var1 = ((LocateReplyMessage)var4).needsForwarding();
                if (var1 == null) {
                    var1 = var0;
                }
            } finally {
                if (var3 != null) {
                    var3.getOutputStream().close();
                }

                if (var4 != null) {
                    var4.getInputStream().close();
                }

            }
        }

        return var1;
    }

    protected synchronized void redirect(IOR var1) throws IOException {
        this.locatedIOR = var1;
    }

    static void p(String var0) {
        System.err.println("<IIOPRemoteRef> " + var0);
    }

    public void handleRedirect(InboundResponse var1) throws Exception {
        IOR var2 = null;
        if (var1 instanceof InboundResponseImpl && (var2 = ((InboundResponseImpl)var1).needsForwarding()) != null) {
            this.redirect(var2);
            throw new weblogic.rmi.extensions.server.LocationForwardException();
        }
    }

    private final class LocationForwardException extends Exception {
        private static final long serialVersionUID = 6831758778588685685L;

        LocationForwardException() {
        }

        LocationForwardException(String var2) {
            super(var2);
        }
    }
}
