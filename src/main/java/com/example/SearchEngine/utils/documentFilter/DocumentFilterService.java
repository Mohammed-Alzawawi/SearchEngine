package com.example.SearchEngine.utils.documentFilter;

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
    private HashMap<String, List<Integer>> allCurrentDocuments = new HashMap<>();
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
        if (!this.allCurrentDocuments.containsKey(schemaName)) {
            allCurrentDocuments.put(schemaName, new ArrayList<>());
        }
        allCurrentDocuments.get(schemaName).add((Integer) documentJson.get("id"));
        if (schema.get("filters") instanceof HashMap<?,?>) {
            HashMap<String, Object> filters = (HashMap<String, Object>) schema.get("filters");
            HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
            for (String filter : filters.keySet()) {
                HashMap<String, Object> originalProperty = (HashMap<String, Object>) properties.get(filter);
                Long convertedValue = converter.convert(documentJson.get(filter), originalProperty.get("type").toString(), ((HashMap<String, Object>) filters.get(filter)).get("converter").toString());
                this.schemaPropertiesBSTs.get(schemaName).get(filter).add(new DocumentNode(convertedValue, ((Integer) documentJson.get("id")).longValue()));
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
    private List<Integer> rangeFiltering(String schemaName, HashMap<String, HashMap<String, Object>> ranges) throws Exception {
        List<Integer> filteredDocuments = allCurrentDocuments.getOrDefault(schemaName, new ArrayList<>());
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
            else {
                minObject = ((Integer) minObject).longValue();
            }
            if (maxObject instanceof String) {
                if (maxObject.equals("inf")) {
                    maxObject =  Long.MAX_VALUE;
                }
                else {
                    maxObject = Long.MIN_VALUE;
                }
            }
            else {
                maxObject = ((Integer) maxObject).longValue();
            }
            Long min = (Long) minObject;
            Long max = (Long) maxObject;
            SortedSet<DocumentNode> RangeSubSet = propertyBST.subSet(new DocumentNode(min, Long.MIN_VALUE), true, new DocumentNode(max, Long.MAX_VALUE), true);
            List<Integer> documentsInRange = new ArrayList<>();
            for (DocumentNode documentNode : RangeSubSet) {
                documentsInRange.add((documentNode.getSecond().intValue()));
            }
            Collections.sort(documentsInRange);
            filteredDocuments = mergeTwoLists(filteredDocuments, documentsInRange);
        }
        return filteredDocuments;
    }

    private List<Integer> matchFiltering(String schemaName, HashMap<String, String> keywords) {
        List<Integer> filteredDocuments = allCurrentDocuments.getOrDefault(schemaName, new ArrayList<>());
        KeywordsNode root = SchemaRoot.getKeywordsSchemaRoot(schemaName);
        for (String fieldName : keywords.keySet()) {
            String word = keywords.get(fieldName);
            if (!keywordsTrie.checkWordExistence(root, word)) {
                filteredDocuments = new ArrayList<>();
                return filteredDocuments;
            }
            List<Integer> documentsInMatch = new ArrayList<>(keywordsTrie.getWordsLastNode(root, word)
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

    public List<Integer> getDocuments(String schemaName, HashMap<String, Object> filters) throws Exception {
        HashMap<String, String> matches = (HashMap<String, String>) filters.get("match");
        HashMap<String, HashMap<String, Object>> ranges = (HashMap<String, HashMap<String, Object>>) filters.get("range");
        List<Integer> filteredDocuments = rangeFiltering(schemaName, ranges);
        filteredDocuments = mergeTwoLists(filteredDocuments, matchFiltering(schemaName, matches));
        return filteredDocuments;
    }

    public void saveAllCurrentDocuments() throws Exception {
        String path = SCHEMA_ALL_CURRENT_DOCUMENTS_PATH + "AllCurrentDocuments";
        if (FileUtil.checkExistence(path)) {
            FileUtil.deleteFile(path);
        }
        File file = new File(path);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(allCurrentDocuments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllCurrentDocuments() {
        String path = SCHEMA_ALL_CURRENT_DOCUMENTS_PATH + "AllCurrentDocuments";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            allCurrentDocuments = (HashMap<String, List<Integer>>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Integer> mergeTwoLists(List<Integer> firstRange, List<Integer> secondRange) {
        if (firstRange.isEmpty()) {
            return firstRange;
        }
        else if (secondRange.isEmpty()) {
            return secondRange;
        }
        List<Integer> result = new ArrayList<>();
        int pointer1 = 0, pointer2 = 0;
        Integer max = Integer.max(firstRange.get(pointer1), secondRange.get(pointer2));
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
            max = Integer.max(firstRange.get(pointer1), secondRange.get(pointer2));
            pointer1 = lowerBound(firstRange, max);
            pointer2 = lowerBound(secondRange, max);
        }
        return result;
    };

    private int lowerBound(List<Integer> targetList, int targetElement) {
        int result = Collections.binarySearch(targetList, targetElement);
        if (result < 0) {
            result = -result - 1;
        }
        return result;
    }
}