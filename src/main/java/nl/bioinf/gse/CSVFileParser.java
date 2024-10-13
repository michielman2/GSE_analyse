package nl.bioinf.gse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVFileParser {

    // Method to parse DEGs (degs.csv)
    public List<GeneRecord> readDEGs(String filePath) throws IOException {
        List<GeneRecord> geneRecords = new ArrayList<>();

        // Specify that the CSV file has no header
        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : parser) {
                // Access the data by index
                String geneSymbol = record.get(0);  // Assuming the first column is the gene symbol
                double logFoldChange = Double.parseDouble(record.get(1));  // Assuming the second column is log2 fold change
                double adjustedPValue = Double.parseDouble(record.get(2));  // Assuming the third column is adjusted p-value

                GeneRecord geneRecord = new GeneRecord(geneSymbol, logFoldChange, adjustedPValue);
                geneRecords.add(geneRecord);
            }
        }

        return geneRecords;
    }

    // Method to parse pathways (pathways.csv) and aggregate data by KEGG Pathway ID
    public Map<String, PathwayRecord> readPathways(String pathwaysFilePath, String hsaPathwaysFilePath) throws IOException {
        Map<String, PathwayRecord> pathwayMap = new HashMap<>();

        // Read the KEGG pathways descriptions (hsa_pathways.csv)
        Map<String, String> pathwayDescriptions = new HashMap<>();
        try (CSVParser parser = new CSVParser(new FileReader(hsaPathwaysFilePath), CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : parser) {
                String pathwayID = record.get(0);  // Assuming the first column is KEGG pathway ID
                String description = record.get(1); // Assuming the second column is Description
                pathwayDescriptions.put(pathwayID, description);
            }
        }

        // Now read the pathways data (pathways.csv)
        try (CSVParser parser = new CSVParser(new FileReader(pathwaysFilePath), CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : parser) {
                String pathwayID = record.get(0);  // Assuming the first column is KEGG pathway ID
                String entrezGeneID = record.get(1); // Assuming the second column is Entrez gene ID
                String geneSymbol = record.get(2);    // Assuming the third column is Gene symbol
                String ensemblGeneID = record.get(3); // Assuming the fourth column is Ensembl gene ID

                // Get the pathway description
                String description = pathwayDescriptions.getOrDefault(pathwayID, "Unknown Pathway");

                // Check if the pathway is already in the map
                PathwayRecord existingPathway = pathwayMap.get(pathwayID);
                if (existingPathway == null) {
                    // Create a new PathwayRecord if not present
                    List<String> entrezGeneIDs = new ArrayList<>();
                    List<String> ensemblGeneIDs = new ArrayList<>();
                    List<String> geneSymbols = new ArrayList<>();

                    entrezGeneIDs.add(entrezGeneID);
                    ensemblGeneIDs.add(ensemblGeneID);
                    geneSymbols.add(geneSymbol);

                    pathwayMap.put(pathwayID, new PathwayRecord(pathwayID, description, entrezGeneIDs, ensemblGeneIDs, geneSymbols));
                } else {
                    // Add gene information to the existing PathwayRecord
                    existingPathway.entrezGeneIDs().add(entrezGeneID);
                    existingPathway.ensemblGeneIDs().add(ensemblGeneID);
                    existingPathway.geneSymbols().add(geneSymbol);
                }
            }
        }

        return pathwayMap;
    }
}