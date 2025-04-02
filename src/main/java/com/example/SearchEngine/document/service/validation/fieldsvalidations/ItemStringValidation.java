package com.example.SearchEngine.document.service.validation.fieldsvalidations;

import java.util.List;

public class ItemStringValidation implements FieldValidation {
    @Override
    public boolean validate(Object object) {
        List<Object> list = (List<Object>) object;
        StringValidation stringValidation = new StringValidation();
        for (Object item : list) {
            if (!stringValidation.validate(item)) {
                return false;
            }
        }
        return true;
    }
}
