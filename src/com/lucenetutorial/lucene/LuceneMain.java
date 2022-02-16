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

public class LuceneMain {
	
	//MAC
	public static String dataDir = "/Users/macbookpro2017/eclipse-workspace/NotGoogle/Data/Reuters_articles";
	public static String indexDir = "/Users/macbookpro2017/eclipse-workspace/NotGoogle/Index";
	
	//WINDOWS
/*
	public static String indexDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index";
	String dataDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Data\\Reuters_articles";
	*/
	private static Indexer indexer;
	private static Searcher searcher;
	private static String [] args_javafx;
	
	public static void main(String[] args) throws IOException {

		args_javafx=args;//xreiazomai ta args tis main gia na trexei to javafx
		try {
			//add here whatever you want to run
			Update();//Delete previous index and create new one
			JavaFx.main(args_javafx);//runs the javafx 	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Creates new Index
		private static void createIndex() throws IOException {
			indexer = new Indexer(indexDir);
			long startTime = System.currentTimeMillis();
			int numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
			long endTime = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexed+" File(s) indexed, time taken: " +(endTime-startTime)/1000+" sec");
		}

		//Search
		ArrayList<Document> doc_arr = new ArrayList<Document>();//Arraylist gia na pernaw ta docs kai na ta emfanizw		
		public void search(String searchQuery,String flag_of_query,int type) throws IOException, ParseException {
			searcher = new Searcher(indexDir,flag_of_query,type);
			System.currentTimeMillis();
			ArrayList<TopDocs> hits = searcher.search(searchQuery);
			System.currentTimeMillis();
			
			if(flag_of_query.contains("end")) {// an teleiwse i anazitisi
				JavaFx.setDoc_arr(doc_arr);
			}
			else {	
				//vazoyme ta docs se mia arraylist gia na ta steiloyme sto javafx kai na efmanistoyn
				for(TopDocs hit : hits) {
					for(ScoreDoc scoreDoc : hit.scoreDocs) {
						//System.out.println(scoreDoc.score); //edw fainontai ta score
						Document doc = searcher.getDocument(scoreDoc);
						doc_arr.add(doc);//add docs into arraylist
						//FormatofDoc(doc); //episis emfanizoyme tis apantiseis
					}
				}	
			}
			//vazei tis apantiseis sto JavaFx kommati
			searcher.close();
		}
		
		//Display the Right form to console for check
		public static void FormatofDoc(Document doc) {
			System.out.println("---------------------------------");
			System.out.println("Title: "+doc.get(LuceneConstants.TITLE));
			System.out.println("Body: "+doc.get(LuceneConstants.BODY));	
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
			System.out.println("---------------------------------");
		}
		
		//Delete Index
		//https://softwarecave.org/2018/03/24/delete-directory-with-contents-in-java/
		private static void deleteDirectoryStream(Path path) throws IOException {
			if(Files.exists(path)) {
			  Files.walk(path)
			    .sorted(Comparator.reverseOrder())
			    .map(Path::toFile)
			    .forEach(File::delete);
			}
		}

		//Update and makes a new Index
		public static void Update() throws IOException {
			//we used this when we add a file to update or create the index
			deleteDirectoryStream(new File(indexDir).toPath());
			createIndex();
		}
}