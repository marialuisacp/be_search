package com.biible.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

  IndexSearcher indexSearcher;
  QueryParser queryParser;
  Query query;
  FuzzyQuery fuzzyQuery;

  public Searcher(String indexDirectoryPath) throws IOException, ParseException {
    Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
    indexSearcher = new IndexSearcher(indexDirectory);
    queryParser = new QueryParser(Version.LUCENE_36, LuceneConstants.CONTENTS,
        new BrazilianAnalyzer(Version.LUCENE_36));
  }

  public Query getQuery() {
    return this.query;
  }

  public IndexSearcher getIndexSearcher() {
    return this.indexSearcher;
  }

  public TopDocs search(String searchQuery) throws IOException, ParseException {
    query = queryParser.parse(searchQuery);
    return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
  }

  public TopDocs search(String searchQuery, int maxSearch) throws IOException, ParseException {
    query = queryParser.parse(searchQuery);
    return indexSearcher.search(query, maxSearch);
  }

  public TopDocs search(Query query) throws IOException, ParseException {
    return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
  }

  public TopDocs search(Query query, Sort sort) throws IOException, ParseException {
    return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, sort);
  }

  public TopDocs search(String searchQuery, Sort sort) throws IOException, ParseException {
    query = queryParser.parse(searchQuery);
    return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, sort);
  }

  public TopDocs search(String searchQuery, Sort sort, int maxSearch) throws IOException, ParseException {
    query = queryParser.parse(searchQuery);
    return indexSearcher.search(query, maxSearch);
  }

  public TopDocs searchFuzzy(String searchQuery, Sort sort) throws IOException, ParseException {
    float minimumSimilarity = 0.8f;
    Term term = new Term(LuceneConstants.CONTENTS, searchQuery);
    query = new FuzzyQuery(term, minimumSimilarity);

    return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, sort);
  }

  public TopDocs searchFuzzy(String searchQuery, Sort sort, int maxSearch) throws IOException, ParseException {
    float minimumSimilarity = 0.8f;
    Term term = new Term(LuceneConstants.CONTENTS, searchQuery);
    query = new FuzzyQuery(term, minimumSimilarity);
    System.out.println(term + " " + query);

    return indexSearcher.search(query, maxSearch);
  }

  public void setDefaultFieldSortScoring(boolean doTrackScores, boolean doMaxScores) {
    indexSearcher.setDefaultFieldSortScoring(doTrackScores, doMaxScores);
  }

  public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
    return indexSearcher.doc(scoreDoc.doc);
  }

  public void close() throws IOException {
    indexSearcher.close();
  }
}