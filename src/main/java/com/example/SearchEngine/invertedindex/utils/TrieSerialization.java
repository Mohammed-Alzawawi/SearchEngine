package com.example.SearchEngine.invertedindex.utils;

import com.example.SearchEngine.invertedindex.TrieNode;
import com.example.SearchEngine.invertedindex.service.fuzzysearch.FuzzyNode;
import com.example.SearchEngine.invertedindex.service.fuzzysearch.FuzzyRoot;
import com.example.SearchEngine.schema.log.TrieLogLoader;
import com.example.SearchEngine.schema.log.TrieLogService;
import com.example.SearchEngine.schema.utils.SchemaRoot;
import com.example.SearchEngine.utils.documentfilter.DocumentFilterService;
import com.example.SearchEngine.utils.documentfilter.matchfilter.KeywordsNode;
import com.example.SearchEngine.utils.documentfilter.rangefilter.PropertiesBSTs;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

import static com.example.SearchEngine.constants.Constants.Paths.SCHEMA_KEYWORD_TRIES_PATH;
import static com.example.SearchEngine.constants.Constants.Paths.SCHEMA_PATH_DICTIONARY_PATH;


@Service
public class TrieSerialization {
    @Autowired
    SchemaPathService schemaPathService;
    @Autowired
    TrieLogService trieLogService;
    @Autowired
    TrieLogLoader trieLogLoader;
    @Autowired
    private DocumentFilterService documentFilterService;
    @Autowired
    private PropertiesBSTs propertiesBSTs;


    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    public void saveTrie() throws Exception {
        CollectionInfo.save();
        for (String schemaName : FuzzyRoot.roots.keySet()) {
            String path = schemaPathService.getSchemaPath(schemaName) + "fuzzyTrie";
            if (FileUtil.checkExistence(path)) {
                FileUtil.deleteFile(path);
            }
            File file = new File(path);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(FuzzyRoot.roots.get(schemaName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String schemaName : SchemaRoot.keywordsRoots.keySet()) {
            String path = SCHEMA_KEYWORD_TRIES_PATH + schemaName + "KeywordsTrie";
            if (FileUtil.checkExistence(path)) {
                FileUtil.deleteFile(path);
            }
            File file = new File(path);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(SchemaRoot.getKeywordsSchemaRoot(schemaName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String schemaName : SchemaRoot.invertedIndexRoots.keySet()) {
            String path = schemaPathService.getSchemaPath(schemaName) + "trie";
            if (FileUtil.checkExistence(path)) {
                FileUtil.deleteFile(path);
            }
            File file = new File(path);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(SchemaRoot.invertedIndexRoots.get(schemaName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            trieLogService.refresh(schemaName);
        }
        propertiesBSTs.savePropertiesBSTs();
    }

    public void loadTrie() throws Exception {
        CollectionInfo.load();
        List<String> schemasNames = FileUtil.getFilesInDirectory(SCHEMA_PATH_DICTIONARY_PATH);
        for (String schemaName : schemasNames) {
            String path = schemaPathService.getSchemaPath(schemaName);
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + "fuzzyTrie"))) {
                FuzzyRoot.roots.put(schemaName, (FuzzyNode) ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + "trie"))) {
                SchemaRoot.invertedIndexRoots.put(schemaName, (TrieNode) ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCHEMA_KEYWORD_TRIES_PATH + schemaName + "KeywordsTrie"))) {
                SchemaRoot.keywordsRoots.put(schemaName, (KeywordsNode) ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            trieLogLoader.load(schemaName);
        }
        propertiesBSTs.loadPropertiesBSTs();
    }

}
