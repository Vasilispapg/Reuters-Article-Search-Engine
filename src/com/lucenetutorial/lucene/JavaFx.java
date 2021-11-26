package com.lucenetutorial.lucene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFx extends Application{
	
	static VBox resultpane = new VBox();
	Button bt_search,bt_update;
	static ArrayList<Document> docs;
	
    @Override
    public void start(Stage stage) { 
    			
    	//Image and Style
    	InputStream stream = null;
		try {
			stream = new FileInputStream("media\\NotGoogle.jpg");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
        imageView.setFitWidth(320);

        //image pane
        VBox imagepane = new VBox(imageView);
        imagepane.setAlignment(Pos.CENTER);
        
        //Search Bar
        TextField search = new TextField();
        search.setPromptText("Search here..");
        search.setPrefSize(400, 50);
        search.setStyle("-fx-border-radius:10;"
        		+"-fx-background-radius: 10;"
        		+ "-fx-font-size:16pt;");
        
        search.setAlignment(Pos.BOTTOM_CENTER);
        
        
        //ButtonHandler
        ButtonHandler handler = new ButtonHandler();
        //Buttons
        bt_search = new Button("Search"); 
        bt_search.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        bt_search.setPrefSize(150, 30);
        bt_search.setOnAction(handler);
        
        bt_update = new Button("Update"); 
        bt_update.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        bt_update.setPrefSize(150, 30);
        bt_update.setOnAction(handler);
        
        HBox buttons = new HBox(bt_search,bt_update);
        buttons.setPadding(new Insets(0,50,0,50));
        buttons.setSpacing(15);
        
        HBox.setHgrow(buttons, Priority.ALWAYS);
        buttons.setAlignment(Pos.BASELINE_CENTER);
        
        //Main Pane 
        VBox mainpane = new VBox(imagepane,search,buttons);
    	mainpane.setSpacing(5);
    	mainpane.setStyle("-fx-background-color: #FFFFFF;");

    	//Pane style
    	VBox.setVgrow(search, Priority.ALWAYS);
        VBox.setVgrow(imageView, Priority.ALWAYS);
        mainpane.setPadding(new Insets(0,20,0,20));
        mainpane.setSpacing(10);
  
        //add result pane to mainpane
       
        
        //resultpane
        resultpane.setSpacing(5);
        resultpane.setPadding(new Insets(10,0,0,0));
        
      //Creating the scroll pane
        ScrollPane scroll = new ScrollPane();
        scroll.setPrefSize(595, 200);
        //Setting content to the scroll pane
        scroll.setContent(resultpane);
        scroll.setStyle("-fx-background-color: #FFFFFF;");


        
        mainpane.getChildren().add(scroll);
    	//Scene
        Scene scene = new Scene(mainpane, 600, 350);
        stage.setScene(scene);
        stage.setTitle("Not Google");
        stage.show();
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());

    }
    
    private class ButtonHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			Object source = event.getSource();
			try {
				if(source == bt_search) {
					DisplayDoc();
				}
				else if(source == bt_update) {
					LuceneTester.Update();
				}
				
			}catch(Exception e) {
				System.out.println(e);
			}
			
		}
    	
    }
    
    //Display the search answer
//    ButtonHandler2 handler2 = new ButtonHandler2();
    public static void DisplayDoc() {
    	
    	docs.forEach((doc)-> {
    		Button bt = new Button();
//    		bt.setOnAction(handler2);
    		bt.setStyle("-fx-font-size:13pt");
    		bt.setText(doc.get(LuceneConstants.TITLE).toString());
    		resultpane.getChildren().add(bt);
    	});
//    	mainpane.getChildren().addAll();
//    	System.out.println(docs.get(0));
    }
    
    private class ButtonHandler2 implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			Object source = event.getSource();
			try {
				System.out.println(source.toString());
				
			}catch(Exception e) {
				System.out.println(e);
			}
			
		}
    	
    }

    public static void main(String[] args,ArrayList<Document> docs_arr) {
    	docs=docs_arr; //takes arraylist to local variable
    	launch(args);
    }

}
