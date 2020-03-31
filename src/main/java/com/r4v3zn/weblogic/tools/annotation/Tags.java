package com.r4v3zn.weblogic.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Title: Tags
 * Desc: 标签
 * Date:2020/3/31 21:17
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tags {

    String[] value() default {};

    public static class Utils {
        public static String[] getTags(AnnotatedElement annotated) {
            Tags tags = annotated.getAnnotation(Tags.class);
            if (tags != null && tags.value() != null) {
                return tags.value();
            } else {
                return new String[0];
            }
        }

        public static String[] getTagsSimple(AnnotatedElement annotated) {
            return getTags(annotated);
        }
    }
}
