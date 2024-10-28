package nl.bioinf.gse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String degsFilePath = "example_data/degs.csv";
        String pathwaysFilePath = "example_data/pathways.csv";
        String hsaPathwaysFilePath = "example_data/hsa_pathways.csv";

        CSVFileParser csvFileParser = new CSVFileParser();

        try {
            List<GeneRecord> geneRecords = csvFileParser.readDEGs(degsFilePath);
            Map<String, PathwayRecord> pathwayRecords = csvFileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath);

            // Write the gene records to CSV
            writeGeneRecordsToCSV(geneRecords, "gene_records_output.csv");

            // Write the pathway records to CSV
            writePathwayRecordsToCSV(pathwayRecords, "pathway_records_output.csv");

            // Perform GSEA analysis
            GSEAWithHypergeometric gsea = new GSEAWithHypergeometric();
            List<GSEAWithHypergeometric.GSEAResult> results = gsea.performGSEA(geneRecords, pathwayRecords, 0.05);

            // Output the results
            for (GSEAWithHypergeometric.GSEAResult result : results) {
                System.out.println("Pathway: " + result.pathwayID);
                System.out.println("P-Value: " + result.pValue);
                System.out.println("Adjusted P-Value: " + result.adjustedPValue);
                System.out.println("Enrichment Score: " + result.enrichmentScore);
                System.out.println("-----------------------------------");
            }

            // Print the table below everything else
            for (GSEAWithHypergeometric.GSEAResult result : results) {
                String table = TableBuilder.tableBuilder(geneRecords, pathwayRecords, result.pathwayID);
                System.out.println("Table for Pathway: " + result.pathwayID);
                System.out.println(table);
                System.out.println(); // Print an empty line for better separation
            }

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
