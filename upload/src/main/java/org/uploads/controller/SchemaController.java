package org.uploads.controller;

import org.uploads.service.DbService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uploads.response.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController

@RequestMapping("/schema")
public class SchemaController {

    @Autowired
    DbService dbService;

    @PostMapping("/tables")
    public APIResponse getTableNames() {
        APIResponse response = new APIResponse();
        try {
            List<String> listOfUploadTables = dbService.getAllUploadsTable();
            response.setData(listOfUploadTables);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    @PostMapping("/columns")
    public APIResponse getTableColumnNames(@RequestBody String tableName) {
        APIResponse response = new APIResponse();
        try {
            List<String> listOfUploadTablesColumns = dbService.getAllColumnFromUploadsTable(tableName);
            response.setData(listOfUploadTablesColumns);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }
}
