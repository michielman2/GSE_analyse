package nl.bioinf.gse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GSEAFactory is responsible for performing Gene Set Enrichment Analysis (GSEA) on given pathways.
 * This class calculates the p-values, adjusted p-values, and enrichment scores for each pathway,
 * given pathway records and gene records.
 */
public class GSEAFactory {

    /**
     * Performs GSEA over the given pathways.
     *
     * @param totalDEGs      The total number of Differentially Expressed Genes (DEGs).
     * @param totalGenes     The total number of genes in the dataset.
     * @param pathwayRecords A map containing pathway records with pathwayIDs as keys.
     * @param geneRecords    A list of GeneRecord objects representing individual genes.
     * @param threshold      The threshold for filtering genes in enrichment calculations to classify if it's a DEG.
     * @return A list of GSEARecord objects, each containing enrichment data for a specific pathway.
     */
    public List<GSEARecord> performGSEA(long totalDEGs, long totalGenes, Map<String, PathwayRecord> pathwayRecords, List<GeneRecord> geneRecords, double threshold) {
        GSEA gsea = new GSEA();
        List<GSEARecord> gseaResults = new ArrayList<>();

        // Store the number of pathways for p-value adjustment calculations
        int numPathways = pathwayRecords.size();

        // Loop over each pathwayID in the pathway records map
        for (String pathwayID : pathwayRecords.keySet()) {

            // Calculate the number of DEGs associated with the current pathway, applying the threshold filter
            long degsInPathway = TableBuilder.degsInPathway(pathwayRecords, geneRecords, pathwayID, threshold);

            // Calculate the total number of genes in the pathway that meet the threshold
            long genesInPathway = TableBuilder.genesInPathway(geneRecords, pathwayRecords, pathwayID, threshold);

            // Calculate the p-value if DEGs are present; otherwise, set p-value to 1.0
            double pValue = (degsInPathway > 0)
                    ? gsea.calculatePValue(degsInPathway, totalDEGs, genesInPathway, totalGenes)
                    : 1.0;

            // Adjust the p-value based on the number of pathways
            double adjustedPValue = gsea.adjustPValue(pValue, numPathways);

            // Calculate the expected number of DEGs for this pathway
            double expectedDEGs = gsea.calculateExpectedDEGs(totalDEGs, genesInPathway, totalGenes);

            // Set observedDEGs to degsInPathway and calculate the enrichment score
            double observedDEGs = degsInPathway;
            double enrichmentScore = gsea.calculateEnrichmentScore(observedDEGs, expectedDEGs);

            // Retrieve the description of the pathway
            String description = pathwayRecords.get(pathwayID).description();

            // Add the calculated values to a new GSEARecord and store it in the results list
            gseaResults.add(new GSEARecord(pathwayID, pValue, adjustedPValue, enrichmentScore, observedDEGs, expectedDEGs, description));
        }

        // Return the list of GSEA results containing enrichment information for all pathways
        return gseaResults;
    }
}
