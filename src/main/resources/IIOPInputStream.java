//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package weblogic.iiop;

import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import weblogic.common.internal.PeerInfo;
import weblogic.common.internal.PeerInfoable;
import weblogic.corba.idl.AnyImpl;
import weblogic.corba.idl.TypeCodeImpl;
import weblogic.corba.utils.ClassInfo;
import weblogic.corba.utils.IndirectionHashtable;
import weblogic.corba.utils.RemoteInfo;
import weblogic.corba.utils.RepositoryId;
import weblogic.corba.utils.ValueHandlerImpl;
import weblogic.protocol.ServerChannel;
import weblogic.protocol.ServerChannelStream;
import weblogic.rmi.spi.MsgInput;
import weblogic.utils.AssertionError;
import weblogic.utils.Debug;
import weblogic.utils.Hex;
import weblogic.utils.StringUtils;
import weblogic.utils.collections.Pool;
import weblogic.utils.collections.StackPool;
import weblogic.utils.io.Chunk;
import weblogic.utils.io.ChunkInput;
import weblogic.utils.io.ChunkedInputStream;
import weblogic.utils.io.ObjectInput;
import weblogic.utils.io.ObjectStreamClass;
import weblogic.utils.io.StringInput;

public final class IIOPInputStream extends InputStream implements PeerInfoable, ServerChannelStream, StringInput, ObjectInput, ChunkInput, MsgInput, ValueInputStream {
    private static final int NULL_TAG = 0;
    private static final boolean debugValueTypes = false;
    private static final boolean DEBUG = false;
    private boolean SUPPORT_JDK_13_CHUNKING;
    private int minorVersion;
    private static final ValueHandler valueHandler;
    private static final String READ_METHOD = "read";
    private static final String INSERT_METHOD = "insert";
    private static final String TYPE_METHOD = "type";
    private static final Class[] READ_METHOD_ARGS;
    private static final Class[] NO_ARGS_METHOD;
    private static final Object[] NO_ARGS;
    private static final String NULL_STRING = "";
    private static final int VT_INDIRECTION = -1;
    private IIOPInputStream.Fragment fragmentHead;
    private IIOPInputStream.Fragment fragmentTail;
    private final IIOPInputStream.Marker marker;
    private Chunk head;
    private Chunk firstChunk;
    private int streamPos;
    private int chunkPos;
    private boolean chunking;
    private int chunkLength;
    private int endTag;
    private boolean littleEndian;
    private int needLongAlignment;
    private String annotation;
    private boolean needEightByteAlignment;
    private boolean needEightByteAlignmentSave;
    private boolean endianSave;
    private int alignSave;
    private boolean secure;
    private ORB orb;
    private static final boolean ASSERT = false;
    private final EndPoint endPoint;
    private int char_codeset;
    private int wchar_codeset;
    private java.io.ObjectInput oinput;
    private ObjectInputStream objectStream;
    private String possibleCodebase;
    private int nestingLevel;
    private IndirectionHashtable indirections;
    private IndirectionHashtable tcIndirections;
    private int tcNestingLevel;
    private IIOPInputStream parentStream;
    static final int MAX_ENCAP_SIZE = 67108864;
    static final int MAX_STRING_SIZE = 524288;
    private static final int MAP_POOL_SIZE = 1024;
    private static final Pool mapPool;
    private IndirectionHashtable encapsulations;
    private static Constructor odeCtor;
    private boolean readingObjectKey;

    static IndirectionHashtable getHashMap() {
        IndirectionHashtable var0 = (IndirectionHashtable)mapPool.remove();
        if (var0 == null) {
            var0 = new IndirectionHashtable();
        }

        return var0;
    }

    static void releaseHashMap(IndirectionHashtable var0) {
        if (var0 != null) {
            var0.clear();
            mapPool.add(var0);
        }

    }

    IIOPInputStream(ChunkedInputStream var1, boolean var2, EndPoint var3) {
        this(var1.getChunks(), var2, var3);
    }

    IIOPInputStream(Chunk var1, boolean var2, EndPoint var3) {
        this.SUPPORT_JDK_13_CHUNKING = true;
        this.minorVersion = 2;
        this.marker = new IIOPInputStream.Marker();
        this.streamPos = 0;
        this.chunkPos = 0;
        this.chunking = false;
        this.chunkLength = 0;
        this.endTag = 0;
        this.needLongAlignment = 0;
        this.needEightByteAlignment = false;
        this.alignSave = 0;
        this.secure = false;
        this.orb = null;
        this.nestingLevel = 0;
        this.indirections = null;
        this.tcIndirections = null;
        this.tcNestingLevel = 0;
        this.encapsulations = null;
        this.readingObjectKey = false;
        this.endPoint = var3;
        this.head = var1;
        this.indirections = getHashMap();
        if (var3 != null) {
            this.wchar_codeset = var3.getWcharCodeSet();
            this.char_codeset = var3.getCharCodeSet();
        } else {
            this.char_codeset = CodeSet.getDefaultCharCodeSet();
            this.wchar_codeset = CodeSet.getDefaultWcharCodeSet();
        }

        this.secure = var2;
        this.chunkPos = 0;
        this.orb = weblogic.corba.orb.ORB.getInstance();
    }

    public IIOPInputStream(byte[] var1) {
        this((Chunk)((Chunk)null), false, (EndPoint)null);
        Chunk var2 = null;
        int var3 = var1.length;
        int var4 = 0;

        int var6;
        for(this.SUPPORT_JDK_13_CHUNKING = false; var3 > 0; var2.end = var6) {
            Chunk var5 = var2;
            var2 = Chunk.getChunk();
            if (var5 != null) {
                var5.next = var2;
            } else {
                this.head = var2;
            }

            var6 = min(var2.buf.length, var3);
            System.arraycopy(var1, var4, var2.buf, 0, var6);
            var4 += var6;
            var3 -= var6;
        }

    }

    public IIOPInputStream(IIOPInputStream var1, EndPoint var2) {
        this((Chunk)null, false, var2);
        this.parentStream = var1;
        int var3 = var1.peek_long();
        long var4 = var1.startEncapsulation(false);
        this.SUPPORT_JDK_13_CHUNKING = var1.SUPPORT_JDK_13_CHUNKING;
        this.nestingLevel = var1.nestingLevel;
        this.readingObjectKey = var1.readingObjectKey;

        int var8;
        for(Chunk var6 = null; var3 > 0; var6.end = var8) {
            Chunk var7 = var6;
            var6 = Chunk.getChunk();
            if (var7 != null) {
                var7.next = var6;
            } else {
                this.head = var6;
            }

            var8 = min(var6.buf.length, var3);
            var1.read_octet_array((byte[])var6.buf, 0, var8);
            var3 -= var8;
        }

        var1.endEncapsulation(var4);
        this.consumeEndian();
    }

    public IIOPInputStream(IIOPInputStream var1) {
        this(var1, var1.endPoint);
    }

    public final void setSupportsJDK13Chunking(boolean var1) {
        this.SUPPORT_JDK_13_CHUNKING = var1;
    }

    void setPossibleCodebase(String var1) {
        this.possibleCodebase = var1;
    }

    private String getPossibleCodebase() {
        if (this.possibleCodebase != null) {
            return this.possibleCodebase;
        } else {
            return this.parentStream != null ? this.parentStream.getPossibleCodebase() : null;
        }
    }

    private final boolean useCompliantChunking() {
        return !this.SUPPORT_JDK_13_CHUNKING || this.nestingLevel != 0 && this.readingObjectKey;
    }

    public int getMinorVersion() {
        return this.endPoint == null || this.nestingLevel != 0 && (!this.SUPPORT_JDK_13_CHUNKING || this.readingObjectKey) ? 2 : this.minorVersion;
    }

    public void setMinorVersion(int var1) {
        this.minorVersion = var1;
    }

    public PeerInfo getPeerInfo() {
        return this.endPoint != null && this.endPoint.getPeerInfo() != null ? this.endPoint.getPeerInfo() : PeerInfo.FOREIGN;
    }

    private final Object getIndirection(int var1) {
        return this.indirections == null ? null : this.indirections.get(var1);
    }

    private final void addIndirection(int var1, Object var2) {
        this.indirections.put(var1, var2);
    }

