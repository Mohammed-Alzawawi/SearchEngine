package com.example.SearchEngine.schema.log;

import com.example.SearchEngine.Constants.Constants;
import com.example.SearchEngine.document.service.DocumentStorageService;
import com.example.SearchEngine.invertedIndex.InvertedIndex;
import com.example.SearchEngine.utils.storage.FileUtil;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LogUtil {
    @Autowired
    private static InvertedIndex trieInvertedIndex;

    @Autowired
    private static DocumentStorageService documentStorageService;

    private static final Object lock = new Object();
    public static void write(Command command, String documentId, String schemaName){
        if (command == null){
            throw new NullPointerException("command is null");
        }
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName;
        String logFilePath = folderPath + "/currentLog.txt";
        updateLog(command, documentId, logFilePath);
    }

    @Synchronized("lock")
    public static void refresh(String schemaName) throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName;
        String currentLogPath = folderPath + "/currentLog.txt";
        String logPath = folderPath + "/log.txt";
        try{
            BufferedReader reader = new BufferedReader(new FileReader(currentLogPath));
            FileWriter fileWriter = new FileWriter(logPath, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            String line;
            while ((line = reader.readLine()) != null) {
                printWriter.println(line);
            }
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        FileUtil.deleteFile(currentLogPath);
        FileUtil.createFile(currentLogPath, "");
    }

    public static void commitLog(String schemaName) throws Exception {
        String currentLogPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName + "/currentLog.txt";
        BufferedReader reader = new BufferedReader(new FileReader(currentLogPath));
        String line;
        while((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            if (Command.valueOf(words[1]) == Command.INSERT){
                Map<String, Object> document = documentStorageService.getDocument(schemaName, Integer.parseInt(words[2]));
                trieInvertedIndex.addDocument(schemaName, document);
            } else if (Command.valueOf(words[1]) == Command.DELETE){
                trieInvertedIndex.deleteDocument(schemaName, Integer.parseInt(words[2]));
            } else if (Command.valueOf(words[1]) == Command.UPDATE){
                // Update method from DocumentStorageService
            }
        }
        reader.close();
    }

    @Synchronized("lock")
    private static void updateLog(Command command, String documentId, String logPath){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss_MM/dd/yyyy");
        String date = now.format(formatter);
        String line = date + " " + command.toString() + " " + documentId;
        try {
            FileWriter fileWriter = new FileWriter(logPath, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(line);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void cleanUp(String logPath) throws Exception {
        Map<String, List<Pair>> actions = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(logPath));
        String line;
        long currentLine = 0;
        Command command = null;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            try{
                command = Command.valueOf(words[1]);
            }
            catch (Exception e){
                throw new IllegalArgumentException(e);
            }
            String documentId = words[2];
            if (!actions.containsKey(documentId)){
                actions.put(documentId, new ArrayList<>());
            }
            if (command == Command.DELETE){
                actions.remove(documentId);
            }
            else {
                List<Pair> pairs = actions.get(documentId);
                pairs.add(new Pair(currentLine++, command));
            }
        }
        reader.close();
        List<Tuple> updatedActions = new ArrayList<>();
        for (Map.Entry<String, List<Pair>> entry : actions.entrySet()){
            String documentId = entry.getKey();
            List<Pair> actionList = entry.getValue();
            for (Pair pair : actionList){
                Tuple tuple = new Tuple(pair.first, pair.second, documentId);
                updatedActions.add(tuple);
            }
        }
        FileUtil.deleteFile(logPath);
        FileUtil.createFile(logPath, "");
        Collections.sort(updatedActions);
        BufferedWriter writer = new BufferedWriter(new FileWriter(logPath));
        for (Tuple tuple : updatedActions){
            writer.write(tuple.toString());
            writer.newLine();
        }
        writer.close();
    }
}