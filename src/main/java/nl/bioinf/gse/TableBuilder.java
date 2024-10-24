package nl.bioinf.gse;

import java.util.*;

public class TableBuilder {

    public static long totalNonDEGS(List<GeneRecord> geneRecords) {
        long count = geneRecords.stream()
                .filter(record -> record.adjustedPValue() > 0.05)
                .count();

        return count;
    }

    public static long totalDEGS(List<GeneRecord> geneRecords) {
        long count = geneRecords.stream()
                .filter(record -> record.adjustedPValue() < 0.05)
                .count();

        return count;
    }


    public static int degsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID) {
        List<String> geneID = new ArrayList<>();
        int degCount = 0;

        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneSymbols());

        for (GeneRecord geneRecord : geneRecords) {
            if (geneID.contains(geneRecord.geneSymbol())){
                if (geneRecord.adjustedPValue() < 0.05){
                    degCount++;
                }
            }
        }


        return degCount;
    }
    public static int nonDEGsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID){
        List<String> geneID = new ArrayList<>();
        int geneCount = 0;

        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneSymbols());

        for (GeneRecord geneRecord : geneRecords) {
            if (geneID.contains(geneRecord.geneSymbol())){
                if (geneRecord.adjustedPValue() > 0.05){
                    geneCount++;
                }
            }
        }


        return geneCount;

    }

    public static String tableBuilder(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID) {
        int degsInPathway = degsInPathway(pathwayRecords, geneRecords, pathwayID);
        int nonDEGsInPathway = nonDEGsInPathway(pathwayRecords, geneRecords, pathwayID);
        long totalDEGs = totalDEGS(geneRecords);
        long totalNonDEGs = totalNonDEGS(geneRecords);

        int genesInPathway = degsInPathway + nonDEGsInPathway;
        long totalGenes = totalNonDEGs + totalDEGs;
        long degsNotInPathway = totalDEGs - degsInPathway;
        long nonDEGsNotInPathway = totalNonDEGs - nonDEGsInPathway;
        long genesNotInPathway = totalGenes - genesInPathway;

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
