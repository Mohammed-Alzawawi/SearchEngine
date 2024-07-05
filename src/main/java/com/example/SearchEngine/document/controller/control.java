package com.example.SearchEngine.document.controller;

import com.example.SearchEngine.document.service.docMmanipulate.DocStorageService;
import com.example.SearchEngine.document.service.docSchemaValidation.ValidateDocToSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("schema")
public class control {
    @Autowired
    private ValidateDocToSchema validateDocToSchema;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DocStorageService docStorageService;


    @PostMapping("/{schemaName}")
    String addSchema(@PathVariable String schemaName , @RequestBody String JsonArticle ) throws Exception {
        Map<String , Object> json = objectMapper.readValue(JsonArticle , Map.class) ;
        docStorageService.addDoc(schemaName , json);
        return  "Done";
   }
}
