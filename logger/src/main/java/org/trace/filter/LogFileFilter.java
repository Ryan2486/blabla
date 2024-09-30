package org.trace.filter;

import java.util.function.Predicate;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LogFileFilter {
    private static final String LOG_DIRECTORY = "./LOGS";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public static File getFilesFrom(LocalDate date) {
        List<File> fileFromDate =  filterFiles(file -> getDateFromFileName(file).isEqual(date));
        if(!fileFromDate.isEmpty()){
            return fileFromDate.get(0);
        }
        return null;
    }

    public static List<File> getFilesBefore(LocalDate date) {
        return filterFiles(file -> getDateFromFileName(file).isBefore(date));
    }

    public static List<File> getFilesAfter(LocalDate date) {
        return filterFiles(file -> getDateFromFileName(file).isAfter(date));
    }

    public static List<File> getFilesBetween(LocalDate startDate, LocalDate endDate) {
        return filterFiles(file -> {
            LocalDate fileDate = getDateFromFileName(file);
            return !fileDate.isBefore(startDate) && !fileDate.isAfter(endDate);
        });
    }

    private static List<File> filterFiles(Predicate<File> predicate) {
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles((dir, name) -> name.matches("logs_\\d{2}_\\d{2}_\\d{4}\\.json"));
        
        if (files == null) {
            return null;
        }

        return Arrays.stream(files)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static LocalDate getDateFromFileName(File file) {
        String fileName = file.getName();
        String dateString = fileName.substring(5, 15); // Extract "DD_MM_YYYY"
        return LocalDate.parse(dateString, FILE_DATE_FORMAT);
    }

    // Example usage
    public static void main(String[] args) {
        LocalDate someDate = LocalDate.of(2024, 9, 12);
        LocalDate startDate = LocalDate.of(2024, 9, 12);
        LocalDate endDate = LocalDate.of(2024, 9, 14 );

        File filesFrom = LogFileFilter.getFilesFrom(someDate);
        List<File> filesBefore = LogFileFilter.getFilesBefore(someDate);
        List<File> filesAfter = LogFileFilter.getFilesAfter(someDate);
        List<File> filesBetween = LogFileFilter.getFilesBetween(startDate, endDate);
        
        System.out.println("Files from " + someDate + ": " + filesFrom);
        System.out.println("Files before " + someDate + ": " + filesBefore);
        System.out.println("Files after " + someDate + ": " + filesAfter);
        System.out.println("Files between " + startDate + " and " + endDate + ": " + filesBetween);
    }
}
