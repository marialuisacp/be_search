package com.biible.lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
 
	String localPath = System.getProperty("user.dir");
	String indexDir = localPath + "/index_pt"; 
	String dataDir = localPath + "/data_pt";  
	Indexer indexer;
	Searcher searcher; 
	
   public LuceneTester(String lang) {
	   if(lang.equals("en")) {
			this.dataDir = localPath + "/data_en";
			this.indexDir = localPath + "/index_en"; 
		} else {
			this.dataDir = localPath + "/data_pt";
			this.indexDir = localPath + "/index_pt"; 
		}
		System.out.println(this.dataDir);
		System.out.println(this.indexDir);
	}

public List<Verse> searchTerms(String term, LuceneTester tester, int maxSearch){
	   List<Verse> result = new ArrayList();
	   try {
		   if(maxSearch == 0)
	         result = tester.sortUsingRelevance(term, 0);
		   else
		     result = tester.sortUsingRelevance(term, maxSearch);
	         return result;
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (ParseException e) {
	         e.printStackTrace();
	      }
	return result;
   }
   
   public void createIndex() throws IOException{
      indexer = new Indexer(this.indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(this.dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }

   private List<String> search(String searchQuery) throws IOException, ParseException{
	   
	  String file_name = "", file_path = "";	  
	  List<String> test_files = new ArrayList<>();
      
	  searcher = new Searcher(this.indexDir);
      long startTime = System.currentTimeMillis();
      
      //do the search
      TopDocs hits = searcher.search(searchQuery);
      long endTime = System.currentTimeMillis();
   
      System.out.println(hits.totalHits +
         " documents found. Time :" + (endTime - startTime));
                  
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
    	 
         Document doc = searcher.getDocument(scoreDoc);
         
         file_name = doc.get(LuceneConstants.FILE_NAME);
         file_path = doc.get(LuceneConstants.FILE_PATH);
         
         // return file names 
         //test_files.add(file_name);
         
         try {
             FileReader arq = new FileReader(file_path);
             BufferedReader readFile = new BufferedReader(arq);

             String line = readFile.readLine();
             test_files.add(line);
             
             arq.close();
         } catch (IOException e) {
             System.err.printf("Erro na abertura do arquivo: %s.\n",
                 e.getMessage());
         }
      }
      searcher.close();
            
      return test_files;
   }
   
   private List<Verse> sortUsingRelevance(String searchQuery, int maxSearch) throws IOException, ParseException{
	   
	  List<Verse> test_files = new ArrayList<>();
	    
      searcher = new Searcher(this.indexDir);
      long startTime = System.currentTimeMillis(); 
      
      searcher.setDefaultFieldSortScoring(true, false);
      
      //do the search
      //TopDocs hits = searcher.search(searchQuery, Sort.INDEXORDER);
      
      System.out.println("searchQuery " + searchQuery);
      TopDocs hits;
      if(maxSearch == 0)
    	  hits = searcher.search(searchQuery, Sort.RELEVANCE);
      else
    	  hits = searcher.search(searchQuery, Sort.RELEVANCE, maxSearch);
      
      if(hits.totalHits == 0){
    	  System.out.println("Nenhum resultado");
    	  hits = searcher.searchFuzzy(searchQuery, Sort.RELEVANCE);
    	  
    	  if(maxSearch == 0)
    		  hits = searcher.searchFuzzy(searchQuery, Sort.RELEVANCE);
          else
        	  hits = searcher.searchFuzzy(searchQuery, Sort.RELEVANCE, maxSearch);
      }
      long endTime = System.currentTimeMillis();

      System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime) + "ms");
      System.out.println(localPath);
      
      String file_path = "", file_name = "";
      
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
         Document doc = searcher.getDocument(scoreDoc);

         file_path = doc.get(LuceneConstants.FILE_PATH);
         file_name = doc.get(LuceneConstants.FILE_NAME);
         
         Verse v = new Verse();
         try {
             FileReader arq = new FileReader(file_path);
             BufferedReader readFile = new BufferedReader(arq);

             String line = readFile.readLine();
             v.text  = line;
             v.id = file_name;
             v.search = searchQuery;
             test_files.add(v);
             
             arq.close();
         } catch (IOException e) {
             System.err.printf("Erro na abertura do arquivo: %s.\n",
                 e.getMessage());
         }
                 
      }
      
      searcher.close();
      return test_files;
   }
   
}
