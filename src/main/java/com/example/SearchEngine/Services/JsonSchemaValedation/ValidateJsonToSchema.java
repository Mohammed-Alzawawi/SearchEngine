package com.example.SearchEngine.Services.JsonSchemaValedation;

import com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks.ConstrainCheck;
import com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks.ItemTypeCheck;
import com.example.SearchEngine.Services.JsonSchemaValedation.ConstrainsChecks.TypeCheck;
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
    ObjectMapper mapper ;
    Map<String , List<FieldValidation>> fields ;

    public ValidateJsonToSchema() {
        this.fields = new HashMap<>();

    }

    private void fiilFields( Map<String , Object> schema ) {
        schema = (Map<String, Object>) schema.get("properties") ;
        ConstrainCheck constrainCheck ;
        for (String key : schema.keySet()) {
            fields.put(key , new ArrayList<>()) ;
            Map<String , Object> field = (Map<String, Object>) schema.get(key) ;
            for (String fieldConstrain : field.keySet()) {
                if (fieldConstrain.equals("type")) {
                    constrainCheck = new TypeCheck() ;
                    fields.get(key).add(constrainCheck.check(field.get(fieldConstrain).toString())) ;
                } else if (fieldConstrain.equals("items") ) {
                    Map<String , Object> items = (Map<String, Object>) field.get("items") ;
                    for (String itemConstrain : items.keySet()) {
                        if(itemConstrain.equals("type")) {
                            constrainCheck = new ItemTypeCheck();
                            fields.get(key).add(constrainCheck.check(items.get(itemConstrain).toString())) ;
                        }
                    }
                }
            }
        }
    }

    public  boolean mandatoryCheck(Map<String , Object> schema , Map<String , Object> json) {
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

    public boolean fieldCheck(Map<String , Object> schema , Map<String , Object> json) {
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
