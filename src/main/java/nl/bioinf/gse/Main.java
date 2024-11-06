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
        boolean boxPlot = commandlineProcessor.getBoxPlot();
        boolean scatterPlot = commandlineProcessor.getScatterPlot();
        double treshold = commandlineProcessor.getTreshold();

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
            Map<String, PathwayRecord> pathwayRecords = fileParser.readPathways(pathwaysFilePath, hsaPathwaysFilePath, headerLength);

            // Perform GSEA analysis
            GSEAFactory gseaFactory = new GSEAFactory();
            List<GSEARecord> gseaResults = gseaFactory.performGSEA(TableBuilder.totalDEGS(geneRecords, treshold),TableBuilder.totalGenes(geneRecords, treshold),pathwayRecords, geneRecords, treshold);

            // Output the results
            for (GSEARecord result : gseaResults) {
                System.out.println("Pathway: " + result.pathwayID());
                System.out.println("P-Value: " + result.pValue());
                System.out.println("Adjusted P-Value: " + result.adjustedPValue());
                System.out.println("Enrichment Score: " + result.enrichmentScore());
                System.out.println("Expected DEGs: "+ result.expectedDEGs());
                System.out.println("Observed DEGs: "+ result.observedDEGs());
                System.out.println("-----------------------------------");
            }

            // Print the table below everything else
//            for (GSEARecord result : gseaResults) {
//                String table = TableBuilder.tableBuilder(geneRecords, pathwayRecords, result.pathwayID(), treshold);
//                System.out.println("Table for Pathway: " + result.pathwayID());
//                System.out.println(table);
//                System.out.println(); // Print an empty line for better separation
//            }
            if (boxPlot){Boxplot.showChart(gseaResults);}
            if (scatterPlot){ScatterPlot.showChart(gseaResults);}

        } catch (IOException e) {
            System.err.println("Error reading CSV files: " + e.getMessage());
        }
    }
}
