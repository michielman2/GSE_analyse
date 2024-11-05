package nl.bioinf.gse;

import java.util.*;

public class TableBuilder {
    public static long totalNonDEGS(List<GeneRecord> geneRecords, double treshold) {
        long count = geneRecords.stream()
                .filter(record -> record.adjustedPValue() > treshold)
                .count();

        return count;
    }

    public static long totalDEGS(List<GeneRecord> geneRecords, double treshold) {
        long count = geneRecords.stream()
                .filter(record -> record.adjustedPValue() < treshold)
                .count();

        return count;
    }


    public static int degsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID, double treshold) {
        List<String> geneID = new ArrayList<>();
        int degCount = 0;

        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneSymbols());

        for (GeneRecord geneRecord : geneRecords) {
            if (geneID.contains(geneRecord.geneSymbol())){
                if (geneRecord.adjustedPValue() < treshold){
                    degCount++;
                }
            }
        }


        return degCount;
    }
    public static int nonDEGsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID, double treshold){
        List<String> geneID = new ArrayList<>();
        int geneCount = 0;

        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneSymbols());

        for (GeneRecord geneRecord : geneRecords) {
            if (geneID.contains(geneRecord.geneSymbol())){
                if (geneRecord.adjustedPValue() > treshold){
                    geneCount++;
                }
            }
        }


        return geneCount;

    }

    public static int genesInPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        int degsInPathway = degsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);
        int nonDEGsInPathway = nonDEGsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);

        return degsInPathway + nonDEGsInPathway;
    }

    public static long totalGenes(List<GeneRecord> geneRecords, double treshold){
        long totalNonDEGs = totalNonDEGS(geneRecords, treshold);
        long totalDEGs = totalDEGS(geneRecords, treshold);

        return totalNonDEGs + totalDEGs;
    }

    public static long degsNotinPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        long totalDEGs = totalDEGS(geneRecords, treshold);
        long degsInPathway = degsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);

        return totalDEGs - degsInPathway;
    }

    public static long nonDEGsNotInPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        long totalNonDEGs = totalNonDEGS(geneRecords, treshold);
        long nonDegsInPathway = nonDEGsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);

        return totalNonDEGs - nonDegsInPathway;
    }

    public static long genesNotInPathway(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID, double treshold){
        long totalgenes = totalGenes(geneRecords, treshold);
        long genesinPathway = genesInPathway(geneRecords, pathwayRecords, pathwayID, treshold);

        return totalgenes - genesinPathway;
    }

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
