//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.corba.idl;

import java.io.IOException;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;
import weblogic.corba.utils.ClassInfo;
import weblogic.iiop.IIOPInputStream;
import weblogic.iiop.IIOPOutputStream;
import weblogic.iiop.IOR;
import weblogic.iiop.Utils;

public class AnyImpl extends Any {
    private static final long serialVersionUID = -4455920080878260180L;
    private static final boolean DEBUG = false;
    private Object value;
    private long longValue = 0L;
    private double doubleValue = 0.0D;
    private TypeCode type = TypeCodeImpl.get_primitive_tc(0);
    private boolean initialized = false;

    public AnyImpl() {
        this.initialized = true;
    }

    public void type(TypeCode var1) {
        this.type = var1;
        this.reset();
    }

    public TypeCode type() {
        return this.type;
    }

    public boolean equal(Any var1) {
        if (var1 == this) {
            return true;
        } else if (var1 != null && var1 instanceof AnyImpl) {
            AnyImpl var2 = (AnyImpl)var1;
            return this.type.equal(var1.type()) && (this.value == null && var2.value == null || this.value != null && this.value.equals(var2.value)) && this.longValue == var2.longValue && this.doubleValue == var2.doubleValue;
        } else {
            return false;
        }
    }

    public boolean equals(Object var1) {
        return var1 instanceof Any ? this.equal((Any)var1) : false;
    }

    public void read_value(InputStream var1, TypeCode var2) throws MARSHAL {
        this.type(var2);
        this.read_value_internal(var1, var2);
    }

    /**
     * add read_value
     * @param var1 iiopinputstream
     * @param var2 typecode
     * @param ior ior
     */
    public void read_value(IIOPInputStream var1, TypeCode var2, IOR ior){
        this.type(var2);
        this.read_value_internal(var1, var2, ior);
    }

    /**
     * add read_value_internal
     * @param var1
     * @param var2
     * @param ior
     * @throws MARSHAL
     */
    private void read_value_internal(IIOPInputStream var1, TypeCode var2, IOR ior) throws MARSHAL {
        switch(var2.kind().value()) {
            case 0:
            case 1:
                break;
            case 2:
                this.insert_short(var1.read_short());
                break;
            case 3:
                this.insert_long(var1.read_long());
                break;
            case 4:
                this.insert_ushort(var1.read_ushort());
                break;
            case 5:
                this.insert_ulong(var1.read_ulong());
                break;
            case 6:
                this.insert_float(var1.read_float());
                break;
            case 7:
                this.insert_double(var1.read_double());
                break;
            case 8:
                this.insert_boolean(var1.read_boolean());
                break;
            case 9:
                this.insert_char(var1.read_char());
                break;
            case 10:
                this.insert_octet(var1.read_octet());
                break;
            case 11:
                this.value = var1.read_any();
                break;
            case 12:
                this.value = var1.read_TypeCode();
                break;
            case 13:
                this.value = var1.read_Principal();
                break;
            case 14:
                this.value = var1.read_Object(ior);
                break;
            case 15:
            case 16:
            case 17:
            case 19:
            case 20:
            case 22:
                ClassInfo var3 = ClassInfo.findClassInfo(TypeCodeImpl.getRepositoryId(var2));
                if (var3 != null && var3.forClass() != null) {
                    this.value = IIOPInputStream.read_IDLEntity(var1, var3.forClass());
                } else {
                    IIOPOutputStream var4 = new IIOPOutputStream();
                    copyStreamable(var4, var1, var2);
                    this.value = var4.createExactInputStream();
                }
                break;
            case 18:
                this.value = var1.read_string();
                break;
            case 21:
                try {
                    this.read_value_internal(var1, var2.content_type());
                    break;
                } catch (BadKind var5) {
                    throw new MARSHAL(var5.toString());
                }
            case 23:
                this.insert_longlong(var1.read_longlong());
                break;
            case 24:
                this.insert_ulonglong(var1.read_ulonglong());
                break;
            case 25:
                this.insert_double(var1.read_double());
                break;
            case 26:
                this.insert_wchar(var1.read_wchar());
                break;
            case 27:
                this.value = var1.read_wstring();
                break;
            case 28:
            case 31:
            default:
                throw new MARSHAL("Unsupported any type: " + var2);
            case 29:
            case 30:
                this.value = ((org.omg.CORBA_2_3.portable.InputStream)var1).read_value();
                break;
            case 32:
                this.value = ((org.omg.CORBA_2_3.portable.InputStream)var1).read_abstract_interface();
        }

        this.initialized = true;
    }



