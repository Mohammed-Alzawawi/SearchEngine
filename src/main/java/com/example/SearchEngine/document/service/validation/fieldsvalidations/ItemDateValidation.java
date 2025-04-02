package com.example.SearchEngine.document.service.validation.fieldsvalidations;

import java.util.List;

public class ItemDateValidation implements  FieldValidation{
    @Override
    public boolean validate(Object object) {
        DateValidation dateValidation = new DateValidation();
        List<Object> list = (List<Object>) object ;
        for (Object item : list) {
            if (!dateValidation.validate(item)) {
                return false;
            }
        }
        return  true ;
    }
}
