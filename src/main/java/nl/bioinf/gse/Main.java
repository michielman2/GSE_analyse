package nl.bioinf.gse;

import picocli.CommandLine;

public class Main {
    PathwayAnalysis analysis;
    CSVParser csvParser;
    ChartFactory chartFactory;


    public static void main(String[] args) {
        int exitCode = new CommandLine(new CommandlineProcessor()).execute(args);
        System.exit(exitCode);
    }
}