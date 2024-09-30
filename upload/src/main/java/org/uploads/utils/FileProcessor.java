package org.uploads.utils;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;

public class FileProcessor {

    public static String readFile(String inputFilePath) throws IOException{
        // Read the file from the resources directory
        ClassLoader classLoader = FileProcessor.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(inputFilePath);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found in resources: " + inputFilePath);
        }
        // Convert InputStream to String using Java 8 streams
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }


    // Function to create a new file with the modified content in the resources folder
    public static void createNewFile(String outputFileName, String modifiedContent) throws IOException {
        // Get the absolute path of the resources folder
        String resourceDirectory = Paths.get("src/main/resources").toFile().getAbsolutePath();

        // Create a new file path in the resources directory
        Path newFilePath = Paths.get(resourceDirectory, outputFileName);

        // Write the modified content to a new file
        try (BufferedWriter writer = Files.newBufferedWriter(newFilePath)) {
            writer.write(modifiedContent);
        }

        System.out.println("New file created in resources folder: " + newFilePath);
    }
}
