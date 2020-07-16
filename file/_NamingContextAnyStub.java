//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.corba.cos.naming;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextExtPackage.*;
import org.omg.CosNaming.NamingContextPackage.*;
import weblogic.corba.cos.naming.NamingContextAnyPackage.*;
import weblogic.iiop.IIOPInputStream;
import weblogic.iiop.IOR;
import weblogic.iiop.RequestMessage;
import weblogic.iiop.spi.MessageStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class _NamingContextAnyStub extends ObjectImpl implements NamingContextAny {
    private static String[] __ids = new String[]{"IDL:weblogic/corba/cos/naming/NamingContextAny:1.0", "IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0"};

    public _NamingContextAnyStub() {
    }

    public void bind_any(WNameComponent[] var1, Any var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound, TypeNotSupported {
        InputStream var3 = null;

        try {
            OutputStream var4 = this._request("bind_any", true);
            WNameHelper.write(var4, var1);
            var4.write_any(var2);
            var3 = this._invoke(var4);
            return;
        } catch (ApplicationException var10) {
            var3 = var10.getInputStream();
            String var5 = var10.getId();
            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/NotFound:1.0")) {
                throw NotFoundHelper.read(var3);
            }

            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(var3);
            }

            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/TypeNotSupported:1.0")) {
                throw TypeNotSupportedHelper.read(var3);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var11) {
            this.bind_any(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

    }

    public void rebind_any(WNameComponent[] var1, Any var2) throws NotFound, CannotProceed, InvalidName, TypeNotSupported {
        InputStream var3 = null;

        try {
            OutputStream var4 = this._request("rebind_any", true);
            WNameHelper.write(var4, var1);
            var4.write_any(var2);
            var3 = this._invoke(var4);
            return;
        } catch (ApplicationException var10) {
            var3 = var10.getInputStream();
            String var5 = var10.getId();
            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/NotFound:1.0")) {
                throw NotFoundHelper.read(var3);
            }

            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/TypeNotSupported:1.0")) {
                throw TypeNotSupportedHelper.read(var3);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var11) {
            this.rebind_any(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

    }

    public Any resolve_any(WNameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
        InputStream var2 = null;

        Any var3;
        try {
            OutputStream var4 = this._request("resolve_any", true);
            WNameHelper.write(var4, var1);
            OutputStream var18 = var4;
            IOR var6 = null;
            try {
                RequestMessage var7 = (RequestMessage)((MessageStream)var18).getMessage();
                var6 = var7.getIOR();
            } catch (Exception var14) {
            }
            var2 = this._invoke(var4);

            if (var2 instanceof IIOPInputStream) {
                IIOPInputStream var19 = (IIOPInputStream)var2;
                var3 = var19.read_any(var6);
            } else {
                var3 = var2.read_any();
            }

            Any var8 = var3;
            return var8;
        } catch (ApplicationException var15) {
            var2 = var15.getInputStream();
            String var5 = var15.getId();
            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/NotFound:1.0")) {
                throw NotFoundHelper.read(var2);
            }

            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var16) {
            var3 = this.resolve_any(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public Any resolve_str_any(String var1) throws NotFound, CannotProceed, InvalidName {
        InputStream var2 = null;

        Any var3;
        try {
            OutputStream var4 = this._request("resolve_str_any", true);
            WStringNameHelper.write(var4, var1);
            var2 = this._invoke(var4);
            var3 = var2.read_any();
            Any var6 = var3;
            return var6;
        } catch (ApplicationException var11) {
            var2 = var11.getInputStream();
            String var5 = var11.getId();
            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/NotFound:1.0")) {
                throw NotFoundHelper.read(var2);
            }

            if (var5.equals("IDL:weblogic/corba/cos/naming/NamingContextAny/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var12) {
            var3 = this.resolve_str_any(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public String to_string(NameComponent[] var1) throws InvalidName {
        InputStream var2 = null;

        String var3;
        try {
            OutputStream var4 = this._request("to_string", true);
            NameHelper.write(var4, var1);
            var2 = this._invoke(var4);
            var3 = StringNameHelper.read(var2);
            String var6 = var3;
            return var6;
        } catch (ApplicationException var11) {
            var2 = var11.getInputStream();
            var3 = var11.getId();
            if (var3.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var3);
        } catch (RemarshalException var12) {
            var3 = this.to_string(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public NameComponent[] to_name(String var1) throws InvalidName {
        InputStream var2 = null;

        NameComponent[] var3;
        try {
            OutputStream var4 = this._request("to_name", true);
            StringNameHelper.write(var4, var1);
            var2 = this._invoke(var4);
            var3 = NameHelper.read(var2);
            NameComponent[] var6 = var3;
            return var6;
        } catch (ApplicationException var11) {
            var2 = var11.getInputStream();
            String var5 = var11.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var12) {
            var3 = this.to_name(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public String to_url(String var1, String var2) throws InvalidAddress, InvalidName {
        InputStream var3 = null;

        String var4;
        try {
            OutputStream var5 = this._request("to_url", true);
            AddressHelper.write(var5, var1);
            StringNameHelper.write(var5, var2);
            var3 = this._invoke(var5);
            var4 = URLStringHelper.read(var3);
            String var7 = var4;
            return var7;
        } catch (ApplicationException var12) {
            var3 = var12.getInputStream();
            var4 = var12.getId();
            if (var4.equals("IDL:omg.org/CosNaming/NamingContextExt/InvalidAddress:1.0")) {
                throw InvalidAddressHelper.read(var3);
            }

            if (var4.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            throw new MARSHAL(var4);
        } catch (RemarshalException var13) {
            var4 = this.to_url(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

        return var4;
    }

    public Object resolve_str(String var1) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName {
        InputStream var2 = null;

        Object var3;
        try {
            OutputStream var4 = this._request("resolve_str", true);
            StringNameHelper.write(var4, var1);
            var2 = this._invoke(var4);
            var3 = ObjectHelper.read(var2);
            Object var6 = var3;
            return var6;
        } catch (ApplicationException var11) {
            var2 = var11.getInputStream();
            String var5 = var11.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var12) {
            var3 = this.resolve_str(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public void bind(NameComponent[] var1, Object var2) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName, AlreadyBound {
        InputStream var3 = null;

        try {
            OutputStream var4 = this._request("bind", true);
            NameHelper.write(var4, var1);
            ObjectHelper.write(var4, var2);
            var3 = this._invoke(var4);
            return;
        } catch (ApplicationException var10) {
            var3 = var10.getInputStream();
            String var5 = var10.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(var3);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var11) {
            this.bind(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

    }

    public void rebind(NameComponent[] var1, Object var2) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName {
        InputStream var3 = null;

        try {
            OutputStream var4 = this._request("rebind", true);
            NameHelper.write(var4, var1);
            ObjectHelper.write(var4, var2);
            var3 = this._invoke(var4);
            return;
        } catch (ApplicationException var10) {
            var3 = var10.getInputStream();
            String var5 = var10.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var11) {
            this.rebind(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

    }

    public void bind_context(NameComponent[] var1, NamingContext var2) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName, AlreadyBound {
        InputStream var3 = null;

        try {
            OutputStream var4 = this._request("bind_context", true);
            NameHelper.write(var4, var1);
            NamingContextHelper.write(var4, var2);
            var3 = this._invoke(var4);
            return;
        } catch (ApplicationException var10) {
            var3 = var10.getInputStream();
            String var5 = var10.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(var3);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var11) {
            this.bind_context(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

    }

    public void rebind_context(NameComponent[] var1, NamingContext var2) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName {
        InputStream var3 = null;

        try {
            OutputStream var4 = this._request("rebind_context", true);
            NameHelper.write(var4, var1);
            NamingContextHelper.write(var4, var2);
            var3 = this._invoke(var4);
            return;
        } catch (ApplicationException var10) {
            var3 = var10.getInputStream();
            String var5 = var10.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var3);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var3);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var11) {
            this.rebind_context(var1, var2);
        } finally {
            this._releaseReply(var3);
        }

    }

    public Object resolve(NameComponent[] var1) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName {
        InputStream var2 = null;

        Object var3;
        try {
            OutputStream var4 = this._request("resolve", true);
            NameHelper.write(var4, var1);
            var2 = this._invoke(var4);
            var3 = ObjectHelper.read(var2);
            Object var6 = var3;
            return var6;
        } catch (ApplicationException var11) {
            var2 = var11.getInputStream();
            String var5 = var11.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var12) {
            var3 = this.resolve(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public void unbind(NameComponent[] var1) throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName {
        InputStream var2 = null;

        try {
            OutputStream var3 = this._request("unbind", true);
            NameHelper.write(var3, var1);
            var2 = this._invoke(var3);
            return;
        } catch (ApplicationException var9) {
            var2 = var9.getInputStream();
            String var4 = var9.getId();
            if (var4.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var2);
            }

            if (var4.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var2);
            }

            if (var4.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var4);
        } catch (RemarshalException var10) {
            this.unbind(var1);
        } finally {
            this._releaseReply(var2);
        }

    }

    public NamingContext new_context() {
        InputStream var1 = null;

        NamingContext var2;
        try {
            OutputStream var3 = this._request("new_context", true);
            var1 = this._invoke(var3);
            var2 = NamingContextHelper.read(var1);
            NamingContext var5 = var2;
            return var5;
        } catch (ApplicationException var10) {
            var1 = var10.getInputStream();
            String var4 = var10.getId();
            throw new MARSHAL(var4);
        } catch (RemarshalException var11) {
            var2 = this.new_context();
        } finally {
            this._releaseReply(var1);
        }

        return var2;
    }

    public NamingContext bind_new_context(NameComponent[] var1) throws org.omg.CosNaming.NamingContextPackage.NotFound, AlreadyBound, org.omg.CosNaming.NamingContextPackage.CannotProceed, InvalidName {
        InputStream var2 = null;

        NamingContext var3;
        try {
            OutputStream var4 = this._request("bind_new_context", true);
            NameHelper.write(var4, var1);
            var2 = this._invoke(var4);
            var3 = NamingContextHelper.read(var2);
            NamingContext var6 = var3;
            return var6;
        } catch (ApplicationException var11) {
            var2 = var11.getInputStream();
            String var5 = var11.getId();
            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.read(var2);
            }

            if (var5.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(var2);
            }

            throw new MARSHAL(var5);
        } catch (RemarshalException var12) {
            var3 = this.bind_new_context(var1);
        } finally {
            this._releaseReply(var2);
        }

        return var3;
    }

    public void destroy() throws NotEmpty {
        InputStream var1 = null;

        try {
            OutputStream var2 = this._request("destroy", true);
            var1 = this._invoke(var2);
            return;
        } catch (ApplicationException var8) {
            var1 = var8.getInputStream();
            String var3 = var8.getId();
            if (var3.equals("IDL:omg.org/CosNaming/NamingContext/NotEmpty:1.0")) {
                throw NotEmptyHelper.read(var1);
            }

            throw new MARSHAL(var3);
        } catch (RemarshalException var9) {
            this.destroy();
        } finally {
            this._releaseReply(var1);
        }

    }

    public void list(int var1, BindingListHolder var2, BindingIteratorHolder var3) {
        InputStream var4 = null;

        try {
            OutputStream var5 = this._request("list", true);
            var5.write_ulong(var1);
            var4 = this._invoke(var5);
            var2.value = BindingListHelper.read(var4);
            var3.value = BindingIteratorHelper.read(var4);
            return;
        } catch (ApplicationException var11) {
            var4 = var11.getInputStream();
            String var6 = var11.getId();
            throw new MARSHAL(var6);
        } catch (RemarshalException var12) {
            this.list(var1, var2, var3);
        } finally {
            this._releaseReply(var4);
        }

    }

    public String[] _ids() {
        return (String[])((String[])((String[])__ids.clone()));
    }

    private void readObject(ObjectInputStream var1) throws IOException {
        String var2 = var1.readUTF();
        java.lang.Object var3 = null;
        java.lang.Object var4 = null;
        ORB var5 = ORB.init((String[])((String[])var3), (Properties)var4);

        try {
            Object var6 = var5.string_to_object(var2);
            Delegate var7 = ((ObjectImpl)var6)._get_delegate();
            this._set_delegate(var7);
        } finally {
            var5.destroy();
        }

    }

    private void writeObject(ObjectOutputStream var1) throws IOException {
        java.lang.Object var2 = null;
        java.lang.Object var3 = null;
        ORB var4 = ORB.init((String[])((String[])var2), (Properties)var3);

        try {
            String var5 = var4.object_to_string(this);
            var1.writeUTF(var5);
        } finally {
            var4.destroy();
        }

    }
}
