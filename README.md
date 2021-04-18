# Be - RI (Information Retrieval)

### About

This project is using:

* Apache Lucene
* DeepLearning4j to Word2Vec

### Application Setup

First step is the installation of java jre e jdk.
After, install Maven. 

* Is important to install java in version 8.

```
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home
```

If you want to check what version of Maven is running on project, you can run:

```
mvn -v
```

The data paths below, need to exists on repository with the files to be indexing for Lucene. The download of files can be done for the link: [link](https://drive.google.com/drive/folders/170MP2rcKuvjubUTEfeIfTVXFNYPlhaCm). 

* data_pt/
* data_en/

The paths below need exists too and in there will be created the Lucene indexes.

* index_en/
* index_pt/

The texts to be indexed for Word2Vec have to be in the path:

* texts/

### Running

To run application:

```
mvn spring-boot:run
```

ou 

```
mvn clean package
```


