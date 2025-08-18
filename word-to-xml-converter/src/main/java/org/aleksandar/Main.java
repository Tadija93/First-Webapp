package org.aleksandar;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the absolute path(s) to the Word file(s) as arguments.");
            return;
        }

        FileProcessor processor = new FileProcessor();

        for (String filePath : args) {
            try {
                System.out.println("Processing file: " + filePath);
                if (!processor.isValidFile(filePath)) {
                    System.out.println("Invalid file path: " + filePath);
                    continue;
                }

                // Generate the output file path
                String outputXmlPath = processor.generateOutputFilePath(filePath);

                // Convert the Word document to XML
                processor.convertWordToXml(filePath, outputXmlPath);

                System.out.println("Converted XML file saved at: " + outputXmlPath);

            } catch (Exception e) {
                System.out.println("An error occurred while processing the file: " + filePath);
                e.printStackTrace();
            }
        }
    }
}
