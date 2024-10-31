package nl.bioinf.gse;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GSEAWithHypergeometric {

    public static class GSEAResult {
        String pathwayID;
        double pValue;
        double adjustedPValue;
        double enrichmentScore;

        public GSEAResult(String pathwayID, double pValue, double adjustedPValue, double enrichmentScore) {
            this.pathwayID = pathwayID;
            this.pValue = pValue;
            this.adjustedPValue = adjustedPValue;
            this.enrichmentScore = enrichmentScore;
        }
    }

    public List<GSEAResult> performGSEA(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathways, double fdrThreshold) {

        List<GSEAResult> results = new ArrayList<>();

        // Get the total number of DEGs (i.e., genes where FDR <= threshold)
        long totalDEGs = geneRecords.stream()
                .filter(g -> g.adjustedPValue() <= fdrThreshold)
                .count();
        long totalGenes = geneRecords.size(); // Total number of genes in the dataset (DEGs + non-DEGs)

        for (String pathwayID : pathways.keySet()) {
            PathwayRecord pathway = pathways.get(pathwayID);
            Set<String> pathwayGenes = new HashSet<>(pathway.geneSymbols());
            long genesInPathway = pathwayGenes.size();

            // Find how many DEGs are in the pathway
            long degsInPathway = geneRecords.stream()
                    .filter(g -> g.adjustedPValue() <= fdrThreshold && pathwayGenes.contains(g.geneSymbol()))
                    .count();


            double pValue = runHypergeometricTest((int) totalGenes, (int) genesInPathway, (int) totalDEGs, (int) degsInPathway);

            // Adjust p-value (Bonferroni correction for now)
            double adjustedPValue = adjustPValue(pValue, pathways.size());

            // Calculate Enrichment Score (ES)
            double enrichmentScore = calculateEnrichmentScore(degsInPathway, totalDEGs, genesInPathway, totalGenes);

            results.add(new GSEAResult(pathwayID, pValue, adjustedPValue, enrichmentScore));
        }

        return results;
    }

    private double runHypergeometricTest(int totalGenes, int genesInPathway, int totalDEGs, int degsInPathway) {
        HypergeometricDistribution hypergeom = new HypergeometricDistribution(totalGenes, totalDEGs, genesInPathway);
        return 1.0 - hypergeom.cumulativeProbability(degsInPathway - 1);
    }

    private double adjustPValue(double pValue, int numPathways) {
        return Math.min(1.0, pValue * numPathways); // Simple Bonferroni correction for now
    }

    private double calculateEnrichmentScore(long degsInPathway, long totalDEGs, long genesInPathway, long totalGenes) {
        // ES = (DEGs in Pathway / Total DEGs) / (Genes in Pathway / Total Genes)
        return (double) degsInPathway / totalDEGs / ((double) genesInPathway / totalGenes);
    }
}
