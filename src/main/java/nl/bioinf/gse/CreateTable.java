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

    public static int calculateRest(int degsInPathway, int genesInPathway, int totalDEGs, int totalGenes) {
        int degsNotInPathway = degsInPathway - totalDEGs;
        int nonDEGsInPathway = genesInPathway - degsInPathway;
        int nonDEGGenes = totalGenes - totalDEGs;
        int genesNotInPathway = totalGenes - genesInPathway;
        int nonDEGsNotInPathway = nonDEGGenes - nonDEGsInPathway;

        return new AbstractMap.SimpleEntry<Integer, Integer>(degsNotInPathway,nonDEGsInPathway,nonDEGGenes)

    }
}
