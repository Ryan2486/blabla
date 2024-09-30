package org.trace.utils;

public class StatusStatisticsOfEachEndpoint {
    String endpoint;
    StatusStatistics statistics;

    public void setEndpoint(String name){
        this.endpoint = name;
    }

    public String getEndpoint(){
        return this.endpoint;
    }

    public void setStatistics(StatusStatistics stat){
        this.statistics = stat;
    }
    public StatusStatistics getStatistics(){
        return this.statistics;
    }

    public StatusStatisticsOfEachEndpoint(String endpoint){
        this.setEndpoint(endpoint);
        this.setStatistics(new StatusStatistics());
    }
}
