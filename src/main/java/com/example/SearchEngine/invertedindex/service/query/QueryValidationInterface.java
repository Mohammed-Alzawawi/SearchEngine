package com.example.SearchEngine.invertedindex.service.query;

import java.util.Map;

public interface QueryValidationInterface {
    public boolean validate(Map<String  , Object> query , String schemaName) throws Exception;
}
