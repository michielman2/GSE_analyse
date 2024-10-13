package nl.bioinf.gse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Define the file paths for your CSV files
        String degsFilePath = "path/to/degs.csv";
        String pathwaysFilePath = "path/to/pathways.csv";
        String hsaPathwaysFilePath = "path/to/hsa_pathways.csv";

        // Create an instance of CSVFileParser
        CSVFileParser csvFileParser = new CSVFileParser();

        try {
            // Parse the DEGs file and get a list of GeneRecord objects
            List<GeneRecord> geneRecords = csvFileParser.readDEGs(degsFilePath);
            System.out.println("Parsed DEGs:");
            for (GeneRecord geneRecord : geneRecords) {
                System.out.println(geneRecord);
            }

            // Parse the pathways file and hsa_pathways file, then aggregate by KEGG Pathway ID
            Map<String, PathwayRecord> pathwayRecords = csvFileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath);
            System.out.println("\nParsed Pathways:");
            for (Map.Entry<String, PathwayRecord> entry : pathwayRecords.entrySet()) {
                System.out.println("Pathway ID: " + entry.getKey() + ", Pathway Record: " + entry.getValue());
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV files: " + e.getMessage());
        }
    }
}
