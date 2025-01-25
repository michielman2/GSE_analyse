package nl.bioinf.gse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileParserTest {

    private FileParser fileParser;
    private final String degsFilePath = "test_degs.csv";
    private final String pathwaysFilePath = "test_pathways.csv";
    private final String hsaPathwaysFilePath = "test_hsa_pathways.csv";

    @BeforeEach
    public void setUp() throws IOException {
        fileParser = new FileParser();
        createTestDEGsFile();
        createTestPathwaysFile();
        createTestHSAPathwaysFile();
    }

    @Test
    public void testReadDEGs() throws IOException {
        List<GeneRecord> geneRecords = fileParser.readDEGs(degsFilePath, 1);
        assertEquals(3, geneRecords.size());

        GeneRecord gene1 = geneRecords.get(0);
        assertEquals("GeneA", gene1.geneSymbol());
        assertEquals(1.5, gene1.logFoldChange(), 0.01);
        assertEquals(0.05, gene1.adjustedPValue(), 0.01);

        GeneRecord gene2 = geneRecords.get(1);
        assertEquals("GeneB", gene2.geneSymbol());
        assertEquals(-2.0, gene2.logFoldChange(), 0.01);
        assertEquals(0.01, gene2.adjustedPValue(), 0.01);

        GeneRecord gene3 = geneRecords.get(2);
        assertEquals("GeneC", gene3.geneSymbol());
        assertEquals(0.8, gene3.logFoldChange(), 0.01);
        assertEquals(0.15, gene3.adjustedPValue(), 0.01);
    }

    @Test
    public void testReadDEGsWithNAValues() throws IOException {
        try (FileWriter writer = new FileWriter(degsFilePath, true)) {
            writer.write("GeneD,NA,NA\n");
        }

        List<GeneRecord> geneRecords = fileParser.readDEGs(degsFilePath, 1);
        assertEquals(3, geneRecords.size());
    }

    @Test
    public void testReadPathwaysWithEntrezGeneType() throws IOException {
        Map<String, PathwayRecord> pathways = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, 1, "Entrez");
        assertEquals(2, pathways.size());

        PathwayRecord pathway1 = pathways.get("P1");
        assertNotNull(pathway1);
        assertEquals("Pathway One", pathway1.description());
        assertTrue(pathway1.geneIDs().contains("12345"));

        PathwayRecord pathway2 = pathways.get("P2");
        assertNotNull(pathway2);
        assertEquals("Pathway Two", pathway2.description());
        assertTrue(pathway2.geneIDs().contains("67890"));
    }

    @Test
    public void testReadPathwaysWithGeneSymbolGeneType() throws IOException {
        Map<String, PathwayRecord> pathways = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, 1, "Gene_symbol");
        assertEquals(2, pathways.size());

        PathwayRecord pathway1 = pathways.get("P1");
        assertNotNull(pathway1);
        assertEquals("Pathway One", pathway1.description());
        assertTrue(pathway1.geneIDs().contains("GeneA"));

        PathwayRecord pathway2 = pathways.get("P2");
        assertNotNull(pathway2);
        assertEquals("Pathway Two", pathway2.description());
        assertTrue(pathway2.geneIDs().contains("GeneB"));
    }

    @Test
    public void testReadPathwaysWithEnsemblGeneType() throws IOException {
        Map<String, PathwayRecord> pathways = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, 1, "Ensembl");
        assertEquals(2, pathways.size());

        PathwayRecord pathway1 = pathways.get("P1");
        assertNotNull(pathway1);
        assertEquals("Pathway One", pathway1.description());
        assertTrue(pathway1.geneIDs().contains("ENSG000001"));

        PathwayRecord pathway2 = pathways.get("P2");
        assertNotNull(pathway2);
        assertEquals("Pathway Two", pathway2.description());
        assertTrue(pathway2.geneIDs().contains("ENSG000002"));
    }

    @Test
    public void testReadPathwaysWithInvalidGeneType() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, 1, "InvalidType");
        });
    }

    private void createTestDEGsFile() throws IOException {
        try (FileWriter writer = new FileWriter(degsFilePath)) {
            writer.write("Gene,LogFoldChange,AdjustedPValue\n"); // Header
            writer.write("GeneA,1.5,0.05\n");
            writer.write("GeneB,-2.0,0.01\n");
            writer.write("GeneC,0.8,0.15\n");
        }
    }

    private void createTestPathwaysFile() throws IOException {
        try (FileWriter writer = new FileWriter(pathwaysFilePath)) {
            writer.write("PathwayID,Entrez,GeneSymbol,Ensembl\n"); // Header
            writer.write("P1,12345,GeneA,ENSG000001\n");
            writer.write("P2,67890,GeneB,ENSG000002\n");
        }
    }

    private void createTestHSAPathwaysFile() throws IOException {
        try (FileWriter writer = new FileWriter(hsaPathwaysFilePath)) {
            writer.write("PathwayID,Description\n"); // Header
            writer.write("P1,Pathway One\n");
            writer.write("P2,Pathway Two\n");
        }
    }
} //
