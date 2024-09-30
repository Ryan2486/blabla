package org.uploads.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ExcelReader {

    public static List<String[]> readExcel(String filePath) throws Exception {
        List<String[]> objects = new ArrayList<>();
        FileInputStream fileInputStream = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (isRowCurrentlyEmpty(row)) {
                break; // Stop processing if the row is empty
            }
            if (row.getRowNum() == 0) { // Skip header row
                continue;
            }
            objects.add(processCurrentRow(row));
            // System.out.println(objects.size());
        }
        workbook.close();
        fileInputStream.close();
        return objects;
    }

    public static boolean isRowCurrentlyEmpty(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        for (Cell cell : row) {
            String cellValue = dataFormatter.formatCellValue(cell).trim();
            if (!cellValue.isEmpty()) {
                return false; // Row is not empty if any cell has a value
            }
        }
        return true; // Row is empty if all cells are empty
    }

    public static String[] processCurrentRow(Row row) throws Exception {
        List<String> rowData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                                    .withZone(ZoneId.of("UTC"));
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case STRING:
                    rowData.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Handle date cells
                        rowData.add(formatter.format(cell.getDateCellValue().toInstant()));
                    } else {
                        rowData.add(String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case BOOLEAN:
                    rowData.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                case FORMULA:
                    CellValue cellValue = row.getSheet().getWorkbook().getCreationHelper()
                                            .createFormulaEvaluator().evaluate(cell);
                    switch (cellValue.getCellType()) {
                        case STRING:
                            rowData.add(cellValue.getStringValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                rowData.add(formatter.format(DateUtil.getJavaDate(cellValue.getNumberValue()).toInstant()));
                            } else {
                                rowData.add(String.valueOf(cellValue.getNumberValue()));
                            }
                            break;
                        case BOOLEAN:
                            rowData.add(String.valueOf(cellValue.getBooleanValue()));
                            break;
                        default:
                            rowData.add("");
                    }
                    break;
                default:
                    break;
            }
        }
        return rowData.toArray(new String[0]);
    }
}