    public EndPoint getEndPoint() {
        return this.endPoint;
    }

    Chunk getChunks() {
        return this.head;
    }

    void addChunks(IIOPInputStream var1) {
        if (this.fragmentHead == null) {
            this.fragmentHead = this.fragmentTail = new IIOPInputStream.Fragment(var1.getChunks(), var1.chunkPos);
        } else {
            this.fragmentTail.next = new IIOPInputStream.Fragment(var1.getChunks(), var1.chunkPos);
            this.fragmentTail = this.fragmentTail.next;
        }

    }

    public void setCodeSets(int var1, int var2) {
        this.char_codeset = var1;
        this.wchar_codeset = var2;
    }

    private final int getWcharCodeSet() {
        return this.nestingLevel > 0 && !this.SUPPORT_JDK_13_CHUNKING && !this.readingObjectKey ? 65801 : this.wchar_codeset;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public java.io.ObjectInput getObjectInput(boolean var1) {
        if (!var1) {
            return this;
        } else {
            if (this.oinput == null) {
                this.oinput = new IDLMsgInput(this);
            }

            return this.oinput;
        }
    }

    public ObjectInputStream getObjectInputStream(Object var1, ObjectStreamClass var2, boolean var3, byte var4) throws IOException {
        if (this.objectStream == null) {
            this.objectStream = new ObjectInputStreamImpl(this, var1, var2, var3, var4);
        } else {
            ((ObjectInputStreamImpl)this.objectStream).pushCurrent(var1, var2, var3, var4);
        }

        return this.objectStream;
    }

    public boolean consumeEndian() {
        boolean var1 = this.littleEndian;
        this.littleEndian = (this.read_octet() & 1) == 1;
        return var1;
    }

    void setEndian(boolean var1) {
        this.littleEndian = var1;
    }

    void setORB(ORB var1) {
        this.orb = var1;
    }

    void recordStart() {
        this.firstChunk = this.head;
    }

    public boolean isLittleEndian() {
        return this.littleEndian;
    }

    public final byte read_octet() {
        if (this.needEightByteAlignment) {
            this.checkEightByteAlignment();
        }

        if (this.chunkPos == this.head.end) {
            this.advance();
        }

        if (this.chunking) {
            this.checkChunk(1);
        }

        return this.head.buf[this.chunkPos++];
    }

    private void startChunk() {
        this.chunking = false;
        this.mark(0);
        this.chunkLength = this.read_long();
        if (this.chunkLength >= 0 && this.chunkLength < 2147483392) {
            this.clearMark();
        } else {
            this.reset();
        }

        if (this.useCompliantChunking()) {
            --this.endTag;
        }

        this.chunking = true;
    }

    private final void checkChunk(int var1) {
        if (this.endOfChunk(var1)) {
            throw new MARSHAL("stream corrupted: reading past end of chunk at: " + this.pos(), 1330446344, CompletionStatus.COMPLETED_NO);
        }
    }

    private final boolean endOfChunk(int var1) {
        if (!this.chunking) {
            return false;
        } else {
            if (var1 >= this.chunkLength && (var1 == 0 || var1 > this.chunkLength)) {
                if (this.chunkLength != 0) {
                    return true;
                }

                if (this.peek_long() >= 2147483392) {
                    return false;
                }

                if (!this.continuation()) {
                    return true;
                }
            }

            this.chunkLength -= var1;
            return false;
        }
    }

    private boolean continuation() {
        int var1 = this.peek_long();
        if (var1 == 0) {
            throw new MARSHAL("stream corrupted: '0' tag reserved");
        } else if (var1 > 0 && var1 < 2147483392) {
            this.chunking = false;
            this.chunkLength = this.read_long();
            this.chunking = true;
            return true;
        } else {
            return false;
        }
    }

    private void endChunk(boolean var1) {
        if (this.chunking) {
            this.chunking = false;
            if (this.chunkLength > 0) {
                this.skip((long)this.chunkLength);
                this.chunkLength = 0;
            }

            int var2;
            for(var2 = this.peek_long(); var2 < this.endTag; var2 = this.peek_long()) {
                this.read_long();
                this.continuation();
                this.skip((long)this.chunkLength);
                this.chunkLength = 0;
            }

            if (var2 > this.endTag) {
                if (!this.useCompliantChunking()) {
                    this.chunking = var1;
                } else {
                    ++this.endTag;
                    this.chunking = true;
                }

            } else {
                this.read_long();
                if (!this.useCompliantChunking()) {
                    this.chunking = var1;
                } else {
                    this.chunking = var2 < -1;
                    ++this.endTag;
                }

                if (this.chunking) {
                    this.continuation();
                }

            }
        }
    }

    boolean eof() {
        return this.chunkPos == this.head.end && this.head.next == null && this.fragmentHead == null || this.endOfChunk(0);
    }

    private static final int min(int var0, int var1) {
        return var0 <= var1 ? var0 : var1;
    }

    public final void read_octet_array(byte[] var1, int var2, int var3) {
        if (this.needEightByteAlignment) {
            this.checkEightByteAlignment();
        }

        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_octet_array");
        } else if (var3 != 0) {
            if (this.chunking) {
                this.checkChunk(0);
            }

            while(var3 > 0) {
                if (this.chunkPos == this.head.end) {
                    this.advance();
                }

                int var4 = min(this.head.end - this.chunkPos, var3);
                this.checkChunk(var4);
                System.arraycopy(this.head.buf, this.chunkPos, var1, var2, var4);
                this.chunkPos += var4;
                var2 += var4;
                var3 -= var4;
            }

        }
    }

