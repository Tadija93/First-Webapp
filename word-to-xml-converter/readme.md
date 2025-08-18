# Word to XML Converter

This project provides a simple command-line application to convert Word documents (.docx) into XML format. The conversion process extracts paragraphs and shapes from the document and generates an XML file that retains the structure and content of the original file.

Features
- Converts Word documents (.docx) to XML format.
- Extracts text from paragraphs and handles shapes.
- Includes alternative text for shapes in the generated XML.
- Generates output XML files with the same name as the input Word file.

Requirements
- Java Development Kit (JDK) 8 or higher
- Maven
- Aspose.Words for Java library (included in the project)

Installation
1. Clone the repository:
   git clone https://github.com/yourusername/word-to-xml-converter.git
2. navigate to your folder
   cd word-to-xml-converter
3. mvn clean install
4. java -jar target/word-to-xml-converter-1.0-SNAPSHOT-shaded.jar /path/to/your/document.docx - To convert a Word document to XML, use the following command

