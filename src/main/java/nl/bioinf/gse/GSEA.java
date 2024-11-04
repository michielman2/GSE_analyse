package nl.bioinf.gse;

import java.util.List;
import java.util.Map;

public class GSEA {
    private double hyperGeometricTest(long totalDEGs, long totalGenes, long degsInPathway, long genesInPathway) {
        if (totalDEGs > totalGenes || degsInPathway > genesInPathway || totalDEGs < 0 || totalGenes < 0 || degsInPathway < 0 || genesInPathway < 0) {
            return 0.0;
        }

        double numerator = calculateBinominalCoefficient(totalDEGs, degsInPathway) * calculateBinominalCoefficient(totalGenes - totalDEGs, genesInPathway - degsInPathway);
        double denominator = calculateBinominalCoefficient(totalGenes, genesInPathway);
        return numerator / denominator;
    }

    private double calculateEnrichmentScore(long degsInPathway, long totalDEGs, long genesInPathway, long totalGenes) {
        // ES = (DEGs in Pathway / Total DEGs) / (Genes in Pathway / Total Genes)
        return (double) degsInPathway / totalDEGs / ((double) genesInPathway / totalGenes);
    }

    private double calculatePValue(long degsInPathway, long totalDEGs, long genesInPathway, long totalGenes) {
        if (genesInPathway == 0 || totalDEGs == 0) {
            return 1.0;
        }

        double pValue = 0.0;
        for (long i = degsInPathway; i <= genesInPathway; i++) {
            pValue += hyperGeometricTest(totalDEGs, totalGenes, i, genesInPathway);
        }

        return Double.isNaN(pValue) ? 1.0 : pValue;
    }

    private double calculateBinominalCoefficient(long n, long k){
        if (k > n) return 0;
        if (k == 0 || k == n) return 1;

        double coefficient = 1;
        for (int i = 1; i <= k; i++) {
            coefficient *= (n - i + 1);
            coefficient /= i;
        }
        return coefficient;
    }
    private double calculateExpectedDEGs(List<GeneRecord> geneRecords, Map<String, PathwayRecord> pathwayRecords, String pathwayID){
        double degProportion = TableBuilder.totalDEGS(geneRecords)/ (double) TableBuilder.genesInPathway(geneRecords, pathwayRecords, pathwayID);

        return TableBuilder.genesInPathway(geneRecords, pathwayRecords, pathwayID) * degProportion;

    }
    private double adjustPValue(double pValue, int numPathways) {
        return Math.min(1.0, pValue * numPathways); // Simple Bonferroni correction for now
    }
}
