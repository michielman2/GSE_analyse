package nl.bioinf.gse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GSEATest {

    private GSEA gsea;

    @BeforeEach
    void setUp() {
        gsea = new GSEA();
    }

    @Test
    void testCalculateEnrichmentScore() {
        // Test with valid values
        double observedDegCount = 50;
        double expectedDegCount = 40;
        double enrichmentScore = gsea.calculateEnrichmentScore(observedDegCount, expectedDegCount);
        assertEquals(0.7071, enrichmentScore, 0.0001);

        // Test with expectedDegCount <= 0 (should return 0)
        enrichmentScore = gsea.calculateEnrichmentScore(50, 0);
        assertEquals(0, enrichmentScore);
    }

    @Test
    void testCalculatePValue() {
        // Edge case where genesInPathway or totalDEGs are zero
        double pValue = gsea.calculatePValue(0, 0, 0, 0);
        assertEquals(1.0, pValue);

        // Test with valid values
        long degsInPathway = 3;
        long totalDEGs = 100;
        long genesInPathway = 20;
        long totalGenes = 200;
        pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        assertTrue(pValue >= 0 && pValue <= 1); // p-value should be in range [0, 1]

        // Test with extreme values
        pValue = gsea.calculatePValue(0, 100, 20, 200);
        assertTrue(pValue >= 0 && pValue <= 1);
    }

    @Test
    void testHyperGeometricTest() {
        // Test with valid values
        double probability = gsea.hyperGeometricTest(3, 20, 100, 200);
        assertTrue(probability >= 0 && probability <= 1);

        // Test with invalid values (should return 0.0)
        probability = gsea.hyperGeometricTest(0, 20, 100, 0);
        assertEquals(0.0, probability);
    }

    @Test
    void testCalculateBinomialCoefficient() {
        // Test valid values
        long n = 10;
        long k = 5;
        double coefficient = gsea.calculateBinomialCoefficient(n, k);
        assertEquals(252.0, coefficient);

        // Test edge cases
        coefficient = gsea.calculateBinomialCoefficient(10, 0);
        assertEquals(1.0, coefficient); // n choose 0 = 1

        coefficient = gsea.calculateBinomialCoefficient(10, 10);
        assertEquals(1.0, coefficient); // n choose n = 1

        coefficient = gsea.calculateBinomialCoefficient(5, 6);
        assertEquals(0.0, coefficient); // n choose k where k > n = 0
    }

    @Test
    void testCalculateExpectedDEGs() {
        // Test with valid values
        long totalDEGs = 100;
        long genesInPathway = 20;
        long totalGenes = 200;
        double expectedDegs = gsea.calculateExpectedDEGs(totalDEGs, genesInPathway, totalGenes);
        assertEquals(10.0, expectedDegs);

        // Test with zero totalDEGs
        expectedDegs = gsea.calculateExpectedDEGs(0, genesInPathway, totalGenes);
        assertEquals(0.0, expectedDegs);
    }

    @Test
    void testAdjustPValue() {
        // Test with valid p-value
        double pValue = 0.02;
        int numPathways = 100;
        double adjustedPValue = gsea.adjustPValue(pValue, numPathways);
        assertEquals(1.0, adjustedPValue); // Since 0.02 * 100 = 2.0, capped at 1.0

        // Test with p-value greater than 1.0
        pValue = 1.5;
        adjustedPValue = gsea.adjustPValue(pValue, numPathways);
        assertEquals(1.0, adjustedPValue); // p-value should be capped at 1.0
    }
}
