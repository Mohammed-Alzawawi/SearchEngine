package com.example.SearchEngine.invertedIndex.utility;

import com.example.SearchEngine.Constants.Constants;
import com.example.SearchEngine.utils.storage.FileUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CollectionInfo {
    private static Map<String, Integer> numberOfDocument = new HashMap<>();
    private static Map<String, Map<String, Long>> fieldTotalLength = new HashMap<>();
    private static Map<String, Map<Integer, Map<String, Long>>> documentInfo = new HashMap<>();

    public static Long getDocumentLength(String schemaName, Integer documentId, String fieldName) {
        return documentInfo.getOrDefault(schemaName, new HashMap<>())
                .getOrDefault(documentId, new HashMap<>())
                .getOrDefault(fieldName, 0L);
    }

    public static void updateDocumentLength(String schemaName, Integer documentId, Long length, String fieldName) {
        documentInfo.putIfAbsent(schemaName, new HashMap<>());
        documentInfo.get(schemaName).putIfAbsent(documentId, new HashMap<>());
        documentInfo.get(schemaName).get(documentId).put(fieldName, length);
    }

    public static Long getFieldTotalLength(String schemaName, String fieldName) {
        return fieldTotalLength.getOrDefault(schemaName, new HashMap<>())
                .getOrDefault(fieldName, 0L);
    }

    public static void updateFieldTotalLength(String schemaName, String fieldName, Long length) {
        fieldTotalLength.putIfAbsent(schemaName, new HashMap<>());
        fieldTotalLength.get(schemaName).putIfAbsent(fieldName, 0L);
        fieldTotalLength.get(schemaName).put(fieldName, fieldTotalLength.get(schemaName).get(fieldName) + length);
    }

    public static void updateNumberOfDocument(String schemaName) {
        numberOfDocument.putIfAbsent(schemaName, 0);
        numberOfDocument.put(schemaName, numberOfDocument.get(schemaName) + 1);
    }

    public static Integer getNumberOfDocument(String schemaName) {
        return numberOfDocument.getOrDefault(schemaName, 0);
    }

    private static void saveAttribute(Object object, String path) throws Exception {
        if (FileUtil.checkExistence(path)) {
            FileUtil.deleteFile(path);
        }
        File file = new File(path);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() throws Exception {
        saveAttribute(numberOfDocument, Constants.Paths.SCHEMA_STORAGE_PATH + "numberOfDocument");
        saveAttribute(fieldTotalLength, Constants.Paths.SCHEMA_STORAGE_PATH + "fieldTotal");
        saveAttribute(documentInfo, Constants.Paths.SCHEMA_STORAGE_PATH + "documentInfo");
    }

    private static Object loadAttribute(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void load() throws Exception {
        numberOfDocument = (Map<String, Integer>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "numberOfDocument");
        fieldTotalLength = (Map<String, Map<String, Long>>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "fieldTotal");
        documentInfo = (Map<String, Map<Integer, Map<String, Long>>>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "documentInfo");
    }
}
