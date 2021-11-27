package com.lucenetutorial.lucene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFx extends Application{

	private static VBox mainpane;
	private Button bt_search,bt_update,bt_delete,bt_insert;
	private static ArrayList<Document> docs;
	private static TextField search;
	private static ListView<String> document_list;
	private static Stage stage;
	private static Scene scene;

	
	
	public void DoQuery() throws IOException, ParseException {
		String query = search.getText();
		try {
			new LuceneTester().search(query);
		} catch (ParseException e) {
			e.printStackTrace();
			}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getDoc_arr(ArrayList<Document> docs_arr) {
		docs=docs_arr; //takes arraylist to local variable
	}
	
    @Override
    public void start(Stage stage) { 
    			
    	this.stage=stage;
    	//----------Image--------------
    	VBox imagepane=DisplayImage();
    	//----------Search-------------
    	TextField search = DisplaySearchBar();       
        
        //---------Buttons----------
        bt_search =ButtonForm("Search");
        bt_update =ButtonForm("Update");
        bt_delete =ButtonForm("Delete");
        bt_insert =ButtonForm("Insert");
      
        HBox buttons = new HBox(bt_search,bt_update,bt_delete,bt_insert);
        buttons.setPadding(new Insets(0,50,0,50));
        buttons.setSpacing(15);  
        HBox.setHgrow(buttons, Priority.ALWAYS);
        buttons.setAlignment(Pos.BASELINE_CENTER);
        
        //Main Pane 
        mainpane = new VBox(imagepane,search,buttons);
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
    
    private static void DisplayDocument() {
    	 //add List of documents(results) pane to mainpane
        document_list = new ListView<String>();
        document_list.setPrefSize(200,500);
        document_list.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        document_list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//multi select
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
				else if(source == bt_update) {
					LuceneTester.Update();
				}
				else if(source==bt_insert) {
					
				}
				else if(source==bt_delete) {
					try {
						ObservableList<String> selectedItems =document_list.getSelectionModel().getSelectedItems();
						for(String lb : selectedItems) {
							document_list.getItems().remove(lb);//from current list not from index/data
						}
					}catch(Exception e) {
						//min emfaniseis tpt
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
			Object source = event.getSource();
			
			//Double click
			if(event.getClickCount()==2) {
				for(int i=0;i<docs.size();i++) {
					//event.getTargert ->ayto poy patithike an periexei to titlo toy doc
					if(event.getTarget().toString().contains(docs.get(i).get(LuceneConstants.TITLE))) {
						ReadTheDoc rtd = new ReadTheDoc(docs.get(i),scene);
						try {
							rtd.start(stage);
						} catch (Exception e) {
							// TODO Auto-generated catch block
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
			stream = new FileInputStream("media\\NotGoogle.jpg");
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
    
    private static TextField DisplaySearchBar() {
    	//Search Bar
    	search = new TextField();
        search.setPromptText("Search here..");
        search.setPrefSize(400, 50);
        search.setStyle("-fx-border-radius:10;"
        		+"-fx-background-radius: 10;"
        		+ "-fx-font-size:16pt;");
        
        search.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(search, Priority.ALWAYS);
        
        return search;
    }
    
     
    private static void DisplayDoc() {
    	document_list.getItems().clear(); //clear the previous list
    	docs.forEach((doc)-> {
    		document_list.getItems().add(doc.get(LuceneConstants.TITLE));
    	});
    	MouseClicked mc = new MouseClicked();
    	document_list.setOnMouseClicked(mc);

    	mainpane.getChildren().add(document_list); 
    	stage.setHeight(850);//make the window bigger for the results
    }
    
    public static void main(String[] args) {
    	launch(args);
    }

}
