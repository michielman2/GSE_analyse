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
        // Test with dummy values
        double observedDegCount = 50;
        double expectedDegCount = 40;
        double enrichmentScore = gsea.calculateEnrichmentScore(observedDegCount, expectedDegCount);
        System.out.println(enrichmentScore);
        assertEquals(1.5811388300841895, enrichmentScore, 0.0001);


        // enrichtmentscore should be 0
        enrichmentScore = gsea.calculateEnrichmentScore(50, 0);
        assertEquals(0, enrichmentScore);
    }


    @Test
    void testCalculatePValue() {
        // edge case
        double pValue = gsea.calculatePValue(0, 0, 0, 0);
        assertEquals(1.0, pValue);

        // Test with known values
        long degsInPathway = 3;
        long totalDEGs = 100;
        long genesInPathway = 20;
        long totalGenes = 200;

        // Run the actual calculation
        double cumulativePValue = 0.0;
        for (long i = degsInPathway; i <= genesInPathway; i++) {
            cumulativePValue += gsea.hyperGeometricTest(i, genesInPathway, totalDEGs, totalGenes);
        }


        // Test the result against the method's output
        pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        assertEquals(cumulativePValue, pValue, 0.0001); // Otherwise it failed the test due to rounding

    }


    @Test
    void testHyperGeometricTest() {
        // Test with values
        double probability = gsea.hyperGeometricTest(3, 20, 100, 200);
        assertTrue(probability >= 0 && probability <= 1);

        // should return 0
        probability = gsea.hyperGeometricTest(0, 20, 100, 0);
        assertEquals(0.0, probability);
    }

    @Test
    void testCalculateBinomialCoefficient() {
        // Test with values
        long n = 10;
        long k = 5;
        double coefficient = gsea.calculateBinomialCoefficient(n, k);
        assertEquals(252.0, coefficient);

        // Test
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

        // Test with zero expected totalDEGs
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

        // Test with p-value bigger than 1.0
        pValue = 1.5;
        adjustedPValue = gsea.adjustPValue(pValue, numPathways);
        assertEquals(1.0, adjustedPValue); // p-value should be capped at 1.0
    }

    @Test
    void testBoundaryNoDEGsInPathway() {
        // Case 1: No DEGs in the pathway
        long degsInPathway = 0;
        long totalDEGs = 200;
        long genesInPathway = 50;
        long totalGenes = 1000;

        double pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        System.out.println("Boundary Test (No DEGs in Pathway) P-value: " + pValue);
        assertEquals(1.0, pValue, 1e-10, "Expected p-value to be approximately 1.0 when no DEGs are in the pathway");
    }

    @Test
    void testBoundaryNoGenesInPathway() {
        // Case 2: No genes in the pathway
        long degsInPathway = 5;
        long totalDEGs = 200;
        long genesInPathway = 0;
        long totalGenes = 1000;

        double pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        System.out.println("Boundary Test (No Genes in Pathway) P-value: " + pValue);
        assertEquals(1.0, pValue, 1e-10, "Expected p-value to be approximately 1.0 when there are no genes in the pathway");
    }

    @Test
    void testBoundaryNoTotalDEGs() {
        // Case 3: Total DEGs is zero
        long degsInPathway = 5;
        long totalDEGs = 0;
        long genesInPathway = 50;
        long totalGenes = 1000;

        double pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        System.out.println("Boundary Test (No Total DEGs) P-value: " + pValue);
        assertEquals(1.0, pValue, 1e-10, "Expected p-value to be approximately 1.0 when there are no total DEGs in the dataset");
    }

    @Test
    void testBoundaryNoTotalDEGsAndGenesInPathway() {
        // Case 4: Both totalDEGs and genesInPathway are zero
        long degsInPathway = 5;
        long totalDEGs = 0;
        long genesInPathway = 0;
        long totalGenes = 1000;

        double pValue = gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes);
        System.out.println("Boundary Test (No Total DEGs and Genes in Pathway) P-value: " + pValue);
        assertEquals(1.0, pValue, 1e-10, "Expected p-value to be approximately 1.0 when there are no total DEGs and no genes in the pathway");
    }
}