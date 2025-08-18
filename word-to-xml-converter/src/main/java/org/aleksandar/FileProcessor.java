package org.aleksandar;

import com.aspose.words.*;

import java.io.File;
import java.util.UUID;

public class FileProcessor {

    // Method to validate if the file exists and is a valid Word file
    public boolean isValidFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && (filePath.endsWith(".docx") || filePath.endsWith(".doc"));
    }

    // Method to convert Word file to XML using Aspose.Words
    public void convertWordToXml(String inputFilePath, String outputFilePath) throws Exception {
        // Load the Word document
        Document doc = new Document(inputFilePath);

        // Extract paragraphs and handle shapes
        StringBuilder xmlContent = new StringBuilder();
        xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlContent.append("<document>\n");

        // Get all paragraph nodes
        NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH, true);

        for (Paragraph paragraph : (Iterable<Paragraph>) paragraphs) {
            xmlContent.append("  <paragraph>\n");

            // Extract runs (text) from the paragraph
            for (Run run : paragraph.getRuns()) {
                xmlContent.append("    <text>").append(run.getText().trim()).append("</text>\n");
            }

            // Get all shapes within the paragraph
            NodeCollection shapes = paragraph.getChildNodes(NodeType.SHAPE, true);

            for (Shape shape : (Iterable<Shape>) shapes) {
                String shapePlaceholder = "<shape_placeholder/>";
                xmlContent.append("    ").append(shapePlaceholder).append("\n");

                // Add alternative text of the shape in a separate paragraph if present
                if (shape.getAlternativeText() != null && !shape.getAlternativeText().isEmpty()) {
                    xmlContent.append("  <paragraph>\n");
                    xmlContent.append("    <alt_text>").append(shape.getAlternativeText().trim()).append("</alt_text>\n");
                    xmlContent.append("  </paragraph>\n");
                }
            }

            xmlContent.append("  </paragraph>\n");
        }

        xmlContent.append("</document>");

        // Save XML content to a file
        java.nio.file.Files.write(new File(outputFilePath).toPath(), xmlContent.toString().getBytes());
    }

    // Helper method to generate a unique output file path
    public String generateOutputFilePath(String inputFilePath) {
        String outputDir = new File(inputFilePath).getParent();
        String uniqueName = "output_" + UUID.randomUUID() + ".xml";
        return outputDir + File.separator + uniqueName;
    }
}
