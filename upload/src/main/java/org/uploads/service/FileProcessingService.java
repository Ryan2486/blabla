package org.uploads.service;

import org.uploads.entity.Lot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FileProcessingService {

    private final SequenceService sequence;
    private final JdbcTemplate jdbcTemplate;
    private final DbService dbService;

    public FileProcessingService(SequenceService sequenceService, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sequence = sequenceService;
        this.dbService = new DbService(jdbcTemplate);
    }

    public void prepareImport(List<String> columns, String fileName) throws Exception{
        dbService.prepareImport(columns, fileName);
    }

    @Transactional
    public void processGenericExcelFile(String filePath, Lot exelLot, String tableName, String[] columns) throws Exception{
        try {
            // if(!tableName.contains("UP_")){
            //     tableName = "UP_" + tableName.toUpperCase();
            // }
            List<String[]> objects = ExcelReader.readExcel(filePath);
            String numeroLot = insertLot(exelLot, tableName); 
            this.dbService.insertDataIntoOracle(objects, tableName, columns, numeroLot);
        } catch (Exception e) {
            throw e;
        }
    }

    public String insertLot(Lot excelLot, String tableName) throws Exception{
        String sequenceName = "lot_" + tableName + "_seq";
        String lotTableName = "lot_" + tableName;
        String primaryKey = this.sequence.generateFormattedSequenceValue(sequenceName, "LOT", 12);
        String insertSql = "INSERT INTO #tableName#(ID_LOT, NOM_LOT, DATE_IMPORTATION) VALUES (?, ?, ?)";
        insertSql = insertSql.replace("#tableName#", lotTableName);
        DateTimeFormatter formatter =  DateTimeFormatter.ISO_DATE_TIME;

        // Parse the date-time string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(excelLot.getDate(), formatter);
        
        // Convert LocalDateTime to java.sql.Timestamp
        Timestamp sqlTimestamp = Timestamp.valueOf(localDateTime);
        int value = jdbcTemplate.update(insertSql,
            primaryKey,
            excelLot.getNom(),
            sqlTimestamp);
            
        System.out.println("NUMBER OF ROW UPDATED: " + value);
        return primaryKey;
    }
}

