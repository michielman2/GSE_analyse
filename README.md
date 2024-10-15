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
