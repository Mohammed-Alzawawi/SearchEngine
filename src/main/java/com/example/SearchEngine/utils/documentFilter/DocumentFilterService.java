package com.example.SearchEngine.utils.documentFilter;

import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import com.example.SearchEngine.utils.documentFilter.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocumentFilterService {

    @Autowired
    private SchemaServiceInterface schemaService;

    @Autowired
    private Converter converter;
    private HashMap<String, HashMap<String, NavigableSet<DocumentNode>>> schemaPropertiesBSTs = new HashMap<>();

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
    private List<Long> rangeFiltering(String schemaName, HashMap<String, HashMap<String, Long>> ranges) {
        List<Long> filteredDocuments = new ArrayList<>();
        for (String property : ranges.keySet()) {
            NavigableSet<DocumentNode> propertyBST = schemaPropertiesBSTs.get(schemaName).get(property);
//            "-inf" and "inf" case
            Long min = ranges.get(property).get("min");
            Long max = ranges.get(property).get("max");
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

    public List<Long> getDocuments(String schemaName, HashMap<String, Object> filters) {
        HashMap<String, HashMap<String, Long>> ranges = (HashMap<String, HashMap<String, Long>>) filters.get("rangeFilter");
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
}