package com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks;

import com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations.*;

public class ItemTypeChecker implements ConstrainChecker {
    @Override
    public FieldValidation check(String value) {
        switch (value) {
            case "date":
                return new ItemDateValidation();
            case "integer":
                return new ItemIntgerValidation();
            case "double":
                return new ItemDoubleValidation();
            case "long":
                return  new ItemLongValidation();
            default:
                return new ItemStringValidation();
        }
    }
}
