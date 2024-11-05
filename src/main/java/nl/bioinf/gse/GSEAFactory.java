package nl.bioinf.gse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GSEAFactory {
    public List<GSEARecord> performGSEA(long totalDEGs, long totalGenes, Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, double treshold){
        GSEA gsea = new GSEA();
        List<GSEARecord> gseaResults = new ArrayList<>();

        int numPathways = pathwayRecords.size();

        for (String pathwayID : pathwayRecords.keySet()) {
            long degsInPathway = TableBuilder.degsInPathway(pathwayRecords, geneRecords, pathwayID, treshold);
            long genesInPathway = TableBuilder.genesInPathway(geneRecords, pathwayRecords, pathwayID, treshold);

            double pValue = (degsInPathway > 0)
                    ? gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes)
                    : 1.0;


            double adjustedPValue = gsea.adjustPValue(pValue, numPathways);



            double expectedDEGs = gsea.calculateExpectedDEGs(totalDEGs, genesInPathway, totalGenes);

            double observedDEGs = degsInPathway;
            double enrichmentScore = gsea.calculateEnrichmentScore(observedDEGs, expectedDEGs);

            gseaResults.add(new GSEARecord(pathwayID, pValue, adjustedPValue, enrichmentScore, observedDEGs, expectedDEGs));
        }
        return gseaResults;
    }
}
