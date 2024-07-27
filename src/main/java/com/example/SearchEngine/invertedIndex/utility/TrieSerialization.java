package com.example.SearchEngine.invertedIndex.utility;

import com.example.SearchEngine.invertedIndex.TrieNode;
import com.example.SearchEngine.schema.util.SchemaRoot;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

import static com.example.SearchEngine.Constants.Constants.Paths.SCHEMA_PATH_DICTIONARY_PATH;


@Service
public class TrieSerialization {
    @Autowired
    SchemaPathService schemaPathService;

    public void saveTrie() throws Exception {
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
        }
    }

    public void loadTrie() throws Exception {
        List<String> schemasNames = FileUtil.getFilesInDirectory(SCHEMA_PATH_DICTIONARY_PATH);
        for (String schemaName : schemasNames) {
            String path = schemaPathService.getSchemaPath(schemaName);
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + "trie"))) {
                SchemaRoot.roots.put(schemaName, (TrieNode) ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