    public void read_octet_array(OctetSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null OctetSeqHolder as parameter to read_octet_array");
        } else {
            this.read_octet_array(var1.value, var2, var3);
        }
    }

    public byte[] read_octet_sequence() {
        byte[] var1 = null;
        int var2 = this.read_long();
        if (var2 > 67108864) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read octet sequence of length " + Integer.toHexString(var2));
        } else {
            if (var2 > 0) {
                var1 = new byte[var2];
                this.read_octet_array((byte[])var1, 0, var2);
            }

            return var1;
        }
    }

    public boolean read_boolean() {
        boolean var1 = this.read_octet() != 0;
        return var1;
    }

    public void read_boolean_array(boolean[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_boolean_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_boolean();
            }

        }
    }

    public void read_boolean_array(BooleanSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null BooleanSeqHolder as parameter to read_boolean_array");
        } else {
            this.read_boolean_array(var1.value, var2, var3);
        }
    }

    private final void alignWithoutChunking(int var1) {
        if (this.needEightByteAlignment) {
            this.checkEightByteAlignment();
        }

        int var2 = this.streamPos + this.chunkPos + this.needLongAlignment;
        int var3 = (var1 - var2 % var1) % var1;
        if (var3 <= this.head.end - this.chunkPos) {
            this.chunkPos += var3;
        } else {
            this.skip((long)var3);
        }

    }

    final void align(int var1) {
        if (this.needEightByteAlignment) {
            this.checkEightByteAlignment();
        }

        int var2 = this.streamPos + this.chunkPos + this.needLongAlignment;
        int var3 = (var1 - var2 % var1) % var1;
        if (var3 <= this.head.end - this.chunkPos) {
            this.chunkPos += var3;
        } else {
            this.skip((long)var3);
        }

        if (this.chunking) {
            Debug.assertion(this.chunkLength == 0 || this.chunkLength >= var3);
            this.chunkLength -= min(var3, this.chunkLength);
        }

    }

    public final void setNeedEightByteAlignment() {
        this.needEightByteAlignment = true;
    }

    public final void checkEightByteAlignment() {
        this.needEightByteAlignment = false;
        this.align(8);
    }

    public void mark(int var1) {
        this.mark(this.marker);
        this.endianSave = this.littleEndian;
        this.alignSave = this.needLongAlignment;
        this.needEightByteAlignmentSave = this.needEightByteAlignment;
    }

    public boolean markSupported() {
        return true;
    }

    void mark(IIOPInputStream.Marker var1) {
        var1.streamPos = this.streamPos;
        var1.chunkPos = this.chunkPos;
        var1.head = this.head;
        var1.fragmentHead = this.fragmentHead;
        var1.chunkLength = this.chunkLength;
    }

    final void reset(IIOPInputStream.Marker var1) {
        this.streamPos = var1.streamPos;
        this.chunkPos = var1.chunkPos;
        this.head = var1.head;
        this.chunkLength = var1.chunkLength;
        this.fragmentHead = var1.fragmentHead;
    }

    public void reset() {
        if (this.marker.head == null) {
            this.streamPos = 0;
            this.chunkPos = 0;
            this.head = this.firstChunk;
            this.chunkLength = 0;
            if (this.tcIndirections != null) {
                this.tcIndirections.clear();
            }

            if (this.indirections != null) {
                this.indirections.clear();
            }
        } else {
            this.reset(this.marker);
            this.marker.head = null;
            this.marker.fragmentHead = null;
        }

        this.littleEndian = this.endianSave;
        this.needLongAlignment = this.alignSave;
        this.needEightByteAlignment = this.needEightByteAlignmentSave;
    }

    public void clearMark() {
        while(this.marker.head != null && this.marker.head != this.head && this.firstChunk == null) {
            Chunk var1 = this.marker.head;
            this.marker.head = this.marker.head.next;
            Chunk.releaseChunk(var1);
        }

        this.marker.head = null;
        this.marker.fragmentHead = null;
    }

    public long skip(long var1) {
        while(var1 > 0L) {
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            int var3 = min(this.head.end - this.chunkPos, (int)var1);
            this.chunkPos += var3;
            var1 -= (long)var3;
        }

        return var1;
    }

    public final int bytesLeft(long var1) {
        var1 &= 536870911L;
        return var1 > (long)this.pos() ? (int)(var1 - (long)this.pos()) : 0;
    }

    public final int pos() {
        return this.streamPos + this.chunkPos;
    }

    private final void advance() {
        this.streamPos += this.head.end;
        this.chunkPos = 0;
        Chunk var1 = this.head.next;
        if (this.marker.head == null && this.firstChunk == null) {
            Chunk.releaseChunk(this.head);
        }

        this.head = var1;
        if (this.head == null) {
            if (this.fragmentHead == null) {
                throw new MARSHAL("EOF at " + this.pos());
            }

            this.streamPos -= this.fragmentHead.start;
            this.head = this.fragmentHead.chunk;
            this.chunkPos = this.fragmentHead.start;
            this.fragmentHead = this.fragmentHead.next;
        }

    }

    public void close() {
        releaseHashMap(this.indirections);
        this.indirections = null;
        releaseHashMap(this.tcIndirections);
        this.tcIndirections = null;
        releaseHashMap(this.encapsulations);
        this.encapsulations = null;
        this.marker.head = null;
        this.firstChunk = null;
        this.marker.fragmentHead = null;
        this.fragmentTail = null;
        this.annotation = null;
        releaseChunks(this.head);
        this.head = null;

        IIOPInputStream.Fragment var1;
        for(this.possibleCodebase = null; this.fragmentHead != null; this.fragmentHead = var1) {
            var1 = this.fragmentHead.next;
            releaseChunks(this.fragmentHead.chunk);
        }

        this.parentStream = null;
    }

    private static void releaseChunks(Chunk var0) {
        while(var0 != null) {
            Chunk var1 = var0.next;
            Chunk.releaseChunk(var0);
            var0 = var1;
        }

    }

    public short read_short() {
        if (this.chunking) {
            this.align(2);
            this.checkChunk(2);
        } else {
            this.alignWithoutChunking(2);
        }

        int var1;
        int var2;
        if (this.chunkPos + 2 <= this.head.end) {
            var1 = this.head.buf[this.chunkPos++] & 255;
            var2 = this.head.buf[this.chunkPos++] & 255;
        } else {
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var1 = this.head.buf[this.chunkPos++] & 255;
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var2 = this.head.buf[this.chunkPos++] & 255;
        }

        short var3;
        if (this.littleEndian) {
            var3 = (short)(var2 << 8 | var1);
        } else {
            var3 = (short)(var1 << 8 | var2);
        }

        return var3;
    }

    public void read_short_array(short[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_short_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_short();
            }

        }
    }

    public void read_short_array(ShortSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null ShortSeqHolder as parameter to read_short_array");
        } else {
            this.read_short_array(var1.value, var2, var3);
        }
    }

    public short read_ushort() {
        return this.read_short();
    }

    public int read_unsigned_short() {
        this.align(2);
        int var1 = this.read_octet() & 255;
        int var2 = this.read_octet() & 255;
        int var3;
        if (this.littleEndian) {
            var3 = var2 << 8 | var1;
        } else {
            var3 = var1 << 8 | var2;
        }

        return var3;
    }

    public int peek_slow_long() {
        this.mark(0);
        boolean var1 = this.chunking;
        this.chunking = false;
        int var2 = this.read_long();
        this.chunking = var1;
        this.reset();
        return var2;
    }

    public final int peek_long() {
        int var1 = this.streamPos + this.chunkPos + this.needLongAlignment;
        int var2 = this.needEightByteAlignment ? 8 : 4;
        int var3 = (var2 - var1 % var2) % var2 + this.chunkPos;
        return var3 + 4 <= this.head.end && !this.littleEndian ? (this.head.buf[var3++] & 255) << 24 | (this.head.buf[var3++] & 255) << 16 | (this.head.buf[var3++] & 255) << 8 | this.head.buf[var3++] & 255 : this.peek_slow_long();
    }

    public final int read_long() {
        if (this.chunking) {
            this.align(4);
            this.checkChunk(4);
        } else {
            this.alignWithoutChunking(4);
        }

        int var1;
        int var2;
        int var3;
        int var4;
        if (this.chunkPos + 4 <= this.head.end) {
            var1 = this.head.buf[this.chunkPos++] & 255;
            var2 = this.head.buf[this.chunkPos++] & 255;
            var3 = this.head.buf[this.chunkPos++] & 255;
            var4 = this.head.buf[this.chunkPos++] & 255;
        } else {
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var1 = this.head.buf[this.chunkPos++] & 255;
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var2 = this.head.buf[this.chunkPos++] & 255;
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var3 = this.head.buf[this.chunkPos++] & 255;
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var4 = this.head.buf[this.chunkPos++] & 255;
        }

        int var5;
        if (this.littleEndian) {
            var5 = var4 << 24 | var3 << 16 | var2 << 8 | var1;
        } else {
            var5 = var1 << 24 | var2 << 16 | var3 << 8 | var4;
        }

        return var5;
    }

    public void read_long_array(int[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_long_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_long();
            }

        }
    }

    public void read_long_array(LongSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null LongSeqHolder as parameter to read_long_array");
        } else {
            this.read_long_array(var1.value, var2, var3);
        }
    }

    public final int read_ulong() {
        return this.read_long();
    }

    public void read_ulong_array(int[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_ulong_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_ulong();
            }

        }
    }

    public void read_ulong_array(ULongSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null ULongSeqHolder as parameter to read_ulong_array");
        } else {
            this.read_ulong_array(var1.value, var2, var3);
        }
    }

    public final long read_longlong() {
        if (this.chunking) {
            this.align(4);
            this.checkChunk(8);
            this.align(8);
        } else {
            this.alignWithoutChunking(8);
        }

        long var1;
        long var3;
        long var5;
        long var7;
        long var9;
        long var11;
        long var13;
        long var15;
        if (this.chunkPos + 8 <= this.head.end) {
            var1 = (long)(this.head.buf[this.chunkPos++] & 255);
            var3 = (long)(this.head.buf[this.chunkPos++] & 255);
            var5 = (long)(this.head.buf[this.chunkPos++] & 255);
            var7 = (long)(this.head.buf[this.chunkPos++] & 255);
            var9 = (long)(this.head.buf[this.chunkPos++] & 255);
            var11 = (long)(this.head.buf[this.chunkPos++] & 255);
            var13 = (long)(this.head.buf[this.chunkPos++] & 255);
            var15 = (long)(this.head.buf[this.chunkPos++] & 255);
        } else {
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var1 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var3 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var5 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var7 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var9 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var11 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var13 = (long)(this.head.buf[this.chunkPos++] & 255);
            if (this.chunkPos == this.head.end) {
                this.advance();
            }

            var15 = (long)(this.head.buf[this.chunkPos++] & 255);
        }

        long var17;
        if (this.littleEndian) {
            var17 = var15 << 56 | var13 << 48 | var11 << 40 | var9 << 32 | var7 << 24 | var5 << 16 | var3 << 8 | var1;
        } else {
            var17 = var1 << 56 | var3 << 48 | var5 << 40 | var7 << 32 | var9 << 24 | var11 << 16 | var13 << 8 | var15;
        }

        return var17;
    }

    public void read_longlong_array(long[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_longlong_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_longlong();
            }

        }
    }

    public void read_longlong_array(LongLongSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null LongLongSeqHolder as parameter to read_longlong_array");
        } else {
            this.read_longlong_array(var1.value, var2, var3);
        }
    }

    public long read_ulonglong() {
        return this.read_longlong();
    }

    public void read_ulonglong_array(long[] var1, int var2, int var3) {
        this.read_longlong_array(var1, var2, var3);
    }

    public void read_ulonglong_array(ULongLongSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null ULongLongSeqHolder as parameter to read_ulonglong_array");
        } else {
            this.read_ulonglong_array(var1.value, var2, var3);
        }
    }

    public float read_float() {
        return Float.intBitsToFloat(this.read_long());
    }

    public void read_float_array(float[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_float_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_float();
            }

        }
    }

    public void read_float_array(FloatSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null FloatSeqHolder as parameter to read_float_array");
        } else {
            this.read_float_array(var1.value, var2, var3);
        }
    }

    public double read_double() {
        return Double.longBitsToDouble(this.read_longlong());
    }

    public void read_double_array(double[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_double_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_double();
            }

        }
    }

    public void read_double_array(DoubleSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null DoubleSeqHolder as parameter to read_double_array");
        } else {
            this.read_double_array(var1.value, var2, var3);
        }
    }

    public char read_char() {
        return (char)(this.read_octet() & 255);
    }

    public void read_char_array(char[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_char_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_char();
            }

        }
    }

    public void read_char_array(CharSeqHolder var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null CharSeqHolder as parameter to read_char_array");
        } else {
            this.read_char_array(var1.value, var2, var3);
        }
    }

    public char read_wchar() {
        byte var1 = 0;
        byte var2 = 0;
        int var3 = 0;
        boolean var4 = this.littleEndian;
        byte var5;
        switch(this.getWcharCodeSet()) {
            case 65792:
                switch(this.getMinorVersion()) {
                    case 0:
                    case 1:
                        this.align(2);
                        var1 = this.read_octet();
                        var2 = this.read_octet();
                        break;
                    case 2:
                        var5 = this.read_octet();
                        var1 = this.read_octet();
                        var2 = this.read_octet();
                }

                if (var4) {
                    var3 = ((var2 & 255) << 8) + ((var1 & 255) << 0);
                } else {
                    var3 = ((var1 & 255) << 8) + ((var2 & 255) << 0);
                }
                break;
            case 65801:
                switch(this.getMinorVersion()) {
                    case 0:
                    case 1:
                        this.align(2);
                        var1 = this.read_octet();
                        var2 = this.read_octet();
                        break;
                    case 2:
                        var5 = this.read_octet();
                        var1 = this.read_octet();
                        var2 = this.read_octet();
                        var4 = false;
                        if (var1 == -1 && var2 == -2) {
                            var1 = this.read_octet();
                            var2 = this.read_octet();
                            var4 = true;
                        } else if (var1 == -2 && var2 == -1) {
                            var1 = this.read_octet();
                            var2 = this.read_octet();
                        }
                }

                if (var4) {
                    var3 = ((var2 & 255) << 8) + ((var1 & 255) << 0);
                } else {
                    var3 = ((var1 & 255) << 8) + ((var2 & 255) << 0);
                }
                break;
            case 83951617:
                var3 = this.read_UTF8wchar();
        }

        return (char)var3;
    }

    public int read() {
        return this.chunkPos == this.head.end && this.head.next == null && this.fragmentHead == null ? -1 : this.read_octet() & 255;
    }

    public ORB orb() {
        return this.orb;
    }

    public void read_wchar_array(char[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_wchar_array");
        } else {
            for(int var4 = 0; var4 < var3; ++var4) {
                var1[var4 + var2] = this.read_wchar();
            }

        }
    }

    /**
     * add read_object
     * @param var1 class
     * @param ior ior
     * @return
     */
    public org.omg.CORBA.Object read_Object(Class var1, IOR ior) {
        IOR var2 = new IOR(this);
        try{
            Field profielsField = IOR.class.getDeclaredField("profiles");
            profielsField.setAccessible(true);
            Field iopProfileField = IOR.class.getDeclaredField("iopProfile");
            iopProfileField.setAccessible(true);
            IOPProfile iopProfile = ior.getProfile();
            iopProfileField.set(var2, iopProfile);
            profielsField.set(var2, new Profile[]{iopProfile});
        }catch (Exception e){
            //
        }

        if (var2.isNull()) {
            return null;
        } else {
            if (var1 == null && var2.getTypeId().isIDLType()) {
                RemoteInfo var3 = RemoteInfo.findRemoteInfo(var2.getTypeId(), var2.getCodebase());
                if (var3 != null) {
                    Class var4 = var3.getTheClass();
                    if (var4 != null) {
                        var1 = Utils.getStubFromClass(var4);
                    }
                }
            }

            try {
                if (var2.getTypeId().isIDLType()) {
                    try {
                        return IIOPReplacer.createCORBAStub(var2, var1 == null ? null : Utils.getClassFromStub(var1), var1);
                    } catch (InstantiationException var5) {
                        return IIOPReplacer.makeInvocationHandler(var2, var1);
                    }
                } else {
                    return IIOPReplacer.makeInvocationHandler(var2, var1);
                }
            } catch (IOException var6) {
                throw new MARSHAL("IOException reading CORBA object " + var6.getMessage());
            } catch (IllegalAccessException var7) {
                throw new MARSHAL("IllegalAccessException reading CORBA object " + var7.getMessage());
            }
        }
    }

    public org.omg.CORBA.Object read_Object(Class var1) {
        IOR var2 = new IOR(this);
        if (var2.isNull()) {
            return null;
        } else {
            if (var1 == null && var2.getTypeId().isIDLType()) {
                RemoteInfo var3 = RemoteInfo.findRemoteInfo(var2.getTypeId(), var2.getCodebase());
                if (var3 != null) {
                    Class var4 = var3.getTheClass();
                    if (var4 != null) {
                        var1 = Utils.getStubFromClass(var4);
                    }
                }
            }

            try {
                if (var2.getTypeId().isIDLType()) {
                    try {
                        return IIOPReplacer.createCORBAStub(var2, var1 == null ? null : Utils.getClassFromStub(var1), var1);
                    } catch (InstantiationException var5) {
                        return IIOPReplacer.makeInvocationHandler(var2, var1);
                    }
                } else {
                    return IIOPReplacer.makeInvocationHandler(var2, var1);
                }
            } catch (IOException var6) {
                throw new MARSHAL("IOException reading CORBA object " + var6.getMessage());
            } catch (IllegalAccessException var7) {
                throw new MARSHAL("IllegalAccessException reading CORBA object " + var7.getMessage());
            }
        }
    }

    public org.omg.CORBA.Object read_Object() {
        return this.read_Object((Class)null);
    }

    /**
     * add read_object
     * @param ior
     * @return
     */
    public org.omg.CORBA.Object read_Object(IOR ior) {
        return this.read_Object((Class)null, ior);
    }

    private final char read_UTF8wchar() {
        this.align(1);
        long var1 = 0L;
        boolean var3 = false;
        if (this.getMinorVersion() >= 2) {
            byte var4 = this.read_octet();
        }

        return this.readUTF8wchar();
    }

    private final char readUTF8wchar() {
        int var1 = this.read_octet() & 255;
        if ((var1 & 128) != 0) {
            int var2;
            if ((var1 & 224) == 192) {
                var2 = this.read_octet() & 255;
                var1 = ((var1 & 31) << 6) + (var2 & 63);
            } else {
                var2 = this.read_octet() & 255;
                int var3 = this.read_octet() & 255;
                var1 = ((var1 & 15) << 12) + ((var2 & 63) << 6) + (var3 & 63);
            }
        }

        return (char)var1;
    }

    private final String readUTF8String(int var1) {
        char[] var2 = new char[var1];
        int var3 = 0;
        int var4 = 0;
        this.checkChunk(var1);
        boolean var5 = this.chunking;

        int var8;
        for(this.chunking = false; var3 < var1; this.chunkPos = var8) {
            int var6;
            while(this.head.end - this.chunkPos < 3 && var3 < var1) {
                var6 = this.pos();
                var2[var4++] = this.readUTF8wchar();
                var3 += this.pos() - var6;
            }

            var6 = min(this.head.end - this.chunkPos - 2, var1 - var3);

            int var7;
            for(var8 = this.chunkPos; var8 < this.chunkPos + var6; var2[var4++] = (char)var7) {
                var7 = this.head.buf[var8++] & 255;
                if ((var7 & 128) != 0) {
                    int var9;
                    if ((var7 & 224) == 192) {
                        var9 = this.head.buf[var8++] & 255;
                        var7 = ((var7 & 31) << 6) + (var9 & 63);
                    } else {
                        var9 = this.head.buf[var8++] & 255;
                        int var10 = this.head.buf[var8++] & 255;
                        var7 = ((var7 & 15) << 12) + ((var9 & 63) << 6) + (var10 & 63);
                    }
                }
            }

            var3 += var8 - this.chunkPos;
        }

        this.chunking = var5;
        return StringUtils.getString(var2, 0, var4);
    }

    private final char readUTF16wchar(boolean var1) {
        int var2 = this.read_octet() & 255;
        int var3 = this.read_octet() & 255;
        return var1 ? (char)((var3 << 8) + (var2 << 0)) : (char)((var2 << 8) + (var3 << 0));
    }

    private final String readUTF16String(int var1) {
        if (var1 < 2) {
            return "";
        } else {
            char[] var2 = new char[var1 / 2];
            int var3 = 2;
            int var4 = 0;
            boolean var5 = this.littleEndian;
            this.checkChunk(var1);
            boolean var6 = this.chunking;
            this.chunking = false;
            int var7 = this.read_octet() & 255;
            int var8 = this.read_octet() & 255;
            if (var7 == 255 && var8 == 254) {
                var5 = true;
            } else if (var7 == 254 && var8 == 255) {
                var5 = false;
            } else if (var5) {
                var2[var4++] = (char)((var8 << 8) + (var7 << 0));
            } else {
                var2[var4++] = (char)((var7 << 8) + (var8 << 0));
            }

            int var9;
            for(; var3 < var1; var3 += var9) {
                if (this.head.end - this.chunkPos < 2) {
                    var2[var4++] = this.readUTF16wchar(var5);
                    var3 += 2;
                }

                var9 = min(this.head.end - this.chunkPos - (this.head.end - this.chunkPos) % 2, var1 - var3);
                int var10 = this.chunkPos;
                this.chunkPos += var9;
                if (var5) {
                    while(var10 < this.chunkPos) {
                        var7 = this.head.buf[var10++] & 255;
                        var8 = this.head.buf[var10++] & 255;
                        var2[var4++] = (char)((var8 << 8) + (var7 << 0));
                    }
                } else {
                    while(var10 < this.chunkPos) {
                        var7 = this.head.buf[var10++] & 255;
                        var8 = this.head.buf[var10++] & 255;
                        var2[var4++] = (char)((var7 << 8) + (var8 << 0));
                    }
                }
            }

            this.chunking = var6;
            return StringUtils.getString(var2, 0, var4);
        }
    }

    public final String read_wstring() {
        int var1 = this.read_ulong();
        if (var1 == 0) {
            return "";
        } else if (var1 > 67108864) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read wstring of length " + Integer.toHexString(var1));
        } else if (this.getMinorVersion() < 2) {
            return GIOP10Helper.read_wstring(this, this.getWcharCodeSet(), this.littleEndian, var1);
        } else {
            String var2 = null;
            switch(this.getWcharCodeSet()) {
                case 65792:
                case 65801:
                    var2 = this.readUTF16String(var1);
                    break;
                case 83951617:
                    var2 = this.readUTF8String(var1);
                    break;
                default:
                    throw new CODESET_INCOMPATIBLE("Unsupported codeset: " + Integer.toHexString(this.getWcharCodeSet()));
            }

            return var2;
        }
    }

    public final Chunk readChunks() throws IOException {
        int var1 = this.read_long();
        Debug.assertion(this.fragmentHead == null);
        Chunk var2;
        if (var1 > this.head.end - this.chunkPos) {
            var2 = Chunk.split(this.head, this.chunkPos);
            Chunk var3 = Chunk.split(var2, var1);
            this.head.next = var3;
        } else {
            var2 = Chunk.getChunk();
            System.arraycopy(this.head.buf, this.chunkPos, var2.buf, 0, var1);
            this.chunkPos += var1;
        }

        return var2;
    }

    public void read_ushort_array(short[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to read_ushort_array");
        } else {
            for(int var4 = var2; var4 < var3; ++var4) {
                var1[var4] = this.read_ushort();
            }

        }
    }

    public final Any read_any() {
        return this.read_any(this.read_TypeCode());
    }


    public final Any read_any(IOR ior){
        return this.read_any(this.read_TypeCode(), ior);
    }

    public final Any read_any(TypeCode var1, IOR ior) {
        Debug.assertion(var1 != null);
        AnyImpl var2 = new AnyImpl();
        var2.type(var1);
        var2.read_value(this, var2.type());
        return var2;
    }


    public final Any read_any(TypeCode var1) {
        Debug.assertion(var1 != null);
        AnyImpl var2 = new AnyImpl();
        var2.type(var1);
        var2.read_value(this, var2.type());
        return var2;
    }

    public final void read_any(Any var1, TypeCode var2) {
        Debug.assertion(var1 != null);
        if (var1 instanceof AnyImpl) {
            var1.type(var2);
            var1.read_value(this, var2);
        } else {
            Any var3 = this.read_any(var2);
            OutputStream var4 = var1.create_output_stream();
            var3.write_value(var4);
            var1.read_value(var4.create_input_stream(), var1.type());
        }

    }

    public Object readAny() {
        TypeCode var2 = this.read_TypeCode();
        Object var3 = null;
        Object var4 = null;
        boolean var5 = false;
        int var14 = var2.kind().value();
        if (var14 == 21) {
            try {
                var14 = var2.content_type().kind().value();
                var2 = var2.content_type();
            } catch (BadKind var13) {
                throw new MARSHAL("IOException reading Any " + var13.getMessage());
            }
        }

        RepositoryId var6 = TypeCodeImpl.getRepositoryId(var2);
        switch(var14) {
            case 14:
                IOR var7 = new IOR(this);

                try {
                    IIOPReplacer.getIIOPReplacer();
                    var4 = IIOPReplacer.resolveObject(var7);
                    break;
                } catch (IOException var12) {
                    throw new MARSHAL("IOException reading Any " + var12.getMessage());
                }
            case 29:
                var4 = this.read_value(var6);
                break;
            case 30:
                var4 = this.read_value(var6);
                break;
            case 32:
                boolean var8 = this.read_boolean();
                if (!var8) {
                    if (var6.equals(RepositoryId.NULL)) {
                        var4 = this.read_value();
                    } else {
                        var4 = this.read_value(var6);
                    }
                } else {
                    IOR var9 = new IOR(this);

                    try {
                        IIOPReplacer.getIIOPReplacer();
                        var4 = IIOPReplacer.resolveObject(var9);
                    } catch (IOException var11) {
                        throw new MARSHAL("IOException reading Any " + var11.getMessage());
                    }
                }
                break;
            default:
                throw new MARSHAL("Can't handle TypeCode: " + var14 + " at pos: " + this.pos());
        }

        return var4;
    }

    public final long startEncapsulation() {
        return this.startEncapsulation(true);
    }

    final long startEncapsulation(boolean var1) {
        int var2 = this.read_long();
        if (var2 > 0) {
            ++this.nestingLevel;
            if (this.chunking) {
                this.checkChunk(0);
            }

            int var3 = this.pos();
            long var4 = this.chunking ? (long)var3 : (long)(var3 + var2);
            Debug.assertion((var4 & -1073741824L) == 0L);
            if (this.littleEndian) {
                var4 |= -2147483648L;
            }

            if (this.needLongAlignment > 0) {
                var4 |= 1073741824L;
            }

            this.needLongAlignment = (this.streamPos + this.chunkPos) % 8 != 0 ? 4 : 0;
            if (this.chunking) {
                var4 |= 536870912L;
                if (this.encapsulations == null) {
                    this.encapsulations = getHashMap();
                }

                this.encapsulations.put(var3, new IIOPInputStream.EncapsulationWrapper(var2, this.chunking, this.chunkLength, this.endTag));
                this.chunking = false;
                this.chunkLength = 0;
                this.endTag = 0;
            }

            if (var1) {
                this.consumeEndian();
            }

            return var4;
        } else {
            return 0L;
        }
    }

    public void endEncapsulation(long var1) {
        if (var1 != 0L) {
            --this.nestingLevel;
            this.littleEndian = (var1 & -2147483648L) != 0L;
            this.needLongAlignment = (var1 & 1073741824L) != 0L ? 4 : 0;
            boolean var3 = (var1 & 536870912L) != 0L;
            var1 &= 536870911L;
            if (var3) {
                IIOPInputStream.EncapsulationWrapper var4 = (IIOPInputStream.EncapsulationWrapper)this.encapsulations.remove((int)var1);
                if (var4 == null) {
                    throw new MARSHAL("No encapsulation information at: " + this.pos());
                }

                var1 += (long)var4.encapLength;
                this.chunking = var4.chunking;
                this.endTag = var4.endTag;
                this.chunkLength = var4.chunkLength;
                this.checkChunk(var4.encapLength);
            } else {
                this.chunking = false;
            }

            if (var1 > (long)this.pos()) {
                this.skip(var1 - (long)this.pos());
            } else if (var1 < (long)this.pos()) {
                throw new MARSHAL("read beyond encapsulation at position: " + this.pos());
            }
        }
    }

    public void start_value() {
        this.startValue();
    }

    public boolean startValue() {
        int var1 = this.read_long();
        if (var1 == 0) {
            return false;
        } else if (var1 == -1) {
            throw new MARSHAL("Illegal indirection for serial format version 2 data at " + this.pos());
        } else if ((var1 & 6) != 2) {
            throw new MARSHAL("Illegal value tag: " + Integer.toHexString(var1) + " for serial format version 2 data at " + this.pos());
        } else {
            boolean var2 = this.chunking;
            this.chunking = false;
            if ((String)this.read_indirection() == null) {
                int var4 = this.pos();
                String var3 = this.read_string();
                this.addIndirection(var4, var3);
            }

            this.chunking = var2;
            boolean var5 = (var1 & 8) == 8;
            if (var5 || this.chunking) {
                this.startChunk();
            }

            return true;
        }
    }

    public void end_value() {
        if (this.chunking) {
            this.endChunk(true);
        }

    }

    public Serializable read_value() {
        return this.read_value((Class)null);
    }

    public Serializable read_value(BoxedValueHelper var1) {
        return this.read_value((Class)null);
    }

    public Serializable read_value(Class var1) {
        Class var2 = var1;
        boolean var3 = false;
        int var4 = this.read_long();
        if (var4 == 0) {
            return null;
        } else {
            int var18;
            if (var4 == -1) {
                var18 = this.read_long();
                Debug.assertion(var18 < -4);
                var18 = var18 + this.pos() - 4;
                Debug.assertion(var18 > 0);
                Serializable var19 = (Serializable)this.getIndirection(var18);
                if (var19 == null) {
                    throw new IndirectionException(var18);
                } else {
                    return var19;
                }
            } else {
                var18 = this.pos() - 4;
                int var5 = this.indirections.reserve(var18);
                boolean var6 = this.chunking;
                this.chunking = false;
                boolean var7 = (var4 & 1) == 1;
                String var8 = null;
                boolean var9 = false;
                if (var7 && (var8 = (String)this.read_indirection()) == null) {
                    int var20 = this.pos();
                    var8 = this.read_string();
                    this.addIndirection(var20, var8);
                }

                if (var8 == null) {
                    var8 = this.getPossibleCodebase();
                }

                ClassInfo var10 = null;
                RepositoryId var11 = null;
                switch(var4 & 6) {
                    case 0:
                    default:
                        break;
                    case 2:
                        var10 = this.readIndirectingRepositoryId(var8);
                        var11 = var10.getRepositoryId();
                        break;
                    case 6:
                        ClassInfo[] var12 = this.readIndirectingRepositoryIdList(var8);
                        var10 = var12[0];
                        var11 = var10.getRepositoryId();
                }

                this.chunking = var6;
                boolean var21 = (var4 & 8) == 8;
                if (!this.useCompliantChunking()) {
                    --this.endTag;
                }

                if (var21 || this.chunking) {
                    this.startChunk();
                }

                if (var11 != null) {
                    if (var11.isClassDesc()) {
                        var8 = (String)this.read_value(String.class);
                        String var24 = (String)this.read_value(String.class);
                        if (var8 != null && (var8.startsWith("RMI:") || var8.startsWith("IDL:"))) {
                            String var22 = var8;
                            var8 = var24;
                            var24 = var22;
                        }

                        Class var23 = Utils.getClassFromID(new RepositoryId(var24), var8);
                        if (var23 == null) {
                            throw new MARSHAL("Class not found: " + var24);
                        }

                        this.indirections.putReserved(var5, var18, var23);
                        if (this.chunking) {
                            this.endChunk(var6);
                        }

                        if (!this.useCompliantChunking()) {
                            ++this.endTag;
                        }

                        return var23;
                    }

                    if (var11.compareStrings(RepositoryId.OLD_EJB_EXCEPTION)) {
                        var11 = RepositoryId.EJB_EXCEPTION;
                        var10 = ClassInfo.findClassInfo(var11);
                    }
                }

                Object var13 = null;
                if (var11 == null) {
                    var10 = ClassInfo.findClassInfo(var1);
                    var11 = var10.getRepositoryId();
                } else if (var10 != null) {
                    var2 = var10.forClass();
                }

                if (var2 != String.class && !var11.compareStrings(RepositoryId.STRING)) {
                    if (var2 != null && var10.isIDLEntity()) {
                        var13 = this.read_IDLValue(var2);
                        this.indirections.putReserved(var5, var18, var13);
                    } else if (var2 != null && (var2.isArray() || var10.getRepositoryId() == var10.getLocalRepositoryId()) && (Externalizable.class.isAssignableFrom(var2) || ObjectStreamClass.supportsUnsafeSerialization())) {
                        try {
                            ObjectStreamClass var14 = ObjectStreamClass.lookup(var2);
                            var13 = (Serializable)ValueHandlerImpl.allocateValue(this, var14);
                            this.indirections.putReserved(var5, var18, var13);
                            Serializable var15 = (Serializable)ValueHandlerImpl.readValue(this, var14, var13);
                            if (var15 != var13) {
                                var13 = var15;
                                this.indirections.putReserved(var5, var18, var15);
                            }
                        } catch (ClassNotFoundException var16) {
                            throw Utils.wrapMARSHALWithCause(var16);
                        } catch (IOException var17) {
                            throw Utils.wrapMARSHALWithCause(var17);
                        }
                    } else {
                        var13 = valueHandler.readValue(this, var18, var2, var11.toString(), this.endPoint != null ? this.endPoint.getRemoteCodeBase() : null);
                        this.indirections.putReserved(var5, var18, var13);
                    }
                } else {
                    var13 = this.read_wstring();
                    this.indirections.putReserved(var5, var18, var13);
                }

                if (this.chunking) {
                    this.endChunk(var6);
                }

                if (!this.useCompliantChunking()) {
                    ++this.endTag;
                }

                return (Serializable)var13;
            }
        }
    }

    public Serializable read_value(RepositoryId var1) {
        Class var2 = null;
        if (var1 != null) {
            ClassInfo var3 = ClassInfo.findClassInfo(var1);
            var2 = var3.forClass();
        }

        return this.read_value(var2);
    }

    public Serializable read_value(String var1) {
        Object var2 = null;
        return var1 != null && var1.length() > 0 ? this.read_value(new RepositoryId(var1)) : this.read_value((Class)null);
    }

    public Serializable read_value(Serializable var1) {
        if (var1 instanceof StreamableValue) {
            StreamableValue var2 = (StreamableValue)var1;
            int var3 = this.pos();
            this.addIndirection(var3, var1);
            var2._read(this);
            return var1;
        } else {
            throw Utils.wrapMARSHALWithCause(new NO_IMPLEMENT());
        }
    }

    private final Serializable read_IDLValue(Class var1) {
        try {
            if (CustomValue.class.isAssignableFrom(var1)) {
                throw new MARSHAL("Custom marshalled valuetypes not supported");
            } else if (StreamableValue.class.isAssignableFrom(var1)) {
                ValueFactory var5 = (ValueFactory)Utils.getHelper(var1, "DefaultFactory").newInstance();
                return var5.read_value(this);
            } else if (ValueBase.class.isAssignableFrom(var1)) {
                BoxedValueHelper var2 = (BoxedValueHelper)Utils.getHelper(var1, "Helper").newInstance();
                return var2.read_value(this);
            } else {
                return this.read_IDLEntity(var1);
            }
        } catch (InstantiationException var3) {
            throw new MARSHAL(var3.getMessage());
        } catch (IllegalAccessException var4) {
            throw new MARSHAL(var4.getMessage());
        }
    }

    final IDLEntity read_IDLEntity(Class var1) {
        return read_IDLEntity(this, var1);
    }

    public static final IDLEntity read_IDLEntity(org.omg.CORBA.portable.InputStream var0, Class var1) {
        Class var2 = Utils.getIDLHelper(var1);
        if (var2 == null) {
            throw new MARSHAL("Couldn't find helper for " + var1.getName());
        } else {
            return (IDLEntity)readWithHelper(var0, var2);
        }
    }

    public static final Object readWithHelper(org.omg.CORBA.portable.InputStream var0, Class var1) {
        try {
            Method var2 = Utils.getDeclaredMethod(var1, "read", READ_METHOD_ARGS);
            if (var2 == null) {
                throw new MARSHAL("No read method for " + var1.getName());
            } else {
                return var2.invoke((Object)null, var0);
            }
        } catch (IllegalAccessException var3) {
            throw Utils.wrapMARSHALWithCause(var3);
        } catch (InvocationTargetException var4) {
            throw Utils.wrapMARSHALWithCause(var4.getTargetException());
        }
    }

    public Object read_abstract_interface() {
        return this.read_boolean() ? this.read_Object() : this.read_value();
    }

    public Object read_abstract_interface(Class var1) {
        return this.read_boolean() ? this.read_Object() : this.read_value(var1);
    }

    public String dumpBuf() {
        StringBuffer var1 = new StringBuffer(Hex.dump(this.head.buf, this.chunkPos, this.head.end - this.chunkPos));

        for(Chunk var2 = this.head.next; var2 != null; var2 = var2.next) {
            var1.append("\n").append(Hex.dump(var2.buf, 0, var2.end));
        }

        return var1.toString();
    }

    public String toString() {
        return "IIOPInputStream:\n" + this.dumpBuf();
    }

    private static final String getStringBytes(byte[] var0, int var1) {
        String var2 = null;

        try {
            switch(var1) {
                case 65537:
                    var2 = new String(var0, "iso-8859-1");
                    break;
                case 65568:
                    var2 = new String(var0, 0);
                    break;
                case 83951617:
                    var2 = new String(var0, "utf-8");
            }
        } catch (UnsupportedEncodingException var4) {
            var2 = new String(var0);
        }

        return var2;
    }

    public final String read_string() {
        int var1 = this.read_ulong();
        if (var1 > 67108864) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read string of length " + Integer.toHexString(var1));
        } else if (var1 == 0) {
            return "";
        } else {
            byte[] var2 = new byte[var1 - 1];
            this.read_octet_array((byte[])var2, 0, var2.length);
            String var3 = getStringBytes(var2, this.char_codeset);
            this.read_octet();
            return var3;
        }
    }

    public final int read_numeric_string() throws NumberFormatException {
        int var1 = this.read_ulong();
        int var2 = 0;
        int var3 = 1;
        if (var1 <= 1) {
            if (var1 == 1) {
                this.read_octet();
            }

            throw new NumberFormatException("");
        } else if (var1 > 524288) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read string of length " + Integer.toHexString(var1));
        } else {
            byte[] var4 = new byte[var1 - 1];
            this.read_octet_array((byte[])var4, 0, var4.length);

            for(int var5 = var1 - 2; var5 >= 0; --var5) {
                if (var4[var5] <= 57 && var4[var5] >= 48) {
                    var2 += (var4[var5] - 48) * var3;
                    var3 *= 10;
                } else if (var4[var5] == 45) {
                    var2 = -var2;
                } else if (var4[var5] != 43) {
                    String var6 = getStringBytes(var4, this.char_codeset);
                    this.read_octet();
                    throw new NumberFormatException(var6);
                }
            }

            this.read_octet();
            return var2;
        }
    }

    final String read_guessed_string(String var1, byte[] var2) {
        int var3 = this.read_ulong();
        Debug.assertion(var1 != null && var2 != null);
        if (var3 > 524288) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read string of length " + Integer.toHexString(var3));
        } else if (var3 <= 1) {
            if (var3 == 1) {
                this.read_octet();
            }

            return "";
        } else {
            if (var2.length == var3 - 1 && this.chunkPos + var3 <= this.head.end && (!this.chunking || var3 <= this.chunkLength)) {
                int var4 = 0;
                int var5 = this.chunkPos;

                while(var4 < var3 - 1 && var2[var4++] == this.head.buf[this.chunkPos++]) {
                }

                if (var4 == var3 - 1) {
                    ++this.chunkPos;
                    if (this.chunking) {
                        this.chunkLength -= var3;
                    }

                    return var1;
                }

                this.chunkPos = var5;
            }

            byte[] var6 = new byte[var3 - 1];
            this.read_octet_array((byte[])var6, 0, var6.length);
            String var7 = getStringBytes(var6, this.char_codeset);
            this.read_octet();
            return var7;
        }
    }

    final void annotateStream() {
        this.annotation = Utils.getAnnotation((ClassLoader)null);
    }

    public final RepositoryId read_repository_id() {
        return this.read_repository_id(this.read_ulong());
    }

    private final RepositoryId read_repository_id(int var1) {
        if (var1 > 524288) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read string of length " + Integer.toHexString(var1));
        } else if (var1 <= 1) {
            if (var1 == 1) {
                this.read_octet();
            }

            return null;
        } else {
            return new RepositoryId(this, var1);
        }
    }

    private final ClassInfo readIndirectingRepositoryId(String var1) {
        ClassInfo var2 = (ClassInfo)this.read_indirection();
        if (var2 == null) {
            int var3 = this.read_ulong();
            int var4 = this.pos() - 4;
            RepositoryId var5 = this.read_repository_id(var3);
            if (var5 != null) {
                var2 = ClassInfo.findClassInfo(var5, var1);
                this.addIndirection(var4, var2);
            }
        }

        return var2;
    }

    private final ClassInfo[] readIndirectingRepositoryIdList(String var1) {
        ClassInfo[] var2 = (ClassInfo[])((ClassInfo[])this.read_indirection());
        if (var2 == null) {
            var2 = new ClassInfo[this.read_long()];
            int var3 = this.pos() - 4;
            int var4 = this.indirections.reserve(var3);

            for(int var5 = 0; var5 < var2.length; ++var5) {
                var2[var5] = this.readIndirectingRepositoryId(var1);
            }

            this.indirections.putReserved(var4, var3, var2);
        }

        return var2;
    }

    private final Object read_indirection() {
        Object var1 = null;
        this.mark(0);
        if (this.read_long() == -1) {
            int var2 = this.read_long();
            Debug.assertion(var2 < -4);
            var2 = var2 + this.pos() - 4;
            Debug.assertion(var2 > 0);
            var1 = this.getIndirection(var2);
            this.clearMark();
            if (var1 == null) {
                throw new IndirectionException(var2);
            }
        } else {
            this.reset();
        }

        return var1;
    }

    final void discard_string() {
        this.read_octet_sequence();
    }

    public Principal read_Principal() {
        int var1 = this.read_ulong();
        this.skip((long)var1);
        return null;
    }

    private final TypeCode getTypeCodeIndirection(int var1) {
        return this.tcIndirections == null ? null : (TypeCode)this.tcIndirections.get(var1);
    }

    private final int addTypeCodeIndirection(int var1) {
        if (this.tcIndirections == null) {
            this.tcIndirections = getHashMap();
        }

        return this.tcIndirections.reserve(var1);
    }

    public TypeCode read_TypeCode() {
        boolean var3 = true;
        this.align(4);
        int var1 = this.pos();
        if (this.tcNestingLevel == 0 && this.tcIndirections != null) {
            this.tcIndirections.clear();
        }

        ++this.tcNestingLevel;
        int var2 = this.read_long();
        Object var4;
        if (var2 != -1) {
            int var5 = this.addTypeCodeIndirection(var1);
            var4 = new TypeCodeImpl(var2);
            this.tcIndirections.putReserved(var5, var1, var4);
            ((TypeCodeImpl)var4).read(this);
        } else {
            var1 = this.pos();
            var1 += this.read_ulong();
            var4 = this.getTypeCodeIndirection(var1);
            if (var4 == null) {
                throw new MARSHAL("Couldn't read indirected TypeCode at " + this.pos() + " indirection to " + var1);
            }
        }

        --this.tcNestingLevel;
        return (TypeCode)var4;
    }

    private void throwOptionalData(int var1) throws OptionalDataException {
        try {
            OptionalDataException var2 = (OptionalDataException)odeCtor.newInstance(new Boolean(true));
            var2.length = var1;
            throw var2;
        } catch (Exception var3) {
            throw new AssertionError("Couldn't build an OptionalDataException");
        }
    }

    public final void readFully(byte[] var1) throws IOException {
        if (var1 == null) {
            throw new MARSHAL("null array as parameter to readFully");
        } else {
            this.readFully(var1, 0, var1.length);
        }
    }

    public final void readFully(byte[] var1, int var2, int var3) throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            this.read_octet_array(var1, var2, var3);
        }
    }

    public final int skipBytes(int var1) throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return (int)this.skip((long)var1);
        }
    }

    public final boolean readBoolean() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_boolean();
        }
    }

    public final byte readByte() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_octet();
        }
    }

    public final int readUnsignedByte() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_octet() & 255;
        }
    }

    public final short readShort() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_short();
        }
    }

    public final int readUnsignedShort() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_unsigned_short();
        }
    }

    public final char readChar() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_wchar();
        }
    }

    public final int readInt() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_long();
        }
    }

    public final long readLong() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_longlong();
        }
    }

    public final float readFloat() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_float();
        }
    }

    public final double readDouble() throws IOException {
        if (this.eof()) {
            throw new EOFException();
        } else {
            return this.read_double();
        }
    }

    public final String readLine() throws IOException {
        if (this.eof()) {
            this.throwOptionalData(0);
        }

        throw new IOException("readLine() is not supportable for IIOP streams");
    }

    public final String readUTF() throws IOException {
        if (this.eof()) {
            this.throwOptionalData(0);
        }

        return this.read_wstring();
    }

    public String readUTF8() throws IOException {
        if (this.eof()) {
            this.throwOptionalData(0);
        }

        int var1 = this.read_ulong();
        if (var1 == 0) {
            return "";
        } else if (var1 > 67108864) {
            throw new MARSHAL("Stream corrupted at " + this.pos() + ": tried to read wstring of length " + Integer.toHexString(var1));
        } else {
            return this.readUTF8String(var1);
        }
    }

    public final String readASCII() throws IOException {
        if (this.eof()) {
            this.throwOptionalData(0);
        }

        return this.read_string();
    }

    public final Object readObject() throws ClassNotFoundException, IOException {
        if (this.eof()) {
            this.throwOptionalData(0);
        }

        return this.read_abstract_interface();
    }

    public final Object readObject(Class var1) throws ClassNotFoundException, IOException {
        try {
            if (Remote.class.isAssignableFrom(var1)) {
                IOR var2 = new IOR(this);
                if (var1 != null && IDLUtils.isARemote(var1)) {
                    RemoteInfo var3 = RemoteInfo.findRemoteInfo(var1);
                    IIOPReplacer.getIIOPReplacer();
                    return IIOPReplacer.resolveObject(var2, var3);
                } else {
                    IIOPReplacer.getIIOPReplacer();
                    return IIOPReplacer.resolveObject(var2);
                }
            } else if (!var1.equals(Object.class) && !var1.equals(Serializable.class) && !var1.equals(Externalizable.class)) {
                if (org.omg.CORBA.Object.class.isAssignableFrom(var1)) {
                    return this.read_Object(var1);
                } else if (Utils.isIDLException(var1)) {
                    return this.read_IDLEntity(var1);
                } else {
                    return Utils.isAbstractInterface(var1) ? this.read_abstract_interface(var1) : this.read_value(var1);
                }
            } else {
                return this.readAny();
            }
        } catch (SystemException var4) {
            throw Utils.mapSystemException(var4);
        }
    }

    public final int read(byte[] var1, int var2, int var3) throws IOException {
        if (this.eof()) {
            return -1;
        } else {
            if (this.chunking) {
                var3 = min(var3, this.chunkLength);
            }

            int var4 = 0;
            if (this.needEightByteAlignment) {
                this.checkEightByteAlignment();
            }

            if (var1 == null) {
                throw new MARSHAL("null array as parameter to read_octet_array");
            } else {
                while(var3 > 0 && (this.head != null || this.fragmentHead != null)) {
                    if (this.chunkPos == this.head.end) {
                        this.advance();
                    }

                    int var5 = min(this.head.end - this.chunkPos, var3);
                    this.checkChunk(var5);
                    System.arraycopy(this.head.buf, this.chunkPos, var1, var2, var5);
                    this.chunkPos += var5;
                    var2 += var5;
                    var3 -= var5;
                    var4 += var5;
                }

                return var4;
            }
        }
    }

    public final int available() throws IOException {
        return 0;
    }

    static void p(String var0) {
        System.err.println("<IIOPInputStream> " + var0);
    }

    public ServerChannel getServerChannel() {
        return this.endPoint == null ? null : this.endPoint.getServerChannel();
    }

    final void setReadingObjectKey(boolean var1) {
        this.readingObjectKey = var1;
    }

    final boolean isReadingObjectKey() {
        return this.readingObjectKey;
    }

    static {
        IIOPService.load();
        valueHandler = Util.createValueHandler();
        READ_METHOD_ARGS = new Class[]{org.omg.CORBA.portable.InputStream.class};
        NO_ARGS_METHOD = new Class[0];
        NO_ARGS = new Object[0];
        mapPool = new StackPool(1024);
        odeCtor = null;

        try {
            odeCtor = OptionalDataException.class.getDeclaredConstructor(Boolean.TYPE);
            odeCtor.setAccessible(true);
        } catch (Exception var1) {
        }

    }

    static final class Marker {
        int streamPos;
        int chunkPos;
        Chunk head;
        IIOPInputStream.Fragment fragmentHead;
        int chunkLength;

        Marker() {
        }

        void copy(IIOPInputStream.Marker var1) {
            this.streamPos = var1.streamPos;
            this.chunkPos = var1.chunkPos;
            this.head = var1.head;
            this.fragmentHead = var1.fragmentHead;
            this.chunkLength = var1.chunkLength;
        }
    }

    static class EncapsulationWrapper {
        int encapLength;
        boolean chunking;
        int chunkLength;
        int endTag;

        EncapsulationWrapper(int var1, boolean var2, int var3, int var4) {
            this.chunking = var2;
            this.chunkLength = var3;
            this.encapLength = var1;
            this.endTag = var4;
        }
    }

    private static class Fragment {
        IIOPInputStream.Fragment next;
        int start;
        Chunk chunk;

        Fragment(Chunk var1, int var2) {
            this.chunk = var1;
            this.start = var2;
        }
    }
}
