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
        System.out.println(enrichmentScore);
        assertEquals(1.5811388300841895, enrichmentScore, 0.0001);


        // Test with expectedDegCount <= 0 (should return 0)
        enrichmentScore = gsea.calculateEnrichmentScore(50, 0);
        assertEquals(0, enrichmentScore);
    }

    @Test
    void testCalculatePValue() {
        // Edge case where genesInPathway or totalDEGs are zero
        double pValue = gsea.calculatePValue(0, 0, 0, 0);
        assertEquals(1.0, pValue);

        // Test with known expected value for valid inputs
        long degsInPathway = 3;
        long totalDEGs = 100;
        long genesInPathway = 20;
        long totalGenes = 200;

        // Run the actual calculation in a way that mimics the method's logic
        double cumulativePValue = 0.0;
        for (long i = degsInPathway; i <= genesInPathway; i++) {
            cumulativePValue += gsea.hyperGeometricTest(i, genesInPathway, totalDEGs, totalGenes);
        }



        // Test the result against the method's output
        pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        assertEquals(cumulativePValue, pValue, 0.0001); // Allow a small delta for precision
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
