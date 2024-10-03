package org.uploads.controller;

import org.uploads.response.APIResponse;
import org.uploads.service.FileService;
import org.trace.service.APICallDetails;
import org.trace.service.APILogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    @Autowired
    private FileService fileUploadService;

    @PostMapping("/upload_generic")
    @PreAuthorize("hasAuthority('Users')")
    public ResponseEntity<?> uploadFileGeneric(
        @RequestParam("file") MultipartFile file,
        @RequestParam("tableName") String tableName,
        @RequestParam("columns") String columnsJson,
        @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
        HttpServletRequest request, 
        HttpServletResponse response
        ) {
        APIResponse message = new APIResponse();
        String data = "";
        try {
            data += "Filename :" + file.getOriginalFilename();
            data += ", CheckSum : " + FileService.getChecksum(file);
            if(!tableName.equals("") && !columnsJson.equals("[]")){
                ObjectMapper mapper = new ObjectMapper();
                String[] columns = mapper.readValue(columnsJson, String[].class);
                message = fileUploadService.genericUploadAndReadFile(file, tableName, columns);
            }else if(!tableName.equals("") && columnsJson.equals("[]")){
                message = fileUploadService.genericUploadAndReadFile(file, tableName);
            }
            else{
                message = fileUploadService.genericUploadAndReadFile(file);
            }
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
            message.setError(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            e.printStackTrace();
            message.setError(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            APILogger.logRequest(APICallDetails.getClientDetails(forwardedFor, request, response, data));
        }
        return ResponseEntity.status(response.getStatus()).body(message);
    }
}
