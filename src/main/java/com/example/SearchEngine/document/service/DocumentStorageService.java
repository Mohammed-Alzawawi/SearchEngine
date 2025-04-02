package com.example.SearchEngine.document.service;

import com.example.SearchEngine.document.service.validation.DocumentValidator;
import com.example.SearchEngine.invertedindex.InvertedIndex;
import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import com.example.SearchEngine.schema.log.Command;
import com.example.SearchEngine.schema.log.TrieLogService;
import com.example.SearchEngine.utils.documentfilter.DocumentFilterService;
import com.example.SearchEngine.utils.documentfilter.matchfilter.KeywordsTrie;
import com.example.SearchEngine.utils.documentfilter.rangefilter.PropertiesBSTs;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class DocumentStorageService {

    private final ConcurrentHashMap<String, ReentrantReadWriteLock> lockMap = new ConcurrentHashMap<>();
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
    private PropertiesBSTs propertiesBSTs;
    @Autowired
    private KeywordsTrie keywordsTrie;
    @Autowired
    private Snowflake snowflake;

    private ReentrantReadWriteLock getLock(String schemaName, Long documentId) {
        return lockMap.computeIfAbsent(schemaName + ":" + documentId, s -> new ReentrantReadWriteLock());
    }

    public void addDocument(String schemaName, Map<String, Object> document) throws Exception {
        if (!documentValidator.validate(schemaName, document)) {
            throw new IllegalStateException("document not valid to schema");
        }

        long threadId = Thread.currentThread().getId();
        Long id = snowflake.generate(threadId);
        document.put("id", id);

        JsonNode jsonNode = mapper.convertValue(document, JsonNode.class);
        String path = schemaPathService.getSchemaPath(schemaName) + "documents/" + jsonNode.get("id").toString();
        String content;
        try {
            content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error writing json file");
        }
        if (CollectionInfo.isDocumentExist(schemaName, (Long) document.get("id"))) {
            throw new IllegalStateException("this document already exists");
        }

        FileUtil.createFile(path, content);
        trieInvertedIndex.addDocument(schemaName, document);
        propertiesBSTs.addDocument(schemaName, (HashMap<String, Object>) document);
        keywordsTrie.addDocument(schemaName, (HashMap<String, Object>) document);
        trieLogService.write(Command.INSERT, jsonNode.get("id").toString(), schemaName);
    }

    public void deleteDocument(String schemaName, Long documentId) throws Exception {
        if (!CollectionInfo.isDocumentExist(schemaName, documentId)) {
            throw new IllegalStateException("this document not exists");
        }

        Map<String, Object> document = getDocument(schemaName, documentId);
        trieInvertedIndex.deleteDocument(schemaName, document);
        propertiesBSTs.removeDocument(schemaName, (HashMap<String, Object>) document);
        keywordsTrie.deleteDocument(schemaName, (HashMap<String, Object>) document);
        trieLogService.write(Command.DELETE,documentId.toString(), schemaName);
    }


    public void updateDocument(String schemaName, Long documentId, Map<String, Object> fields) throws Exception {
        ReentrantReadWriteLock.WriteLock writeLock = getLock(schemaName, documentId).writeLock();
        writeLock.lock();
        try {
            if (!documentValidator.updateValidation(schemaName, fields)) {
                throw new IllegalStateException("not valid update");
            }

            Map<String, Object> document = getDocument(schemaName, documentId);
            for (String fieldName : fields.keySet()) {
                if (document.containsKey(fieldName)) {
                    trieInvertedIndex.removeField(schemaName, document, fieldName);
                }
                document.put(fieldName, fields.get(fieldName));
                trieInvertedIndex.addField(schemaName, document, fieldName);
            }

            JsonNode jsonNode = mapper.convertValue(document, JsonNode.class);
            String path = schemaPathService.getSchemaPath(schemaName) + "documents/" + jsonNode.get("id").toString();
            FileUtil.deleteFile(path);
            String content;
            try {
                content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error writing json file");
            }
            FileUtil.createFile(path, content);
        } finally {
            writeLock.unlock();
        }
    }

    public Map<String, Object> getDocument(String schemaName, Long documentId) throws Exception {
        ReentrantReadWriteLock.ReadLock readLock = getLock(schemaName, documentId).readLock();
        readLock.lock();
        try {
            String path = schemaPathService.getSchemaPath(schemaName);
            path += "documents/" + documentId.toString();
            String content = FileUtil.readFileContents(path);
            Map<String, Object> document = mapper.readValue(content, Map.class);
            return document;
        } finally {
            readLock.unlock();
        }
    }
}
