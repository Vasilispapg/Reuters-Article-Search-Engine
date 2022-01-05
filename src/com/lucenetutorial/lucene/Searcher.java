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
	private int type;

	public Searcher(String indexDirectoryPath,String flag_of_query,int type) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		//edw toy les poy na kanei anazitisi
		this.flag_of_query=flag_of_query;
		
		this.type=type;
		
	}
	
	public ArrayList<TopDocs> search(String searchQuery) throws IOException, ParseException {
		ArrayList<TopDocs> hits = new ArrayList<TopDocs>();
		//kanonikopoisi
		searchQuery=searchQuery.toLowerCase();
		//kathe fora anazitw se diaforetiko pedio
		switch(flag_of_query) {
		case "phrase":
			searchQuery=searchQuery.replace("\"","");//svinw ta aftakia gia na doylepsei
			switch(type) {
			case 0:
				PhraseQuery phquery = new PhraseQuery(LuceneConstants.TITLE,searchQuery);
				hits.add(indexSearcher.search(phquery, LuceneConstants.MAX_SEARCH));
				break;
			case 1:
				phquery = new PhraseQuery(LuceneConstants.BODY,searchQuery);
				hits.add(indexSearcher.search(phquery, LuceneConstants.MAX_SEARCH));
				break;
			case 2:
				phquery = new PhraseQuery(LuceneConstants.PEOPLEINDEX,searchQuery);
				hits.add(indexSearcher.search(phquery, LuceneConstants.MAX_SEARCH));
				break;
			case 3:
				phquery = new PhraseQuery(LuceneConstants.PLACEINDEX,searchQuery);
				hits.add(indexSearcher.search(phquery, LuceneConstants.MAX_SEARCH));
				break;
			}
			return hits;
		case "boolean":
			System.out.println("boolean");
			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
			Query q = new QueryParser(LuceneConstants.TITLE, new StandardAnalyzer()).parse(searchQuery) ;
			booleanQuery.add(q,BooleanClause.Occur.MUST);
			hits.add(indexSearcher.search(booleanQuery.build(),10));  //den eimai sigoyros gia ayto 
			return hits;
		case "query":
			//Create The Parsers
			switch(type) {
			case 0:
				QueryParser queryParserTitle = new QueryParser(LuceneConstants.TITLE, new StandardAnalyzer());
				query = queryParserTitle.parse(searchQuery);
				TopDocs hitsTitle = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
				hits.add(hitsTitle);
				break;
			case 1:QueryParser queryParserBody = new QueryParser(LuceneConstants.BODY, new StandardAnalyzer());
			query = queryParserBody.parse(searchQuery);
			TopDocs hitsBody= indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			hits.add(hitsBody);
				break;
			case 2:QueryParser queryParserPeople = new QueryParser(LuceneConstants.PEOPLEINDEX, new StandardAnalyzer());
			query = queryParserPeople.parse(searchQuery);
			TopDocs hitsPeople = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			hits.add(hitsPeople);
				break;
			case 3:QueryParser queryParserPlace = new QueryParser(LuceneConstants.PLACEINDEX, new StandardAnalyzer());
			query = queryParserPlace.parse(searchQuery);
			TopDocs hitsPlace= indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			hits.add(hitsPlace);
				break;
			}
			return hits;
			
		case "end":
			
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