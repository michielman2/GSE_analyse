package nl.bioinf.gse;

import java.util.List;
import java.util.Map;

/**
 * Class for printing enrichment tables and GSEA results to the terminal.
 */
public class TerminalOutput {

    /**
     * Prints GSEA results to the terminal.
     *
     * @param gseaResults   A list of GSEARecord objects containing enrichment analysis results.
     * @param pathwayRecords A map of pathway IDs to PathwayRecord objects.
     * @param pathwayName    The name or ID of the pathway to print results for. Special values:
     *                       "no_pathways" to print nothing, "all_pathways" to print all pathways.
     * @throws IllegalArgumentException If the specified pathwayName does not exist in the pathwayRecords map.
     */
    public static void printGSEAResults(List<GSEARecord> gseaResults, Map<String, PathwayRecord> pathwayRecords, String pathwayName){
        if (pathwayName.equals("no_pathways")) {
            // Do nothing
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

    /**
     * Prints enrichment tables to the terminal.
     *
     * @param gseaResults   A list of GSEARecord objects containing enrichment analysis results.
     * @param pathwayRecords A map of pathway IDs to PathwayRecord objects.
     * @param pathwayName    The name or ID of the pathway to print tables for. Special values:
     *                       "no_pathways" to print nothing, "all_pathways" to print all pathways.
     * @param geneRecords    A list of GeneRecord objects representing genes in the analysis.
     * @param treshold       A double value that determines if a gene is a DEG.
     * @throws IllegalArgumentException If the specified pathwayName does not exist in the pathwayRecords map.
     */
    public static void printEnrichmentTables(List<GSEARecord> gseaResults,Map<String, PathwayRecord> pathwayRecords, String pathwayName, List<GeneRecord> geneRecords, double treshold){
        if (pathwayName.equals("no_pathways")) {
            // Do nothing
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
