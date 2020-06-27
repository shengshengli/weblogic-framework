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

package com.r4v3zn.weblogic.framework.utils;

import java.util.*;

public class StringUtils {
    public static String join(Iterable<String> strings, String sep, String prefix, String suffix) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (! first) sb.append(sep);
            if (prefix != null) sb.append(prefix);
            sb.append(s);
            if (suffix != null) sb.append(suffix);
            first = false;
        }
        return sb.toString();
    }

    public static String repeat(String str, int num) {
        final String[] strs = new String[num];
        Arrays.fill(strs, str);
        return join(Arrays.asList(strs), "", "", "");
    }

    public static List<String> formatTable(List<String[]> rows) {
        Integer colLength = rows.get(0).length;
        Integer[] maxLength = new Integer[rows.get(0).length];
        for (int i = 0; i < colLength; i++) {
//            rows.get(i);
        }
        final Integer[] maxLengths = new Integer[rows.get(0).length];
        for (String[] row : rows) {
            if (maxLengths.length != row.length) throw new IllegalStateException("mismatched columns");
            for (int i = 0; i < maxLengths.length; i++) {
                if (maxLengths[i] == null || maxLengths[i] < row[i].length()) {
                    maxLengths[i] = row[i].length();
                }
            }
        }

        final List<String> lines = new LinkedList<String>();
        for (String[] row : rows) {
            for (int i = 0; i < maxLengths.length; i++) {
                final String pad = repeat(" ", maxLengths[i] - row[i].length());
                row[i] = row[i] + pad;
            }
            lines.add(join(Arrays.asList(row), " ", "", ""));
        }
        return lines;
    }

    public static class ToStringComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) { return o1.toString().compareTo(o2.toString()); }
    }

    /**
     * 获取指定长度随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
