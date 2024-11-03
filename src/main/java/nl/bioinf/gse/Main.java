package nl.bioinf.gse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import picocli.CommandLine;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Create an instance of CommandlineProcessor
        CommandlineProcessor commandlineProcessor = new CommandlineProcessor();

        // Parse the command line arguments and execute the Callable
        int exitCode = new CommandLine(commandlineProcessor).execute(args);

        // Use commandlineProcessor instance to access the parsed options
        File geneFile = commandlineProcessor.getGeneFile();
        File pathwayFile = commandlineProcessor.getPathwayFile();
        File pathwayDescFile = commandlineProcessor.getPathwayDescFile();
        String geneId = commandlineProcessor.getGeneId();
        int headerLength = commandlineProcessor.getHeaderLength();
        String pathwayName = commandlineProcessor.getPathwayName();

        // Validate exit code before proceeding
        if (exitCode != 0) {
            System.err.println("Failed to parse command line arguments.");
            System.exit(exitCode);
        }

        // Check if geneFile is set correctly before using it
        String degsFilePath = geneFile.getAbsolutePath();
        String pathwaysFilePath = pathwayFile.getAbsolutePath();
        String hsaPathwaysFilePath = pathwayDescFile.getAbsolutePath();

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
            Boxplot.showChart(results);

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
