package com.example.SearchEngine.document.controller;

import com.example.SearchEngine.document.service.DocumentStorageService;
import com.example.SearchEngine.document.service.Validation.DocumentValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("schema")
public class DocumentController {
    @Autowired
    private DocumentValidator documentValidator;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DocumentStorageService documentStorageService;


    @PostMapping("/{schemaName}")
    Map<String, Object> addDoc(@PathVariable String schemaName, @RequestBody String JsonArticle) throws Exception {
        Map<String, Object> json = objectMapper.readValue(JsonArticle, Map.class);
        documentStorageService.addDocument(schemaName, json);
        return json;
    }

}