    private void read_value_internal(InputStream var1, TypeCode var2) throws MARSHAL {
        switch(var2.kind().value()) {
            case 0:
            case 1:
                break;
            case 2:
                this.insert_short(var1.read_short());
                break;
            case 3:
                this.insert_long(var1.read_long());
                break;
            case 4:
                this.insert_ushort(var1.read_ushort());
                break;
            case 5:
                this.insert_ulong(var1.read_ulong());
                break;
            case 6:
                this.insert_float(var1.read_float());
                break;
            case 7:
                this.insert_double(var1.read_double());
                break;
            case 8:
                this.insert_boolean(var1.read_boolean());
                break;
            case 9:
                this.insert_char(var1.read_char());
                break;
            case 10:
                this.insert_octet(var1.read_octet());
                break;
            case 11:
                this.value = var1.read_any();
                break;
            case 12:
                this.value = var1.read_TypeCode();
                break;
            case 13:
                this.value = var1.read_Principal();
                break;
            case 14:
                this.value = var1.read_Object();
                break;
            case 15:
            case 16:
            case 17:
            case 19:
            case 20:
            case 22:
                ClassInfo var3 = ClassInfo.findClassInfo(TypeCodeImpl.getRepositoryId(var2));
                if (var3 != null && var3.forClass() != null) {
                    this.value = IIOPInputStream.read_IDLEntity(var1, var3.forClass());
                } else {
                    IIOPOutputStream var4 = new IIOPOutputStream();
                    copyStreamable(var4, var1, var2);
                    this.value = var4.createExactInputStream();
                }
                break;
            case 18:
                this.value = var1.read_string();
                break;
            case 21:
                try {
                    this.read_value_internal(var1, var2.content_type());
                    break;
                } catch (BadKind var5) {
                    throw new MARSHAL(var5.toString());
                }
            case 23:
                this.insert_longlong(var1.read_longlong());
                break;
            case 24:
                this.insert_ulonglong(var1.read_ulonglong());
                break;
            case 25:
                this.insert_double(var1.read_double());
                break;
            case 26:
                this.insert_wchar(var1.read_wchar());
                break;
            case 27:
                this.value = var1.read_wstring();
                break;
            case 28:
            case 31:
            default:
                throw new MARSHAL("Unsupported any type: " + var2);
            case 29:
            case 30:
                this.value = ((org.omg.CORBA_2_3.portable.InputStream)var1).read_value();
                break;
            case 32:
                this.value = ((org.omg.CORBA_2_3.portable.InputStream)var1).read_abstract_interface();
        }

        this.initialized = true;
    }

