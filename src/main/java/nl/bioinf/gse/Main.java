package nl.bioinf.gse;

import picocli.CommandLine;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    /**
     * Main method to parse commandline arguments and execute the GSEA pipeline.
     * @param args Commandline arguments to be parsed by the CommandlineProcessor.
     */
    public static void main(String[] args) {
        // Create an instance of CommandlineProcessor
        CommandlineProcessor commandlineProcessor = new CommandlineProcessor();

        // Parse commandline arguments and retrieve the exit code
        int exitCode = new CommandLine(commandlineProcessor).execute(args);

        // Retrieve commandline arguments after parsing
        File geneFile = commandlineProcessor.getGeneFile();
        File pathwayFile = commandlineProcessor.getPathwayFile();
        File pathwayDescFile = commandlineProcessor.getPathwayDescFile();
        String geneId = commandlineProcessor.getGeneId();
        int headerLength = commandlineProcessor.getHeaderLength();
        String pathwayName = commandlineProcessor.getPathwayName();
        String boxPlot = commandlineProcessor.getBoxPlot();
        String scatterPlot = commandlineProcessor.getScatterPlot();
        double treshold = commandlineProcessor.getTreshold();
        boolean savePlot = commandlineProcessor.getSavePlot();

        // Validate exit code
        if (exitCode != 0) {
            System.err.println("Failed to parse command line arguments.");
            System.exit(exitCode);
        }

        // Convert gene, pathway, and description files to absolute paths so all location can use it
        String degsFilePath = geneFile.getAbsolutePath();
        String pathwaysFilePath = pathwayFile.getAbsolutePath();
        String hsaPathwaysFilePath = pathwayDescFile.getAbsolutePath();


        FileParser fileParser = new FileParser();

        try {
            // Read DEGs from the specified file
            List<GeneRecord> geneRecords = fileParser.readDEGs(degsFilePath, headerLength);
            // Read pathways and descriptions from the pathway files
            Map<String, PathwayRecord> pathwayRecords = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, headerLength, geneId);

            // Perform GSEA analysis using parsed data
            GSEAFactory gseaFactory = new GSEAFactory();
            List<GSEARecord> gseaResults = gseaFactory.performGSEA(
                    TableBuilder.totalDEGS(geneRecords, treshold),
                    TableBuilder.totalGenes(geneRecords, treshold),
                    pathwayRecords, geneRecords, treshold
            );

            // Display enrichment tables and GSEA results for specified pathways
            TerminalOutput.printEnrichmentTables(gseaResults, pathwayRecords, pathwayName, geneRecords, treshold);
            TerminalOutput.printGSEAResults(gseaResults, pathwayRecords, pathwayName);

            // Generate boxplot if selected
            if (boxPlot != "no_boxplot") {
                Boxplot.showChart(gseaResults, savePlot, boxPlot);
            }
            // Generate scatterplot if selected
            if (scatterPlot != "no_scatterplot") {
                ScatterPlot.showChart(gseaResults, savePlot, scatterPlot);
            }

        } catch (IOException e) {
            // Print potential error message
            System.err.println("Error reading CSV files: " + e.getMessage());
        }
    }
}
