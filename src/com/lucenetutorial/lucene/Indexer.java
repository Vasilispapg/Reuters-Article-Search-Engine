package com.lucenetutorial.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Indexer {

 private IndexWriter writer;
//evretiria  dimiourgia / enhmerwsh
 Analyzer analyzer = new StandardAnalyzer();
 
	public Indexer(String indexDirectoryPath) throws IOException {
		//this directory will contain the indexes
		Path indexPath = Paths.get(indexDirectoryPath);
		if(!Files.exists(indexPath)) {
			Files.createDirectory(indexPath);
		}
		//Path indexPath = Files.createTempDirectory(indexDirectoryPath);
		Directory indexDirectory = FSDirectory.open(indexPath);
		//create the indexer
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(indexDirectory, config);
	}
 
 public void close() throws CorruptIndexException, IOException {
	 writer.close();
 }
 
 private Document getDocument(File file) throws IOException {
	 Document document = new Document();	 
	 //index file contents
	 BufferedReader br = new BufferedReader(new FileReader(file));

	 String currentLine = br.readLine().toString();
	 String places="",title="",body="",people="";
	 int count=0;
	 do{
		switch(count){
			case 0: places =currentLine;
				places=places.replace("<PLACES>", "");
				places=places.replace("</PLACES>", "");
			break;
			case 1: people = currentLine;
				people=people.replace("<PEOPLE>", "");
				people=people.replace("</PEOPLE>", "");
			break;
			case 2: title = currentLine;
				title=title.replace("<TITLE>", "");
				title=title.replace("</TITLE>", "");
			break;
			case 3: body=currentLine;
				body=body.replace("<BODY>", "");
				body=body.toLowerCase();
			break;
			default: body=body.concat(currentLine);break;
		}
		count+=1;
		currentLine = br.readLine();
	 }while(null!=currentLine);

	 body=body.replace("</BODY>", ""); // gia na min trexei synexeia

	 //displayTokenUsingStandardAnalyzer(title);
	 
	  	/* In Lucene, they are different, even it's not looks so obvious. A string is a single unit that 
	  	not supposed to be separated, analyzed. For example, the id, email, url, date, etc.
	  	The string itself is a term.
		Text is content, article, post, document and anything that may read by human. 
		This is the thing you want to index and search. 
		It should be analyzed, indexed and optionally stored. It's very sensible to encapsulate 
		all these properties in to an abstraction, this is what TextField for, a sugar class 
	   */

	 Field contentFieldPlaces = new Field(LuceneConstants.PLACE, places,TextField.TYPE_STORED);
	 Field contentFieldPeople = new Field(LuceneConstants.PEOPLE, people,StringField.TYPE_STORED);
	 Field contentFieldTitle = new Field(LuceneConstants.TITLE, title,TextField.TYPE_STORED);
	 Field contentFieldBody = new Field(LuceneConstants.BODY, body,TextField.TYPE_STORED);
	 //index file name
	 Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),StringField.TYPE_STORED);
	 //index file path
	 Field filePathField = new Field(LuceneConstants.FILE_PATH,file.getCanonicalPath(), StringField.TYPE_STORED);

	 document.add(contentFieldPlaces);
	 document.add(contentFieldPeople);
	 document.add(contentFieldTitle);
	 document.add(contentFieldBody);
	 document.add(fileNameField);
	 document.add(filePathField);
	 
	
	 br.close();
	 return document;
 }

 
 private void indexFile(File file) throws IOException {
	 System.out.println("Indexing "+file.getCanonicalPath());
	 Document document = getDocument(file);
	 writer.addDocument(document);
 }
 public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
	 //get all files in the data directory
	 File[] files = new File(dataDirPath).listFiles();
	 for (File file : files) {
		 if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)){
			 indexFile(file);
		 }
	 }
	 return writer.numRamDocs();
 }
 
 
 //TODO KANE STEMMING KAI ALLES MALAKIES
 public void displayTokenUsingStandardAnalyzer(String text) throws IOException {
     TokenStream tokenStream = analyzer.tokenStream(LuceneConstants.CONTENTS, new StringReader(text));
     CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
     tokenStream.reset();
//     System.out.println(title);
     while (tokenStream.incrementToken()) {
         System.out.print("[" + term.toString() + "] ");
     }
     tokenStream.close();
 }

}
