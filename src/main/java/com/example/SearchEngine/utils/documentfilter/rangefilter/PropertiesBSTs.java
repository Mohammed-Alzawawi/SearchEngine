package com.example.SearchEngine.utils.documentfilter.rangefilter;

import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import com.example.SearchEngine.utils.documentfilter.converter.Converter;
import com.example.SearchEngine.utils.storage.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.NavigableSet;
import java.util.TreeSet;

import static com.example.SearchEngine.constants.Constants.Paths.SCHEMA_PROPERTIES_BSTs_PATH;

@Service
public class PropertiesBSTs {

    @Autowired
    private SchemaServiceInterface schemaService;
    @Autowired
    private Converter converter;
    private HashMap<String, HashMap<String, NavigableSet<BSTNode>>> schemaPropertiesBSTs = new HashMap<>();

    public void addNewSchema(HashMap<String, Object> schema) {
        schemaPropertiesBSTs.putIfAbsent(schema.get("id").toString(), new HashMap<>());
        HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
        for (String filter : filters.keySet()) {
            schemaPropertiesBSTs.get(schema.get("id").toString()).put(filter, new TreeSet<>());
        }
    }

    public void addDocument(String schemaName, HashMap<String, Object> documentJson) throws Exception {
        HashMap<String, Object> schema = schemaService.getSchema(schemaName);
        if (!this.schemaPropertiesBSTs.containsKey(schemaName)) {
            addNewSchema(schemaService.getSchema(schemaName));
        }
        CollectionInfo.insertDocument(schemaName, (Long) documentJson.get("id")); // no need for this, added from invertedIndex
        if (schema.get("filters") instanceof HashMap<?,?>) {
            HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
            HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
            for (String filter : filters.keySet()) {
                HashMap<String, Object> originalProperty = (HashMap<String, Object>) properties.get(filter);
                Long convertedValue = converter.convert(documentJson.get(filter), originalProperty.get("type").toString(), ((HashMap<String, Object>) filters.get(filter)).get("converter").toString());
                this.schemaPropertiesBSTs.get(schemaName).get(filter).add(new BSTNode(convertedValue, ((Long) documentJson.get("id"))));
            }
        }
    }

    public void removeDocument(String schemaName, HashMap<String, Object> documentJson) throws Exception {
        HashMap<String, Object> schema = schemaService.getSchema(schemaName);
        if (schema.get("filters") instanceof HashMap<?,?>) {
            HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
            HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
            for (String filter : filters.keySet()) {
                HashMap<String, Object> originalProperty = (HashMap<String, Object>) properties.get(filter);
                Long convertedValue = converter.convert(documentJson.get(filter), originalProperty.get("type").toString(),  ((HashMap<String, Object>) filters.get(filter)).get("converter").toString());
                this.schemaPropertiesBSTs.get(schemaName).get(filter).remove(new BSTNode(convertedValue, ((Long) documentJson.get("id"))));
            }
        }
    }

    public NavigableSet<BSTNode> getPropertyBST(String schemaName, String property) {
        return schemaPropertiesBSTs.get(schemaName).get(property);
    }

    public void savePropertiesBSTs() throws Exception {
        String path = SCHEMA_PROPERTIES_BSTs_PATH + "BSTsHashMap";
        if (FileUtil.checkExistence(path)) {
            FileUtil.deleteFile(path);
        }
        File file = new File(path);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(schemaPropertiesBSTs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPropertiesBSTs() {
        String path = SCHEMA_PROPERTIES_BSTs_PATH + "BSTsHashMap";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            schemaPropertiesBSTs = (HashMap<String, HashMap<String, NavigableSet<BSTNode>>>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
