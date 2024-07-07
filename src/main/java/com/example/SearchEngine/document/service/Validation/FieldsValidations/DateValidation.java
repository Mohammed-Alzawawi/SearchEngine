package com.example.SearchEngine.document.service.Validation.FieldsValidations;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidation implements FieldValidation {
    @Override
    public boolean validate(Object object) {
        String str = object.toString();
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy", "MM-dd-yyyy"};
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);  // Set to false for stricter parsing
                sdf.parse(str);
                return true;
            } catch (ParseException e) {
                // Not valid in this format, continue looping
            }
        }
        return false;
    }
}
