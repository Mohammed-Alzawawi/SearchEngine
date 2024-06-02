package com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations;

import java.util.List;

public class ItemIntgerValidation implements FieldValidation{
    @Override
    public boolean validate(Object object) {
        List<Object> list = (List<Object>) object;
        IntegerValidation integerValidation = new IntegerValidation();
        for (Object item : list) {
            if (!integerValidation.validate(item)) {
                return false;
            }
        }
        return true;
    }
}
