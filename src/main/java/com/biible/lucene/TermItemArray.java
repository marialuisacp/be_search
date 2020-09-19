package com.biible.lucene;

public class TermItemArray {
	public String termId;
	public String similar;
	public Double cosSim;
	
	public Double getSimilarityCoeficient() {
	    return cosSim;
	  }
}