    private static void copyStreamable(OutputStream var0, InputStream var1, TypeCode var2) {
        try {
            int var5;
            switch(var2.kind().value()) {
                case 0:
                case 1:
                    break;
                case 2:
                    var0.write_short(var1.read_short());
                    break;
                case 3:
                    var0.write_long(var1.read_long());
                    break;
                case 4:
                    var0.write_ushort(var1.read_ushort());
                    break;
                case 5:
                    var0.write_ulong(var1.read_ulong());
                    break;
                case 6:
                    var0.write_float(var1.read_float());
                    break;
                case 7:
                    var0.write_double(var1.read_double());
                    break;
                case 8:
                    var0.write_boolean(var1.read_boolean());
                    break;
                case 9:
                    var0.write_char(var1.read_char());
                    break;
                case 10:
                    var0.write_octet(var1.read_octet());
                    break;
                case 11:
                    var0.write_any(var1.read_any());
                    break;
                case 12:
                    var0.write_TypeCode(var1.read_TypeCode());
                    break;
                case 13:
                    var0.write_Principal(var1.read_Principal());
                    break;
                case 14:
                    var0.write_Object(var1.read_Object());
                    break;
                case 16:
                    AnyImpl var3 = new AnyImpl();
                    var3.read_value(var1, var2.discriminator_type());
                    var3.write_value(var0);
                    copyStreamable(var0, var1, var2.member_type(getUnionIndex(var2, var3)));
                    break;
                case 17:
                    var0.write_long(var1.read_long());
                    break;
                case 18:
                    var0.write_string(var1.read_string());
                    break;
                case 19:
                    int var4 = var1.read_ulong();
                    var0.write_ulong(var4);

                    for(var5 = 0; var5 < var4; ++var5) {
                        copyStreamable(var0, var1, var2.content_type());
                    }

                    return;
                case 20:
                    var5 = var2.length();

                    for(int var6 = 0; var6 < var5; ++var6) {
                        copyStreamable(var0, var1, var2.content_type());
                    }

                    return;
                case 21:
                case 30:
                    copyStreamable(var0, var1, var2.content_type());
                    break;
                case 22:
                    var0.write_string(var1.read_string());
                case 15:
                    for(int var9 = 0; var9 < var2.member_count(); ++var9) {
                        copyStreamable(var0, var1, var2.member_type(var9));
                    }

                    return;
                case 23:
                    var0.write_longlong(var1.read_longlong());
                    break;
                case 24:
                    var0.write_ulonglong(var1.read_ulonglong());
                    break;
                case 25:
                    var0.write_double(var1.read_double());
                    break;
                case 26:
                    var0.write_wchar(var1.read_wchar());
                    break;
                case 27:
                    var0.write_wstring(var1.read_wstring());
                    break;
                case 28:
                case 31:
                default:
                    throw new MARSHAL("Unsupported any type: " + var2);
                case 29:
                    ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_value(((org.omg.CORBA_2_3.portable.InputStream)var1).read_value());
                    break;
                case 32:
                    ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_abstract_interface(((org.omg.CORBA_2_3.portable.InputStream)var1).read_abstract_interface());
            }

        } catch (BadKind var7) {
            throw new MARSHAL("Corrupt any type: " + var2);
        } catch (Bounds var8) {
            throw new MARSHAL("Corrupt any type: " + var2);
        }
    }

    private static int getUnionIndex(TypeCode var0, Any var1) throws BadKind, Bounds {
        for(int var2 = 0; var2 < var0.member_count(); ++var2) {
            if (var0.member_label(var2).equal(var1)) {
                return var2;
            }
        }

        return var0.default_index();
    }

    public void write_value(OutputStream var1) {
        this.write_value(var1, this.type);
    }

    public void write_value(OutputStream var1, TypeCode var2) {
        if (var2 == null) {
            var2 = this.type;
            var1.write_TypeCode(var2);
        }

        int var3 = var2.kind().value();
        switch(var3) {
            case 0:
            case 1:
                break;
            case 2:
                var1.write_short(this.extract_short());
                break;
            case 3:
                var1.write_long(this.extract_long());
                break;
            case 4:
                var1.write_ushort(this.extract_ushort());
                break;
            case 5:
                var1.write_ulong(this.extract_ulong());
                break;
            case 6:
                var1.write_float(this.extract_float());
                break;
            case 7:
                var1.write_double(this.extract_double());
                break;
            case 8:
                var1.write_boolean(this.extract_boolean());
                break;
            case 9:
                var1.write_char(this.extract_char());
                break;
            case 10:
                var1.write_octet(this.extract_octet());
                break;
            case 11:
                var1.write_any((Any)this.value);
                break;
            case 12:
                var1.write_TypeCode((TypeCode)this.value);
                break;
            case 13:
                var1.write_Principal((Principal)this.value);
                break;
            case 14:
                var1.write_Object((org.omg.CORBA.Object)this.value);
                break;
            case 15:
            case 16:
            case 17:
            case 19:
            case 20:
            case 22:
                if (this.value instanceof IDLEntity) {
                    Class var4 = Utils.getClassFromID(TypeCodeImpl.getRepositoryId(var2));
                    IIOPOutputStream.write_IDLEntity(var1, (IDLEntity)this.value, var4);
                } else {
                    if (!(this.value instanceof InputStream)) {
                        throw new MARSHAL("Cannot marshal: " + this.value + " of type " + var3);
                    }

                    InputStream var8 = (InputStream)this.value;

                    try {
                        var8.reset();
                    } catch (IOException var6) {
                    }

                    copyStreamable(var1, var8, var2);
                }
                break;
            case 18:
                var1.write_string((String)this.value);
                break;
            case 21:
                try {
                    this.write_value(var1, var2.content_type());
                    break;
                } catch (BadKind var7) {
                    throw new MARSHAL(var7.toString());
                }
            case 23:
                var1.write_longlong(this.extract_longlong());
                break;
            case 24:
                var1.write_ulonglong(this.extract_ulonglong());
                break;
            case 25:
                var1.write_double(this.extract_double());
                break;
            case 26:
                var1.write_wchar(this.extract_wchar());
                break;
            case 27:
                var1.write_wstring((String)this.value);
                break;
            case 28:
            case 31:
            default:
                throw new MARSHAL("Unsupported any type: " + var3);
            case 29:
            case 30:
                ((org.omg.CORBA_2_3.portable.OutputStream)var1).write_value((Serializable)this.value);
                break;
            case 32:
                ((org.omg.CORBA_2_3.portable.OutputStream)var1).write_abstract_interface(this.value);
        }

    }

