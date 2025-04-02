package com.example.SearchEngine.invertedindex.service.query;

import com.example.SearchEngine.document.service.validation.constrainschecks.TypeChecker;
import com.example.SearchEngine.document.service.validation.fieldsvalidations.DateValidation;
import com.example.SearchEngine.document.service.validation.fieldsvalidations.FieldValidation;
import com.example.SearchEngine.document.service.validation.fieldsvalidations.LongValidation;
import com.example.SearchEngine.document.service.validation.fieldsvalidations.StringValidation;
import com.example.SearchEngine.schema.service.SchemaDefaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class QueryValidator implements QueryValidationInterface {

    @Autowired
    private SchemaDefaultService schemaDefaultService;
    private DateValidation dateValidation = new DateValidation();
    private LongValidation longValidation = new LongValidation();
    private StringValidation stringValidation = new StringValidation();

    public QueryValidator() {

    }

    private boolean checkRange(Map<String, Object> rangeFilters, Map<String, Object> schema) {
        Map<String, Object> schemaFilters = (Map<String, Object>) schema.get("filters");
        for (String key : rangeFilters.keySet()) {
            if (!schemaFilters.containsKey(key)) {
                return false;
            }
            Map<String, Object> details = (Map<String, Object>) rangeFilters.get(key);

            if (!details.containsKey("min") || !details.containsKey("max")) {
                return false;
            }

            for (String field : details.keySet()) {
                if (field.equals("max") || field.equals("min")) {
                    if (details.get(field).equals("inf") || details.get(field).equals("-inf")) {
                        continue;
                    } else if (dateValidation.validate(details.get(field))) {
                        LocalDate localDate = LocalDate.parse(details.get(field).toString(), DateTimeFormatter.ofPattern(dateValidation.getFormat()));
                        details.put(field, localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    } else if (longValidation.validate(details.get(field))) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkMatch(Map<String, Object> matchFilters, Map<String, Object> schema) {
        TypeChecker typeChecker = new TypeChecker();
        Map<String, Object> schemaFilters = (Map<String, Object>) schema.get("filters");
        Map<String, Object> schemaProperties = (Map<String, Object>) schema.get("properties");

        for (String key : matchFilters.keySet()) {
            FieldValidation fieldValidation = typeChecker.check((String) ((Map<String, Object>) schemaProperties.get(key)).get("type")) ;
            if (!fieldValidation.validate(matchFilters.get(key))) {
                return false ;
            }

        }
        return true;
    }


    @Override
    public boolean validate(Map<String, Object> query, String schemaName) throws Exception {

        Map<String, Object> schema = (Map<String, Object>) schemaDefaultService.getSchema(schemaName);
        if (query.containsKey("query") && !stringValidation.validate(query.get("query"))) {
            System.out.println("wrong text");
            return false;
        }

        if (!query.containsKey("filters")) {
            return  true ;
        }

        if (!schema.containsKey("filters")) {
            System.out.println("22");
            return  false ;
        }

        Map<String, Object> filters = (Map<String, Object>) query.get("filters");
        for (String key : filters.keySet()) {
            if (key.equals("range")) {
                if (!checkRange((Map<String, Object>) filters.get("range"), schema)) {
                    System.out.println("33");
                    return false;
                }
            } else if (key.equals("match")) {
                if (!checkMatch((Map<String, Object>) filters.get("match"), schema)) {
                    System.out.println("44");
                    return false;
                }
            }
        }
        return true;
    }
}
