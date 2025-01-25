## Overview <br>
This java tool analyses DEGs and genes in Pathways to find out which DEGs are present in a given pathway. <br>
The tool is capable of given a table for a pathway based on the amount of total genes and DEGs absent and present in a given pathway of the users liking.<br>

### Features
**DEG proccesing** - Reads and procceses genes from a csv/tsv file <br>
**Pathways proccesing** - Reads and procceses pathways from a csv/tsv file <br>
**Gene Counting** - Calculates the genes in a given pathway. Total genes, genes in the pathway, DEGs in the pathway, non DEGs in the pathway.<br>
**Creating pathway table** - Creates a table based on the values above.<br>

### Requirements <br>
**Java 17** (Make sure you have JDK 17 installed)<br>
**Apache Commons** CSV/TSV library (for reading and writing CSV files)<br>
**Gradle** (for building and running the project)<br>


### Setup and installation <br>
```
git clone git@github.com:michielman2/GSE_analyse.git
```
**Confige the JDK version**<br>
Go to File > Project Structure > Project Settings > Project.<br>
Set the Project SDK to JDK 17.<br>

**Install gradle**<br>
Download gradle from the official website <br>
Alternatively, if you're using IntelliJ, Gradle is managed automatically as part of the project dependencies.<br>

##### Build the project <br>
When first downloading the project, click on the gradle button in the top right of the intellij IDE. <br>
Then hit the "reload all gradle projects" button. after that type in the terminal: <br>
```
./gradlew clean build    
```


### Input files<br>
genes.csv/tsv, The name of the file doesn't matter <br>
This file should contain all the genes with the following collumns, **in this order** ,<br>
-GeneSymbol<br>
-Log fold change<br>
-Adjusted p-value<br>

Pathways.csv/tsv, this file should contain pathways with the following collumns, **in this order**,<br>
-Pathway ID<br>
-Entrez ID<br>
-GeneSymbol<br>
-Ensembl ID<br>

Hsa pathway csv/tsv, this file contain the description of the pathways <br>
-Pathway ID <br>
-Pathway description <br>

### How to run 
Here is an example of the most basic command line argument when running the program: <br>
```
java -jar .\build\libs\GSEA_project-1.0-SNAPSHOT-all.jar -g example_data/degs_smokers.tsv -pf example_data/pathways.csv -pd example_data/hsa_pathways.csv -gid gene_symbol -h 1   --boxplot enrichmentscore
```
| Argument       | Description                             | Required |
|----------------|---------------------------------------|----------------|
| `-g`           | The gene input file.                   | Yes      |
| `-pf`       | The pathways file containing the pathways.           | Yes      |
| `-pd`       | The pathways description file.          | Yes      |
| `-gid`      | The gene ID format, should be 'gene_symbol'.  | Yes |
| `-t`        | The cutoff value for the adjusted P-Value for a gene to be seen as a DEG. Default is 0.05. | No|
| `-pn`       | The specific pathwayID you want to show. Is left empty, all enrichment table and gsea results will be shown. If filled in  `-no_pathways`, it will be left empty. | No| 
| `-h`        | The lenght of your header, default value is 1 | No |
| `-png`      | Select if you want to save the graph to a png | No |
| `--boxplot` | Gives a boxplot of the "enrichmentscore", "pvalue", "adjusted_pvalue", after `--boxplot`, type one of these 3 option after a space | No |
| `--scatterplot`| Gives a scatterplot of the "enrichmentscore" or "avglogfoldchange", after `--scatterplot`, type of these 2 options after a space | No |

### Example<br>
When running the main the application will procces the deg and pathways csv files. When given a pathway it will return a table contain all the information about that pathway.<br>
Given these commandline arguments(pathway hsa04330, boxplot of enrichmentscore and scatterplot of logfoldchange): <br>
```java -jar .\build\libs\GSEA_project-1.0-SNAPSHOT-all.jar -g .\example_data\degs_smokers.tsv -pf .\example_data\pathways.csv -pd .\example_data\hsa_pathways.csv -gid gene_symbol  --boxplot enrichmentscore  -png --scatterplot avglogfoldchange -pn``` <br>
The output should look like this: <br>

