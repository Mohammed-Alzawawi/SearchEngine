package com.example.SearchEngine.Controller;

import com.example.SearchEngine.Services.JsonSchemaValedation.ValidateJsonToSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("schema")
public class control {
    @Autowired
    private ValidateJsonToSchema validateJsonToSchema ;
    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/{schemaName}")
    String addSchema(@PathVariable String schemaName , @RequestBody String JsonArticle ) throws IOException {
        Map<String , Object> json = objectMapper.readValue(JsonArticle , Map.class) ;
        if(validateJsonToSchema.validate(schemaName , json)) {
            return  "Done" ;
        }
        return  "not Done";
   }
}
