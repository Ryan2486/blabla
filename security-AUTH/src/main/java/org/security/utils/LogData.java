package org.security.utils;

public class LogData {
    String ipAddress;
    String location;
    String dateTime;
    int status;
    String data;
    String endpoint;
    String method;

    public LogData(){}
    
    public LogData(String ipAddress, String location, String dateTime, int status, String endpoint, String method) {
        this.setIpAddress(ipAddress);
        this.setLocation(location);
        this.setDateTime(dateTime);
        this.setStatus(status);
        this.setData(data);
        this.setEndpoint(endpoint);
        this.setMethod(method);    
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        data = data.replace("\n", "");
        this.data = data;
    }
}
