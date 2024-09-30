package org.uploads.service; 


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.uploads.entity.Lot;
import org.uploads.response.APIResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.math.BigInteger;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private static final String UPLOAD_DIR = "UPLOADS/";
    private final FileProcessingService processingService;

    public FileService(FileProcessingService fileProcessingService){
        this.processingService = fileProcessingService;
    }

    public void checkExcelColumns(ArrayList<String> columnsToVerify, ArrayList<String> columnsPattern) throws IOException{
        for (String column : columnsToVerify) {
            if(!columnsPattern.contains(column)){
                throw new IllegalArgumentException("The columns of the file you provided don't follow the pattern on the column: "+ column +", please make them like this :" + columnsPattern.toString());
            }
        }
    }

    public APIResponse genericUploadAndReadFile(MultipartFile file, String tableName, String[] columns) throws Exception {
        APIResponse result= new APIResponse();
        InputStream inputstream = file.getInputStream();
        if(isExcelFile(file.getInputStream())){
            // Read only the first row of the Excel content
            ArrayList<String> firstRowContent = readFirstRow(inputstream, file.getOriginalFilename());
            result.setMessage("File uploaded successfully: " + file.getOriginalFilename());
            result.setData(firstRowContent);
            uploadGenericFile(file, tableName, columns);
            return result;
        }else{
            throw new IllegalArgumentException("The file is not an Excel file.");
        }
    }

    public APIResponse genericUploadAndReadFile(MultipartFile file) throws Exception {
        APIResponse result= new APIResponse();
        InputStream inputstream = file.getInputStream();
        if(isExcelFile(file.getInputStream())){
            // Read only the first row of the Excel content
            String filename = file.getOriginalFilename();
            ArrayList<String> firstRowContent = readFirstRow(inputstream,filename);
            result.setMessage("File uploaded successfully: " +filename);
            result.setData(firstRowContent);
            uploadGenericFile(file, firstRowContent);
            return result;
        }else{
            throw new IllegalArgumentException("The file is not an Excel file.");
        }
    } 

    
    public APIResponse genericUploadAndReadFile(MultipartFile file, String tableName) throws Exception {
        APIResponse result= new APIResponse();
        InputStream inputstream = file.getInputStream();
        if(isExcelFile(file.getInputStream())){
            // Read only the first row of the Excel content
            String filename = file.getOriginalFilename();
            ArrayList<String> firstRowContent = readFirstRow(inputstream,filename);
            result.setMessage("File uploaded successfully: " +filename);
            result.setData(firstRowContent);
            uploadGenericFile(file, tableName, firstRowContent);
            return result;
        }else{
            throw new IllegalArgumentException("The file is not an Excel file.");
        }
    } 

    
    public String proccesFileName(String fileName){
        String[] splitedFileName = fileName.split("\\.");
        return splitedFileName[0];
    }

    public String uploadGenericFile(MultipartFile file, List<String> columns) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }
        // Create the directory if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Get the file and save it to the directory
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + filename);
        File tempFile = new File(UPLOAD_DIR);
        tempFile.mkdir();
        Files.write(path, bytes);
        Lot lot = new Lot();
        lot.setNom(filename);
        lot.setDate(LocalDateTime.now().toString());
        processingService.prepareImport(columns, proccesFileName(filename));
        processingService.processGenericExcelFile(path.toString(), lot, proccesFileName(filename), columns.toArray(new String[0]));
        return "File uploaded successfully: " + filename;
    }  

    public String uploadGenericFile(MultipartFile file, String tableName, List<String> columns) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }
        // Create the directory if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Get the file and save it to the directory
        byte[] bytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        File tempFile = new File(UPLOAD_DIR);
        tempFile.mkdir();
        Files.write(path, bytes);
        Lot lot = new Lot();
        lot.setNom(fileName);
        lot.setDate(LocalDateTime.now().toString());
        processingService.prepareImport(columns, tableName);
        processingService.processGenericExcelFile(path.toString(), lot, tableName, columns.toArray(new String[0]));
        return "File uploaded successfully: " + fileName;
    }  

    public String uploadGenericFile(MultipartFile file, String tableName, String[] columns) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }
        // Create the directory if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Get the file and save it to the directory
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + filename);
        File tempFile = new File(UPLOAD_DIR);
        tempFile.mkdir();
        Files.write(path, bytes);
        Lot lot = new Lot();
        lot.setNom(filename);
        lot.setDate(LocalDateTime.now().toString());
        processingService.processGenericExcelFile(path.toString(), lot, tableName, columns);
        return "File uploaded successfully: " + filename;
    }  
    
    private ArrayList<String> readFirstRow(InputStream inputStream, String filename) throws IOException {
        Workbook workbook = null;
        ArrayList<String> firstRowContent = new ArrayList<>();
        if (filename.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);  // For `.xls` files
        } else if (filename.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);  // For `.xlsx` files
        }
        // Selecting only in the first sheet
        Sheet sheet = workbook.getSheetAt(0);
        Row firstRow = sheet.getRow(0);
        int i = 1;
        while(firstRowContent.isEmpty()){
            if (firstRow != null) {
                for (Cell cell : firstRow) {
                    firstRowContent.add(getCellValue(cell).trim());
                }
            } else {
                firstRowContent.add("No data found in the first row.");
            }
            firstRowContent.remove("");
            firstRow = sheet.getRow(i);
            i++;
        }        
        workbook.close();
        return firstRowContent;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private static boolean isExcelFile(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4]; // Read the first 4 bytes
        InputStream clonedInputStream  = inputStream;
        int bytesRead = clonedInputStream.read(buffer);
        if (bytesRead < 4) {
            throw new IOException("Not enough bytes available to determine file type.");
        }
        String hexSignature = toHexString(buffer);
        return hexSignature.contains("D0CF") || hexSignature.contains("504B"); // Check for .xls and .xlsx signatures
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

      // Compute checksum of a file using SHA-256 algorithm
    public static String getChecksum(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        // Create MessageDigest instance for SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Read file data as InputStream and update digest
        try (InputStream fis = file.getInputStream()) {
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        // Get the byte array from the digest and convert to a hexadecimal string
        byte[] bytes = digest.digest();
        BigInteger no = new BigInteger(1, bytes);

        // Convert message digest into hex value
        StringBuilder checksum = new StringBuilder(no.toString(16));

        // Add leading zeros to make it 64 chars long (for SHA-256)
        while (checksum.length() < 64) {
            checksum.insert(0, "0");
        }

        return checksum.toString();
    }
}
