package com.example.SearchEngine.schema.log;

import com.example.SearchEngine.Constants.Constants;
import com.example.SearchEngine.document.service.DocumentStorageService;
import com.example.SearchEngine.invertedIndex.InvertedIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

@Service
public class TrieLogLoader {
    @Autowired
    private DocumentStorageService documentStorageService;

    @Autowired
    private InvertedIndex trieInvertedIndex;

    public void load(String schemaName) throws Exception {
        String currentLogPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName + "/currentLog.txt";
        BufferedReader reader = new BufferedReader(new FileReader(currentLogPath));
        String line;
        while((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            if (Command.valueOf(words[1]) == Command.INSERT){
                Map<String, Object> document = documentStorageService.getDocument(schemaName, Integer.parseInt(words[2]));
                trieInvertedIndex.addDocument(schemaName, document);
            } else if (Command.valueOf(words[1]) == Command.DELETE){
                trieInvertedIndex.deleteDocument(schemaName, Integer.parseInt(words[2]));
            } else if (Command.valueOf(words[1]) == Command.UPDATE){
                // Update method from DocumentStorageService
            }
        }
        reader.close();
    }
}
