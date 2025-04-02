package com.example.SearchEngine.document.service.validation.fieldsvalidations;

public class StringValidation implements FieldValidation {

    @Override
    public boolean validate(Object object) {
        return object.getClass().getSimpleName().equals("String") ;
    }
}
