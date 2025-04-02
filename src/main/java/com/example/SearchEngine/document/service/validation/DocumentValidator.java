package com.example.SearchEngine.document.service.validation;

import com.example.SearchEngine.document.service.validation.constrainschecks.ConstrainChecker;
import com.example.SearchEngine.document.service.validation.constrainschecks.ItemTypeChecker;
import com.example.SearchEngine.document.service.validation.constrainschecks.TypeChecker;
import com.example.SearchEngine.document.service.validation.fieldsvalidations.FieldValidation;
import com.example.SearchEngine.schema.service.SchemaDefaultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class DocumentValidator {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private SchemaDefaultService schemaDefaultService;
    private Map<String, List<FieldValidation>> fields;

    public DocumentValidator() {
        this.fields = new HashMap<>();
    }

    private void fillFields(Map<String, Object> schema) {
        schema = (Map<String, Object>) schema.get("properties");
        ConstrainChecker constrainChecker;
        for (String key : schema.keySet()) {
            fields.put(key, new ArrayList<>());
            Map<String, Object> field = (Map<String, Object>) schema.get(key);
            if (field.containsKey("type")) {
                constrainChecker = new TypeChecker();
                fields.get(key).add(constrainChecker.check(field.get("type").toString()));
            }
            if (field.containsKey("items")) {
                Map<String, Object> items = (Map<String, Object>) field.get("items");
                if (items.containsKey("type")) {
                    constrainChecker = new ItemTypeChecker();
                    fields.get(key).add(constrainChecker.check(items.get("type").toString()));
                }
            }
        }
    }

    private boolean mandatoryCheck(Map<String, Object> schema, Map<String, Object> json) {
        schema = (Map<String, Object>) schema.get("properties");
        for (String key : schema.keySet()) {
            Map<String, Object> field = (Map<String, Object>) schema.get(key);
            boolean value = (boolean) field.get("mandatory");
            if (value && !json.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    private boolean fieldCheck(Map<String, Object> schema, Map<String, Object> json) {
        schema = (Map<String, Object>) schema.get("properties");
        for (String key : json.keySet()) {
            if (key == "id") {
                continue;
            }
            if (!schema.containsKey(key)) {
                return false;
            }
        }
        return true;
    }


    public boolean validate(String schemaName, Map<String, Object> json) throws Exception {
        Map<String, Object> schema;
        schema = schemaDefaultService.getSchema(schemaName);
        if (!fieldCheck(schema, json) || !mandatoryCheck(schema, json)) {
            return false;
        }
        fillFields(schema);
        for (String key : json.keySet()) {
            if (key == "id") {
                continue;
            }
            for (FieldValidation validation : fields.get(key)) {
                if (!validation.validate(json.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean updateValidation(String schemaName, Map<String, Object> json) throws Exception {
        Map<String, Object> schema;
        schema = schemaDefaultService.getSchema(schemaName);
        if (!fieldCheck(schema, json) || json.containsKey("id")) {
            return false;
        }
        fillFields(schema);
        return json.keySet().stream().allMatch(key ->
                fields.get(key).stream().allMatch(validation ->
                        validation.validate(json.get(key))
                )
        );
    }

}
