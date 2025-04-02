package com.example.SearchEngine.utils.documentfilter.converter;

import java.util.HashMap;
import java.util.Map;

public class ObjectToSize implements TypeConverterInterface{
    private final Map<String, ToSizeInterface> converters = new HashMap<>();

    public ObjectToSize() {
        this.converters.put("text", new StringSize());
        this.converters.put("string", new StringSize());
        this.converters.put("array", new ListSize());
        this.converters.put("json", new HashMapSize());
    }

    public Long convert(String type, Object object) {
        return converters.get(type).getSize(object);
    }
}
