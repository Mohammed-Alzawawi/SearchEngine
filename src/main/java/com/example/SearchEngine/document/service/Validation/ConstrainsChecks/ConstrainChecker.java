package com.example.SearchEngine.document.service.Validation.ConstrainsChecks;

import com.example.SearchEngine.document.service.Validation.FieldsValidations.FieldValidation;

public interface ConstrainChecker {
    public FieldValidation check(String value) ;
}
