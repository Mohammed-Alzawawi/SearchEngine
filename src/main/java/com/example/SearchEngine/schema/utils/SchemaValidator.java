package com.example.SearchEngine.schema.utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.example.SearchEngine.constants.Constants.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import static com.example.SearchEngine.schema.utils.InstanceChecker.isInstance;

@Component
public class SchemaValidator {
    private final List<String> dataTypes;
    private final List<String> filterConverters;
    private final Map<String, HashMap<String, Object>> typeForms;
    private final Map<String, String> typesInJava;
    private final HashMap<String, Object> generalForm;
    private final SchemaFormatter schemaFormatter;

    public SchemaValidator() throws IOException {
        schemaFormatter = new SchemaFormatter();
        HashMap<String, Object> metadata = readJSON(Paths.SCHEMA_FORMS_PATH + "metadata.json");
        List<Object> typesList = (List<Object>) metadata.get("dataTypes");
        List<String> filterConvertersList = (List<String>) metadata.get("filterConverters");
        this.dataTypes = new ArrayList<>();
        this.filterConverters = new ArrayList<>();
        this.typeForms = new HashMap<>();
        this.typesInJava = new HashMap<>();
        for (Object type : typesList) {
            if (!(type instanceof String)) {
                throw new IllegalStateException("\"type\" field not found");
            }
            this.dataTypes.add((String) type);
            this.typeForms.put((String) type, readJSON(Paths.SCHEMA_FORMS_PATH + (String) type + "Form.json"));
        }
        for (String filterConverter : filterConvertersList) {
            filterConverters.add(filterConverter);
        }
        this.generalForm = readJSON(Paths.SCHEMA_FORMS_PATH + "generalAttributeForm.json");
        HashMap<String, String> typesInJava = (HashMap<String, String>) metadata.get("typesInJava");
        this.typesInJava.putAll(typesInJava);
    }

    public void validateSchema(HashMap<String, Object> receivedSchema) {
        Object name = receivedSchema.get("name");
        Object properties = receivedSchema.get("properties");
        Object filters = receivedSchema.get("filters");
        if (!(name instanceof String)) {
            throw new IllegalStateException(String.format(Messages.SCHEMA_ATTRIBUTE_NOT_FOUND, "name"));
        }
        if (!(properties instanceof HashMap)) {
            throw new IllegalStateException(String.format(Messages.SCHEMA_ATTRIBUTE_NOT_FOUND, "properties"));
        }
        if (!(filters instanceof HashMap)) {
            throw new IllegalStateException(String.format(Messages.SCHEMA_ATTRIBUTE_NOT_FOUND, "filters"));
        }
        validateProperties((HashMap<String, Object>) properties);
        validateFilters((HashMap<String, Object>) properties, (HashMap<String, Object>) filters);
    }

    private void validateProperties(HashMap<String, Object> receivedProperties) {
        Set<String> attributeNames = receivedProperties.keySet();
        Set<String> generalAttributeFields = generalForm.keySet();
        for (String attributeName : attributeNames) {
            HashMap<String, Object> attribute = (HashMap<String, Object>) receivedProperties.get(attributeName);
            schemaFormatter.setGeneralDefaults(attribute);
            for (String field : generalAttributeFields) {
                String fieldType = generalForm.get(field).toString();
                if (!isInstance(attribute.get(field), typesInJava.get(fieldType))) {
                    throw new IllegalStateException(String.format(Messages.NOT_FOUND_OR_INVALID_FIELD, field));
                }
            }
            validateDataType(attribute);
        }
    }

    private void validateDataType(HashMap<String, Object> receivedForm) {
        Object type = receivedForm.get("type");
        if (!(type instanceof String) || !dataTypes.contains(type.toString())) {
            throw new IllegalStateException(String.format(Messages.NOT_FOUND_OR_INVALID_FIELD, "type"));
        }
        schemaFormatter.setTypeDefaults(receivedForm);
        HashMap<String, Object> typeForm = typeForms.get(type.toString());
        Set<String> formFields = typeForm.keySet();
        for (String field : formFields) {
            if (field.equals("type")) {
                continue;
            }
            if (typeForm.get(field).equals("dataType")) {
                Object formToBeChecked = receivedForm.get(field);
                if (!(formToBeChecked instanceof HashMap)) {
                    throw new IllegalStateException(Messages.INVALID_SCHEMA_REPRESENTATION);
                }
                validateDataType((HashMap<String, Object>) formToBeChecked);
            } else {
                String fieldType = typeForm.get(field).toString();
                if (!isInstance(receivedForm.get(field), typesInJava.get(fieldType))) {
                    throw new IllegalStateException(String.format(Messages.NOT_FOUND_OR_INVALID_FIELD, field));
                }
            }
        }
        Set<String> formKeys = receivedForm.keySet();
        Optional<String> first = formKeys
                .stream()
                .map(Object::toString)
                .filter(field -> !typeForm.containsKey(field) && !generalForm.containsKey(field))
                .findFirst();
        if (first.isPresent()) {
            throw new IllegalStateException(String.format(Messages.UNKNOWN_FIELD_NAME, first.get()));
        }
    }

    private void validateFilters(HashMap<String, Object> receivedProperties, HashMap<String, Object> receivedFilters) {
        for (String filter : receivedFilters.keySet()) {
            if (!receivedProperties.containsKey(filter)) {
                throw new IllegalStateException("Schema is supposed to be filtered based on a property that it doesn't have at all");
            }
            if (!(receivedFilters.get(filter) instanceof HashMap<?,?>)) {
                throw new IllegalStateException("Filters must have a json object describes their specifications");
            }
            HashMap<String, Object> filterSpecifications = (HashMap<String, Object>) receivedFilters.get(filter);
            Object converter = filterSpecifications.get("converter");
            if (!(converter instanceof String) || !filterConverters.contains(converter.toString())) {
                throw new IllegalStateException("Unknown converter provided");
            }
        }
    }

    private HashMap<String, Object> readJSON(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        HashMap<String, Object> result = objectMapper.readValue(file, HashMap.class);
        return result;
    }

}
