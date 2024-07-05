package com.example.SearchEngine.document.service.docSchemaValidation.FieldsValidations;

public class LongValidation implements FieldValidation {
    @Override
    public boolean validate(Object object) {
        return object.getClass().getSimpleName().equals("Long") || object.getClass().getSimpleName().equals("Integer");
    }
}
