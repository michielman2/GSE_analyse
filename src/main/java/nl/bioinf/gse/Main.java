package nl.bioinf.gse;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The main entry point for the Gene Set Enrichment Analysis application.
 * This class parses command-line arguments, reads input files, performs GSEA analysis,
 * and generates output including enrichment tables, boxplots, and scatter plots.
 */
public class Main {

    /**
     * The main method to run the GSEA application.
     *
     * @param args Command-line arguments specifying input files, options, and parameters.
     */
    public static void main(String[] args) {
        // Create an instance of CommandlineProcessor to handle command-line arguments
        CommandlineProcessor commandlineProcessor = new CommandlineProcessor();

        // Parse command-line arguments
        int exitCode = new CommandLine(commandlineProcessor).execute(args);

        // Validate the exit code to ensure arguments are parsed successfully
        if (exitCode != 0) {
            System.err.println("Failed to parse command line arguments.");
            System.exit(exitCode);
        }

        // Retrieve parsed command-line options
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

        // Validate input files and retrieve their paths
        String degsFilePath = geneFile.getAbsolutePath();
        String pathwaysFilePath = pathwayFile.getAbsolutePath();
        String hsaPathwaysFilePath = pathwayDescFile.getAbsolutePath();

        // Initialize the FileParser to process input files
        FileParser fileParser = new FileParser();

        try {
            // Read differentially expressed genes (DEGs) and pathway data
            List<GeneRecord> geneRecords = fileParser.readDEGs(degsFilePath, headerLength);
            Map<String, PathwayRecord> pathwayRecords = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, headerLength, geneId);

            // Perform Gene Set Enrichment Analysis (GSEA)
            GSEAFactory gseaFactory = new GSEAFactory();
            List<GSEARecord> gseaResults = gseaFactory.performGSEA(
                    TableBuilder.totalDEGS(geneRecords, treshold),
                    TableBuilder.totalGenes(geneRecords, treshold),
                    pathwayRecords,
                    geneRecords,
                    treshold
            );

            // Generate terminal output for GSEA results
            TerminalOutput.printGSEAResults(gseaResults, pathwayRecords, pathwayName);

            // Generate boxplot if specified
            if (!"no_boxplot".equals(boxPlot)) {
                Boxplot.showChart(gseaResults, savePlot, boxPlot);
            }

            // Generate scatter plot if specified
            if (!"no_scatterplot".equals(scatterPlot)) {
                ScatterPlot.showChart(gseaResults, savePlot, scatterPlot);
            }

        } catch (IOException e) {
            // Handle exceptions related to file reading
            System.err.println("Error reading CSV files: " + e.getMessage());
        }
    }
}
