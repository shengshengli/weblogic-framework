/*
 * Copyright (c) 2020. r4v3zn.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weblogic.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Title: Versions
 * Desc: 版本
 * Date:2020/3/29 18:39
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
