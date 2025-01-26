package nl.bioinf.gse;

import java.util.List;

/**
 * A record representing a biological pathway and its associated metadata.
 *
 * @param pathwayID  The unique identifier for the pathway.
 * @param description A textual description of the pathway.
 * @param geneIDs    A list of gene identifiers associated with the pathway.
 */
public record PathwayRecord(String pathwayID, String description, List<String> geneIDs) {}
