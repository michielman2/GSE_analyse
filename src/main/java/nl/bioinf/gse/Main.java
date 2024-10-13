package nl.bioinf.gse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Define the file paths for your CSV files
        String degsFilePath = "C:\\Users\\roord\\OneDrive\\Documenten\\School\\gsea\\degs.csv";
        String pathwaysFilePath = "C:\\Users\\roord\\OneDrive\\Documenten\\School\\gsea\\pathways.csv";
        String hsaPathwaysFilePath = "C:\\Users\\roord\\OneDrive\\Documenten\\School\\gsea\\hsa_pathways.csv";

        // Create an instance of CSVFileParser
        CSVFileParser csvFileParser = new CSVFileParser();

        try {
            // Parse the DEGs file and get a list of GeneRecord objects
            List<GeneRecord> geneRecords = csvFileParser.readDEGs(degsFilePath);
            writeGeneRecordsToCSV(geneRecords, "gene_records_output.csv");

            // Parse the pathways file and hsa_pathways file, then aggregate by KEGG Pathway ID
            Map<String, PathwayRecord> pathwayRecords = csvFileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath);
            writePathwayRecordsToCSV(pathwayRecords, "pathway_records_output.csv");

        } catch (IOException e) {
            System.err.println("Error reading CSV files: " + e.getMessage());
        }
    }

    private static void writeGeneRecordsToCSV(List<GeneRecord> geneRecords, String outputPath) throws IOException {
        try (FileWriter out = new FileWriter(outputPath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("Gene Symbol", "Log Fold Change", "Adjusted P-Value"))) {
            for (GeneRecord geneRecord : geneRecords) {
                printer.printRecord(geneRecord.geneSymbol(), geneRecord.logFoldChange(), geneRecord.adjustedPValue());
            }
        }
    }

    private static void writePathwayRecordsToCSV(Map<String, PathwayRecord> pathwayRecords, String outputPath) throws IOException {
        try (FileWriter out = new FileWriter(outputPath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("Pathway ID", "Description", "Entrez Gene IDs", "Ensembl Gene IDs", "Gene Symbols"))) {
            for (PathwayRecord pathwayRecord : pathwayRecords.values()) {
                printer.printRecord(pathwayRecord.pathwayID(),
                        pathwayRecord.description(),
                        String.join(", ", pathwayRecord.entrezGeneIDs()),
                        String.join(", ", pathwayRecord.ensemblGeneIDs()),
                        String.join(", ", pathwayRecord.geneSymbols()));
            }
        }
    }
}
