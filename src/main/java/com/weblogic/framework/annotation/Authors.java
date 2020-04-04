package com.weblogic.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Title: Authors
 * Desc: Authors
 * Date:2020/3/24 1:20
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authors {

    /**
     * r4v3zn
     */
    String R4V3ZN = "r4v3zn";

    /**
     * lufei
     */
    String LUFEI = "lufei";

    /**
     * 舒肤佳
     */
    String SHUFUJIA = "舒肤佳";

    String[] value() default {};

    public static class Utils {
        public static String[] getAuthors(AnnotatedElement annotated) {
            Authors authors = annotated.getAnnotation(Authors.class);
            if (authors != null && authors.value() != null) {
                return authors.value();
            } else {
                return new String[0];
            }
        }
    }
}
