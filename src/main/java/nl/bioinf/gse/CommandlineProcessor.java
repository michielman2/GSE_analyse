package nl.bioinf.gse;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

public class CommandlineProcessor implements Callable<Integer> {

    // Required options
    @Option(names = {"-g", "--genefile"}, description = "The gene file to analyze", required = true)
    private File geneFile;

    @Option(names = {"-pf", "--pathwayfile"}, description = "The pathway file containing pathways", required = true)
    private File pathwayFile;

    @Option(names = {"-pd", "--pathwaydescfile"}, description = "The pathway description file", required = true)
    private File pathwayDescFile;

    @Option(names = {"-gid", "--geneid"}, description = "The gene ID format used (e.g., Ensembl, Entrez)", required = true)
    private String geneId;

    // Optional options
    @Option(names = {"-h", "--headerlength"}, description = "The header length of files", defaultValue = "1")
    private int headerLength;

    @Option(names = {"-pn", "--pathwayname"}, description = "The specific pathway name to analyze")
    private String pathwayName;

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

        if (pathwayName != null) {
            System.out.println("Analyzing specific pathway: " + pathwayName);
        } else {
            System.out.println("Analyzing all pathways.");
        }

        // Insert your pathway analysis logic here

        return 0; // Success
    }
}