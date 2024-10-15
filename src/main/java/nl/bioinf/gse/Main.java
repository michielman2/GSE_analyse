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
            writeGeneRecordsToCSV(geneRecords, "gene_records_output.csv");


            Map<String, PathwayRecord> pathwayRecords = csvFileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath);
            writePathwayRecordsToCSV(pathwayRecords, "pathway_records_output.csv");
            int uniqueGeneCount = CreateTable.countUniqueGenes(pathwayRecords);
            System.out.println("Number of unique genes in pathways.csv: " + uniqueGeneCount);
            int degCount = CreateTable.totalDEGS(geneRecords);
            System.out.println("Number of DEGs in degs.csv: " + degCount);
            int pathwayGenes = CreateTable.genesInPathway(pathwayRecords, "hsa00010");
            System.out.println("Number of genes in given pathway: " + pathwayGenes);
            int degsInPathway= CreateTable.degsInPathway(pathwayRecords,geneRecords,"hsa00010");
            System.out.println("degs in given pathway: " + degsInPathway);
            String table = CreateTable.tablebuilder(degsInPathway,pathwayGenes,degCount,uniqueGeneCount);
            System.out.println(table);
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
