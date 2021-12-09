package com.lucenetutorial.lucene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Random;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class InsertFx {
	
	private Boolean flag_big_screen=false;//me ayto jerw an eixe patisei search
	private Scene scene;
	private TextField title_tf,people_tf,places_tf;
	private TextArea body_ta;
	public InsertFx(Scene scene) {
		this.scene=scene;
		flag_big_screen=false;
	}
	
	public void start(Stage stage) { 
      
		
        //Main Pane 
        VBox mainpane = new VBox(ButtonForm("Back",stage), Sections("Title"),
        		Sections("People"),Sections("Places")
        		,BodySection(),ButtonForm("Submit",stage));
    	mainpane.setStyle("-fx-background-color: #FFFFFF;");
        mainpane.setPadding(new Insets(0,20,0,20));
        mainpane.setSpacing(10);
        mainpane.setAlignment(Pos.CENTER);

        //Scene startup
        Scene scene = new Scene(mainpane, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Not Google");
        if(stage.getHeight()>439)flag_big_screen=true; //gia na jerei pws itan to proigoymeno parathyro
        stage.setHeight(439);
        stage.setWidth(616);
        stage.setMaxHeight(439);
        stage.setMaxWidth(616);
        stage.show();
        stage.getIcons().add(new Image("file:media//NotGoogle-icon.jpg"));
        stage.setMinWidth(stage.getWidth());

    }
	
	private VBox BodySection() {
		Label label = new Label("Body:");
		label.setFont(new Font(16));
		TextArea ta = new TextArea();
		body_ta=ta;
		ta.setWrapText(true);
		ta.setMaxSize(570, 130);
		ta.setPrefSize(570, 130);
		VBox vb = new VBox(label,ta);
		return vb;
	}
	private HBox Sections(String section) {
		Label label = new Label(section+":");
		label.setFont(new Font(16));
		
		TextField textfield = new TextField();
		textfield.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		switch(section) {
		case "Title":textfield.setPrefSize(517,30);title_tf=textfield;break;
		case "Places":textfield.setPrefSize(505, 30);places_tf=textfield;break;
		case "People":textfield.setPrefSize(500, 30);people_tf=textfield;break;
		}
		HBox hb = new HBox(label,textfield);
		return hb;
	}
	
	//to pws tha fainontai ta koympia kai ti tha kanoyn
	private Button ButtonForm(String name,Stage stage) {
		Button bt;
		if(name.equals("Back")) {
			ImageView img = new ImageView(new Image(new File("media/back.png").getAbsolutePath()));
		 	bt = new Button(name,img); 
	    	 bt.setOnAction((e)->{
	 			if(flag_big_screen) { //se periptwsi poy itan megali i othoni prin na jana ginei opws itan
					stage.setHeight(850);
					stage.setWidth(616);
				}
	    		 stage.setScene(scene);
	    	 });
		}
		else{
			bt = new Button(name);
			bt.setOnAction((e)->{
				String title="<TITLE>"+title_tf.getText()+"</TITLE>";
				String people="<PEOPLE>"+people_tf.getText()+"</PEOPLE>";
				String places="<PLACES>"+places_tf.getText()+"</PLACES>";
				String body="<BODY>"+body_ta.getText()+"</BODY>";
				//takes ta values
				
				//generates tha new file
				int generateName= new Random().nextInt();
				int maxValue=5000000;
				File f =new File(new LuceneMain().dataDir+"\\Article"+generateName+".txt");
				do {
					generateName= new Random().nextInt(maxValue);
					f=new File(new LuceneMain().dataDir+"\\Article"+generateName+".txt");
				}while(f.exists());//se periptwsi poy yparxei to arxeio	
				try {
					//create File
					f.createNewFile();
					//write index to file
					writeToFile(f,places,people,title,body);
					//dialogs
					AlertDialog(f.getName(),stage);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
		}
	 	bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	 	bt.setPrefSize(30, 30);
	 	//sets the button
         return bt;
    }
	
	private void writeToFile(File f,String places,String people,String title,String body) {
		FileWriter fw;
		try {
			fw = new FileWriter(f.getCanonicalFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
	        pw.println(places);
	        pw.println(people);
	        pw.println(title);
	        pw.println(body);
	        
			pw.flush();
	        pw.close();
	        bw.close();
	        fw.close();
	        System.out.println(f.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void AlertDialog(String name,Stage stage) throws IOException {
		//create a dialog to continue adding file or finish
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Successful");
		alert.setHeaderText("The file "+name+" created successfully!");
		alert.setContentText("If you want more click <Continue>");
		
		ButtonType buttonContinue = new ButtonType("Continue");
		ButtonType buttonTypeLeave = new ButtonType("Leave");
		alert.getButtonTypes().setAll(buttonContinue, buttonTypeLeave);
		
		Optional<ButtonType> result=alert.showAndWait();
		
		if(result.get()==buttonContinue) {
			title_tf.setText("");
			people_tf.setText("");
			places_tf.setText("");
			body_ta.setText("");
		}
		else if(result.get()==buttonTypeLeave) {
			if(flag_big_screen) {
				stage.setHeight(850);
				stage.setWidth(616);
			}
			LuceneMain.Update();
			stage.setScene(scene);
		}
	}
}
