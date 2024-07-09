package com.example.SearchEngine.document.service;

import com.example.SearchEngine.document.service.Validation.DocumentValidator;
import com.example.SearchEngine.invertedIndex.TrieInvertedIndex;
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
    TrieInvertedIndex trieInvertedIndex;
    @Autowired
    private DocumentValidator documentValidator;
    @Autowired
    private ObjectMapper mapper;

    private void checkID(JsonNode jsonNode) {
        if (!jsonNode.has("id") || (!jsonNode.get("id").isInt() && !jsonNode.get("id").isLong())) {
            throw new IllegalStateException("ID not found");
        }
    }


    public void addDocument(String schemaName, Map<String, Object> document) throws Exception {
        if (documentValidator.validate(schemaName, document)) {
            String path = schemaPathService.getSchemaPath(schemaName);

            JsonNode jsonNode = mapper.convertValue(document, JsonNode.class);
            checkID(jsonNode);
            path += "documents/" + jsonNode.get("id").toString();
            String content;
            try {
                content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error writing json file");
            }
            FileUtil.createFile(path, content);
            trieInvertedIndex.addDocument(schemaName, document);
        } else {
            throw new IllegalStateException("document not valid to schema");
        }
    }

    public void deleteDocument(String schemaName, Integer documentId) throws Exception {
        Map<String, Object> document = getDocument(schemaName, documentId);
        JsonNode jsonNode = mapper.convertValue(document, JsonNode.class);
        checkID(jsonNode);
        String path = schemaPathService.getSchemaPath(schemaName);
        path += "documents/" + jsonNode.get("id").toString();
        FileUtil.deleteFile(path);
        trieInvertedIndex.deleteDocument(schemaName, documentId);
    }

    public Map<String, Object> getDocument(String schemaName, Integer documentId) throws Exception {
        String path = schemaPathService.getSchemaPath(schemaName);
        path += "documents/" + documentId.toString();
        String content = FileUtil.readFileContents(path);
        Map<String, Object> document = mapper.readValue(content, Map.class);
        return document;
    }
}
