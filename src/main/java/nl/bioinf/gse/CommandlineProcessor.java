package nl.bioinf.gse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Processes command-line arguments for a pathway analysis application.
 * This class uses the PicoCLI library to handle command-line options and
 * validates required input files and parameters.
 */
@Command(name = "CommandlineProcessor", mixinStandardHelpOptions = true, version = "1.0",
        description = "Processes command-line arguments for pathway analysis.")
public class CommandlineProcessor implements Callable<Integer> {

    // Required options
    /**
     * The path to the gene file to analyze.
     */
    @Option(names = {"-g", "--genefile"}, description = "The path to the gene file to analyze.", required = true)
    private File geneFile;

    /**
     * The path to the pathway file containing pathways.
     */
    @Option(names = {"-pf", "--pathwayfile"}, description = "The path to the pathway file containing pathways.", required = true)
    private File pathwayFile;

    /**
     * The path to the pathway description file.
     */
    @Option(names = {"-pd", "--pathwaydescfile"}, description = "The path to the pathway description file.", required = true)
    private File pathwayDescFile;

    /**
     * The gene ID format used (options: Entrez, Ensembl, Gene_symbol).
     */
    @Option(names = {"-gid", "--geneid"}, description = "The gene ID format used (options: Entrez, Ensembl, Gene_symbol).", required = true)
    private String geneId;

    // Optional options
    /**
     * The cutoff for the adjusted P-value of a gene to be considered as a DEG.
     * Default value is 0.05.
     */
    @Option(names = {"-t", "--treshold"}, description = "Sets the cutoff for the adjusted P-value of a gene to be seen as a DEG.", defaultValue = "0.05")
    private double treshold;

    /**
     * Flag indicating whether to save all generated plots as PNG files.
     */
    @Option(names = {"-png", "--saveplotstopng"}, description = "If used, all generated plots will be saved as PNG.")
    private boolean savePlot = false;

    /**
     * The number of lines that the header takes up in the given files.
     * Default value is 0.
     */
    @Option(names = {"-hl", "--headerlength"}, description = "The amount of lines that the header takes up in the given files.", defaultValue = "0")
    private int headerLength;

    /**
     * Specifies a pathway to analyze. If not provided, all pathways are analyzed.
     * Use "no_pathways" to skip enrichment tables and GSEA results.
     */
    @Option(names = {"-pn", "--pathwayname"}, description = "Analyze a specific pathway or all pathways if not specified. Use 'no_pathways' to skip enrichment tables and GSEA results.", defaultValue = "all_pathways")
    private String pathwayName;

    /**
     * Specifies whether to generate a boxplot for the enrichment scores.
     * Options: no_boxplot, enrichmentscore, pvalue, adjusted_pvalue.
     */
    @Option(names = {"--boxplot"}, description = "Generates a boxplot of the enrichment scores for all pathways.", defaultValue = "no_boxplot")
    private String boxplot;

    /**
     * Specifies whether to generate a scatterplot for the enrichment score or the avg logfoldchange.
     * Options: no_scatterplot, enrichmentscore, avglogfoldchange.
     */
    @Option(names = {"--scatterplot"}, description = "Generates a scatterplot for the enrichment score or the avg logfoldchange.", defaultValue = "no_scatterplot")
    private String scatterplot;

    /**
     * Validates input files and prints the received options.
     *
     * @return 0 if successful, 1 if validation fails.
     * @throws Exception if an error occurs during processing.
     */
    @Override
    public Integer call() throws Exception {
        if (!geneFile.exists() || !pathwayFile.exists() || !pathwayDescFile.exists()) {
            System.err.println("One or more input files do not exist.");
            return 1;
        }

        // Print options
        System.out.println("Gene file: " + geneFile.getAbsolutePath());
        System.out.println("Pathway file: " + pathwayFile.getAbsolutePath());
        System.out.println("Pathway description file: " + pathwayDescFile.getAbsolutePath());
        System.out.println("Gene ID format: " + geneId);
        System.out.println("Header length: " + headerLength);
        System.out.println("Boxplot: " + boxplot);
        System.out.println("Scatterplot: " + scatterplot);

        if (pathwayName != null) {
            System.out.println("Analyzing specific pathway: " + pathwayName);
        } else {
            System.out.println("Analyzing all pathways.");
        }

        return 0; // Success
    }

    // Getters with validation
    /**
     * Retrieves the gene file.
     * @return The gene file.
     */
    public File getGeneFile() {
        if (!geneFile.exists()) {
            System.err.println("The gene file does not exist.");
        }
        return geneFile;
    }

    /**
     * Retrieves the pathway file.
     * @return The pathway file.
     */
    public File getPathwayFile() {
        if (!pathwayFile.exists()) {
            System.err.println("The pathway file does not exist.");
        }
        return pathwayFile;
    }

    /**
     * Retrieves the pathway description file.
     * @return The pathway description file.
     */
    public File getPathwayDescFile() {
        if (!pathwayDescFile.exists()) {
            System.err.println("The pathway description file does not exist.");
        }
        return pathwayDescFile;
    }

    /**
     * Retrieves the gene ID format.
     * @return The gene ID format.
     */
    public String getGeneId() {
        return geneId;
    }

    /**
     * Retrieves the threshold value for adjusted P-value.
     * @return The threshold value.
     */
    public double getTreshold() {
        return treshold;
    }

    /**
     * Retrieves the header length.
     * @return The header length.
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * Retrieves the pathway name for analysis.
     * @return The pathway name.
     */
    public String getPathwayName() {
        return pathwayName;
    }

    /**
     * Retrieves the boxplot option.
     * @return The boxplot option.
     */
    public String getBoxPlot() {
        if (boxplot.equalsIgnoreCase("no_boxplot") || boxplot.equalsIgnoreCase("enrichmentscore") || boxplot.equalsIgnoreCase("pvalue") || boxplot.equalsIgnoreCase("adjusted_pvalue")) {
            return boxplot;
        } else {
            throw new IllegalArgumentException("Unsupported boxplot option chosen.");
        }
    }

    /**
     * Retrieves the scatterplot option.
     * @return The scatterplot option.
     */
    public String getScatterPlot() {
        if (scatterplot.equalsIgnoreCase("no_scatterplot") || scatterplot.equalsIgnoreCase("enrichmentscore") || scatterplot.equalsIgnoreCase("avglogfoldchange")) {
            return scatterplot;
        } else {
            throw new IllegalArgumentException("Unsupported scatterplot option chosen.");
        }
    }

    /**
     * Retrieves the save plot flag.
     * @return True if plots should be saved, false otherwise.
     */
    public boolean getSavePlot() {
        return savePlot;
    }
}
