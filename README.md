## Overview 
This java tool analyses DEGs and genes in Pathways to find out which DEGs are present in a given pathway. 
The tool is capable of given a table for a pathway based on the amount of total genes and DEGs absent and present in a given pathway of the users liking.

### Features
**DEG proccesing** - Reads and procceses genes from a csv file
**Pathways proccesing** - Reads and procceses pathways from a csv file
**Gene Counting** - Calculates the genes in a given pathway. Total genes, genes in the pathway, DEGs in the pathway, non DEGs in the pathway.
**Creating pathway table** - Creates a table based on the values above.

### Requirements 
**Java 17** (Make sure you have JDK 17 installed)
**Apache Commons** CSV library (for reading and writing CSV files)
**Gradle** (for building and running the project)


### Setup and installation 
```
git clone git@github.com:michielman2/GSE_analyse.git
```
**Confige the JDK version**
Go to File > Project Structure > Project Settings > Project.
Set the Project SDK to JDK 17.

**Install gradle**
Download gradle from the official website 
Alternatively, if you're using IntelliJ, Gradle is managed automatically as part of the project dependencies.

##### Build the project 
```
./gradlew build
```
#### Run the project 
```
./gradlew run

```
The data is located in the example data folder.

### Input files
DEGs csv, this file should contain DEGS with the following collumns,
-GeneSymbol
-Log fold change
-Adjusted p-value

Pathways csv, this file should contain pathways with the following collumns,
-Pathway ID
-Entrez ID
-GeneSymbol
-Ensembl ID

Hsa pathway csv, this file contain the description of the pathways 


### Example
When running the main the application will procces the deg and pathways csv files. When given a pathway it will return a table contain all the information about that pathway.
For example, when giving the hsa04330 pathway as input, the output should be like this:
Notch signaling pathway (hsa04330)
 | D    | D*   | Sum
 --------------------
 C    | 18   | 44   | 62
 C*   | 2886 | 923  | 3809
 Sum  | 2904 | 18926 | 21830

### Future implementations
- Adding the math and statistics steps to our programme.
- Adding the visualisation steps to our programme
- Adding unit testing.
