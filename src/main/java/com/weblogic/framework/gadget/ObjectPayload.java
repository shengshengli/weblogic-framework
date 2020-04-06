package com.weblogic.framework.gadget;


import org.reflections.Reflections;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;

/**
 * Title: ObjectPayload
 * Desc: ObjectPayload
 * Date:2020/3/29 0:35
 * @version 1.0.0
 */
@SuppressWarnings ( "rawtypes" )
public interface ObjectPayload<T> {

    /**
     * 获取序列化 payload (Runtime)
     * @param command 执行的命令
     * @return 序列结果
     * @throws Exception
     */
    T getObject(String command, URLClassLoader urlClassLoader) throws Exception;

    /**
     * 获取序列化 payload
     * @param codeByte 需要序列化的字节码
     * @param bootArgs 执行中参数
     * @param className 反射的 class name
     * @return 序列结果
     * @throws Exception
     */
    T getObject(final byte[] codeByte, final String[] bootArgs, String className, URLClassLoader urlClassLoader)throws Exception;

    public static class Utils {

        // get payload classes by classpath scanning
        public static Set<Class<? extends ObjectPayload>> getPayloadClasses () {
            final Reflections reflections = new Reflections(ObjectPayload.class.getPackage().getName());
            final Set<Class<? extends ObjectPayload>> payloadTypes = reflections.getSubTypesOf(ObjectPayload.class);
            for ( Iterator<Class<? extends ObjectPayload>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
                Class<? extends ObjectPayload> pc = iterator.next();
                if ( pc.isInterface() || Modifier.isAbstract(pc.getModifiers()) ) {
                    iterator.remove();
                }
            }
            return payloadTypes;
        }






        @SuppressWarnings ( "unchecked" )
        public static void releasePayload ( ObjectPayload payload, Object object ) throws Exception {
            if ( payload instanceof ReleaseableObjectPayload ) {
                ( (ReleaseableObjectPayload) payload ).release(object);
            }
        }
    }
}
