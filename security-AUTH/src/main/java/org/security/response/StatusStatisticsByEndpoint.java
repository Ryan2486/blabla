package org.security.response;

import java.util.ArrayList;
import java.util.List;

import org.trace.service.Statistics;
import org.trace.utils.LogData;
import org.trace.utils.StatusStatisticsOfEachEndpoint;;

public class StatusStatisticsByEndpoint {
    List<LogData> dataLogs;
    List<String> endpoints = new ArrayList<>();
    List<Integer> status2xx = new ArrayList<>();    
    List<Integer> status4xx = new ArrayList<>();
    List<Integer> status5xx = new ArrayList<>();

    public List<LogData> getDataLogs() {
        return dataLogs;
    }
    public void setDataLogs(List<LogData> dataList) {
        this.dataLogs = dataList;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }
    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
    public List<Integer> getStatus2xx() {
        return status2xx;
    }
    public void setStatus2xx(List<Integer> status2xx) {
        this.status2xx = status2xx;
    }
    public List<Integer> getStatus4xx() {
        return status4xx;
    }
    public void setStatus4xx(List<Integer> status4xx) {
        this.status4xx = status4xx;
    }
    public List<Integer> getStatus5xx() {
        return status5xx;
    }
    public void setStatus5xx(List<Integer> status5xx) {
        this.status5xx = status5xx;
    }

    public StatusStatisticsByEndpoint fromLogDataToStatistics(List<LogData> logs){
        StatusStatisticsByEndpoint stats = new StatusStatisticsByEndpoint();
        stats.setDataLogs(logs);
        List<StatusStatisticsOfEachEndpoint> statsOfEachEndpoint = Statistics.getNumberStatisticsOfEachEndpoint(logs);
        for (StatusStatisticsOfEachEndpoint statusStats : statsOfEachEndpoint) {
            stats.getEndpoints().add(statusStats.getEndpoint());
            stats.getStatus2xx().add(statusStats.getStatistics().getStatux2xx());
            stats.getStatus4xx().add(statusStats.getStatistics().getStatux4xx());
            stats.getStatus5xx().add(statusStats.getStatistics().getStatux5xx());
        }    
        return stats;
    }
}
