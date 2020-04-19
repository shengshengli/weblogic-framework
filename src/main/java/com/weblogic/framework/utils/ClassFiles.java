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

package com.weblogic.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClassFiles {
	public static String classAsFile(final Class<?> clazz) {
		return classAsFile(clazz, true);
	}
	
	public static String classAsFile(final Class<?> clazz, boolean suffix) {
		String str;
		if (clazz.getEnclosingClass() == null) {
			str = clazz.getName().replace(".", "/");
		} else {
			str = classAsFile(clazz.getEnclosingClass(), false) + "$" + clazz.getSimpleName();
		}
		if (suffix) {
			str += ".class";			
		}
		return str;  
	}

	public static byte[] classAsBytes(final Class<?> clazz) {
		try {
			final byte[] buffer = new byte[1024];
			final String file = classAsFile(clazz);
			final InputStream in = ClassFiles.class.getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new IOException("couldn't find '" + file + "'");
			}
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
