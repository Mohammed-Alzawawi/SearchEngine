package com.example.SearchEngine.utils.documentFilter;

import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentFilterService {

    private final SchemaServiceInterface schemaService;
    private HashMap<String, HashMap<String, PropertyBST>> schemaPropertiesBSTs;

    public DocumentFilterService(SchemaServiceInterface schemaService) throws Exception {
        this.schemaService = schemaService;
        for (String schemaName : schemaService.getSchemasNames()) {
            this.schemaPropertiesBSTs.put(schemaName, new HashMap<>());
            Map<String, Object> schema = schemaService.getSchema(schemaName);
            Map<String, Object> filters = new HashMap<>();
            if (schema.containsKey("filters")) {
                filters = (Map<String, Object>) schema.get("filters");
            }
            for (String filter : filters.keySet()) {
                this.schemaPropertiesBSTs.get(schemaName).put(filter, loadPropertyBST(filter));
            }
        }
//        schemaPropertiesBSTs = FiltersBuilder.getBSTsFromFileSystem();
    }
    private PropertyBST loadPropertyBST(String propertyName) {
        return new PropertyBST();
    }

    private List<Integer> rangeFiltering(String schema, HashMap<String, Object> ranges) {
        List<Integer> filteredDocuments = new ArrayList<>();
        for (String property : ranges.keySet()) {
            schemaPropertiesBSTs.get(schema).get(property);
        }
        return filteredDocuments;
    }
    public List<Integer> getDocuments(HashMap<String, Object> filters) {
        List<Integer> filteredDocuments = new ArrayList<>();

        return filteredDocuments;
    }
}
