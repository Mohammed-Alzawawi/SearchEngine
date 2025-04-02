package com.example.SearchEngine.utils.documentfilter.converter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToTimestamp implements TypeConverterInterface{
    private final String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy", "MM-dd-yyyy"};
    public Long convert(String type, Object object) {
        String date = (String) object;
        for (String format : formats) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
//                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date parsedDate = simpleDateFormat.parse(date);
                Timestamp result = new Timestamp(parsedDate.getTime());
                return result.getTime();
            } catch (ParseException e) {
                continue;
            }
        }
        return 0L;
    }
}
