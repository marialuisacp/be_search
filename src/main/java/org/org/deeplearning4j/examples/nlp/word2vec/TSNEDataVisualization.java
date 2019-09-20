package org.deeplearning4j.examples.nlp.word2vec;


import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
import org.deeplearning4j.berkeley.Pair;

public class TSNEDataVisualization {
	
	private static Logger log = LoggerFactory.getLogger(TSNEDataVisualization.class);
	
	public static ArrayList<TermDimension> getTSNE(ArrayList<String> words) throws IOException {        
		int iterations = 100;
        DataTypeUtil.setDTypeForContext(DataBuffer.Type.DOUBLE);
        List<String> cacheList = new ArrayList<>(); 
        String localPath = System.getProperty("user.dir");    

        log.info("Load & Vectorize data....");
        // Word2Vec vec = WordVectorSerializer.readWord2VecModel(localPath + "/models/trainText.txt");
        Word2Vec vec = WordVectorSerializer.loadFullModel("models/train_model_wikipedia.txt");
        
    	String filePath = localPath + "/models/test_model_tsne.txt";
    	
    	Files.deleteIfExists(Paths.get(filePath));
    	File emptyFile = new File(filePath);
    	emptyFile.createNewFile();
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(filePath));
        int hasWord = 0;
		
		for (String word : words) {
			String line = "";
			double[] wordVector = vec.getWordVector(word);
			
			if(vec.getWordVector(word) != null) {
				hasWord = 1;
				int i = 0;
				line += word + " ";
		        for (i = 0; i < wordVector.length; i++) {
		        	line += (wordVector[i] + " ");
		        }
		        line += ("\n");
		        buffWrite.append(line);
			}
		}
		buffWrite.close();
		
		ArrayList<TermDimension> termsDim = new ArrayList<TermDimension>();
		if(hasWord == 1) {
			File model = new File(filePath);
	        Pair<InMemoryLookupTable, VocabCache> vectors = WordVectorSerializer.loadTxt(model);
	        VocabCache cache = vectors.getSecond();
	        INDArray weights = vectors.getFirst().getSyn0();
	
	        for(int i = 0; i < cache.numWords(); i++)
	            cacheList.add(cache.wordAtIndex(i));
	
	        log.info("Build model....");
	        BarnesHutTsne tsne = new BarnesHutTsne.Builder()
	                .setMaxIter(iterations).theta(0.5)
	                .normalize(false)
	                .learningRate(500)
	                .useAdaGrad(false)
	                //.usePca(false)
	                .build();
	
	        log.info("Store TSNE Coordinates for Plotting....");
	        String outputFile = "models/test_tSNEmodel.csv";
	        ((java.io.File) new File(outputFile)).getParentFile().mkdirs();
	        tsne.plot(weights, 2, cacheList, outputFile);
	        
	        BufferedReader csvReader = new BufferedReader(new FileReader(outputFile));
	        String row;
	        
	        while ((row = csvReader.readLine()) != null) {
	            String[] data = row.split(",");
	            TermDimension termDimension = new TermDimension();
	            
	            termDimension.x = Double.parseDouble(data[0]);
	            termDimension.y = Double.parseDouble(data[1]);
	            termDimension.term = data[2];
	            
	            termsDim.add(termDimension);
	        }
	        
	        csvReader.close();
		} else {
			TermDimension termDimension = new TermDimension();
			termDimension.x = 0;
            termDimension.y = 0;
            termDimension.term = "error";
            termDimension.error = true;
            
            termsDim.add(termDimension);
		}
		
		return termsDim;
	}
}
