package nl.bioinf.gse;

import java.util.*;

public class CreateTable {


    public static int countUniqueGenes(Map<String, PathwayRecord> pathwayRecords) {
        Set<String> uniqueGenes = new HashSet<>();


        for (PathwayRecord pathwayRecord : pathwayRecords.values()) {

            uniqueGenes.addAll(pathwayRecord.geneSymbols());
        }


        return uniqueGenes.size();
    }

    public static int totalDEGS(List<GeneRecord> geneRecords) {
        return geneRecords.size();
    }

    public static int genesInPathway(Map<String, PathwayRecord> pathwayRecords, String pathwayID) {
        List<String> geneID = new ArrayList<>();

        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);

        return selectedPathway.geneSymbols().size();
    }

    public static int degsInPathway(Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, String pathwayID){
        List<String> geneID = new ArrayList<>();
        int degCount = 0;

        PathwayRecord selectedPathway = pathwayRecords.get(pathwayID);
        geneID.addAll(selectedPathway.geneSymbols());

        Set<String> geneIDSet = new HashSet<>();
        for (GeneRecord geneRecord : geneRecords) {
            geneIDSet.add(geneRecord.geneSymbol());
        }

        for (String geneIDString : geneID) {
            if (geneIDSet.contains(geneIDString)) {
                degCount++;
            }
        }

        return degCount;
    }

    public static String tablebuilder(int degsInPathway, int genesInPathway, int totalDEGs, int totalGenes) {
        int degsNotInPathway = totalDEGs - degsInPathway;
        int nonDEGsInPathway = genesInPathway - degsInPathway;
        int nonDEGGenes = totalGenes - totalDEGs;
        int genesNotInPathway = totalGenes - genesInPathway;
        int nonDEGsNotInPathway = nonDEGGenes - nonDEGsInPathway;

        String line1 = "   |  D  |  D* | Sum\n";
        String line2 = "--------------------\n";
        String line3 = String.format(" C |%5d|%5d|%5d\n", degsInPathway, nonDEGsInPathway, genesInPathway);
        String line4 = String.format(" C*|%5d|%5d|%5d\n", degsNotInPathway, nonDEGsNotInPathway, genesNotInPathway);
        String line5 = String.format("sum|%5d|%5d|%5d\n", totalDEGs, nonDEGGenes, totalGenes);
        String line6 = "\nC: in pathway, C*: not in pathway\n";
        String line7 = "D: DEG (FDR <= 0.01), D*: non DEG";

        String table = line1 + line2 + line3 + line4 + line5 + line6 + line7;

        return table;
    }
}
