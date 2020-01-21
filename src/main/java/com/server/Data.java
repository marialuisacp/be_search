package com.server;

import java.util.ArrayList;
import java.util.List;

import com.biible.lucene.LuceneTester;
import com.biible.lucene.Verse;

public class Data {
			
    private final long id;
    private final String content;
    private List<String> termsrelevance;
    private List<Integer> countsrelevance;
    private List<Verse> results;
    private List<String> expansion;
    private List<String> synonymous;    
    private int i;

    public Data(long id, String content, LuceneTester search) {
    	this.expansion = new ArrayList<>();
    	this.results = search.searchTerms(content, search, 0);
    	this.id = id;
        this.content = content;        
    }
    
    public Data(long id, String content, ArrayList<String> expansion, ArrayList<String> synonymous, LuceneTester search) {
    	
    	this.id = id;
        this.content = content;
        this.expansion = expansion;
        this.synonymous = synonymous;
        this.results = new ArrayList<>();
        this.termsrelevance = new ArrayList<String>();
        this.countsrelevance = new ArrayList<Integer>();
        
        System.out.println("construtor");
        String termAux = "";
        
    	if(expansion.size() > 0){	
    		// pesquisa com expansao de consulta
    		System.out.println("Consulta expandida");
    		
    		for(i = 0; i < expansion.size(); i++){
    			termAux = expansion.get(i);
    			List<Verse> listItems = search.searchTerms(termAux, search, 0);
    			this.termsrelevance.add(termAux);
    			this.countsrelevance.add(listItems.size());
    			this.results.addAll(listItems);
    		}
    	}
    	else if(synonymous.size() > 0){
    		// pesquisa com sinonimos 
    		// das palavras 
    		System.out.println("Consulta dos sinonimos das palavras");
    		
    		for(i=0; i<synonymous.size(); i++){
    			termAux = synonymous.get(i);
    			List<Verse> listItems = search.searchTerms(termAux, search, 0);
    			this.termsrelevance.add(termAux);
    			this.countsrelevance.add(listItems.size());
    			this.results.addAll(listItems);
    		}     		
    	}else{
    		// pesquisa comum
    		// termo sem expansao de consulta e sem sinonimos 
    		System.out.println("Consulta comum");
	    	this.results = search.searchTerms(content, search, 0); 
    	}
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
    
    public List<Verse> getResults() {
        return results;
    }
        
    public List<String> getExpansionsList() {
        return termsrelevance;
    }

    public List<Integer> getCountTermsRelevance() {
        return countsrelevance;
    }
    
    public List<String> getSynonymous() {
        return synonymous;
    }
}
