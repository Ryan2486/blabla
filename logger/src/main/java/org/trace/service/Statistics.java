package org.trace.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.trace.filter.LogFileFilter;
import org.trace.utils.LogData;
import org.trace.utils.StatusStatisticsOfEachEndpoint;

import java.util.Set;
import java.util.HashSet;

public class Statistics {

    private static Set<String> getAllEndpoint(List<LogData> logs){
        Set<String> endpoints = new HashSet<>();
        for (LogData log : logs) {
            endpoints.add(log.getEndpoint());
        }
        return endpoints;
    }

    private static List<StatusStatisticsOfEachEndpoint> getListOfStatisticsByEndpoints(List<LogData> logs){
        Set<String> endpoints = getAllEndpoint(logs);
        List<StatusStatisticsOfEachEndpoint> listOfStatsByEndPoint = new ArrayList<>();
        for (String endpoint : endpoints) {
            listOfStatsByEndPoint.add(new StatusStatisticsOfEachEndpoint(endpoint));
        }
        return listOfStatsByEndPoint;
    }

    public static List<StatusStatisticsOfEachEndpoint> getNumberStatisticsOfEachEndpoint(List<LogData> logs){
        List<StatusStatisticsOfEachEndpoint> numberStatistics = getListOfStatisticsByEndpoints(logs);
        List<StatusStatisticsOfEachEndpoint> listOfStatsByEndPoint = new ArrayList<>();
        for (StatusStatisticsOfEachEndpoint stats : numberStatistics) {
            for (LogData log : logs) {
                if(log.getEndpoint().equals(stats.getEndpoint())){
                    if(log.getStatus() >= 200 && log.getStatus() < 400){
                        stats.getStatistics().addStatut2xx();
                    }else if(log.getStatus() >= 400 && log.getStatus() < 500){
                        stats.getStatistics().addStatut4xx();
                    }else if(log.getStatus() >= 500){
                        stats.getStatistics().addStatut5xx();                        
                    }
                }
            }
            listOfStatsByEndPoint.add(stats);
        }
        return listOfStatsByEndPoint;
    }
    
    public static List<LogData> getLogBefore(LocalDate date) throws IOException{
        List<LogData> listOfLogDataBeforeGivenDate = new ArrayList<>();
        listOfLogDataBeforeGivenDate = APILogger.LogtoObject(
            LogFileFilter.getFilesBefore(date)
        );
        return listOfLogDataBeforeGivenDate;
    }
    
    public static List<LogData> getLogAfter(LocalDate date) throws IOException{
        List<LogData> listOfLogDataAfterGivenDate = new ArrayList<>();
        listOfLogDataAfterGivenDate = APILogger.LogtoObject(
            LogFileFilter.getFilesAfter(date)
        );
        return listOfLogDataAfterGivenDate;
    }
    
    public static List<LogData> getLogBetween(LocalDate dateStart, LocalDate dateEnd) throws IOException{
        List<LogData> listOfLogDataBetweenGivenDate = new ArrayList<>();
        listOfLogDataBetweenGivenDate = APILogger.LogtoObject(
            LogFileFilter.getFilesBetween(dateStart, dateEnd)
        );
        return listOfLogDataBetweenGivenDate;
    }

    public static List<LogData> getLogFrom(LocalDate date) throws Exception{
        List<LogData> listOfLogDataBeforeGivenDate = new ArrayList<>();
        File file = LogFileFilter.getFilesFrom(date);
        if(file == null){
            throw new Exception("There is no log from this date:" + date.toString());
        }
        listOfLogDataBeforeGivenDate = APILogger.readLogFileEntries(file.getAbsolutePath());
        return listOfLogDataBeforeGivenDate;
    }    
}
