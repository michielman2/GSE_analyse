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

    
    public List<GeneRecord> readDEGs(String filePath) throws IOException {
        List<GeneRecord> geneRecords = new ArrayList<>();

       
        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : parser) {
                
                String geneSymbol = record.get(0);  
                double logFoldChange = Double.parseDouble(record.get(1)); 
                double adjustedPValue = Double.parseDouble(record.get(2));  

                GeneRecord geneRecord = new GeneRecord(geneSymbol, logFoldChange, adjustedPValue);
                geneRecords.add(geneRecord);
            }
        }

        return geneRecords;
    }

    
    public Map<String, PathwayRecord> readPathways(String pathwaysFilePath, String hsaPathwaysFilePath) throws IOException {
        Map<String, PathwayRecord> pathwayMap = new HashMap<>();

       
        Map<String, String> pathwayDescriptions = new HashMap<>();
        try (CSVParser parser = new CSVParser(new FileReader(hsaPathwaysFilePath), CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : parser) {
                String pathwayID = record.get(0); 
                String description = record.get(1); 
                pathwayDescriptions.put(pathwayID, description);
            }
        }

        
        try (CSVParser parser = new CSVParser(new FileReader(pathwaysFilePath), CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : parser) {
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