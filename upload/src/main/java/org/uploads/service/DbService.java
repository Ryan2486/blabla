package org.uploads.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.text.Normalizer;

@Service
public class DbService {
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DbService(JdbcTemplate template){
        this.setJdbcTemplate(template);
    } 

    private String[] processColumnAndValuesList(String[] columns){
        String[] processedColumnAndValues = new String[2];
        boolean containsLot = false;
        processedColumnAndValues[0] = "(";
        processedColumnAndValues[1] = "(";
        for (String column : columns) {
            if(column.toLowerCase().contains("lot")){
                containsLot = true;
            }
            processedColumnAndValues[0] += normalizeColumnName(column) + ", ";
            processedColumnAndValues[1] += "?, ";
        }
        if(!containsLot){
            processedColumnAndValues[0] += "Lot)";
            processedColumnAndValues[1] += "?)";
        }else{
            processedColumnAndValues[0] = processedColumnAndValues[0].substring(0, processedColumnAndValues[0].length() - 2) + ")";
            processedColumnAndValues[1] = processedColumnAndValues[1].substring(0, processedColumnAndValues[1].length() - 2) + ")";
        }
        return processedColumnAndValues;
    }

    public String createQuery(String tableName, String[] columns){
        String[] columnAndValuesList = this.processColumnAndValuesList(columns);
        String sqlQueryFirstPart = "INSERT INTO " + tableName + columnAndValuesList[0];
        String sqlQuerySecondPart = " VALUES " + columnAndValuesList[1];
        
        return sqlQueryFirstPart + sqlQuerySecondPart;
    }

    public void insertDataIntoOracle(List<String[]> data, String tableName, String[] columns, String lot) {
        String sql = this.createQuery(tableName, columns);
        System.out.println(data.get(0).length);
        // Batch update to insert all rows efficiently
        jdbcTemplate.batchUpdate(sql, data, data.size(), (ps, row) -> {
            for (int i = 0; i < row.length; i++) {
                ps.setString(i + 1, row[i]);
            }
            // Set the 'lot' as the last parameter
            ps.setString(row.length + 1, lot);
        });
    }

    public List<String> getAllUploadsTable() throws Exception{
        String sql = "SELECT table_name FROM user_tables WHERE table_name LIKE 'UP_%'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("table_name"));
    }

    public List<String> getAllColumnFromUploadsTable(String tableName) throws Exception{
        String sql = "SELECT column_name FROM user_tab_columns WHERE table_name = ?";
        
        return jdbcTemplate.query(
            sql,
            ps -> ps.setString(1, tableName.toUpperCase().trim()),
            (rs, rowNum) -> rs.getString("column_name")
        );   
    }

        // 1. Create the table LOT_tableName if it doesn't exist
    public void createLotTableIfNotExists(String tableName) {
        String createTableSQL = "BEGIN " +
        "EXECUTE IMMEDIATE 'CREATE TABLE LOT_tableName (" +
        "id_lot VARCHAR2(50), " +
        "nom_lot VARCHAR2(255), " +
        "date_importation DATE, " +
        "UNIQUE(nom_lot), " +
        "PRIMARY KEY(id_lot))'; " +
        "EXCEPTION WHEN OTHERS THEN " +
        "IF SQLCODE = -955 THEN NULL; ELSE RAISE; END IF; " + // Table already exists
        "END;";
        createTableSQL = createTableSQL.replace("tableName", tableName);
        jdbcTemplate.execute(createTableSQL);
    }

    // 2. Create the sequence LOT_tableName
    public void createLotSequenceIfNotExists(String tableName) {
        String createSequenceSQL = "BEGIN " +
        "EXECUTE IMMEDIATE 'CREATE SEQUENCE LOT_tableName_seq START WITH 1 INCREMENT BY 1'; " +
        "EXCEPTION WHEN OTHERS THEN " +
        "IF SQLCODE = -955 THEN NULL; ELSE RAISE; END IF; " + // Sequence already exists
        "END;";
        createSequenceSQL = createSequenceSQL.replace("tableName", tableName);
        jdbcTemplate.execute(createSequenceSQL);
    }

    // 3. Create a table based on the first row of data with a foreign key to LOT_tableName
    public void createImportTableFromFirstRow(List<String> columns, String filename) throws Exception {
        String tableName = normalizeColumnName(filename);  // Ensure the tableName is normalized

        // Building SQL script to create the table
        StringBuilder createTableSQL = new StringBuilder();
        createTableSQL.append("CREATE TABLE ").append(tableName).append(" (");
    
        for (String column : columns) { 
            createTableSQL.append(normalizeColumnName(column)).append(" VARCHAR2(1000), ");
        }
    
        createTableSQL.append("Lot VARCHAR2(50), ");
        createTableSQL.append("FOREIGN KEY (Lot) REFERENCES LOT_").append(normalizeColumnName(tableName)).append("(id_lot))");
    
        String sql = createTableSQL.toString();
        // Now use EXECUTE IMMEDIATE in PL/SQL block to handle if table already exists
        String fullSQL = "BEGIN " +
                         "EXECUTE IMMEDIATE '" + sql + "'; " +
                         "EXCEPTION WHEN OTHERS THEN " +
                         "IF SQLCODE = -955 THEN NULL; ELSE RAISE; END IF; " +
                         "END;";
    
        // Execute the SQL statement
        jdbcTemplate.execute(fullSQL);    }
    
    // Helper method to normalize column names
    private static String normalizeColumnName(String columnName) {
        // Normalize and remove diacritics (accents, trema, etc.)
        String normalized = Normalizer.normalize(columnName, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Replace spaces and other unwanted characters with underscores
        normalized = normalized.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase();

        // Ensure the name starts with a letter (required for Oracle columns)
        if (!Character.isLetter(normalized.charAt(0))) {
            normalized = "COL_" + normalized;
        }
        if(normalized.equals("DATE")){
            normalized = "\"" + normalized + "\"";
        }

        return normalized; // Oracle column names are usually uppercase
    }

    public void prepareImport(List<String> columns, String tableName) throws Exception{
        if(!tableName.contains("UP_")){
            tableName = "UP_" + tableName.toUpperCase();
        }
        createLotTableIfNotExists(tableName);
        createLotSequenceIfNotExists(tableName);
        createImportTableFromFirstRow(columns, tableName);
    }
}
