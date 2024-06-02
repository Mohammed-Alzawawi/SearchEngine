package com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks;

import com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations.*;

public class TypeCheck implements ConstrainCheck {

    public  TypeCheck () {}

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
