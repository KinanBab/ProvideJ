package edu.brown.providej.runtime;

import java.util.Arrays;

public class ProvideJUtil {
    public static String toJSON(Object arr) {
        Class<?> eClass = arr.getClass();
        if (eClass.isArray()) {
            if (eClass == double[].class) {
                return Arrays.toString((double[]) arr);
            } else if (eClass == int[].class) {
                return Arrays.toString((int[]) arr);
            } else if (eClass == long[].class) {
                return Arrays.toString((long[]) arr);
            } else if (eClass == double[].class) {
                return Arrays.toString((double[]) arr);
            } else if (eClass == boolean[].class) {
                return Arrays.toString((boolean[]) arr);
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                for (Object o : (Object[]) arr) {
                    builder.append(ProvideJUtil.toJSON(o));
                    builder.append(",");
                }
                if (((Object[]) arr).length > 0) {
                    builder.setLength(builder.length() - 1);
                }
                builder.append("]");
                return builder.toString();
            }
        } else if (arr instanceof String) {
            return "\"" + ((String) arr) + "\"";
        } else {
            return String.valueOf(arr);
        }
    }
}
