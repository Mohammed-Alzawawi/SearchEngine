package com.example.SearchEngine.document.service.docSchemaValidation.FieldsValidations;

public class IntegerValidation implements FieldValidation {

    @Override
    public boolean validate(Object object) {
        return  object.getClass().getSimpleName().equals("Integer") ;
    }
}
