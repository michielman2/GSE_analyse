package nl.bioinf.gse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreateTable {


    public static int countUniqueGenes(Map<String, PathwayRecord> pathwayRecords) {
        Set<String> uniqueGenes = new HashSet<>();


        for (PathwayRecord pathwayRecord : pathwayRecords.values()) {

            uniqueGenes.addAll(pathwayRecord.geneSymbols());
        }


        return uniqueGenes.size();
    }
}
