package nl.bioinf.gse;

public record GSEARecord(String pathwayID, double pValue, double adjustedPValue, double enrichmentScore, double observedDEGs, double expectedDEGs, String description, double avgLogFoldChange) {
}
