package com.example.SearchEngine.invertedIndex.utility;

import com.example.SearchEngine.Constants.Constants;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.utils.storage.FileUtil;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CollectionInfo {
    private static Map<String, Integer> numberOfDocument = new HashMap<>();
    private static Map<String, Double> documentsTotalLength = new HashMap<>();
    private static Map<String, HashSet<Integer>> allDocuments = new HashMap<>();
    private static Map<String, Map<Integer, Double>> documentLength = new HashMap<>();

    public static Double getDocumentLength(String schemaName, Integer documentId) {
        return documentLength.getOrDefault(schemaName, new HashMap<>())
                .getOrDefault(documentId, 0.0);
    }

    public static Double getDocumentsTotalLength(String schemaName) {
        return documentsTotalLength.getOrDefault(schemaName, 1.0);
    }

    public static void insertDocument(String schemaName, Integer documentId) {
        allDocuments.putIfAbsent(schemaName, new HashSet<>());
        allDocuments.get(schemaName).add(documentId);
        updateNumberOfDocument(schemaName, 1);
    }

    public static boolean isDocumentExist(String schemaName, Integer documentId) {
        allDocuments.putIfAbsent(schemaName, new HashSet<>());
        return allDocuments.get(schemaName).contains(documentId);
    }

    public static void removeDocument(String schemaName, Integer documentId) {
        if (allDocuments.get(schemaName).contains(documentId)) {
            allDocuments.get(schemaName).remove(documentId);
            updateNumberOfDocument(schemaName, -1);
        }
    }


    public static void updateDocumentLength(String schemaName, Integer documentId, Double length) {
        documentLength.putIfAbsent(schemaName, new HashMap<>());
        documentLength.get(schemaName).put(documentId, documentLength.get(schemaName).getOrDefault(documentId, 0.0) + length);
        documentsTotalLength.put(schemaName, documentsTotalLength.getOrDefault(schemaName, 0.0) + length);
    }


    public static void updateNumberOfDocument(String schemaName, Integer value) {
        numberOfDocument.putIfAbsent(schemaName, 0);
        numberOfDocument.put(schemaName, numberOfDocument.get(schemaName) + value);
    }

    public static Integer getNumberOfDocument(String schemaName) {
        return numberOfDocument.getOrDefault(schemaName, 0);
    }

    public static void addField(String schemaName, Integer documentId, List<Token> tokens) {
        Integer length = tokens.size();
        Double weight = tokens.get(0).getWeight();
        updateDocumentLength(schemaName, documentId, weight * length);
    }

    public static void removeField(String schemaName, Integer documentId, List<Token> tokens) {
        Integer length = tokens.size();
        Double weight = tokens.get(0).getWeight();
        updateDocumentLength(schemaName, documentId, -1 * weight * length);
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
        saveAttribute(documentLength, Constants.Paths.SCHEMA_STORAGE_PATH + "documentLength");
        saveAttribute(documentsTotalLength, Constants.Paths.SCHEMA_STORAGE_PATH + "documentsTotalLength");
        saveAttribute(allDocuments, Constants.Paths.SCHEMA_STORAGE_PATH + "allDocuments");
    }

    private static Object loadAttribute(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static void load() throws Exception {
        numberOfDocument = (Map<String, Integer>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "numberOfDocument");
        documentsTotalLength = (Map<String, Double>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "documentsTotalLength");
        documentLength = (Map<String, Map<Integer, Double>>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "documentLength");
        allDocuments = (Map<String, HashSet<Integer>>) loadAttribute(Constants.Paths.SCHEMA_STORAGE_PATH + "allDocuments");
    }
}
