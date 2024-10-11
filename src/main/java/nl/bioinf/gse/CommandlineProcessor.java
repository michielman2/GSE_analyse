package nl.bioinf.gse;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.util.Arrays;

@CommandLine.Command(name="VCFfilter", version="VCFfilter 0.1", mixinStandardHelpOptions = true)
public class CommandlineProcessor implements Runnable{

    @Parameters(index="0", paramLabel = "<output>", description = "Output VCF file name")
    private String outputVCF;

    // FIXME: should be required, but is ignored
    @Parameters(index="1..*", description = "Input VCF file(s)")
    private String[] inputVCF;

    @Option(names = {"-f", "--filter-value"}, description = "Filter value")
    private String filterOptions;

    @Option(names = {"-v"}, description = "Verbose logging")
    private boolean[] verbose;

    @Override
    public void run() {
        System.out.println(outputVCF);
        if (filterOptions != null) {
            System.out.println(filterOptions);
        }


    }
}
