package com.example.SearchEngine.Controller;

import com.example.SearchEngine.Services.JsonSchemaValedation.ValidateJsonToSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("user")
public class control {
    @Autowired
    ValidateJsonToSchema validateJsonToSchema ;
    @Autowired
    ObjectMapper objectMapper;


    @PostMapping("/article")
    void addArticle(@RequestBody String JsonArticle) throws IOException {
        String schemaName = "articel" ;
        Map<String , Object> json = objectMapper.readValue(JsonArticle , Map.class) ;

        if(validateJsonToSchema.validate(schemaName , json)) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }
   }
}
