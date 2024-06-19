package com.example.SearchEngine.Services.JsonSchemaValedation;

import com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks.ConstrainChecker;
import com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks.ItemTypeChecker;
import com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks.TypeChecker;
import com.example.SearchEngine.Services.JsonSchemaValedation.FieldsValidations.FieldValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ValidateJsonToSchema {
    @Autowired
    private ObjectMapper mapper ;
    private Map<String , List<FieldValidation>> fields ;

    public ValidateJsonToSchema() {
        this.fields = new HashMap<>();

    }

    private void fiilFields( Map<String , Object> schema ) {
        schema = (Map<String, Object>) schema.get("properties") ;
        ConstrainChecker constrainChecker;
        for (String key : schema.keySet()) {
            fields.put(key , new ArrayList<>()) ;
            Map<String , Object> field = (Map<String, Object>) schema.get(key) ;
            if (field.containsKey("type")) {
                constrainChecker = new TypeChecker() ;
                fields.get(key).add(constrainChecker.check(field.get("type").toString())) ;
            }
            if (field.containsKey("items")) {
                Map<String , Object> items = (Map<String, Object>) field.get("items") ;
                if(items.containsKey("type") ) {
                    constrainChecker = new ItemTypeChecker();
                    fields.get(key).add(constrainChecker.check(items.get("type").toString())) ;
                }
            }
        }
    }

    private  boolean mandatoryCheck(Map<String , Object> schema , Map<String , Object> json) {
        schema = (Map<String, Object>) schema.get("properties") ;
        for (String key : schema.keySet()) {
            Map<String , Object> field = (Map<String, Object>) schema.get(key) ;
            boolean value = (boolean) field.get("mandatory");
            if (value && !json.containsKey(key)) {
                return  false ;
            }
        }
        return  true ;
    }

    private boolean fieldCheck(Map<String , Object> schema , Map<String , Object> json) {
        schema = (Map<String, Object>) schema.get("properties") ;
        for (String key  : json.keySet()) {
            if (!schema.containsKey(key)) {
                return  false ;
            }
        }
        return  true  ;
    }



    public boolean validate(String  schemaName , Map<String , Object> json ) throws IOException {
        Map<String , Object> schema = mapper.readValue(new File("D:\\Apps\\work space python\\js.json") , Map.class) ;

        if (!fieldCheck(schema , json) || !mandatoryCheck(schema , json) ) {
            return  false ;
        }
        fiilFields(schema);
        for (String key : json.keySet()) {
            for (FieldValidation validation : fields.get(key)) {
                if (!validation.validate(json.get(key))) {
                    return  false ;
                }
            }
        }
        return true ;
    }

}
