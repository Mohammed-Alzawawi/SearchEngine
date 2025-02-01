package com.example.SearchEngine.configuration;

import com.example.SearchEngine.invertedIndex.utility.TrieSerialization;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {
    @Autowired
    private TrieSerialization trieSerialization;

    @PostConstruct
    public void init() throws Exception {
        trieSerialization.loadTrie();
    }
}
