package nl.bioinf.gse;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Create an instance of CommandlineProcessor
        CommandlineProcessor commandlineProcessor = new CommandlineProcessor();

        // temp
        int exitCode = new CommandLine(commandlineProcessor).execute(args);

        // Use commandlineProcessor instance to access the parsed options
        File geneFile = commandlineProcessor.getGeneFile();
        File pathwayFile = commandlineProcessor.getPathwayFile();
        File pathwayDescFile = commandlineProcessor.getPathwayDescFile();
        String geneId = commandlineProcessor.getGeneId();
        int headerLength = commandlineProcessor.getHeaderLength();
        String pathwayName = commandlineProcessor.getPathwayName();
        String boxPlot = commandlineProcessor.getBoxPlot();
        boolean scatterPlot = commandlineProcessor.getScatterPlot();
        double treshold = commandlineProcessor.getTreshold();
        boolean savePlot = commandlineProcessor.getSavePlot();

        // Validate exit code before proceeding
        if (exitCode != 0) {
            System.err.println("Failed to parse command line arguments.");
            System.exit(exitCode);
        }

        // Check if geneFile is set correctly before using it
        String degsFilePath = geneFile.getAbsolutePath();
        String pathwaysFilePath = pathwayFile.getAbsolutePath();
        String hsaPathwaysFilePath = pathwayDescFile.getAbsolutePath();

        FileParser fileParser = new FileParser();

        try {
            List<GeneRecord> geneRecords = fileParser.readDEGs(degsFilePath, headerLength);
            Map<String, PathwayRecord> pathwayRecords = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, headerLength, geneId);

            // Perform GSEA analysis
            GSEAFactory gseaFactory = new GSEAFactory();
            List<GSEARecord> gseaResults = gseaFactory.performGSEA(TableBuilder.totalDEGS(geneRecords, treshold),TableBuilder.totalGenes(geneRecords, treshold),pathwayRecords, geneRecords, treshold);

            TerminalOutput.printEnrichmentTables(gseaResults,pathwayRecords, pathwayName, geneRecords, treshold);
            TerminalOutput.printGSEAResults(gseaResults, pathwayRecords,pathwayName);

            if (boxPlot != "no_boxplot"){Boxplot.showChart(gseaResults, savePlot, boxPlot);}
            if (scatterPlot){ScatterPlot.showChart(gseaResults, savePlot);}

        } catch (IOException e) {
            System.err.println("Error reading CSV files: " + e.getMessage());
        }
    }
}