    public OutputStream create_output_stream() {
        return new IIOPOutputStream();
    }

    public InputStream create_input_stream() {
        IIOPOutputStream var1 = new IIOPOutputStream();
        this.write_value(var1);
        IIOPInputStream var2 = var1.createExactInputStream();
        return var2;
    }

    public Serializable extract_Value() throws BAD_OPERATION {
        if (this.type != null && this.value != null && this.initialized && (this.type.kind().value() == 29 || this.type.kind().value() == 30)) {
            return (Serializable)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_Value(Serializable var1) {
        this.type = TypeCodeImpl.get_primitive_tc(29);
        this.value = var1;
        this.initialized = true;
    }

    public void insert_Value(Serializable var1, TypeCode var2) {
        this.type = var2;
        this.value = var1;
        this.initialized = true;
    }

    public void insert_Streamable(Streamable var1) {
        this.type = var1._type();
        IIOPOutputStream var2 = new IIOPOutputStream();
        var1._write(var2);
        this.value = var2.createExactInputStream();
        this.initialized = true;
    }

    public short extract_short() throws BAD_OPERATION {
        if (this.type.kind().value() == 2 && this.initialized) {
            return (short)((int)(65535L & this.longValue));
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_short(short var1) {
        this.type = TypeCodeImpl.get_primitive_tc(2);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public int extract_long() throws BAD_OPERATION {
        if (this.type.kind().value() == 3 && this.initialized) {
            return (int)(-1L & this.longValue);
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_long(int var1) {
        this.type = TypeCodeImpl.get_primitive_tc(3);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public long extract_longlong() throws BAD_OPERATION {
        if (this.type.kind().value() == 23 && this.initialized) {
            return this.longValue;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_longlong(long var1) {
        this.type = TypeCodeImpl.get_primitive_tc(23);
        this.longValue = var1;
        this.initialized = true;
    }

    public short extract_ushort() throws BAD_OPERATION {
        if (this.type.kind().value() == 4 && this.initialized) {
            return (short)((int)(65535L & this.longValue));
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_ushort(short var1) {
        this.type = TypeCodeImpl.get_primitive_tc(4);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public int extract_ulong() throws BAD_OPERATION {
        if (this.type.kind().value() == 5 && this.initialized) {
            return (int)(-1L & this.longValue);
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_ulong(int var1) {
        this.type = TypeCodeImpl.get_primitive_tc(5);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public long extract_ulonglong() throws BAD_OPERATION {
        if (this.type.kind().value() == 24 && this.initialized) {
            return this.longValue;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_ulonglong(long var1) {
        this.type = TypeCodeImpl.get_primitive_tc(24);
        this.longValue = var1;
        this.initialized = true;
    }

    public float extract_float() throws BAD_OPERATION {
        if (this.type.kind().value() == 6 && this.initialized) {
            return (float)this.doubleValue;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_float(float var1) {
        this.type = TypeCodeImpl.get_primitive_tc(6);
        this.doubleValue = (double)var1;
        this.initialized = true;
    }

    public double extract_double() throws BAD_OPERATION {
        if (this.type.kind().value() == 7 && this.initialized) {
            return this.doubleValue;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_double(double var1) {
        this.type = TypeCodeImpl.get_primitive_tc(7);
        this.doubleValue = var1;
        this.initialized = true;
    }

    public boolean extract_boolean() throws BAD_OPERATION {
        if (this.type.kind().value() == 8 && this.initialized) {
            return this.longValue == 1L;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_boolean(boolean var1) {
        this.type = TypeCodeImpl.get_primitive_tc(8);
        this.longValue = (long)(var1 ? 1 : 0);
        this.initialized = true;
    }

    public char extract_char() throws BAD_OPERATION {
        if (this.type.kind().value() == 9 && this.initialized) {
            return (char)((int)this.longValue);
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_char(char var1) throws DATA_CONVERSION {
        this.type = TypeCodeImpl.get_primitive_tc(9);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public byte extract_octet() throws BAD_OPERATION {
        if (this.type.kind().value() == 10 && this.initialized) {
            return (byte)((int)(255L & this.longValue));
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_octet(byte var1) {
        this.type = TypeCodeImpl.get_primitive_tc(10);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public char extract_wchar() throws BAD_OPERATION {
        if (this.type.kind().value() == 26 && this.initialized) {
            return (char)((int)this.longValue);
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_wchar(char var1) {
        this.type = TypeCodeImpl.get_primitive_tc(26);
        this.longValue = (long)var1;
        this.initialized = true;
    }

    public Any extract_any() throws BAD_OPERATION {
        if (this.type != null && this.value != null && this.initialized && this.type.kind().value() == 11) {
            return (Any)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_any(Any var1) {
        this.type = TypeCodeImpl.get_primitive_tc(11);
        this.value = var1;
        this.initialized = true;
    }

    public String extract_string() throws BAD_OPERATION {
        if (this.type.kind().value() == 18 && this.initialized) {
            return (String)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_string(String var1) throws DATA_CONVERSION, MARSHAL {
        this.type = TypeCodeImpl.get_primitive_tc(18);
        this.value = var1;
        this.initialized = true;
    }

    public String extract_wstring() throws BAD_OPERATION {
        if (this.type.kind().value() == 27 && this.initialized) {
            return (String)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_wstring(String var1) throws MARSHAL {
        this.type = TypeCodeImpl.get_primitive_tc(27);
        this.value = var1;
        this.initialized = true;
    }

    public org.omg.CORBA.Object extract_Object() throws BAD_OPERATION {
        if (this.type != null && this.value != null && this.initialized && this.type.kind().value() == 14) {
            return (org.omg.CORBA.Object)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_Object(org.omg.CORBA.Object var1, TypeCode var2) throws BAD_OPERATION {
        this.type = var2;
        this.value = var1;
        this.initialized = true;
    }

    public void insert_Object(org.omg.CORBA.Object var1) {
        this.type = TypeCodeImpl.get_primitive_tc(14);
        this.value = var1;
        this.initialized = true;
    }

    public TypeCode extract_TypeCode() throws BAD_OPERATION {
        if (this.type != null && this.value != null && this.initialized && this.type.kind().value() == 12) {
            return (TypeCode)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_TypeCode(TypeCode var1) {
        this.type = TypeCodeImpl.get_primitive_tc(12);
        this.value = var1;
        this.initialized = true;
    }

    public Principal extract_Principal() throws BAD_OPERATION {
        if (this.type != null && this.value != null && this.initialized && this.type.kind().value() == 13) {
            return (Principal)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    public void insert_Principal(Principal var1) {
        this.type = TypeCodeImpl.get_primitive_tc(13);
        this.value = var1;
        this.initialized = true;
    }

    public void insert_IDLEntity(IDLEntity var1, TypeCode var2) {
        this.type = var2;
        this.value = var1;
        this.initialized = true;
    }

    public IDLEntity extract_IDLEntity() {
        if (this.type != null && this.value != null && this.initialized) {
            return (IDLEntity)this.value;
        } else {
            throw new BAD_OPERATION();
        }
    }

    private void reset() {
        this.value = null;
        this.longValue = 0L;
        this.doubleValue = 0.0D;
        this.initialized = false;
    }

    public static void write(OutputStream var0, Any var1, TypeCode var2) {
    }

    private static void p(String var0) {
        System.out.println("<AnyImpl> " + var0);
    }

    public String toString() {
        return this.type().toString() + ": " + this.value;
    }
}
