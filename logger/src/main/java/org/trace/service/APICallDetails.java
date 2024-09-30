package org.trace.service;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.trace.utils.LogData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class APICallDetails {

    private static final String IP_LOCATION_API_URL = "http://ip-api.com/json/";

    private APICallDetails(){
        throw new IllegalStateException("Utility class");
    }
    
    public static LogData getClientDetails(String forwardedFor, HttpServletRequest request, HttpServletResponse response, Object data){
        LogData logDataResult = new LogData();
        String ipAddres = getClientIp(request, forwardedFor);
        logDataResult.setIpAddress(ipAddres);
        logDataResult.setLocation(getClientLocation(ipAddres));
        logDataResult.setEndpoint(request.getRequestURI());
        logDataResult.setMethod(request.getMethod());
        if(data == null){
            logDataResult.setData(request.getQueryString());
        }else{
            logDataResult.setData(data.toString());
        }
        logDataResult.setStatus(response.getStatus());
        return logDataResult;
    } 

    public static String getClientIp(HttpServletRequest request, String forwardedFor) {
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0];
        } else {
            return request.getRemoteAddr();
        }
    }
    
    public static String getClientLocation(String ipAddress){
        String location = "Unknown";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(IP_LOCATION_API_URL + ipAddress);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode responseJson = mapper.readTree(json);
                    if(responseJson.get("status").asText().equals("success")){
                        String city = responseJson.get("city").asText();
                        String region = responseJson.get("regionName").asText();
                        String country = responseJson.get("country").asText();
                        double latitude = responseJson.get("lat").asDouble();
                        double longitude = responseJson.get("lon").asDouble();
                        String isp = responseJson.get("lon").asText();
                        location = city + ", " + region + ", " + country + ", longitude: " + longitude + ", latitude: " + latitude + ", Fournisseur: " + isp;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location; 
    }
}
