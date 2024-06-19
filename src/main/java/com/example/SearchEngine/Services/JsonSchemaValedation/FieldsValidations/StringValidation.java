package com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations;

public class StringValidation implements FieldValidation {

    @Override
    public boolean validate(Object object) {
        return object.getClass().getSimpleName().equals("String") ;
    }
}
