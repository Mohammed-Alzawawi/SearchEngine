package com.example.SearchEngine.schema.utils;

import java.util.HashMap;
import java.util.Map;

public class InstanceChecker {
    private static final Map<String, Class<?>> typeMap = new HashMap<>();

    static {
        typeMap.put("Array", Object[].class);
        typeMap.put("Boolean", Boolean.class);
        typeMap.put("Integer", Integer.class);
        typeMap.put("Double", Double.class);
        typeMap.put("String", String.class);
        typeMap.put("Long", Long.class);
    }

    public static boolean isInstance(Object obj, String type) {
        Class<?> clazz = typeMap.get(type);
        if (clazz != null) {
            return clazz.isInstance(obj);
        }
        return false;
    }
}
