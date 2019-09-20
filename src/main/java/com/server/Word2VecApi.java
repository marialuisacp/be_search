package com.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.biible.lucene.Term;

import org.deeplearning4j.examples.nlp.word2vec.TSNEDataVisualization;
import org.deeplearning4j.examples.nlp.word2vec.TermDimension;
import org.deeplearning4j.examples.nlp.word2vec.Word2VecUptrainingExample;
import org.deeplearning4j.models.word2vec.Word2Vec;

public class Word2VecApi {
	public String filePath;
	public Word2Vec vec;
	
	public Word2VecApi() throws IOException {
		Word2VecUptrainingExample.setStopWordsPt();

	    this.vec = Word2VecUptrainingExample.importModelWikipedia();
	}
	
	public Word2VecApi(boolean isTrainable, boolean isExternalText) throws IOException {
		String localPath = System.getProperty("user.dir");
		boolean isBible = !isExternalText;
		Word2VecUptrainingExample.setStopWordsPt();
		System.out.println("Arquivo externo: " + isExternalText);
		if(isExternalText) {
			this.filePath = localPath + "/texts/dataText.txt";
		} else {
			this.filePath = localPath + "/texts/biblia_livre_pt.txt";
		}
		System.out.println("filePath: " + filePath);
		Word2VecUptrainingExample.setStopWordsPt();

    	if(isTrainable) {
    		this.vec = Word2VecUptrainingExample.training(filePath, isBible);
    	} else {
    		if(isBible) {
    			this.vec = Word2VecUptrainingExample.importModelBible();
    		} else {
    			this.vec = Word2VecUptrainingExample.importModelGeneric();
    		}
    	}
	}
	public List<String> getSimilarWords(String term, int sizeNeighborhood) throws IOException {
      List<String> words_result = Word2VecUptrainingExample.getSimilarWordsWithoutStopWords(term, sizeNeighborhood, this.vec);
      return words_result;
	}
	
	public double getSimilarity(String term_1, String term_2) {        
      double cosSim = Word2VecUptrainingExample.getSimilarity(term_1, term_2, this.vec);
      return cosSim;
	}
	
	public Term getMostSimilarWords(String term, List<String> arrayTerms, int size) {
		return Word2VecUptrainingExample.getMostSimilarFromArray(term, arrayTerms, size, this.vec);
	}
	
	
	public double[] getVectorByTerm(String term) {
		return this.vec.getWordVector(term);
	}
	
	public Word2Vec getModel() {
		return this.vec;
	}
	
	public ArrayList<TermDimension> getTSNE(words, vec) {
		return TSNEDataVisualization.getTSNE(words, this.vec);
	}
	
}
