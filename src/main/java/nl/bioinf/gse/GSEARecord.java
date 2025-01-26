package nl.bioinf.gse;

/**
 * Represents a record for Gene Set Enrichment Analysis, containing information about a specific pathway and its analysis results.
 *
 * @param pathwayID         The unique identifier for the pathway.
 * @param pValue            The p-value for the pathway, indicating the statistical significance of enrichment.
 * @param adjustedPValue    The adjusted p-value for the pathway, corrected for multiple testing.
 * @param enrichmentScore   The enrichment score for the pathway, representing the degree of enrichment.
 * @param observedDEGs      The number of observed differentially expressed genes (DEGs) in the pathway.
 * @param expectedDEGs      The expected number of DEGs in the pathway based on random chance.
 * @param description       A brief description of the pathway, typically its name or biological function.
 * @param avgLogFoldChange  The average log fold change of the DEGs in the pathway, indicating the magnitude of expression changes.
 */
public record GSEARecord(String pathwayID, double pValue, double adjustedPValue, double enrichmentScore, double observedDEGs, double expectedDEGs, String description, double avgLogFoldChange) {
}
