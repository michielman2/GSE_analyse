package nl.bioinf.gse;

import java.util.*;

public class TableBuilder {

    /**
     * Counts the total number of non-DEGs
     * in the provided gene records list, filtered by the given adjusted p-value threshold.
     *
     * @param geneRecords List of GeneRecord objects containing gene expression data.
     * @param treshold    The adjusted p-value threshold for filtering non-DEGs.
     * @return            The count of non-DEGs in the gene records list.
     */
    public static long totalNonDEGS(List<GeneRecord> geneRecords, double treshold) {
        long count = geneRecords.stream()
                .filter(record -> record.adjustedPValue() > treshold)
                .count();
        return count;
    }

    /**
     * Counts the total number of DEGs
     * in the provided gene records list, filtered by the specified p-value threshold.
     *
     * @param geneRecords List of GeneRecord objects containing gene expression data.
     * @param treshold    The adjusted p-value threshold for filtering DEGs.
     * @return            The count of DEGs in the gene records list.
     */
    public static long totalDEGS(List<GeneRecord> geneRecords, double treshold) {
        long count = geneRecords.stream()
                .filter(record -> record.adjustedPValue() < treshold)
                .count();
        return count;
    }

    /**
     * Counts the DEGs within a specific pathway, filtered by the adjusted p-value threshold.
     *
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering DEGs.
     * @return               The count of DEGs in the specified pathway.
     */
    public static int degsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID, double treshold) {
        List<String> geneID = new ArrayList<>();
        int degCount = 0;

        // Get geneIDs in the selected pathway
        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneIDs());

        // Count DEGs within the selected pathway
        for (GeneRecord geneRecord : geneRecords) {
            if (geneID.contains(geneRecord.geneSymbol()) && geneRecord.adjustedPValue() < treshold) {
                degCount++;
            }
        }

        return degCount;
    }

    /**
     * Counts non-DEGs within a specified pathway, filtered by the given adjusted p-value threshold.
     *
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering non-DEGs.
     * @return               The count of non-DEGs in the specified pathway.
     */
    public static int nonDEGsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID, double treshold){
        List<String> geneID = new ArrayList<>();
        int geneCount = 0;

        // Get geneIDs in the selected pathway
        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneIDs());

        // Count non-DEGs within the selected pathway
        for (GeneRecord geneRecord : geneRecords) {
            if (geneID.contains(geneRecord.geneSymbol()) && geneRecord.adjustedPValue() > treshold) {
                geneCount++;
            }
        }

        return geneCount;
    }

    /**
     * Calculates the total number of genes in a selected pathway.
     *
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering DEGs and non-DEGs.
     * @return               Total count of genes in the pathway.
     */
    public static int genesInPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        int degsInPathway = degsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);
        int nonDEGsInPathway = nonDEGsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);

        return degsInPathway + nonDEGsInPathway;
    }

    /**
     * Calculates the total number of genes in the dataset.
     *
     * @param geneRecords List of GeneRecord objects with gene expression data.
     * @param treshold    The adjusted p-value threshold for filtering DEGs and non-DEGs.
     * @return            The total number of genes in the dataset.
     */
    public static long totalGenes(List<GeneRecord> geneRecords, double treshold){
        long totalNonDEGs = totalNonDEGS(geneRecords, treshold);
        long totalDEGs = totalDEGS(geneRecords, treshold);

        return totalNonDEGs + totalDEGs;
    }

    /**
     * Calculates the number of DEGs not present in a given pathway.
     *
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering DEGs.
     * @return               The count of DEGs not in the pathway.
     */
    public static long degsNotinPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        long totalDEGs = totalDEGS(geneRecords, treshold);
        long degsInPathway = degsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);

        return totalDEGs - degsInPathway;
    }

    /**
     * Calculates the number of non-DEGs not present in a specified pathway.
     *
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering non-DEGs.
     * @return               The count of non-DEGs not in the pathway.
     */
    public static long nonDEGsNotInPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        long totalNonDEGs = totalNonDEGS(geneRecords, treshold);
        long nonDegsInPathway = nonDEGsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);

        return totalNonDEGs - nonDegsInPathway;
    }

    /**
     * Calculates the number of genes not present in a specified pathway.
     *
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering DEGs and non-DEGs.
     * @return               Total number of genes not in the pathway.
     */
    public static long genesNotInPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        long totalgenes = totalGenes(geneRecords, treshold);
        long genesinPathway = genesInPathway(geneRecords, pathwayRecords, pathwayID, treshold);

        return totalgenes - genesinPathway;
    }

    /**
     * Builds a table as a formatted string showing DEG and non-DEG counts in and out of a pathway.
     *
     * @param geneRecords    List of GeneRecord objects with gene expression data.
     * @param pathwayRecords Map of pathway IDs to PathwayRecord objects containing pathway data.
     * @param pathwayID      The ID of the pathway to examine.
     * @param treshold       The adjusted p-value threshold for filtering DEGs and non-DEGs.
     * @return               A formatted string representing the DEG/non-DEG counts table.
     */
    public static String tableBuilder(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold) {
        int degsInPathway = degsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);
        int nonDEGsInPathway = nonDEGsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);
        long totalDEGs = totalDEGS(geneRecords, treshold);
        long totalNonDEGs = totalNonDEGS(geneRecords, treshold);

        int genesInPathway = genesInPathway(geneRecords, pathwayRecords, pathwayID, treshold);
        long totalGenes = totalGenes(geneRecords, treshold);
        long degsNotInPathway = degsNotinPathway(geneRecords, pathwayRecords, pathwayID, treshold);
        long nonDEGsNotInPathway = nonDEGsNotInPathway(geneRecords, pathwayRecords, pathwayID, treshold);
        long genesNotInPathway = genesNotInPathway(geneRecords, pathwayRecords, pathwayID, treshold);
        // building the table itself
        String line1 = "   |  D  |  D* | Sum\n";
        String line2 = "--------------------\n";
        String line3 = String.format(" C |%5d|%5d|%5d\n", degsInPathway, nonDEGsInPathway, genesInPathway);
        String line4 = String.format(" C*|%5d|%5d|%5d\n", degsNotInPathway, nonDEGsNotInPathway, genesNotInPathway);
        String line5 = String.format("sum|%5d|%5d|%5d\n", totalDEGs, totalNonDEGs, totalGenes);
        String line6 = "\nC: in pathway, C*: not in pathway\n";
        String line7 = "D: DEG (FDR <= 0.01), D*: non DEG";

        String table = line1 + line2 + line3 + line4 + line5 + line6 + line7;

        return table;
    }
}
