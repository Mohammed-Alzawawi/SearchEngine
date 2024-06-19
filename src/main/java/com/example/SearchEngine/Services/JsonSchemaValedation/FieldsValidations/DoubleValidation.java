package com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations;

public class DoubleValidation implements FieldValidation {
    @Override
    public boolean validate(Object object) {
        return object.getClass().getSimpleName().equals("Double") || object.getClass().getSimpleName().equals("Integer")  ;
    }
}
