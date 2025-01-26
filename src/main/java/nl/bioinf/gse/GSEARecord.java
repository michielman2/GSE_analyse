package nl.bioinf.gse;

/**
 * Represents a record for a gene, containing its symbol, log fold change, and adjusted p-value.
 * This record is typically used for storing information about differentially expressed genes (DEGs).
 *
 * @param geneSymbol      The symbol of the gene (e.g., gene name or identifier).
 * @param logFoldChange   The log fold change value associated with the gene, indicating the magnitude of expression change.
 * @param adjustedPValue  The adjusted p-value for the gene, used to assess statistical significance after multiple testing correction.
 */
public record GeneRecord(String geneSymbol, double logFoldChange, double adjustedPValue) {

}
