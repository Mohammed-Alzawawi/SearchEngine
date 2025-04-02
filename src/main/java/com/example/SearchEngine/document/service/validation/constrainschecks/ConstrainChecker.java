package com.example.SearchEngine.document.service.validation.constrainschecks;

import com.example.SearchEngine.document.service.validation.fieldsvalidations.FieldValidation;

public interface ConstrainChecker {
    public FieldValidation check(String value) ;
}
