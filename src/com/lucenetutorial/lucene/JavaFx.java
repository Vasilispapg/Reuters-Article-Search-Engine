package com.lucenetutorial.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JavaFx extends Application{

	private static VBox mainpane;
	private Button bt_search,bt_info,bt_delete,bt_insert,bt_edit;
	private static ArrayList<Document> docs;
	private static TextField search;
	private static ListView<String> document_listView;
	private static Stage stage;
	private static Scene scene;
	private static int counter_docs =0;
	private static ChoiceBox bt_limit;
	private static Text info_text;
	
	public static void DoQuery() throws IOException, ParseException {
		String query = search.getText();
		try {
			query=new Indexer().stemmerStopWords(query);
			//title:Do it right psaxnei mono ston titlo me ayto
			//prashes
			if(Pattern.compile("\"[^\\r\\n\\t\\f\\v]+\"").matcher(query).matches()) {
				new LuceneMain().search(query,"phrase");
			}
			//Logical queries
			else if(Pattern.compile("([a-zA-Z0-9_!@#$%^&*() ]+(AND|OR|&&|\\|\\|)?[a-zA-Z0-9_!@#$%^&*() ]+)").matcher(query).matches()) {
				new LuceneMain().search(query,"boolean");
			}
			else if(Pattern.compile("NOT [a-zA-Z0-9_!@#$%^&*() ]").matcher(query).matches()) {
				new LuceneMain().search(query,"boolean");
			}
			else if(!query.isEmpty())
			{
				new LuceneMain().search(query,"query");
			}
		} catch (ParseException e) {
			e.printStackTrace();
			}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setDoc_arr(ArrayList<Document> docs_arr) {
		docs=docs_arr; //takes arraylist to local variable
	}
	
    @Override
    public void start(Stage stage) throws IOException, ParseException { 
        
    	this.stage=stage;
    	//----------Image--------------
    	VBox imagepane=DisplayImage();
    	//----------Search-------------
    	TextField search = DisplaySearchBar();       
        
        //---------Buttons----------
        bt_search =ButtonForm("Search");
        bt_delete =ButtonForm("Delete");
        bt_insert =ButtonForm("Insert");
		bt_edit=ButtonForm("Edit");
        bt_info=ButtonForm("Info");
        
        //LimitButton
        bt_limit=DisplaylimitDocs();
      
        HBox buttons = new HBox(bt_search,bt_delete,bt_insert,bt_info,bt_limit);
        buttons.setPadding(new Insets(0,50,0,50));
        buttons.setSpacing(15);  
        HBox.setHgrow(buttons, Priority.ALWAYS);
        buttons.setAlignment(Pos.BASELINE_CENTER);
        
        //information button
        info_text=displayInfo();
        
        
        
        //Main Pane 
        mainpane = new VBox(imagepane,search,buttons,info_text);
    	mainpane.setStyle("-fx-background-color: #FFFFFF;");
        mainpane.setPadding(new Insets(0,20,0,20));
        mainpane.setSpacing(10);
  
        DisplayDocument();

        //Scene startup
        Scene scene = new Scene(mainpane, 600, 350);
        this.scene=scene;
        stage.setScene(scene);
        stage.setTitle("Not Google");
        stage.show();
        stage.setMinHeight(stage.getHeight());
        stage.getIcons().add(new Image("file:media//NotGoogle-icon.jpg"));
        stage.setMinWidth(stage.getWidth());

    }
    
    private static ChoiceBox DisplaylimitDocs() {
    	ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList(
    		    "3", "9", "15","Unlimited")
    		);
    	cb.setValue("Unlimited");
    	cb.setPrefSize(150, 30);
    	cb.setOnAction((e)->{
    		if(cb.getValue().toString().contains("Unlimited"))
    			cb.setPrefSize(150, 30);
    		else
    			cb.setPrefSize(50, 30);
    		DisplayDoc();
    	});
    	return cb;
    }
    
    private static Text displayInfo() {
    	Text t = new Text("There are 3 ways to search. 1)Boolean search with this example <term AND|OR term>"
    			+ "2)Prashe search using \"terms\""
    			+ "3)Just words");
    	t.setFont(new Font(16));
    	t.setFill(Color.RED);
    	t.setOpacity(0);
    	t.setWrappingWidth(500);
    	return t;
    }
  
    private static void DisplayDocument() {
    	 //add List of documents(results) pane to mainpane
        document_listView = new ListView<String>();
        document_listView.setPrefSize(200,500);
        document_listView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        mainpane.getChildren().add(document_listView); 
        document_listView.setVisible(false);
        document_listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//multi select
    }
    
    //To have nice control of buttons
    private Button ButtonForm(String name) {
    	 ButtonHandler handler = new ButtonHandler();
    	 Button bt = new Button(name); 
    	 bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    	 bt.setPrefSize(150, 30);
    	 bt.setOnAction(handler);
         return bt;
    }

    private class ButtonHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event) {
			Object source = event.getSource();
			try {
				if(source == bt_search) {
					DoQuery();
					DisplayDoc();
				}
				else if(source==bt_edit) {
					System.out.println("Kati");
					ObservableList<String> selectedItems =document_listView.getSelectionModel().getSelectedItems();
					for(String lb:selectedItems) {
						int edit_this_one=-1;
						for(int i=0;i<docs.size();i++) {
							if(lb.equals(docs.get(i).get(LuceneConstants.TITLE))) {
								edit_this_one=i;
								break;
							}
						}
						EditTheDoc etd = new EditTheDoc(docs.get(edit_this_one),scene);
						try {
							etd.start(stage);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;	
					}
				}
				else if(source==bt_insert) {
					new InsertFx(scene).start(stage);
				}
				else if(source==bt_info) {
					int opacity= info_text.getOpacity()==0?1:0;
					info_text.setOpacity(opacity);
				}
				else if(source==bt_delete) {
					try {
						
						//TODO CHECKARE TA INDEXWRITERS GIA NA SVINEIS

						//EDW KAI KATW ALLA
						ObservableList<String> selectedItems =document_listView.getSelectionModel().getSelectedItems();
						for(String lb : selectedItems) {
							int delete_this_one=-1;
							for(int i =0;i<docs.size();i++) {
								if(lb.equals(docs.get(i).get(LuceneConstants.TITLE))) {
									delete_this_one=i;
									break;
								}
							}
							//find the file
							
					    	//Config gia delete/update/insert
					    	IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
					        conf.setOpenMode(OpenMode.CREATE_OR_APPEND);//me to create sketo apla ta esvine ola
					        Directory directory = FSDirectory.open(new File(new LuceneMain().indexDir).toPath());
					        IndexWriter indexWriter  = new IndexWriter(directory,conf);
					        //Config gia delete/update/insert
					        
							File f  = new File(docs.get(delete_this_one).get(LuceneConstants.FILE_PATH));
							//get the path and delete it
							indexWriter.deleteDocuments(new Term(LuceneConstants.FILE_PATH,f.getAbsolutePath()));
							//delete the file from dir
							f.delete();
							//delete file frm listview
							document_listView.getItems().remove(lb);//from current list not from index/data
							indexWriter.close();
							directory.close();
						}
					}catch(Exception e) {
						System.out.println(e);
					}
				}
			}catch(Exception e) {
				System.out.println(e);
			}
		}
    }
    
    public static class MouseClicked implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {			
			//Double click
			if(event.getClickCount()==2) {
				for(int i=0;i<docs.size();i++) {
					
					//event.getTargert ->ayto poy patithike an periexei to titlo toy doc
					if(event.getTarget().toString().contains(docs.get(i).get(LuceneConstants.TITLE))) {
						ReadTheDoc rtd = new ReadTheDoc(docs.get(i),scene);
						try {
							rtd.start(stage);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
				}
				
			}
			
		}
    	
    }
    
    private static VBox DisplayImage() {
    	//Image and Style
    	InputStream stream = null;
		try {
			stream = new FileInputStream("/Users/macbookpro2017/eclipse-workspace/NotGoogle/media/NotGoogle.jpg");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        Image image = new Image(stream);
        //Creating the image view
        ImageView imageView = new ImageView();
        //Setting image to the image view
        imageView.setImage(image);
        imageView.setX(500);
        
        //Setting the image view parameters
        imageView.setFitHeight(150);
        imageView.setFitWidth(500);
        VBox.setVgrow(imageView, Priority.ALWAYS);

        //image pane
        VBox imagepane = new VBox(imageView);
        imagepane.setAlignment(Pos.CENTER);
        
        
        return imagepane;
        
    }
    
    private static TextField DisplaySearchBar() throws IOException, ParseException{
    	//Search Bar
    	search = new TextField();
        search.setPromptText("Search here..");
        search.setPrefSize(400, 50);
        search.setStyle("-fx-border-radius:10;"
        		+"-fx-background-radius: 10;"
        		+ "-fx-font-size:16pt;");
        
        search.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(search, Priority.ALWAYS);
        search.setOnKeyPressed( event -> {
        	  if( event.getCode() == KeyCode.ENTER ) {
        		  try {
					DoQuery();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		  DisplayDoc();
        	  }
        	} );
        
        return search;
    }
    
     
    private static void DisplayDoc() {
    	document_listView.getItems().clear(); //clear the previous list
    	counter_docs=0;
    	for(Document doc : docs) {
    		document_listView.getItems().add(doc.get(LuceneConstants.TITLE));
    		counter_docs+=1;
    		if(!bt_limit.getValue().toString().contains("Unlimited"))//ama dn einai unlimited
    			if(counter_docs==Integer.parseInt(bt_limit.getValue().toString()))break;//an ftasoyme ton epithimito arithmo
    	}

    	MouseClicked mc = new MouseClicked();
    	document_listView.setOnMouseClicked(mc);
    	document_listView.setVisible(true);
    	
    	stage.setHeight(850);//make the window bigger for the results
    }
    
    public static void main(String[] args) {
    	launch(args);
    }

}
