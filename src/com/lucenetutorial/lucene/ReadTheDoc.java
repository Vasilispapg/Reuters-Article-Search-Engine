package com.lucenetutorial.lucene;

import java.io.File;

import org.apache.lucene.document.Document;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ReadTheDoc extends Application{
	
	private Document doc;
	private Button back_bt;
	
	//---------------ta xreiazomai gia na gyrnaw stin pisw selida------------
	private Scene scene;
	private Stage stage;
	//---------------ta xreiazomai gia na gyrnaw stin pisw selida------------
	
	public ReadTheDoc(Document doc,Scene scene) {
		this.doc=doc;
		this.scene=scene;
	}

	 private Button ButtonForm(String name) {
		 ImageView back_img = new ImageView(new Image(new File("/Users/macbookpro2017/eclipse-workspace/NotGoogle/media/back.png").getAbsolutePath()));
    	 Button bt = new Button(name,back_img); 
    	 bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    	 bt.setPrefSize(30, 30);
    	 bt.setOnAction((e)->{
    		 stage.setScene(scene);
    	 });
         return bt;
    }
	 
	@Override
	public void start(Stage stage) throws Exception {
		
		this.stage=stage;
		this.back_bt = ButtonForm("Back");
        //Title
		Text title = new Text(doc.get(LuceneConstants.TITLE));
		title.setFont(new Font(24));
		title.setWrappingWidth(550);
		
		//Body
		Text body = new Text(doc.get(LuceneConstants.BODY));
		body.setFont(new Font(16));
		
		body.setTextAlignment(TextAlignment.LEFT);
		
		ScrollPane scroll = new ScrollPane();
	    scroll.setPrefSize(550, 850);
	    //Setting content to the scroll pane
	    scroll.setContent(body);
	    scroll.setPadding(new Insets(0,0,0,30));
	    scroll.setStyle("-fx-background-color:transparent");

		//MainPane
		VBox mainpane = new VBox(back_bt,title,scroll);
    	mainpane.setStyle("-fx-background-color: #f4f4f4;");
        mainpane.setSpacing(8);
        
        VBox.setVgrow(body,Priority.ALWAYS);
        VBox.setVgrow(scroll,Priority.ALWAYS);

        mainpane.setAlignment(Pos.TOP_CENTER);
		
		Scene scene = new Scene(mainpane, 650, 900);
	    stage.setScene(scene);
	    stage.setTitle("Not Google");
	    stage.show();
	    body.setWrappingWidth(stage.getWidth()-70);
	    stage.setMinHeight(stage.getHeight());
	    stage.getIcons().add(new Image("file:/Users/macbookpro2017/eclipse-workspace/NotGoogle/media/NotGoogle-icon.jpg"));
	    stage.setMinWidth(stage.getWidth());
	}

}

