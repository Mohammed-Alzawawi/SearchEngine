package com.example.SearchEngine.document.service.validation.fieldsvalidations;

import java.util.List;

public class ItemLongValidation implements FieldValidation{
    @Override
    public boolean validate(Object object) {
        List<Object> list = (List<Object>) object;
        LongValidation longValidation = new LongValidation();
        for (Object item : list) {
            if (!longValidation.validate(item)) {
                return false;
            }
        }
        return true;
    }
}
