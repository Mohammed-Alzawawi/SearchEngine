package com.example.SearchEngine.document.service.validation.fieldsvalidations;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidation implements FieldValidation {

    private String format =  "dd/MM/yyyy" ;
    @Override
    public boolean validate(Object object) {
        String str = object.toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(str);
            return true;
        } catch (ParseException e) {

        }
        return false;
    }

    public  String getFormat() {
        return format;
    }
}