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

                // Check for "NA" and skip parsing those fields
                double logFoldChange = "NA".equals(record.get(1)) ? Double.NaN : Double.parseDouble(record.get(1));
                double adjustedPValue = "NA".equals(record.get(2)) ? Double.NaN : Double.parseDouble(record.get(2));

                geneRecords.add(new GeneRecord(geneSymbol, logFoldChange, adjustedPValue));
            }
        }

        return geneRecords;
    }

    public Map<String, PathwayRecord> readPathways(String pathwaysFilePath, String hsaPathwaysFilePath, int headerLength) throws IOException {
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
                String entrezGeneID = record.get(1);
                String geneSymbol = record.get(2);
                String ensemblGeneID = record.get(3);

                String description = pathwayDescriptions.getOrDefault(pathwayID, "Unknown Pathway");

                PathwayRecord existingPathway = pathwayMap.get(pathwayID);
                if (existingPathway == null) {
                    List<String> entrezGeneIDs = new ArrayList<>();
                    List<String> ensemblGeneIDs = new ArrayList<>();
                    List<String> geneSymbols = new ArrayList<>();

                    entrezGeneIDs.add(entrezGeneID);
                    ensemblGeneIDs.add(ensemblGeneID);
                    geneSymbols.add(geneSymbol);

                    pathwayMap.put(pathwayID, new PathwayRecord(pathwayID, description, entrezGeneIDs, ensemblGeneIDs, geneSymbols));
                } else {
                    existingPathway.entrezGeneIDs().add(entrezGeneID);
                    existingPathway.ensemblGeneIDs().add(ensemblGeneID);
                    existingPathway.geneSymbols().add(geneSymbol);
                }
            }
        }

        return pathwayMap;
    }
}
