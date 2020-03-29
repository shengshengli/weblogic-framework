package com.r4v3zn.weblogic.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Title: Versions
 * Desc: 版本
 * Date:2020/3/29 18:39
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Versions {

    String[] value() default {};

    public static class Utils {
        public static String[] getVersions(AnnotatedElement annotated) {
            Versions deps = annotated.getAnnotation(Versions.class);
            if (deps != null && deps.value() != null) {
                return deps.value();
            } else {
                return new String[0];
            }
        }

        public static String[] getVersionsSimple(AnnotatedElement annotated) {
            return getVersions(annotated);
        }
    }
}
