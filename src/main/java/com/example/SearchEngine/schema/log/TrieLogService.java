package com.example.SearchEngine.schema.log;

import com.example.SearchEngine.constants.Constants;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TrieLogService {

    private final Object lock = new Object();
    @Autowired
    private SchemaPathService schemaPathService;

    public void write(Command command, String documentId, String schemaName){
        if (command == null){
            throw new NullPointerException("command is null");
        }
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName;
        String logFilePath = folderPath + "/currentLog.txt";
        updateLog(command, documentId, logFilePath);
    }

    @Synchronized("lock")
    public void refresh(String schemaName)throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName;
        String currentLogPath = folderPath + "/currentLog.txt";
        String logPath = folderPath + "/log.txt";
        try{
            BufferedReader reader = new BufferedReader(new FileReader(currentLogPath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(logPath, true));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                if (Command.valueOf(words[1]) == Command.DELETE) {
                    String path = schemaPathService.getSchemaPath(schemaName);
                    path += "documents/" + words[2];
                    FileUtil.deleteFile(path);
                }
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            reader.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        try {
            FileUtil.deleteFile(currentLogPath);
            FileUtil.createFile(currentLogPath, "");
        }
        catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }

    @Synchronized("lock")
    private void updateLog(Command command, String documentId, String logPath){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss_MM/dd/yyyy");
        String date = now.format(formatter);
        String line = date + " " + command.toString() + " " + documentId + "\n";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logPath, true));
            writer.write(line);
            writer.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void cleanUp(String logPath) throws Exception {
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logPath, true));
            for (Tuple tuple : updatedActions) {
                writer.write(tuple.toString());
                writer.newLine();
            }
            writer.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}