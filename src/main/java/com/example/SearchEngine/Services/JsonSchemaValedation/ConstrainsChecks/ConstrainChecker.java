package com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks;

import com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations.FieldValidation;

public interface ConstrainChecker {
    public FieldValidation check(String value) ;
}
