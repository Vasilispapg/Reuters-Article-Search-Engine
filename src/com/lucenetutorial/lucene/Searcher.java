package com.lucenetutorial.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParserTitle,queryParserBody,queryParserPeople,queryParserPlace;

	Query query;

	public Searcher(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		//edw toy les poy na kanei anazitisi
		
		queryParserTitle = new QueryParser(LuceneConstants.TITLEINDEX, new StandardAnalyzer());
		queryParserBody = new QueryParser(LuceneConstants.BODYINDEX, new StandardAnalyzer());
		queryParserPeople = new QueryParser(LuceneConstants.PEOPLEINDEX, new StandardAnalyzer());
		queryParserPlace = new QueryParser(LuceneConstants.PLACEINDEX, new StandardAnalyzer());
	}
	
	public ArrayList<TopDocs> search(String searchQuery) throws IOException, ParseException {
		ArrayList<TopDocs> hits = new ArrayList<TopDocs>();
		//kathe fora anazitw se diaforetiko pedio
		
		query = queryParserTitle.parse(searchQuery);
		TopDocs hitsTitle = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
		 
		query = queryParserBody.parse(searchQuery);
		TopDocs hitsBody= indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
		 
		query = queryParserPeople.parse(searchQuery);
		TopDocs hitsPeople = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
		 
		query = queryParserPlace.parse(searchQuery);
		TopDocs hitsPlace= indexSearcher.search(query, LuceneConstants.MAX_SEARCH);

		
		//add topdocs into arraylist
		hits.add(hitsTitle);hits.add(hitsPlace);
		hits.add(hitsPeople);hits.add(hitsBody);
		
		return hits;
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}