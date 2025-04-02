package com.example.SearchEngine.utils.documentfilter.converter;

import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@Service
public class Converter {
    private final HashMap<String, TypeConverterInterface> converters = new HashMap<>();

    public Converter() {
        this.converters.put("timestamp", new DateToTimestamp());
        this.converters.put("size", new ObjectToSize());
    }

    public Long convert(Object originalValue, String originalType, String converter) throws InvocationTargetException, IllegalAccessException {
        return converters.get(converter).convert(originalType, originalValue);
    }
}
