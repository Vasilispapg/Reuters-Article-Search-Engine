package com.lucenetutorial.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
	
	public String dataDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Data\\Demo";
	public String indexDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index";
//	String dataDir = "C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Data\\Reuters_articles";
	private Indexer indexer;
	private Searcher searcher;
	private static String [] args_javafx;
	private static LuceneTester tester;
	
	public static void main(String[] args) throws IOException {

		//--------------DELETE INDEX---------------
		File index = new File("C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index\\");
		deleteDirectoryStream(index.toPath());
		//--------------DELETE INDEX---------------

		args_javafx=args;//xreiazomai ta args tis main gia na trexei to javafx
		
		try {
			//add here whatever you want to run
			new LuceneTester().createIndex();//creates The Index
			JavaFx.main(args_javafx);//runs the javafx 	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Creates new Index
		private void createIndex() throws IOException {
			indexer = new Indexer(indexDir);
			int numIndexed;
			long startTime = System.currentTimeMillis();
			numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
			long endTime = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexed+" File(s) indexed, time taken: " +(endTime-startTime)/1000+" sec");
		}

		//Search
		public void search(String searchQuery,String flag_of_query) throws IOException, ParseException {
			searcher = new Searcher(indexDir,flag_of_query);
			long startTime = System.currentTimeMillis();
			ArrayList<TopDocs> hits = searcher.search(searchQuery);
			long endTime = System.currentTimeMillis();
			
			ArrayList<Document> doc_arr = new ArrayList<Document>(); //Arraylist gia na pernaw ta docs kai na ta emfanizw
			ArrayList<ScoreDoc> scores = new ArrayList<>();
			int x=0;//just to see how many i found
			for(TopDocs hit : hits) {
				switch(x) {
				case 0://title
					//TODO FTIAXTO
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
				
				//μπαινουν ολα 2 φορες με αυτο, αν βαλουμε ενα constant Που να τα εχει ολα μεσα
//				for(ScoreDoc scoreDoc : hit.scoreDocs) {
//					scores.add(scoreDoc);	
//				}
				
				for(ScoreDoc scoreDoc : hit.scoreDocs) {
					System.out.println(scoreDoc.score);
					Document doc = searcher.getDocument(scoreDoc);
					doc_arr.add(doc);//add docs into arraylist
					FormatofDoc(doc);
				}
			}
		
//			Collections.sort(scores, new DocComparator());
//			
//			for(ScoreDoc scoreDoc : scores) {
//				System.out.println(scoreDoc.score+ " doc:"+searcher.getDocument(scoreDoc).get(LuceneConstants.TITLE));
//			}
//			
			JavaFx.setDoc_arr(doc_arr);
			searcher.close();
		}
		
		//Display the Right form to console for check
		public static void FormatofDoc(Document doc) {
			System.out.println("---------------------------------");
			System.out.println("PEOPLE: " + doc.get(LuceneConstants.PEOPLEINDEX));
			System.out.println("Titleindex: "+doc.get(LuceneConstants.TITLEINDEX));
			System.out.println("Title: "+doc.get(LuceneConstants.TITLE));
			System.out.println("Place: " + doc.get(LuceneConstants.PLACEINDEX));
			System.out.println("Bodyindex: "+doc.get(LuceneConstants.BODYINDEX));
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
		
		class DocComparator implements Comparator<ScoreDoc> {
			  
		    // override the compare() method
		    public int compare(ScoreDoc d1, ScoreDoc d2)
		    {
		    	System.out.println("d1score="+d1.score+" d2score:"+d2.score);
		        if ( d1.score == d2.score)
		            return 0;
		        else if (d1.score < d2.score)
		            return 1;
		        else
		            return -1;
		    }

		}
		//Update and makes a new Index
		public static void Update() throws IOException {
			File index = new File("C:\\Users\\Vasilis\\eclipse-workspace\\LuceneProject1\\Index\\");
			deleteDirectoryStream(index.toPath());
			tester = new LuceneTester();
			tester.createIndex();
		}
}