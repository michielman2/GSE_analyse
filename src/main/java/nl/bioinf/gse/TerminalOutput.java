package nl.bioinf.gse;

import java.util.List;
import java.util.Map;

public class TerminalOutput {
    public static void printGSEAResults(List<GSEARecord> gseaResults, Map<String, PathwayRecord> pathwayRecords, String pathwayName){
        if (pathwayName.equals("no_pathways")) {
        } else if (pathwayName.equals("all_pathways")) {
            for (GSEARecord result : gseaResults) {
                System.out.println("Pathway: " + result.description());
                System.out.println("KEGG PathwayID: " + result.pathwayID());
                System.out.println("P-Value: " + result.pValue());
                System.out.println("Adjusted P-Value: " + result.adjustedPValue());
                System.out.println("Enrichment Score: " + result.enrichmentScore());
                System.out.println("Expected DEGs: " + result.expectedDEGs());
                System.out.println("Observed DEGs: " + result.observedDEGs());
                System.out.println("Average LogFoldChange: " + result.avgLogFoldChange());
                System.out.println("-----------------------------------");
            }
        } else {
            if (pathwayRecords.containsKey(pathwayName)) {
                GSEARecord matchingRecord = null;
                for (GSEARecord record : gseaResults) {
                    if (record.pathwayID().equals(pathwayName)) {
                        matchingRecord = record;
                        break;
                    }
                }
                System.out.println("Pathway: " + matchingRecord.description());
                System.out.println("KEGG PathwayID: " + matchingRecord.pathwayID());
                System.out.println("P-Value: " + matchingRecord.pValue());
                System.out.println("Adjusted P-Value: " + matchingRecord.adjustedPValue());
                System.out.println("Enrichment Score: " + matchingRecord.enrichmentScore());
                System.out.println("Expected DEGs: " + matchingRecord.expectedDEGs());
                System.out.println("Observed DEGs: " + matchingRecord.observedDEGs());
                System.out.println("Average LogFoldChange: " + matchingRecord.avgLogFoldChange());
                System.out.println("-----------------------------------");
            } else {
                // pathwayName does not match any pathwayID in the map, throw an exception
                throw new IllegalArgumentException("Error: No pathway found with ID: " + pathwayName);
            }
        }
    }

    public static void printEnrichmentTables(List<GSEARecord> gseaResults,Map<String, PathwayRecord> pathwayRecords, String pathwayName, List<GeneRecord> geneRecords, double treshold){
        if (pathwayName.equals("no_pathways")) {
        } else if (pathwayName.equals("all_pathways")) {
            for (GSEARecord result : gseaResults) {
                String table = TableBuilder.tableBuilder(geneRecords, pathwayRecords, result.pathwayID(), treshold);
                System.out.println("Table for Pathway: " + result.pathwayID());
                System.out.println(table);
                System.out.println(); // Print an empty line for better separation
            }
        } else {
            if (pathwayRecords.containsKey(pathwayName)) {
                GSEARecord matchingRecord = null;
                for (GSEARecord record : gseaResults) {
                    if (record.pathwayID().equals(pathwayName)) {
                        matchingRecord = record;
                        break;
                    }
                }
                    String table = TableBuilder.tableBuilder(geneRecords, pathwayRecords, matchingRecord.pathwayID(), treshold);
                    System.out.println("Table for Pathway: " + matchingRecord.pathwayID());
                    System.out.println(table);
                    System.out.println(); // Print an empty line for better separation
            } else {
                // pathwayName does not match any pathwayID in the map, throw an exception
                throw new IllegalArgumentException("Error: No pathway found with ID: " + pathwayName);
            }
        }
    }
}