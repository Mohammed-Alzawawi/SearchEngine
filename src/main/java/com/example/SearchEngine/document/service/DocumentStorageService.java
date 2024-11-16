package com.example.SearchEngine.document.service;

import com.example.SearchEngine.document.service.Validation.DocumentValidator;
import com.example.SearchEngine.invertedIndex.InvertedIndex;
import com.example.SearchEngine.invertedIndex.utility.CollectionInfo;
import com.example.SearchEngine.schema.log.Command;
import com.example.SearchEngine.schema.log.TrieLogService;
import com.example.SearchEngine.utils.documentFilter.DocumentFilterService;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.example.SearchEngine.utils.storage.Snowflake;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentStorageService {

    @Autowired
    SchemaPathService schemaPathService;
    @Autowired
    InvertedIndex trieInvertedIndex;
    @Autowired
    private DocumentValidator documentValidator;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TrieLogService trieLogService;
    @Autowired
    private DocumentFilterService documentFilterService;
    @Autowired
    private Snowflake snowflake;

    public void addDocument(String schemaName, Map<String, Object> document) throws Exception {
        if (documentValidator.validate(schemaName, document)) {
            String path = schemaPathService.getSchemaPath(schemaName);

            long threadId = Thread.currentThread().getId();
            String id = snowflake.generate(threadId);
            document.put("id", id);
            JsonNode jsonNode = mapper.convertValue(document, JsonNode.class);
            path += "documents/" + jsonNode.get("id").toString();
            String content;
            try {
                content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error writing json file");
            }
            if (CollectionInfo.isDocumentExist(schemaName , (Integer)document.get("id"))) {
                throw new IllegalStateException("this document already exists");
            }
            FileUtil.createFile(path, content);
            trieInvertedIndex.addDocument(schemaName, document);
            documentFilterService.addDocument(schemaName, (HashMap<String, Object>) document);
            trieLogService.write(Command.INSERT, jsonNode.get("id").toString(), schemaName);
        } else {
            throw new IllegalStateException("document not valid to schema");
        }
    }

    public void deleteDocument(String schemaName, Integer documentId) throws Exception {
        if (!CollectionInfo.isDocumentExist(schemaName , documentId) ) {
            throw new IllegalStateException("this document not exists");
        }

        Map<String, Object> document = getDocument(schemaName, documentId);
        trieInvertedIndex.deleteDocument(schemaName,document);
        documentFilterService.removeDocument(schemaName, (HashMap<String, Object>) document);
        trieLogService.write(Command.DELETE,documentId.toString(), schemaName);
    }

    public  void updateDocument(String schemaName, Integer documentId, Map<String, Object> newDocument) throws Exception {
        deleteDocument(schemaName, documentId);
        addDocument(schemaName, newDocument);
    }

    public Map<String, Object> getDocument(String schemaName, Integer documentId) throws Exception {
        String path = schemaPathService.getSchemaPath(schemaName);
        path += "documents/" + documentId.toString();
        String content = FileUtil.readFileContents(path);
        Map<String, Object> document = mapper.readValue(content, Map.class);
        return document;
    }
}
