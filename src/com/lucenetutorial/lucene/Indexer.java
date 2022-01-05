package com.lucenetutorial.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
 private Directory indexDirectory;
 IndexWriterConfig config ;
 
 	public Indexer() {}
 	
	public Indexer(String indexDirectoryPath) throws IOException {
		//this directory will contain the indexes
		Path indexPath = Paths.get(indexDirectoryPath);
		if(!Files.exists(indexPath)) {
			Files.createDirectory(indexPath);
		}
		indexDirectory = FSDirectory.open(indexPath);
		//create the indexer
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer()); 
		try {
			writer = new IndexWriter(indexDirectory, config);
		}catch(Exception e) {
			System.out.println(e);
		}
	}
 
 public void close() throws CorruptIndexException, IOException {
	 writer.close();
	 indexDirectory.close();
 }
 
 private Document getDocument(File file) throws IOException {
	 Document document = new Document();	 
	 //index file contents
	 BufferedReader br = new BufferedReader(new FileReader(file));
	 String currentLine="";
	 currentLine=br.readLine().toString();
	 if(currentLine!=null) {
		 
	 
	 String places="",title="",body="",people="",titleindex="",bodyindex="";
	 //an allajei grami ta kollaei
	 int count=0;
	 do {
		switch(count){
			case 0: 
				if(currentLine.contains("</PLACES>"))count++;
				places = places.concat(currentLine);
			break;
			case 1: 
				if(currentLine.contains("</PEOPLE>"))count++;
				people = people.concat(currentLine);
			break;
			case 2:	
				if(currentLine.contains("</TITLE>"))count++;
				title = title.concat(removeTags(currentLine));
			break;
			case 3: 
				if(currentLine.contains("</BODY>"))count++;
				body = body.concat(removeTags(currentLine));
			break;
		}
			currentLine = br.readLine();
	 }while(null!=currentLine);
	 
	 //CASE FOLDING
	 titleindex = titleindex.toLowerCase();
	 bodyindex = bodyindex.toLowerCase();
	 people=people.toLowerCase();
	 places=places.toLowerCase();
	 String all_contents= titleindex+" "+bodyindex+" "+places+" "+people;
	 all_contents=stemmerStopWords(all_contents);
	 
	 Field contentFieldPeople = new Field(LuceneConstants.PEOPLEINDEX, people,TextField.TYPE_STORED);
	 Field contentFieldPlace = new Field(LuceneConstants.PLACEINDEX, places,TextField.TYPE_STORED);
	 
	 Field allContents= new Field(LuceneConstants.CONTENTS, all_contents,TextField.TYPE_STORED);;
	 Field contentFieldTitle = new Field(LuceneConstants.TITLE, title,TextField.TYPE_STORED);
	 Field contentFieldBody = new Field(LuceneConstants.BODY, body,TextField.TYPE_STORED);
	 //index file name
	 Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),StringField.TYPE_STORED);
	 //index file path
	 Field filePathField = new Field(LuceneConstants.FILE_PATH,file.getCanonicalPath(), StringField.TYPE_STORED);

	 document.add(contentFieldPeople);
	 document.add(contentFieldPlace);
	 
	 document.add(contentFieldTitle);
	 document.add(contentFieldBody);
	 
	 document.add(allContents);
	 document.add(fileNameField);
	 document.add(filePathField);
	 
//	 LuceneTester.FormatofDoc(document);
	 
	 br.close();
	 }
	 return document;
 }
 
 public String stemmerStopWords(String currentLine) {
	 String [] stopword_list= {"a","about","above","after","again","against","all","am","an","and","any","are","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours	ourselves","out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves"};
	 String [] splited_string = currentLine.split(" ");//pairnw tis lejeis
	 StringBuffer sb = new StringBuffer();
	 StringBuffer words = new StringBuffer();
	
	 for(String s_splited : splited_string) {//the string splited in spaces
		 boolean flag=true;//theloyme panta na vazoyme tin leji ektos an yparxei
			 for(String s_stop : stopword_list) {//psaxnoyme sto lejiko
				 if(s_splited.toLowerCase().equals(s_stop)) {//an to vrei
					flag=false;//den tin vazei kai termatizei
					break;
				 }
			 }
			 if(flag) {
				 sb.append(s_splited);
				 sb.append(" ");//vazoyme to keno anamesa se kathe leji
			 }
		 }
	 return removeSpaces(sb.toString());
 }
 
 //SVINOYME TA TAGS
 //ta thelw static giati kati mporei na synexizete gia panw apo 2 grammes
 private static boolean flagtag=true;
 private String removeTags(String s) {

	 String[] s_splited=s.split("");
	 StringBuffer sb = new StringBuffer();

	 for(int i =0;i<s_splited.length-1;i++) {
		 
		 if(s_splited[i].equals("<"))
			 flagtag=false;//min grapeis epita
		 else if(s_splited[i].equals(">")) {
			 flagtag=true;//grapse epeita
		 }
		 if(flagtag && !s_splited[i].equals(">")) {
			 sb.append(s_splited[i]);
		 }
	 }
	 return removeSpaces(sb.toString());
 }

 //SVINOYME TA DIPLA KENA AFTER TAG STEMMING
 private boolean flagspaces=false;
 private String removeSpaces(String s) {
	 String[] s_splited=s.split("");
	 StringBuffer sb = new StringBuffer();
	 
	 for(int i =0;i<s_splited.length;i++) {//pairnoyme tis lejeis
		 if(i+1<s_splited.length-1) {
			 if(s_splited[i].equals(" ") && s_splited[i+1].equals(" ")){ //an vreis se 2 synexomenes theseis keno gine true
				 flagspaces=true;
			 }
			 else if(Pattern.compile("[a-zA-Z0-9_*&%$#@!)(]").matcher(s_splited[i]).find()) { //den jerw an ayto xreiazetai
				 flagspaces=false; //an vrei leji
			 }
		 }
		 if(!flagspaces) { //an den vrei 2 dn ta vazei sto sb (buffer)
			 sb.append(s_splited[i]);
		 }
	 }
	 return sb.toString();
 }
 
 private void indexFile(File file) throws IOException {
	 System.out.println("Indexing "+file.getCanonicalPath());
	 Document document = getDocument(file);
	 if(document!=null)
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
 

}
