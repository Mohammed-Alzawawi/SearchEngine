package com.example.SearchEngine.document.service.validation.fieldsvalidations;

import java.util.List;

public class ItemDoubleValidation implements FieldValidation{
    @Override
    public boolean validate(Object object) {
        List<Object> list = (List<Object>) object;
        DoubleValidation doubleValidation = new DoubleValidation();
        for (Object item : list) {
            if (!doubleValidation.validate(item)) {
                return false;
            }
        }
        return true;
    }
}
