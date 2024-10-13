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

        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                String geneSymbol = record.get("Gene symbol");
                double logFoldChange = Double.parseDouble(record.get("log2 fold change"));
                double adjustedPValue = Double.parseDouble(record.get("adjusted p-value"));

                GeneRecord geneRecord = new GeneRecord(geneSymbol, logFoldChange, adjustedPValue);
                geneRecords.add(geneRecord);
            }
        }

        return geneRecords;
    }

    // Method to parse pathways (pathways.csv) and aggregate data by KEGG Pathway ID
    public Map<String, PathwayRecord> readPathways(String pathwaysFilePath, String hsaPathwaysFilePath) throws IOException {
        // Map to hold pathway information, with the KEGG Pathway ID as the key
        Map<String, PathwayRecord> pathwayMap = new HashMap<>();

        // First read the KEGG pathways descriptions (hsa_pathways.csv)
        Map<String, String> pathwayDescriptions = new HashMap<>();
        try (CSVParser parser = new CSVParser(new FileReader(hsaPathwaysFilePath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                String pathwayID = record.get("KEGG pathway ID");
                String description = record.get("Description");
                pathwayDescriptions.put(pathwayID, description);
            }
        }

        // Now read the pathways data (pathways.csv)
        try (CSVParser parser = new CSVParser(new FileReader(pathwaysFilePath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                String pathwayID = record.get("KEGG pathway ID");
                String entrezGeneID = record.get("Entrez gene ID");
                String geneSymbol = record.get("Gene symbol");
                String ensemblGeneID = record.get("Ensembl gene ID");

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

