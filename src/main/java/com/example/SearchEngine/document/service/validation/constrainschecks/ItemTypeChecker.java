package com.example.SearchEngine.document.service.validation.constrainschecks;

import com.example.SearchEngine.document.service.validation.fieldsvalidations.*;

public class ItemTypeChecker implements ConstrainChecker {
    @Override
    public FieldValidation check(String value) {
        switch (value) {
            case "date":
                return new ItemDateValidation();
            case "integer":
                return new ItemIntegerValidation();
            case "double":
                return new ItemDoubleValidation();
            case "long":
                return  new ItemLongValidation();
            default:
                return new ItemStringValidation();
        }
    }
}
