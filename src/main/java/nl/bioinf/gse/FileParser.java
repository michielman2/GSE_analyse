package nl.bioinf.gse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {

    // Method to read Differentially Expressed Genes (DEGs) from a file
    public List<GeneRecord> readDEGs(String filePath, int headerLength) throws IOException {
        List<GeneRecord> geneRecords = new ArrayList<>();

        // Determine the delimiter based on file extension
        CSVFormat format;
        if (filePath.endsWith(".tsv")) {
            format = CSVFormat.DEFAULT.withDelimiter('\t');
        } else if (filePath.endsWith(".csv")) {
            format = CSVFormat.DEFAULT.withDelimiter(',');
        } else {
            throw new IOException("Unsupported file format. Please provide a .csv or .tsv file.");
        }

        // Create a parser with specified header length (lines to skip)
        try (CSVParser parser = new CSVParser(
                new FileReader(new File(filePath)),
                format.withIgnoreHeaderCase().withTrim().withSkipHeaderRecord(true))) {

            // Skip the specified number of lines
            int linesSkipped = 0;
            for (CSVRecord record : parser) {
                if (linesSkipped < headerLength) {
                    linesSkipped++;
                    continue;
                }

                String geneSymbol = record.get(0);

                // Check for "NA" and skip the entire record if "NA" is found in either relevant field
                String logFoldChangeStr = record.get(1);
                String adjustedPValueStr = record.get(2);

                // If either of these values is "NA", skip this record
                if ("NA".equals(logFoldChangeStr) || "NA".equals(adjustedPValueStr)) {
                    continue; // Skip this record entirely
                }

                // Parse the log fold change and adjusted p-value
                double logFoldChange = Double.parseDouble(logFoldChangeStr);
                double adjustedPValue = Double.parseDouble(adjustedPValueStr);

                // Add the valid GeneRecord to the list
                geneRecords.add(new GeneRecord(geneSymbol, logFoldChange, adjustedPValue));
            }
        }

        return geneRecords;
    }

    // Method to read Pathways from the file
    public Map<String, PathwayRecord> readPathways(String pathwaysFilePath, String hsaPathwaysFilePath, int headerLength, String geneType) throws IOException {
        Map<String, PathwayRecord> pathwayMap = new HashMap<>();
        Map<String, String> pathwayDescriptions = new HashMap<>();

        // Determine the delimiter based on file extension for hsaPathwaysFilePath
        CSVFormat format;
        if (hsaPathwaysFilePath.endsWith(".tsv")) {
            format = CSVFormat.DEFAULT.withDelimiter('\t');
        } else if (hsaPathwaysFilePath.endsWith(".csv")) {
            format = CSVFormat.DEFAULT.withDelimiter(',');
        } else {
            throw new IOException("Unsupported file format for hsa pathways. Please provide a .csv or .tsv file.");
        }

        // Read pathway descriptions file
        try (CSVParser parser = new CSVParser(new FileReader(hsaPathwaysFilePath), format.withIgnoreHeaderCase().withTrim().withSkipHeaderRecord(true))) {
            int linesSkipped = 0;
            for (CSVRecord record : parser) {
                if (linesSkipped < headerLength) {
                    linesSkipped++;
                    continue;
                }

                String pathwayID = record.get(0);
                String description = record.get(1);
                pathwayDescriptions.put(pathwayID, description);
            }
        }

        // Read pathways file and map genes to pathways
        try (CSVParser parser = new CSVParser(new FileReader(pathwaysFilePath), format.withIgnoreHeaderCase().withTrim().withSkipHeaderRecord(true))) {
            int linesSkipped = 0;
            for (CSVRecord record : parser) {
                if (linesSkipped < headerLength) {
                    linesSkipped++;
                    continue;
                }

                String pathwayID = record.get(0);
                String entrezGeneID = record.get(1);  // Column 1: Entrez
                String geneSymbol = record.get(2);   // Column 2: Gene Symbol
                String ensemblGeneID = record.get(3); // Column 3: Ensembl

                String description = pathwayDescriptions.getOrDefault(pathwayID, "Unknown Pathway");

                // Determine which geneID to use based on the geneType
                List<String> geneIDs = new ArrayList<>();
                if ("Entrez".equalsIgnoreCase(geneType)) {
                    geneIDs.add(entrezGeneID);
                } else if ("Gene_symbol".equalsIgnoreCase(geneType)) {
                    geneIDs.add(geneSymbol);
                } else if ("Ensembl".equalsIgnoreCase(geneType)) {
                    geneIDs.add(ensemblGeneID);
                } else {
                    throw new IllegalArgumentException("Unsupported gene type: " + geneType);
                }

                // Add the gene IDs to the pathway record
                PathwayRecord existingPathway = pathwayMap.get(pathwayID);
                if (existingPathway == null) {
                    // Create a new PathwayRecord if it doesn't exist
                    pathwayMap.put(pathwayID, new PathwayRecord(pathwayID, description, geneIDs));
                } else {
                    // Otherwise, add the gene ID to the existing record
                    existingPathway.geneIDs().addAll(geneIDs);
                }
            }
        }

        return pathwayMap;
    }
}
