package nl.bioinf.gse;

import java.util.List;
import java.util.Map;

/**
 * GSEA class provides methods for calculating enrichment scores, p-values, BinomialCoefficient and expected DEG's.
 */
public class GSEA {

    /**
     * Performs a hypergeometric test to determine the probability of finding
     * a certain number of DEGs in a pathway given gene counts.
     *
     * @param degsInPathway  Number of DEGs observed in the pathway.
     * @param genesInPathway Total number of genes in the pathway.
     * @param totalDEGs      Total number of DEGs in the entire dataset.
     * @param totalGenes     Total number of genes in the entire dataset.
     * @return Probability from the hypergeometric test, or 0.0 if input values are invalid.
     */
    double hyperGeometricTest(long degsInPathway, long genesInPathway, long totalDEGs, long totalGenes) {
        // Ensures the values are valid, if any condition fails, return 0.0 as the error indicates
        if (totalDEGs > totalGenes || degsInPathway > genesInPathway || totalDEGs < 0 || totalGenes < 0 || degsInPathway < 0 || genesInPathway < 0) {
            return 0.0;
        }

        // Calculate the numerator using binomial coefficients
        double numerator = calculateBinomialCoefficient(totalDEGs, degsInPathway) * calculateBinomialCoefficient(totalGenes - totalDEGs, genesInPathway - degsInPathway);

        // Calculate the denominator
        double denominator = calculateBinomialCoefficient(totalGenes, genesInPathway);

        // Return the result by deviding the two
        return numerator / denominator;
    }

    /**
     * Calculates the enrichment score based on observed and expected DEGs.
     *
     * @param observedDegCount Number of DEGs observed in the pathway.
     * @param expectedDegCount Expected number of DEGs in the pathway.
     * @return Enrichment score
     */
    public double calculateEnrichmentScore(double observedDegCount, double expectedDegCount) {
        // Avoid division by zero by checking if expected count is greater than 0
        if (expectedDegCount <= 0) {
            return 0;
        }
        return (observedDegCount - expectedDegCount) / Math.sqrt(expectedDegCount);
    }

    /**
     * Calculates the p-value for enrichment by summing probabilities of observing
     * the given number of DEGs in the pathway using a hypergeometric test.
     *
     * @param degsInPathway  Number of DEGs observed in the pathway.
     * @param totalDEGs      Total number of DEGs in the dataset.
     * @param genesInPathway Total number of genes in the pathway.
     * @param totalGenes     Total number of genes in the dataset.
     * @return P-value for enrichment, or 1.0 if pathway or total genes are zero or if the p-value calculation is NaN.
     */
    public double calculatePValue(long degsInPathway, long totalDEGs, long genesInPathway, long totalGenes) {
        // Edge case: if no genes in pathway or no total DEGs, return p-value of 1.0, if so change threshold
        if (genesInPathway == 0 || totalDEGs == 0) {
            return 1.0;
        }

        double pValue = 0.0;
        // Summing probabilities of finding i or more DEGs in the pathway using hypergeometric test
        for (long i = degsInPathway; i <= genesInPathway; i++) {
            pValue += hyperGeometricTest(i, genesInPathway, totalDEGs, totalGenes);
        }

        // Handle any NaN result by setting p-value to 1.0
        return Double.isNaN(pValue) ? 1.0 : pValue;
    }

    /**
     * Calculates the binomial coefficient (n choose k), used in hypergeometric calculations.
     *
     * @param n Total items.
     * @param k Items to choose.
     * @return Binomial coefficient or 0 if k > n.
     */
    double calculateBinomialCoefficient(long n, long k) {
        // the calculations
        if (k > n) return 0;
        if (k == 0 || k == n) return 1;

        double coefficient = 1;
        // the followup calculations
        for (int i = 1; i <= k; i++) {
            coefficient *= (n - i + 1);
            coefficient /= i;
        }
        return coefficient;
    }

    /**
     * Calculates the expected number of DEGs in a pathway based on the proportion of DEGs in the total dataset.
     *
     * @param totalDEGs      Total number of DEGs in the dataset.
     * @param genesInPathway Total number of genes in the pathway.
     * @param totalGenes     Total number of genes in the dataset.
     * @return Expected number of DEGs for the pathway.
     */
    public double calculateExpectedDEGs(long totalDEGs, long genesInPathway, long totalGenes) {
        // Calculate DEG proportion across the total number of genes
        double degProportion = (double) totalDEGs / (double) totalGenes;

        // calculate the expected degs in pathway
        return genesInPathway * degProportion;
    }

    /**
     * Adjusts the p-value using the Bonferroni correction for multiple testing.
     *
     * @param pValue     Original p-value.
     * @param numPathways Number of pathways being tested.
     * @return Adjusted p-value, capped at 1.0.
     */
    public double adjustPValue(double pValue, int numPathways) {
        // Apply Bonferroni correction by multiplying p-value by the number of pathways
        return Math.min(1.0, pValue * numPathways);
    }
}
