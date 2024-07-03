package com.example.SearchEngine.document.service.docSchemaValidation.FieldsValidations;

public class ArrayValidation implements FieldValidation {
    @Override
    public boolean validate(Object object) {
        return  object.getClass().getSimpleName().equals("ArrayList");
    }
}
