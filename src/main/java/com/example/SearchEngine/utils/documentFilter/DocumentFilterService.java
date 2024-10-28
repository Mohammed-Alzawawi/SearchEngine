package com.example.SearchEngine.utils.documentFilter;

import com.example.SearchEngine.invertedIndex.TrieNode;
import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import com.example.SearchEngine.schema.util.SchemaRoot;
import com.example.SearchEngine.utils.documentFilter.converter.Converter;
import com.example.SearchEngine.utils.storage.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static com.example.SearchEngine.constants.Constants.Paths.SCHEMA_PROPERTIES_BSTs_PATH;

@Service
public class DocumentFilterService {

    @Autowired
    private SchemaServiceInterface schemaService;

    @Autowired
    private Converter converter;
    private HashMap<String, HashMap<String, NavigableSet<DocumentNode>>> schemaPropertiesBSTs = new HashMap<>();

    public void addNewSchema(HashMap<String, Object> schema) {
        schemaPropertiesBSTs.putIfAbsent(schema.get("name").toString(), new HashMap<>());
        HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
        for (String filter : filters.keySet()) {
            schemaPropertiesBSTs.get(schema.get("name").toString()).put(filter, new TreeSet<>());
        }
    }

    DocumentFilterService() {}

    public void addDocument(String schemaName, HashMap<String, Object> documentJson) throws Exception {
        HashMap<String, Object> schema = schemaService.getSchema(schemaName);
        if (schema.get("filters") instanceof HashMap<?,?>) {
            HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
            HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
            for (String filter : filters.keySet()) {
                HashMap<String, Object> originalProperty = (HashMap<String, Object>) properties.get(filter);
                Long convertedValue = converter.convert(documentJson.get(filter), originalProperty.get("type").toString(), filters.get(filter).toString());
                this.schemaPropertiesBSTs.get(schemaName).get(filter).add(new DocumentNode(convertedValue, (Long) documentJson.get("id")));
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
                Long convertedValue = converter.convert(documentJson.get(filter), originalProperty.get("type").toString(), filters.get(filter).toString());
                this.schemaPropertiesBSTs.get(schemaName).get(filter).remove(new DocumentNode(convertedValue, (Long) documentJson.get("id")));
            }
        }
    }
    private List<Long> rangeFiltering(String schemaName, HashMap<String, HashMap<String, Object>> ranges) {
        List<Long> filteredDocuments = new ArrayList<>();
        for (String property : ranges.keySet()) {
            NavigableSet<DocumentNode> propertyBST = schemaPropertiesBSTs.get(schemaName).get(property);
            Object minObject = ranges.get(property).get("min");
            Object maxObject = ranges.get(property).get("max");
            if (minObject instanceof String) {
                if (minObject.equals("inf")) {
                    minObject =  Long.MAX_VALUE;
                }
                else {
                    minObject = Long.MIN_VALUE;
                }
            }
            if (maxObject instanceof String) {
                if (maxObject.equals("inf")) {
                    maxObject =  Long.MAX_VALUE;
                }
                else {
                    maxObject = Long.MIN_VALUE;
                }
            }
            Long min = (Long) minObject;
            Long max = (Long) maxObject;
            SortedSet<DocumentNode> RangeSubSet = propertyBST.subSet(new DocumentNode(min, Long.MIN_VALUE), true, new DocumentNode(max, Long.MAX_VALUE), true);
            List<Long> documentsInRange = new ArrayList<>();
            for (DocumentNode documentNode : RangeSubSet) {
                documentsInRange.add(documentNode.getSecond());
            }
            Collections.sort(documentsInRange);
            filteredDocuments = mergeTwoLists(filteredDocuments, documentsInRange);
        }
        return filteredDocuments;
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
            schemaPropertiesBSTs = (HashMap<String, HashMap<String, NavigableSet<DocumentNode>>>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, HashMap<String, NavigableSet<DocumentNode>>> getSchemaPropertiesBSTs() {
        return schemaPropertiesBSTs;
    }

    public List<Long> getDocuments(String schemaName, HashMap<String, Object> filters) {
        HashMap<String, HashMap<String, Object>> ranges = (HashMap<String, HashMap<String, Object>>) filters.get("rangeFilter");
        List<Long> filteredDocuments = rangeFiltering(schemaName, ranges);
        return filteredDocuments;
    }

    private List<Long> mergeTwoLists(List<Long> firstRange, List<Long> secondRange) {
        List<Long> result = new ArrayList<>();
        int pointer1 = 0, pointer2 = 0;
        Long max = Long.max(firstRange.get(pointer1), secondRange.get(pointer2));
        pointer1 = Collections.binarySearch(firstRange, max);
        pointer2 = Collections.binarySearch(secondRange, max);
        while (pointer1 < firstRange.size() && pointer2 < secondRange.size()) {
            while (pointer1 < firstRange.size() && pointer2 < secondRange.size() && firstRange.get(pointer1) == secondRange.get(pointer2)) {
                result.add(firstRange.get(pointer1));
                pointer1++;
                pointer2++;
            }
            max = Long.max(firstRange.get(pointer1), secondRange.get(pointer2));
            pointer1 = Collections.binarySearch(firstRange, max);
            pointer2 = Collections.binarySearch(secondRange, max);
        }
        return result;
    };

    public static void main(String[] args) throws Exception {
        DocumentFilterService documentFilterService = new DocumentFilterService();
//        HashMap<String, NavigableSet<DocumentNode>> something = new HashMap<>();
//        NavigableSet<DocumentNode> emptyNavigable = new TreeSet<>();
//        emptyNavigable.add(new DocumentNode(1L, 2L));
//        something.put("property1", emptyNavigable);
//        documentFilterService.schemaPropertiesBSTs.put("schema1", something);
//        documentFilterService.savePropertiesBSTs();
        documentFilterService.loadPropertiesBSTs();
        System.out.println(documentFilterService.getSchemaPropertiesBSTs());
    }

}