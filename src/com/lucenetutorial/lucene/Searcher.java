package com.lucenetutorial.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	private IndexSearcher indexSearcher;
	private Directory indexDirectory;
	private IndexReader indexReader;
	private String flag_of_query;
	private Query query;

	public Searcher(String indexDirectoryPath,String flag_of_query) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		//edw toy les poy na kanei anazitisi
		this.flag_of_query=flag_of_query;
		
	}
	
	public ArrayList<TopDocs> search(String searchQuery) throws IOException, ParseException {
		ArrayList<TopDocs> hits = new ArrayList<TopDocs>();
		//kanonikopoisi
		searchQuery=searchQuery.toLowerCase();
		//kathe fora anazitw se diaforetiko pedio
		switch(flag_of_query) {
		case "phrase":
			searchQuery=searchQuery.replace("\"","");//svinw ta aftakia gia na doylepsei
			PhraseQuery phquery = new PhraseQuery(LuceneConstants.TITLE,searchQuery);
			hits.add(indexSearcher.search(phquery, LuceneConstants.MAX_SEARCH));
			phquery = new PhraseQuery(LuceneConstants.BODY,searchQuery);
			hits.add(indexSearcher.search(phquery, LuceneConstants.MAX_SEARCH));
			return hits;
		case "boolean":
			//TODO KANE AYTO
			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
			for(String string : searchQuery.split(" ")) {
				Query query = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer()).parse(string);
				booleanQuery.add(query,BooleanClause.Occur.MUST);
			}
			hits.add(indexSearcher.search(booleanQuery.build(),100));  //den eimai sigoyros gia ayto 
			return hits;
		case "query":
			//Create The Parsers
			QueryParser queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
			query = queryParser.parse(searchQuery);
			TopDocs hitsTitle = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			
			//add topdocs into arraylist
			hits.add(hitsTitle);
			return hits;
		case "else":
			
			break;
		}	
		return null;
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}