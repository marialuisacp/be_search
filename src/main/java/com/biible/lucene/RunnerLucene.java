package com.biible.lucene;

import java.io.IOException;

import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Similarity;

//import com.hrstc.lucene.queryexpansion.QueryExpansion;
//import com.hrstc.lucene.*;

public class RunnerLucene {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		LuceneTester testerPt = new LuceneTester("pt");
//		testerPt.createIndex();
		
		DefaultSimilarity n = new DefaultSimilarity(){
	      @Override
	      public float coord(int overlap, int maxOverlap) {
	        return overlap / ((float)maxOverlap - 1);
	      }
		};
	      
		testerPt.searchTerms("casamento", testerPt, 5);
	}

}
