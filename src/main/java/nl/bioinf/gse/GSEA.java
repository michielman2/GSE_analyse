package nl.bioinf.gse;

import java.util.List;
import java.util.Map;

public class GSEA {
    private double hyperGeometricTest(long degsInPathway, long genesInPathway, long totalDEGs, long totalGenes) {
        if (totalDEGs > totalGenes || degsInPathway > genesInPathway || totalDEGs < 0 || totalGenes < 0 || degsInPathway < 0 || genesInPathway < 0) {
            return 0.0;
        }

        double numerator = calculateBinominalCoefficient(totalDEGs, degsInPathway) * calculateBinominalCoefficient(totalGenes - totalDEGs, genesInPathway - degsInPathway);
        double denominator = calculateBinominalCoefficient(totalGenes, genesInPathway);
        return numerator / denominator;
    }

    public double calculateEnrichmentScore(double observedDegCount, double expectedDegCount) {
        if (expectedDegCount <= 0) {
            return 0;
        }
        return (observedDegCount - expectedDegCount) / Math.sqrt(expectedDegCount);
    }

    public double calculatePValue(long degsInPathway, long totalDEGs, long genesInPathway, long totalGenes) {
        if (genesInPathway == 0 || totalDEGs == 0) {
            return 1.0;
        }

        double pValue = 0.0;
        for (long i = degsInPathway; i <= genesInPathway; i++) {
            pValue += hyperGeometricTest(i, genesInPathway, totalDEGs, totalGenes);
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
    public double calculateExpectedDEGs(long totalDEGs, long genesInPathway, long totalGenes) {

        double degProportion = (double) totalDEGs / (double) totalGenes;

        return genesInPathway * degProportion;

    }
    public double adjustPValue(double pValue, int numPathways) {
        return Math.min(1.0, pValue * numPathways); // Simple Bonferroni correction for now
    }
}
