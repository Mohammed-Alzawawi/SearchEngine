package com.example.SearchEngine.document.controller;

import com.example.SearchEngine.document.service.DocumentStorageService;
import com.example.SearchEngine.invertedIndex.service.search.TrieEngine;
import com.example.SearchEngine.invertedIndex.utility.TrieSerialization;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("schema")
public class DocumentController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DocumentStorageService documentStorageService;
    @Autowired
    private TrieSerialization trieSerialization;
    @Autowired
    private TrieEngine trieEngine;


    @PostMapping("/{schemaName}")
    Map<String, Object> addDoc(@PathVariable String schemaName, @RequestBody String JsonArticle) throws Exception {
        Map<String, Object> json = objectMapper.readValue(JsonArticle, Map.class);
        documentStorageService.addDocument(schemaName, json);
        return json;
    }

    @PostMapping("/{schemaName}/{documentId}")
    void deleteDocument(@PathVariable String schemaName, @PathVariable Integer documentId) throws Exception {
        documentStorageService.deleteDocument(schemaName, documentId);
    }

    @PostMapping("/search/{schemaName}")
    List<Object> search(@PathVariable String schemaName, @RequestBody HashMap<String, Object> query) throws Exception {
        return trieEngine.search(query, schemaName);
    }

    @GetMapping("/load")
    void load() throws Exception {
        trieSerialization.loadTrie();
    }

    @GetMapping("/save")
    void save() throws Exception {
        trieSerialization.saveTrie();
    }


}
