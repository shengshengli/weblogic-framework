/*
 * Copyright  2020.  r4v3zn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.weblogic.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Title: Dependencies
 * Desc: Dependencies
 * Date:2020/3/24 1:20
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependencies {
	String[] value() default {};

	public static class Utils {
		public static String[] getDependencies(AnnotatedElement annotated) {
			Dependencies deps = annotated.getAnnotation(Dependencies.class);
			if (deps != null && deps.value() != null) {
				return deps.value();
			} else {
				return new String[0];
			}
		}

		public static String[] getDependenciesSimple(AnnotatedElement annotated) {
		    String[] deps = getDependencies(annotated);
		    String[] simple = new String[deps.length];
		    for (int i = 0; i < simple.length; i++) {
                simple[i] = deps[i].split(":", 2)[1];
            }
            return simple;
        }
	}
}
