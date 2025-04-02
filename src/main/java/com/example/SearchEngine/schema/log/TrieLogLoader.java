package com.example.SearchEngine.schema.log;

import com.example.SearchEngine.constants.Constants;
import com.example.SearchEngine.document.service.DocumentStorageService;
import com.example.SearchEngine.invertedindex.InvertedIndex;
import com.example.SearchEngine.utils.documentfilter.DocumentFilterService;
import com.example.SearchEngine.utils.documentfilter.matchfilter.KeywordsTrie;
import com.example.SearchEngine.utils.documentfilter.rangefilter.PropertiesBSTs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class TrieLogLoader {
    @Autowired
    private DocumentStorageService documentStorageService;
    @Autowired
    DocumentFilterService documentFilterService;
    @Autowired
    private InvertedIndex trieInvertedIndex;
    @Autowired
    private PropertiesBSTs propertiesBSTs;
    @Autowired
    private KeywordsTrie keywordsTrie;

    public void load(String schemaName) throws Exception {
        String currentLogPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName + "/currentLog.txt";
        BufferedReader reader = new BufferedReader(new FileReader(currentLogPath));
        String line;
        while((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            Map<String, Object> document = documentStorageService.getDocument(schemaName, Long.parseLong(words[2]));
            if (Command.valueOf(words[1]) == Command.INSERT){
                trieInvertedIndex.addDocument(schemaName, document);
                propertiesBSTs.addDocument(schemaName, (HashMap<String, Object>) document);
                keywordsTrie.addDocument(schemaName, (HashMap<String, Object>) document);
            } else if (Command.valueOf(words[1]) == Command.DELETE){
                trieInvertedIndex.deleteDocument(schemaName, document);
                propertiesBSTs.removeDocument(schemaName, (HashMap<String, Object>) documentStorageService.getDocument(schemaName, Long.parseLong(words[2])));
                keywordsTrie.deleteDocument(schemaName, (HashMap<String, Object>) documentStorageService.getDocument(schemaName, Long.parseLong(words[2])));
            } else if (Command.valueOf(words[1]) == Command.UPDATE){
                // Update method from DocumentStorageService
            }
        }
        reader.close();
    }
}
