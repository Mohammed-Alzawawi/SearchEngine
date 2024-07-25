package com.example.SearchEngine.schema.log;

import com.example.SearchEngine.Constants.Constants;
import com.example.SearchEngine.utils.storage.FileUtil;
import lombok.Synchronized;

import java.io.*;
import java.util.*;

public class LogUtil {
    private static final Object lock = new Object();

    public static void write(Command command, String documentId, String schemaName){
        if (command == null){
            throw new NullPointerException("command is null");
        }
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName;
        String logFilePath = folderPath + "/" + "currentLog.txt";
        updateLog(command, documentId, logFilePath);
    }

    @Synchronized("lock")
    public static void refresh(String schemaName) throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + schemaName;
        String currentLogPath = folderPath + "/" + "currentLog.txt";
        String logPath = folderPath + "/" + "log.txt";
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
        cleanUp(logPath);
    }

    @Synchronized("lock")
    private static void updateLog(Command command, String documentId, String logPath){
        String line = command.toString() + " " + documentId;
        try {
            FileWriter fileWriter = new FileWriter(logPath, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(line);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void cleanUp(String logPath) throws Exception {
        Map<String, List<Pair>> actions = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(logPath));
        String line;
        long currentLine = 0;
        Command command = null;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            try{
                command = Command.valueOf(words[0]);
            }
            catch (Exception e){
                throw new IllegalArgumentException(e);
            }
            String documentId = words[1];
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