package com.example.SearchEngine.document.service.docSchemaValidation.ConstrainsChecks;

import com.example.SearchEngine.document.service.docSchemaValidation.FieldsValidations.FieldValidation;

public interface ConstrainChecker {
    public FieldValidation check(String value) ;
}
