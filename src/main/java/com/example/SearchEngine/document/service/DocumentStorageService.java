package com.example.SearchEngine.document.service;

import com.example.SearchEngine.document.service.Validation.DocumentValidator;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DocumentStorageService {

    @Autowired
    SchemaPathService schemaPathService;
    @Autowired
    private DocumentValidator documentValidator;
    @Autowired
    private ObjectMapper mapper;


    private void checkID(JsonNode jsonNode) {
        if (!jsonNode.has("id") || (!jsonNode.get("id").isInt() && !jsonNode.get("id").isLong())) {
            throw new IllegalStateException("ID not found");
        }
    }


    public void addDocument(String schemaName, Map<String, Object> doc) throws Exception {
        if (documentValidator.validate(schemaName, doc)) {
            String path = schemaPathService.getSchemaPath(schemaName);

            JsonNode jsonNode = mapper.convertValue(doc, JsonNode.class);
            checkID(jsonNode);
            path += "documents/" + jsonNode.get("id").toString();
            String content;
            try {
                content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error writing json file");
            }
            try {
                FileUtil.createFile(path, content);
                System.out.println("Done creating the Json File");
            } catch (Exception e) {
                throw new IllegalStateException("Problem with creating the Json File!");
            }
        }
    }

    public void deleteDocument(String schemaName, Map<String, Object> doc) throws Exception {
        JsonNode jsonNode = mapper.convertValue(doc, JsonNode.class);
        checkID(jsonNode);
        String path = schemaPathService.getSchemaPath(schemaName);
        path += "documents/" + jsonNode.get("id").toString();
        FileUtil.deleteFile(path);
    }
}
