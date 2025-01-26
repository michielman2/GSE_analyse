package nl.bioinf.gse;

import java.util.List;
import java.util.Map;

/**
 * Utility class for printing GSEA results to the terminal.
 */
public class TerminalOutput {

    /**
     * Prints GSEA results based on the specified pathway name.
     *
     * @param gseaResults    List of GSEARecord objects containing the analysis results.
     * @param pathwayRecords Map of pathway names to PathwayRecord objects.
     * @param pathwayName    The name of the pathway to filter results by. Can be "no_pathways", "all_pathways", or a specific pathway ID.
     * @throws IllegalArgumentException if the pathwayName does not match any pathway in the records.
     */
    public static void printGSEAResults(List<GSEARecord> gseaResults, Map<String, PathwayRecord> pathwayRecords, String pathwayName) {
        if (pathwayName.equals("no_pathways")) {
            return; // No output for "no_pathways"
        }

        if (pathwayName.equals("all_pathways")) {
            for (GSEARecord result : gseaResults) {
                printGSEARecord(result);
            }
        } else {
            if (pathwayRecords.containsKey(pathwayName)) {
                GSEARecord matchingRecord = findMatchingRecord(gseaResults, pathwayName);
                if (matchingRecord != null) {
                    printGSEARecord(matchingRecord);
                } else {
                    throw new IllegalArgumentException("Error: No pathway found with ID: " + pathwayName);
                }
            } else {
                throw new IllegalArgumentException("Error: No pathway found with ID: " + pathwayName);
            }
        }
    }

    /**
     * Prints the details of a single GSEARecord to the terminal.
     *
     * @param record The GSEARecord to be printed.
     */
    private static void printGSEARecord(GSEARecord record) {
        System.out.println("Pathway: " + record.description());
        System.out.println("KEGG PathwayID: " + record.pathwayID());
        System.out.println("P-Value: " + record.pValue());
        System.out.println("Adjusted P-Value: " + record.adjustedPValue());
        System.out.println("Enrichment Score: " + record.enrichmentScore());
        System.out.println("Expected DEGs: " + record.expectedDEGs());
        System.out.println("Observed DEGs: " + record.observedDEGs());
        System.out.println("Average LogFoldChange: " + record.avgLogFoldChange());
        System.out.println("-----------------------------------");
    }

    /**
     * Finds a GSEARecord in the list by its pathway ID.
     *
     * @param gseaResults List of GSEARecord objects to search.
     * @param pathwayName The pathway ID to find.
     * @return The matching GSEARecord, or null if no match is found.
     */
    private static GSEARecord findMatchingRecord(List<GSEARecord> gseaResults, String pathwayName) {
        for (GSEARecord record : gseaResults) {
            if (record.pathwayID().equals(pathwayName)) {
                return record;
            }
        }
        return null;
    }
}