Pathway: Notch signaling pathway <br>
KEGG PathwayID: hsa04330 <br>
P-Value: 0.018301675473708756 <br>
Adjusted P-Value: 1.0 <br>
Enrichment Score: 2.2078377933746425 <br>
Expected DEGs: 8.545788062649923 <br>
Observed DEGs: 15.0 <br>
Average LogFoldChange: 0.05528164677713069 <br>

Also the following graphs will be shows and you can choose wether to save them to png or not: <br>
![table of pathway](https://github.com/michielman2/GSE_analyse/raw/main/images/pathway%20table.png) <br>
This shows the table of the selected pathway <br>
![boxplot enrichmentscore](https://github.com/michielman2/GSE_analyse/raw/main/images/boxplot_en.png) <br>
This show the boxplot that was selected showing the enrichmentscore of all the pathways <br>
![scatterplot logfoldchange](https://github.com/michielman2/GSE_analyse/blob/main/images/logfoldchange_pathways.png) <br>
This shows the scatterplot of the 20 pathways with the highest logfolchange<br>

### Test Suite Documentation <br>
JUnit tests has been used different methods, these are shown below. <br>

- **Number of tests**: 16 tests <Br>
- **Test classes**: The tests are in 2 different files "FileParserTest" and "GSEAtest". <br>
- **what has been tested**: <br>
**FileParserTest**: <br>
  - testReadDEGs: This test checks if the FileParser correctly reads the DEGs. <br>
  - testReadDEGsWithNAValues:  This test checks how the FileParser handles the case where some gene values are marked as "NA". <br>
  - testReadPathwaysWithEntrezGeneType: This test checks if the parser can correctly handle and read pathways with Entrez Gene IDs. <br>
  - testReadPathwaysWithGeneSymbolGeneType: This test checks if the parser works when using Gene Symbols to match pathways. <br>
  - testReadPathwaysWithEnsemblGeneType: This test checks if the parser works when using Ensembl Gene IDs to match pathways. <Br>
  - testReadPathwaysWithInvalidGeneType:  This test ensures that the parser throws an error if an invalid gene type (like "InvalidType") is provided. <br>
**GSEAtest**: <br>
  - testCalculateEnrichmentScore: This test checks if the method that calculates enrichment scores works properly. <Br>
  - testCalculatePValue: This test checks if the method for calculating p-values works correctly. It runs with example values and verifies that the p-value is correct. <br>
  - testHyperGeometricTest: This test checks if the hypergeometric test for probability calculation works correctly. It makes sure the returned value is between 0 and 1. <br>
  - testCalculateBinomialCoefficient: This test checks if the method for calculating the binomial coefficient works. <Br>
  - testCalculateExpectedDEGs: This test checks if the method for calculating expected DEGs works properly. <br>
  - testAdjustPValue: This test checks if the method that adjusts p-values works correctly. It tests with p-values and ensures that they are correctly capped at 1 <br>
  - Boundry cases: These tests check special cases where there are no DEGs in a pathway, no genes in a pathway, or no total DEGs. <br>
- **Results**: All tests passed. <br>


### Support <br>
When using our programme, if you encounter any bugs, please contact us via mail (see Authors and Acknowledgments) <br>
### Authors and Acknowledgments <br>
Devolepers: <br>
* Michiel Meeuwisse M.D.meeuwise@st.hanze.nl    <br>
* Daan Roorda H.A.roorda@st.hanze.nl <br>
Data contributed by: <br>
* Marcel Kempenaar: m.kempenaar@pl.hanze.nl

### License <br>
No license applied for this project <br>

### Changelog <br>
2024-11-6 **V1** First version of the programme




