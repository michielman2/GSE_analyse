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
**Apache Commons** CSV library (for reading and writing CSV files)<br>
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
genes.csv/tsv, this file should contain all the genes with the following collumns,<br>
-GeneSymbol<br>
-Log fold change<br>
-Adjusted p-value<br>

Pathways.csv/tsv, this file should contain pathways with the following collumns,<br>
-Pathway ID<br>
-Entrez ID<br>
-GeneSymbol<br>
-Ensembl ID<br>

Hsa pathway csv/tsv, this file contain the description of the pathways <br>

### How to run 
Here is an example of the most basic command line argument when running the program: <br>
```
java -jar .\build\libs\GSEA_project-1.0-SNAPSHOT-all.jar -g example_data/degs_smokers.tsv -pf example_data/pathways.csv -pd example_data/hsa_pathways.csv -gid Entrezid 
```
| Argument       | Description                             | Required |
|----------------|---------------------------------------|----------------|
| `-g`           | The gene input file.                   | Yes      |
| `-pf`       | The pathways file containing the pathways.           | Yes      |
| `-pd`       | The pathways description file.          | Yes      |
| `-gid`      | The gene ID format used, such as "Entrez, Gene_symbol, Ensembl".  | Yes |
| `-t`        | The cutoff value for the adjusted P-Value for a gene to be seen as a DEG. Default is 0.05. | No|
| `-pn`       | The specific pathwayID you want to show. Is left empty, all enrichment table and gsea results will be shown. If filled in  `-no_pathways`, it will be left empty. | No| 
| `-h`        | The lenght of your header, default value is 1 | No |
| `--boxplot` | Gives a boxplot of the enrichment score | No |
| `--scatterplot`| Gives a scatterplot of the enrichment score | No |

### Example<br>
When running the main the application will procces the deg and pathways csv files. When given a pathway it will return a table contain all the information about that pathway.<br>
For example, when giving the hsa04330 pathway as input, the output should be like this:<br>

```
Notch signaling pathway (hsa04330)
 | D    | D*   | Sum
 --------------------
 C    | 18   | 44   | 62
 C*   | 2886 | 923  | 3809
 Sum  | 2904 | 18926 | 21830
```
