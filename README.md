## Overview <br>
This java tool analyses DEGs and genes in Pathways to find out which DEGs are present in a given pathway. <br>
The tool is capable of given a table for a pathway based on the amount of total genes and DEGs absent and present in a given pathway of the users liking.<br>

### Features
**DEG proccesing** - Reads and procceses genes from a csv file <br>
**Pathways proccesing** - Reads and procceses pathways from a csv file <br>
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
```
./gradlew build
```
#### Run the project <br>
```
./gradlew run

```
The data is located in the example data folder.<br>

### Input files<br>
DEGs csv, this file should contain DEGS with the following collumns,<br>
-GeneSymbol<br>
-Log fold change<br>
-Adjusted p-value<br>

Pathways csv, this file should contain pathways with the following collumns,<br>
-Pathway ID<br>
-Entrez ID<br>
-GeneSymbol<br>
-Ensembl ID<br>

Hsa pathway csv, this file contain the description of the pathways <br>


### Example<br>
When running the main the application will procces the deg and pathways csv files. When given a pathway it will return a table contain all the information about that pathway.<br>
For example, when giving the hsa04330 pathway as input, the output should be like this:<br>

Notch signaling pathway (hsa04330)
 | D    | D*   | Sum
 --------------------
 C    | 18   | 44   | 62
 C*   | 2886 | 923  | 3809
 Sum  | 2904 | 18926 | 21830

### Future implementations<br>
- Adding the math and statistics steps to our programme.<br>
- Adding the visualisation steps to our programme<br>
- Adding unit testing.<br>
