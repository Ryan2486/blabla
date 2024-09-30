package org.security.controller;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trace.service.Statistics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;

import org.security.response.APIResponse;
import org.security.response.StatusStatisticsByEndpoint;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/logs")
public class LogsController {
    
    @GetMapping("/of")    
    public ResponseEntity<?> getLogBetween(
        @RequestParam("dateStart") String dateStart, 
        @RequestParam("dateEnd") String dateEnd,
        HttpServletRequest request, 
        HttpServletResponse response

        ){
        APIResponse apiResponse = new APIResponse(); 
        StatusStatisticsByEndpoint stats = new StatusStatisticsByEndpoint();
        try {
            if(dateStart.equals("") && dateEnd.equals("")){
                LocalDate today = LocalDate.now();
                apiResponse.setData(stats.fromLogDataToStatistics(Statistics.getLogFrom(today)));
            }else if(dateStart.equals("") && !dateEnd.equals("")){
                apiResponse.setData(stats.fromLogDataToStatistics(Statistics.getLogBefore(
                    LocalDate.parse(dateEnd))
                ));
            }else if(!dateStart.equals("") && dateEnd.equals("")){
                apiResponse.setData(stats.fromLogDataToStatistics(Statistics.getLogAfter(
                    LocalDate.parse(dateStart)
                )));
            }else if(!dateStart.equals("") && !dateEnd.equals("")){
                apiResponse.setData(stats.fromLogDataToStatistics(Statistics.getLogBetween(
                    LocalDate.parse(dateStart), 
                    LocalDate.parse(dateEnd))
                ));
            }            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setError(e.getMessage());
        }
        return  ResponseEntity.status(response.getStatus()).body(apiResponse);
    }

}
