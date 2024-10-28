package com.example.SearchEngine.invertedIndex.utility;

import com.example.SearchEngine.invertedIndex.TrieNode;
import com.example.SearchEngine.invertedIndex.service.fuzzySearch.FuzzyNode;
import com.example.SearchEngine.invertedIndex.service.fuzzySearch.FuzzyRoot;
import com.example.SearchEngine.schema.log.TrieLogLoader;
import com.example.SearchEngine.schema.log.TrieLogService;
import com.example.SearchEngine.schema.util.SchemaRoot;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

import static com.example.SearchEngine.constants.Constants.Paths.SCHEMA_PATH_DICTIONARY_PATH;


@Service
public class TrieSerialization {
    @Autowired
    SchemaPathService schemaPathService;
    @Autowired
    TrieLogService trieLogService;
    @Autowired
    TrieLogLoader trieLogLoader;

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

        for (String schemaName : SchemaRoot.roots.keySet()) {
            String path = schemaPathService.getSchemaPath(schemaName) + "trie";
            if (FileUtil.checkExistence(path)) {
                FileUtil.deleteFile(path);
            }
            File file = new File(path);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(SchemaRoot.roots.get(schemaName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            trieLogService.refresh(schemaName);
        }
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
                SchemaRoot.roots.put(schemaName, (TrieNode) ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            trieLogLoader.load(schemaName);
        }
    }

}
