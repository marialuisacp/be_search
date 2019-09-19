package org.deeplearning4j.examples.nlp.word2vec;


import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

//import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.plot.BarnesHutTsne;

import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.scanner.Scanner;

import org.deeplearning4j.arbiter.util.ClassPathResource;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
import org.deeplearning4j.berkeley.Pair;

public class TSNEDataVisualization {
	
	private static Logger log = LoggerFactory.getLogger(TSNEDataVisualization.class);
	
	public static void getTSNE() throws IOException {        
		int iterations = 100;
        //create an n-dimensional array of doubles
        DataTypeUtil.setDTypeForContext(DataBuffer.Type.DOUBLE);
        List<String> cacheList = new ArrayList<>(); //cacheList is a dynamic array of strings used to hold all words

        //STEP 2: Turn text input into a list of words
        log.info("Load & Vectorize data....");
//        ClassPathResource resource = new ClassPathResource("/var/www/html/workspace/biible_server/texts/dataText.txt");
//        File wordFile = resource.getFile();   //Open the file        
        

        Word2Vec vec = WordVectorSerializer.readWord2VecModel("models/trainText.txt");
//        
        String localPath = System.getProperty("user.dir");    
    	String filePath = localPath + "/models/test_model_tsne.txt";
    	
    	Files.deleteIfExists(Paths.get(filePath));
    	File emptyFile = new File(filePath);
    	emptyFile.createNewFile();
	
        String[] words = {"jesus", "rei", "messias", "noé", "diabo", "conhecimento", "reino", "cordeiro", "vivo", "nascimento", "morte"};
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(filePath));

		int j = 0;
		
		for (j = 0; j < words.length; j++) {
			String line = "";
			double[] wordVector = vec.getWordVector(words[j]);
			
			if(vec.getWordVector(words[j]) != null) {
				int i = 0;
				line += words[j] + " ";
		        for (i = 0; i < wordVector.length; i++) {
		        	line += (wordVector[i] + " ");
		        }
		        line += ("\n");
		        buffWrite.append(line);
			}
		}
		buffWrite.close();
           
		File model = new File(filePath);
        Pair<InMemoryLookupTable, VocabCache> vectors = WordVectorSerializer.loadTxt(model);
        VocabCache cache = vectors.getSecond();
        INDArray weights = vectors.getFirst().getSyn0();

        for(int i = 0; i < cache.numWords(); i++)   //seperate strings of words into their own list
            cacheList.add(cache.wordAtIndex(i));

        //STEP 3: build a dual-tree tsne to use later
        log.info("Build model....");
        BarnesHutTsne tsne = new BarnesHutTsne.Builder()
                .setMaxIter(iterations).theta(0.5)
                .normalize(false)
                .learningRate(500)
                .useAdaGrad(false)
//                .usePca(false)
                .build();

        //STEP 4: establish the tsne values and save them to a file
        log.info("Store TSNE Coordinates for Plotting....");
        String outputFile = "models/test_tSNEmodel.csv";
        ((java.io.File) new File(outputFile)).getParentFile().mkdirs();
        tsne.plot(weights,2,cacheList,outputFile);
        //This tsne will use the weights of the vectors as its matrix, have two dimensions, use the words strings as
        //labels, and be written to the outputFile created on the previous line
	}
	
	public static void main(String args[]) throws IOException  {			
		getTSNE();
	}
}
