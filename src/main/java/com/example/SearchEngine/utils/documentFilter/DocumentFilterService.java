package com.example.SearchEngine.utils.documentFilter;

import com.example.SearchEngine.invertedIndex.utility.CollectionInfo;
import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import com.example.SearchEngine.schema.util.SchemaRoot;
import com.example.SearchEngine.utils.documentFilter.converter.Converter;
import com.example.SearchEngine.utils.documentFilter.matchFilter.KeywordsNode;
import com.example.SearchEngine.utils.documentFilter.matchFilter.KeywordsTrie;
import com.example.SearchEngine.utils.storage.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static com.example.SearchEngine.Constants.Constants.Paths.*;

@Service
public class DocumentFilterService {

    @Autowired
    private SchemaServiceInterface schemaService;
    @Autowired
    private Converter converter;
    @Autowired
    private KeywordsTrie keywordsTrie;
    private HashMap<String, HashMap<String, NavigableSet<DocumentNode>>> schemaPropertiesBSTs = new HashMap<>();

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
        CollectionInfo.insertDocument(schemaName, (Long) documentJson.get("id"));
        if (schema.get("filters") instanceof HashMap<?,?>) {
            HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
            HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
            for (String filter : filters.keySet()) {
                HashMap<String, Object> originalProperty = (HashMap<String, Object>) properties.get(filter);
                Long convertedValue = converter.convert(documentJson.get(filter), originalProperty.get("type").toString(), ((HashMap<String, Object>) filters.get(filter)).get("converter").toString());
                this.schemaPropertiesBSTs.get(schemaName).get(filter).add(new DocumentNode(convertedValue, ((Long) documentJson.get("id"))));
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
                this.schemaPropertiesBSTs.get(schemaName).get(filter).remove(new DocumentNode(convertedValue, ((Long) documentJson.get("id"))));
            }
        }
    }
    private List<Long> rangeFiltering(String schemaName, HashMap<String, HashMap<String, Object>> ranges) throws Exception {
        List<Long> filteredDocuments = CollectionInfo.getAllSchemaDocuments(schemaName);
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
                documentsInRange.add((documentNode.getSecond()));
            }
            Collections.sort(documentsInRange);
            filteredDocuments = mergeTwoLists(filteredDocuments, documentsInRange);
        }
        return filteredDocuments;
    }

    private List<Long> matchFiltering(String schemaName, HashMap<String, String> keywords) {
        List<Long> filteredDocuments = CollectionInfo.getAllSchemaDocuments(schemaName);
        KeywordsNode root = SchemaRoot.getKeywordsSchemaRoot(schemaName);
        for (String fieldName : keywords.keySet()) {
            String word = keywords.get(fieldName);
            if (!keywordsTrie.checkWordExistence(root, word)) {
                filteredDocuments = new ArrayList<>();
                return filteredDocuments;
            }
            List<Long> documentsInMatch = new ArrayList<>(keywordsTrie.getWordsLastNode(root, word)
                                                            .getDocuments()
                                                            .getOrDefault(fieldName, new HashSet<>()));
            Collections.sort(documentsInMatch);
            filteredDocuments = mergeTwoLists(documentsInMatch, filteredDocuments);
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
    public List<Long> getDocuments(String schemaName, HashMap<String, Object> filters) throws Exception {
        HashMap<String, HashMap<String, Object>> ranges = (HashMap<String, HashMap<String, Object>>) filters.getOrDefault("range", new HashMap<>());
        HashMap<String, String> matches = (HashMap<String, String>) filters.getOrDefault("match", new HashMap<>());
        List<Long> filteredDocuments = CollectionInfo.getAllSchemaDocuments(schemaName);
        filteredDocuments = mergeTwoLists(rangeFiltering(schemaName, ranges), filteredDocuments);
        filteredDocuments = mergeTwoLists(matchFiltering(schemaName, matches), filteredDocuments);
        return filteredDocuments;
    }

    public List<Long> mergeTwoLists(List<Long> firstRange, List<Long> secondRange) {
        if (firstRange.isEmpty()) {
            return firstRange;
        }
        else if (secondRange.isEmpty()) {
            return secondRange;
        }
        List<Long> result = new ArrayList<>();
        int pointer1 = 0, pointer2 = 0;
        Long max = Math.max(firstRange.get(pointer1), secondRange.get(pointer2));
        pointer1 = lowerBound(firstRange, max);
        pointer2 = lowerBound(secondRange, max);
        while (pointer1 < firstRange.size() && pointer2 < secondRange.size()) {
            while ((pointer1 < firstRange.size()) && (pointer2 < secondRange.size()) && (Objects.equals(firstRange.get(pointer1), secondRange.get(pointer2)))) {
                result.add(firstRange.get(pointer1));
                pointer1++;
                pointer2++;
            }
            if (pointer1 >= firstRange.size() || pointer2 >= secondRange.size()) {
                break;
            }
            max = Math.max(firstRange.get(pointer1), secondRange.get(pointer2));
            pointer1 = lowerBound(firstRange, max);
            pointer2 = lowerBound(secondRange, max);
        }
        return result;
    }

    private int lowerBound(List<Long> targetList, long targetElement) {
        int result = Collections.binarySearch(targetList, targetElement);
        if (result < 0) {
            result = -result - 1;
        }
        return result;
    }
}