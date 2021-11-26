package com.lucenetutorial.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
	
	String dataDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Data\\Demo";
	String indexDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index";
//	String dataDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Data\\Reuters_articles";
	Indexer indexer;
	Searcher searcher;
	static String [] args_javafx;
	static LuceneTester tester;
	
	public static void main(String[] args) throws IOException {
		LuceneTester tester;
		
		//--------------DELETE INDEX---------------
		File index = new File("C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index\\");
		deleteDirectoryStream(index.toPath());
		//--------------DELETE INDEX---------------
		
		args_javafx=args;
		try {
			tester = new LuceneTester();
			tester.createIndex();
			String query="where usa";
//			String[]query2 =query.split(" ");
//			query2=[where,usa];
//			for(query2)
			tester.search(query);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
		e.printStackTrace();
		}
		}
		private void createIndex() throws IOException {
			indexer = new Indexer(indexDir);
			int numIndexed;
			long startTime = System.currentTimeMillis();
			numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
			long endTime = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexed+" File(s) indexed, time taken: " +(endTime-startTime)/1000+" sec");
		}

		private void search(String searchQuery) throws IOException, ParseException {
			searcher = new Searcher(indexDir);
			long startTime = System.currentTimeMillis();
			ArrayList<TopDocs> hits = searcher.search(searchQuery);
			long endTime = System.currentTimeMillis();
			
			
			ArrayList<Document> doc_arr = new ArrayList<Document>(); //Arraylist gia na pernaw ta docs kai na ta emfanizw
			int x=0;
			for(TopDocs hit : hits) {
				switch(x) {
				case 0://title
					System.out.println("into Title ->"+hit.totalHits +" documents found. Time :" + (endTime - startTime));
					break;
				case 1:
					System.out.println("into Place ->"+hit.totalHits +" documents found. Time :" + (endTime - startTime));
					break;
				case 2:
					System.out.println("into People -> "+hit.totalHits +" documents found. Time :" + (endTime - startTime));
					break;
				case 3:
				default:
					System.out.println("into Body->"+hit.totalHits +" documents found. Time :" + (endTime - startTime));
					break;
				}
				x++;
				for(ScoreDoc scoreDoc : hit.scoreDocs) {
					Document doc = searcher.getDocument(scoreDoc);
					doc_arr.add(doc);//add docs into arraylist
					FormatofDoc(doc);
				}
			}
			
			JavaFx.main(args_javafx,doc_arr);//runs the javafx 
			
			searcher.close();
		}
		
		public static void FormatofDoc(Document doc) {
			System.out.println("---------------------------------");
			System.out.println("PEOPLE: " + doc.get(LuceneConstants.PEOPLE));
			System.out.println("Title: "+doc.get(LuceneConstants.TITLE));
			System.out.println("Place: " + doc.get(LuceneConstants.PLACE));
			System.out.println("Body: "+doc.get(LuceneConstants.BODY));	
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
			System.out.println("---------------------------------");
		}
		//Delete Index
		//https://softwarecave.org/2018/03/24/delete-directory-with-contents-in-java/
		private static void deleteDirectoryStream(Path path) throws IOException {
			if(Files.exists(path))
			  Files.walk(path)
			    .sorted(Comparator.reverseOrder())
			    .map(Path::toFile)
			    .forEach(File::delete);
		}
		
		//egw to ftiaja
		public static void Update() throws IOException {
			File index = new File("C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index\\");
			deleteDirectoryStream(index.toPath());
			tester = new LuceneTester();
			tester.createIndex();
		}
}