package com.example.SearchEngine.utils.documentfilter.rangefilter;

import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import com.example.SearchEngine.utils.documentfilter.Merger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RangeFilterService {

    @Autowired
    private PropertiesBSTs propertiesBSTs;
    @Autowired
    private Merger merger;

    public List<Long> getFilteredDocuments(String schemaName, HashMap<String, HashMap<String, Object>> ranges) throws Exception {
        List<Long> filteredDocuments = CollectionInfo.getAllSchemaDocuments(schemaName);
        for (String property : ranges.keySet()) {
            NavigableSet<BSTNode> propertyBST = propertiesBSTs.getPropertyBST(schemaName, property);
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
            Long min = Long.parseLong(minObject.toString());
            Long max = Long.parseLong(maxObject.toString());
            SortedSet<BSTNode> RangeSubSet = propertyBST.subSet(new BSTNode(min, Long.MIN_VALUE), true, new BSTNode(max, Long.MAX_VALUE), true);
            List<Long> documentsInRange = new ArrayList<>();
            for (BSTNode BSTNode : RangeSubSet) {
                documentsInRange.add((BSTNode.getSecond()));
            }
            Collections.sort(documentsInRange);
            filteredDocuments = merger.mergeTwoLists(filteredDocuments, documentsInRange);
        }
        return filteredDocuments;
    }
}
