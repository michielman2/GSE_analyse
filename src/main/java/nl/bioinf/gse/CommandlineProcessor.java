package nl.bioinf.gse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

public class CommandlineProcessor implements Callable<Integer> {

    // Required options
    @Option(names = {"-g", "--genefile"}, description = "The path to the gene file to analyze.", required = true)
    private File geneFile;

    @Option(names = {"-pf", "--pathwayfile"}, description = "The path to the pathway file containing pathways.", required = true)
    private File pathwayFile;

    @Option(names = {"-pd", "--pathwaydescfile"}, description = "The path to the pathway description file.", required = true)
    private File pathwayDescFile;

    @Option(names = {"-gid", "--geneid"}, description = "The gene ID format used (options: Entrez, Ensembl, Gene_symbol).", required = true)
    private String geneId;

    // Optional options
    @Option(names = {"-t", "--treshold"}, description = "sets the cutoff for the adjusted P-value of a gene to be seen as a DEG.", defaultValue = "0.05")
    private double treshold;

    @Option(names = {"-png", "--saveplotstopng"}, description = "if used all generated plots will be saved as a png")
    private boolean savePlot = false;

    @Option(names = {"-h", "--headerlength"}, description = "The amount of lines that the header takes up in the given files.", defaultValue = "0")
    private int headerLength;

    @Option(names = {"-pn", "--pathwayname"}, description = "If used you will only get the enrichment table and the GSEA results for the given pathway. When not used you will get them for all pathways. If you use: no_pathways, it will show no enrichment tables or GSEA results.", defaultValue = "all_pathways")
    private String pathwayName;

    @Option(names = {"--boxplot"}, description = "Will generate a boxplot of the enrichment scores for all pathways when used.", defaultValue = "no_boxplot")
    private String boxplot;

    @Option(names = {"--scatterplot"}, description = "using this option will generate a scatterplot of the enrichmentscore or the avg logfoldchange and the pvalue of the top 20 pathways.", defaultValue = "no_scatterplot")
    private String scatterplot;

    @Override
    public Integer call() throws Exception {
        // Validate file existence for required files
        if (!geneFile.exists() || !pathwayFile.exists() || !pathwayDescFile.exists()) {
            System.err.println("One or more input files do not exist.");
            return 1;
        }

        // Print the options received (replace this with actual logic)
        System.out.println("Gene file: " + geneFile.getAbsolutePath());
        System.out.println("Pathway file: " + pathwayFile.getAbsolutePath());
        System.out.println("Pathway description file: " + pathwayDescFile.getAbsolutePath());
        System.out.println("Gene ID format: " + geneId);
        System.out.println("Header length: " + headerLength);
        System.out.println("boxplot: " + boxplot);
        System.out.println("scatterplot: " + scatterplot);

        if (pathwayName != null) {
            System.out.println("Analyzing specific pathway: " + pathwayName);
        } else {
            System.out.println("Analyzing all pathways.");
        }

        // Insert your pathway analysis logic here

        return 0; // Success
    }

    public File getGeneFile() {
        if (!geneFile.exists()) {
            System.err.println("The gene file does not exist");
        }
        return geneFile;
    }

    public File getPathwayFile() {
        if (!pathwayFile.exists()) {
            System.err.println("The pathway file does not exist");
        }
        return pathwayFile;
    }

    public File getPathwayDescFile() {
        if (!pathwayDescFile.exists()) {
            System.err.println("the pathway description file does not exist");
        }
        return pathwayDescFile;
    }

    public String getGeneId() {
        return geneId;
    }

    // Optional options
    public double getTreshold() {return treshold;}

    public int getHeaderLength() {
        return headerLength;
    }

    public String getPathwayName() {
        return pathwayName;
    }

    public String getBoxPlot() {
        if (boxplot == "no_boxplot" || boxplot.equalsIgnoreCase("enrichmentscore") || boxplot.equalsIgnoreCase("pvalue") || boxplot.equalsIgnoreCase("adjusted_pvalue")) {
            return boxplot;
        } else{
            throw new IllegalArgumentException("Unsupported boxplot option chosen");
        }

    }
    public String getScatterPlot() {
        if (scatterplot == "no_scatterplot" || scatterplot.equalsIgnoreCase("enrichmentscore") || scatterplot.equalsIgnoreCase("avglogfoldchange")) {
            return scatterplot;
        } else{
            throw new IllegalArgumentException("Unsupported scatterplot option chosen");
        }
    }
    public boolean getSavePlot() {return savePlot;}
}