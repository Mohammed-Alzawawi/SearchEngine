package com.example.SearchEngine.document.service.docSchemaValidation.ConstrainsChecks;

import com.example.SearchEngine.document.service.docSchemaValidation.FieldsValidations.*;

public class TypeChecker implements ConstrainChecker {

    public TypeChecker() {}

    @Override
    public FieldValidation check(String value) {
        switch (value) {
            case "array":
                return new ArrayValidation();
            case "date":
                return new DateValidation();
            case "integer":
                return new IntegerValidation();
            case "double":
                return new DoubleValidation();
            case "long":
                return  new LongValidation();
            default:
                return new StringValidation();
        }
    }
}
