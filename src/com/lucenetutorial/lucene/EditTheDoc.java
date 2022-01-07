package com.lucenetutorial.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class EditTheDoc extends Application{
	private Document doc;
	private Button back_bt,submit_bt;
	private TextField title_tf,people_tf,place_tf;
	private TextArea body_ta;
	
	//---------------ta xreiazomai gia na gyrnaw stin pisw selida------------
	private Scene scene;
	private Stage stage;
	//---------------ta xreiazomai gia na gyrnaw stin pisw selida------------
	
	public EditTheDoc(Document doc,Scene scene) {
		this.doc=doc;
		this.scene=scene;
	}

	 private Button ButtonForm(String name) {
		 //ImageView back_img = new ImageView(new Image(new File("/Users/macbookpro2017/eclipse-workspace/NotGoogle/media/back.png").getAbsolutePath()));
    	 //Button bt = new Button(name,back_img); 
		 Button bt = new Button(name); 
    	 bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    	 bt.setPrefSize(30, 30);
    	 if(name.contains("Back"))
	    	 bt.setOnAction((e)->{
	    		 stage.setScene(scene);//returns to search
	    	 });
    	 else {
    		 //submit
    		 bt.setOnAction((e)->{
    		try {
				File file=deleteFile();//delete old file
				UpdateFile(file);//create new edited file
				LuceneMain.Update(); //update the dict
				stage.setScene(scene);//returns to search
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	 });
    	 }
         return bt;
    }
	 
	 //delete the current file
	 private File deleteFile() throws IOException {
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
	    conf.setOpenMode(OpenMode.CREATE_OR_APPEND);//me to create sketo apla ta esvine ola
	    Directory directory = FSDirectory.open(new File(new LuceneMain().indexDir).toPath());
	    IndexWriter indexWriter  = new IndexWriter(directory,conf);
	    //Config gia delete/update/insert
	        
	    File f  = new File(doc.get(LuceneConstants.FILE_PATH));
		//get the path and delete it
		indexWriter.deleteDocuments(new Term(LuceneConstants.FILE_PATH,f.getAbsolutePath()));
		//delete the file from dir
		f.delete();
			
		indexWriter.close();
		directory.close();
		return f;
	 }
	 
	 //Create a new File
	 private void UpdateFile(File file) {
		 //prepare the forms
		 String title="<TITLE>"+title_tf.getText()+"</TITLE>";
		String people="<PEOPLE>"+people_tf.getText()+"</PEOPLE>";
		String places="<PLACES>"+place_tf.getText()+"</PLACES>";
		String body="<BODY>"+body_ta.getText()+"</BODY>";
		new InsertFx().writeToFile(file,places,people,title,body);
	 }
	 
	 //form for textfields
	 private TextField editSections(String name) {
		TextField tf=null;
		switch(name) {
		case "TITLE":
			tf = new TextField(doc.get(LuceneConstants.TITLE));
			tf.setPromptText("Title");
			break;
		case "PEOPLEINDEX":
			tf = new TextField(doc.get(LuceneConstants.PEOPLEINDEX));
			tf.setPromptText("People");
			break;
		case "PLACEINDEX":
			tf = new TextField(doc.get(LuceneConstants.PLACEINDEX));
			tf.setPromptText("Place");
			break;
		}
		tf.setFont(new Font(18));
		tf.setPrefSize(500, 50);
		return tf;
	 }
	 
	@Override
	public void start(Stage stage) throws Exception {
		
		this.stage=stage;
		this.back_bt = ButtonForm("Back");
		this.submit_bt=ButtonForm("Submit");
		
        //Title
		title_tf = editSections("TITLE");
		//People
		people_tf = editSections("PEOPLEINDEX");
		//place
		place_tf = editSections("PLACEINDEX");
		
		//Body
		body_ta = new TextArea(doc.get(LuceneConstants.BODY));
		body_ta.setFont(new Font(16));
		body_ta.setWrapText(true);
		body_ta.setPromptText("Body");
		
		//MainPane
		VBox mainpane = new VBox(back_bt,title_tf,people_tf,place_tf,body_ta,submit_bt);
    	mainpane.setStyle("-fx-background-color: #f4f4f4;");
        mainpane.setSpacing(4);
        
        VBox.setVgrow(body_ta,Priority.ALWAYS);
        mainpane.setAlignment(Pos.TOP_CENTER);
		
		Scene scene = new Scene(mainpane, 650, 500);
	    stage.setScene(scene);
	    stage.setTitle("Not Google");
	    stage.show();
	    
	    body_ta.setPrefWidth(stage.getWidth()-70);
	    body_ta.setPrefHeight(stage.getHeight()-150);
	    
	    stage.setMinHeight(stage.getHeight());
	    //stage.getIcons().add(new Image("file:/Users/macbookpro2017/eclipse-workspace/NotGoogle/media/NotGoogle-icon.jpg"));
	    stage.setMinWidth(stage.getWidth());
	}
}
