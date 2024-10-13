package nl.bioinf.gse;

import java.util.List;

public record PathwayRecord(String pathwayID, String description, List<String> entrezGeneIDs,
                            List<String> ensemblGeneIDs, List<String> geneSymbols) {}

