package org.trace.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.trace.utils.LogData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APILogger {

    private static final String LOG_DIRECTORY_PATH = "./LOGS";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void logRequest(LogData logEntry){
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String logFilePath = LOG_DIRECTORY_PATH + File.separator + "logs_" + currentDate + ".json"; 

        // Create de directory if it doesn't exist
        File file = new File(LOG_DIRECTORY_PATH);
        file.mkdir();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        logEntry.setDateTime(timestamp);
        try {
            // Read existing log entries
            List<LogData> logEntries = readLogFileEntries(logFilePath);
    
            // Add the new log entry
            logEntries.add(logEntry);
    
            // Write the updated list of log entries to the file
            writeLogFileEntries(logFilePath, logEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    // Function to read existing log entries from the file
    public static List<LogData> readLogFileEntries(String logFilePath) throws IOException {
        List<LogData> logEntries = new ArrayList<>();
        File logFile = new File(logFilePath);
        // Read existing log entries if the file exists and is not empty
        if (logFile.exists() && logFile.length() > 0) {
            logEntries = objectMapper.readValue(
                Files.readAllBytes(Paths.get(logFilePath)),
                new TypeReference<List<LogData>>() {}
            );
        }else{
            logFile.createNewFile();
        }
        return logEntries;
    }

    // Function to write log entries to the file
    private static void writeLogFileEntries(String logFilePath, List<LogData> logEntries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logEntries));
        } catch (IOException e) {
            throw e;
        }
    }

    public static List<LogData> AllLogstoObject() throws IOException {
        List<LogData> logResult = new ArrayList<>();
        //Creating a File object for directory
        File directoryPath = new File(LOG_DIRECTORY_PATH);
        //List of all files and directories
        File filesList[] = directoryPath.listFiles();
        for (File file : filesList) {
            logResult.addAll(readLogFileEntries(file.getAbsolutePath()));
        }
        return logResult;
    }

    public static List<String> getAllLogFileName() throws IOException{
        List<String> listOfLogFileName = new ArrayList<>();
        //Creating a File object for directory
        File directoryPath = new File(LOG_DIRECTORY_PATH);
        //List of all files and directories
        File filesList[] = directoryPath.listFiles();
        for (File file : filesList) {
            listOfLogFileName.add(file.getName());
        }
        return listOfLogFileName;
        
    }

    public static List<LogData> LogtoObject(String logFilePath) throws IOException  {
        logFilePath = LOG_DIRECTORY_PATH + File.separator + logFilePath;        
        return readLogFileEntries(logFilePath);        
    }

    public static List<LogData> LogtoObject(List<File> logFiles) throws IOException  {
        List<LogData> listOfLogObject = new ArrayList<>();
        for (File file : logFiles) {
            listOfLogObject.addAll(readLogFileEntries(file.getAbsolutePath()));
        }        
        return listOfLogObject;        
    }
}
