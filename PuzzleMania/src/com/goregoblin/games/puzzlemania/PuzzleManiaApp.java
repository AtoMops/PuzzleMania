package com.goregoblin.games.puzzlemania;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;



	/* hier mal Puzzle Quest nachbasteln
	 * versuch mal HBox und VBox zu verwenden
	 * Spielfeld: 8x8 und quadratisch
	 * 
	 * Symbole irgendwas aber die Grundstruktur erstmal behalten
	 * Farb-Symbole: 4
	 * Spezial 3: Totenköpfe, Münzen, Sternchen   
	 * 
	 * versuch auch mal Sound und Hintergrund-Musik
	 * auch mal ein Haupt-Menü mit Start und Options
	 * 
	 * und Fullscreen! ^^'
	 * 
	 * Für MainMenu müssen wir zwischen InGame und Hauptmenü wechseln können 
	 * 
	 * oder wir arbeiten auf der "root" (vielleicht besser) --> ja besser!! ^^
	 *
	 * 
	 * Cursor ok
	 * Flächenwechsel ok (machen wir auf root)
	 * 
	 * --> wir brauchen ein hübsches "MainMenu" mit
	 *  
	 * 		"Start"
	 * 		"Options"
	 * 		"Exit"
	 * auch Hintergrundbild + Musik + click-sounds für "Buttons" (KEINE Standard-Buttons!!! sind zu häßlich ^^')
	 * 
	 * 
	 * VORSICHT!! WIR HABEN HIER EINEN ANDEREN BZW ERWEITERTEN JAVAFX-MODUL-PFAD!!
	 *  wegen Audio brauchen wir "Media" und "Media"
	 *  
	 *  https://stackoverflow.com/questions/53237287/module-error-when-running-javafx-media-application
	 *  
	 *  auch für MediaPlayer: (man kann ja auch videos abspielen etc)
	 *  https://blog.idrsolutions.com/2015/04/javafx-mp3-music-player-embedding-sound-in-your-application/
	 * 
	 */

public class PuzzleManiaApp extends Application{
	
	// für JavaFX
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private Scene scene;
	
	
	Pane mainMenuPane;
	GridPane gamePane;
	GridPane cursorPane;
	Rectangle cursor;
	
	// to use JavaFX-Plotting
		private Parent launchMainScreen() {
			
			double parentRelSize = 1.25; 
			
			root = new Pane();
			root.setPrefSize(width/parentRelSize, height/parentRelSize);
			root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);"); 
			
//			root.getChildren().addAll(launchGameBoard());
			
			root.getChildren().add(launchMainMenu());
			
			return root;
		}
		
		
		private Pane launchMainMenu() {
			
			double parentRelSize = 1.0; 
			mainMenuPane = new Pane();
			mainMenuPane.setPrefSize(width/parentRelSize, height/parentRelSize);
			mainMenuPane.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);"); 
			
			
			// Hintergrund-Bild laden
			String folderToSearch = "resource/images/";
			String imageToLoad = "Mothra";
			Image imgLoad = importImage(folderToSearch, imageToLoad);
			
			double imgWidth = imgLoad.getWidth();
			double imgHeight = imgLoad.getHeight();
			
			ImageView imgView = new ImageView();
			imgView.setImage(imgLoad);
			
			// das hier klappt besser bei FullScreen
			double xPos = width/2-imgWidth/2;
			double yPos = height/2-imgHeight/2;
			
			imgView.setTranslateX(xPos);
			imgView.setTranslateY(yPos);
			
			imgView.setScaleX(2);
			imgView.setScaleY(2);
			
			/* to create our own "Buttons" we can combine 
			 * HBox, Text and e.g. Rectangle (Or Ellipse etc) 
			 * tried also Group, but Group is not suited to overlay Nodes (which we want)
			 * 
			 * --> an den Buttons noch eine wenig basteln
			 * mit Rectangle und Text hat man alles was man braucht aber
			 * wir müssen die Relationen für die Screen-Positionen noch rausarbeiten
			 * sonst liegen Rectangle und Text nicht gut übereinander
			 * Der Rahmen bzw Hintergrund ist ja nicht exakt so groß wie der Text
			 * das sieht dann ziemlich gequetscht aus.. 
			 * 
			 */
			
			// Text-Fields
			  Text textStart = new Text();      
			  Text textOptions = new Text();
			  Text textHelp = new Text();			  
			  Text textExit = new Text();      

			  
		      //Setting the text to be added. 
			  textStart.setText("Start"); 
			  textOptions.setText("Options"); 
			  textHelp.setText("Help"); 
			  textExit.setText("Exit"); 
			  
			  // text-Editierung
			  txtEdit(textStart);
			  txtEdit(textOptions);
			  txtEdit(textHelp);
			  txtEdit(textExit);

			  
			  /*	zum plazieren der Felder am Besten von Center ausgehen und dann
			   *    +/- zu der gewünschten position
			   *    --> hier verschieben wir ja nur auf Y-Ebene also rauf und runter
			   * 
			   */
			  
			  
			  // Container für Text; Text ohne Container zu platzieren ist echt fies ^^'
			  HBox btnStart = new HBox();	
			  
			  btnStart.setTranslateX(width/2 - textStart.getBoundsInLocal().getWidth()/2);
			  btnStart.setTranslateY(height/2 - textStart.getBoundsInLocal().getHeight()/2);
			  
			  btnStart.getChildren().add(textStart);
//			  btnStart.setStyle("-fx-border-color: red;");// nur zum testen
			  
			  
			  File f = new File("resource/css/myStyle.css");
//		        Media media = new Media(f.toURI().toString());
			  
//			  btnStart.getStylesheets().add(f.toURI().toString());
			 
			  
			  double txtWidth = textStart.getLayoutBounds().getWidth();
			  double txtHeight = textStart.getLayoutBounds().getHeight();
			  
			  double widthAdd = 50;
			  Rectangle startBtnBorder = new Rectangle(txtWidth+widthAdd, txtHeight, Color.TRANSPARENT);
			  
			  
//			  Stop[] stopsStroke = new Stop[] { new Stop(0, Color.ORANGE), new Stop(1, Color.INDIGO)};
//			  LinearGradient lngntStroke = new LinearGradient(0, 0, 1, 0, true, CycleMethod.REFLECT, stopsStroke);			  
//			  startBtnBorder.setStroke(lngntStroke);
			  
			  
			  startBtnBorder.setStrokeWidth(5);
			  
			  
			  /* hier mal versuchen mit der Timeline einen rotierenden Rahmen zu erstellen
			   * also so das die Color um das Rectangle zirkuliert 
			   * mit:
			   * LinearGradient(0, 0, 1, 0, .. ) --> LinearGradient(startX, startY, endX, endY, .. )
			   * 
			   * wir können werte zwischen 0 und 1 setzen..
			   * 
			   * https://stackoverflow.com/questions/24587342/how-to-animate-lineargradient-on-javafx
			   * 
			   * --> kein rotierender Rahmen aber Rahmen blinkt .. sieht gut aus.. ganz ok ^^'
			   * 
			   */
			  
			  
			  // ein Basis-Farbobjekt dem wir einen Listener zuordenen können 
			  ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();
			  
			  //KeyValues definieren 
   	          KeyValue keyValue1 = new KeyValue(baseColor, Color.RED, Interpolator.LINEAR);
              KeyValue keyValue2 = new KeyValue(baseColor, Color.YELLOW);
              
              // KeyValues den Keyframes zuordnen
              KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
              KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);
              
              // keyframes an die Timeline (hier haben wir nur 2 können aber auch mehr reinpacken)
              Timeline timeline = new Timeline(keyFrame1, keyFrame2);
			  
              //  Listener an baseColor; baseColor ist mit den KeyValues verbunden
              baseColor.addListener((obs, oldColor, newColor) -> {  // changed(ObservableValue<? extends Color>, Color, Color)
            	  // hier eine Color setzen reicht (wir könnte auch oldColor setzen; der Effekt wäre der gleiche)
            	  startBtnBorder.setStroke(newColor); 
              });
              
       
              timeline.setAutoReverse(true);
              timeline.setCycleCount(Animation.INDEFINITE);
              timeline.play();	
			  
			  
		      
			  startBtnBorder.setTranslateX(width/2 -startBtnBorder.getWidth()/2);
			  startBtnBorder.setTranslateY(height/2 -startBtnBorder.getHeight()/2); 

		      
		      /*Setting the height and width of the arc
		       * see also here how Arc works:
		       * https://www.tutorialspoint.com/javafx/2dshapes_rounded_rectangle.htm 
		       */
			  startBtnBorder.setArcWidth(30.0); 
			  startBtnBorder.setArcHeight(20.0);  
			  
			  /* for gradient colors see here:
			   * https://www.educba.com/javafx-gradient-color/
			   * 
			   */
			  
			  Stop[] stops = new Stop[] { new Stop(0, Color.INDIGO), new Stop(1, Color.ORANGE)};
//			  LinearGradient lngnt = new LinearGradient(1, 1, 0, 1, true, CycleMethod.NO_CYCLE, stops);
			  
			  /*
			   *     RadialGradient(double focusAngle,
			   *     				double focusDistance,
			   *     			    double centerX,
			   *     			    double centerY,
			   *     			    double radius,
			   *     			    boolean proportional,
			   *     			    CycleMethod cycleMethod,
			   *     			    Stops? stops)  
			   * 
			   */
			  
//		        RadialGradient rdgnt = new RadialGradient(0,  
//		                .1,  
//		                .5,  
//		                .25	,  
//		                .5,  
//		                true,   
//		                CycleMethod.NO_CYCLE,  
//		                stops);  
			  
			  
		        RadialGradient rdgnt = new RadialGradient(0,  
		                .1,  
		                100,  
		                100,  
		                200,  
		                false,  
		                CycleMethod.NO_CYCLE,  
		                stops);  
			  
			  
			  startBtnBorder.setFill(rdgnt);
		  
			  
			  /* see here for animated gradient color
			   * https://stackoverflow.com/questions/24587342/how-to-animate-lineargradient-on-javafx
			   * 
			   */
			  
//			  startBtnBorder.toBack();
//			  startBtnBorder.setOpacity(.5);
			  
			  
			  /* see here for some stuff about Text and adding border etc
			   * https://stackoverflow.com/questions/20598778/drawing-a-border-around-a-javafx-text-node
			   * 
			   * also try CSS:
			   * https://www.javatpoint.com/css-border
			   * 
			   * --> using "-fx-border-width" directly on e.g. HBox does not work !!
			   * see here:
			   * 	https://stackoverflow.com/questions/18154110/set-border-size
			   * 
			   * für Animationen mit CSS AUCH:
			   * 	https://stackoverflow.com/questions/17676274/how-to-make-an-animation-with-css-in-javafx
			   * 
			   * Aber nicht übertreiben mit CSS-Animationen (wenn überhaupt)
			   * besser JavaFX-interne "Transitions" und "Timeline Animations" nutzen:
			   * 
			   * https://docs.oracle.com/javafx/2/animations/basics.htm#CJAJJAGI
			   * 
			   * --> LINKS checken!! 
			   * 
			   */
			  
			  btnBlink(2000, btnStart);
			  btnBlink(2000, startBtnBorder);
		
			  
			
			mainMenuPane.getChildren().add(imgView);
			mainMenuPane.getChildren().add(startBtnBorder);
			mainMenuPane.getChildren().add(btnStart);
	      
			// show font-types
//			 List<String> allFonts = javafx.scene.text.Font.getFamilies();			 
//			 allFonts.stream().forEach(System.out::println);
			 
			
			return mainMenuPane;
		}
		
		
		
		
		// Methode für die Text-Editierung (Schriftart, Größe, usw); soll ja einheitlich sein im Menü
		private void txtEdit(Text text) {
			text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 80));
			text.setFill(Color.NAVAJOWHITE);
		    //Setting the Stroke  
			text.setStrokeWidth(2); 
	      // Setting the stroke color
			text.setStroke(Color.BLACK);
			
//			text.setWrappingWidth(200);
			text.setTextAlignment(TextAlignment.CENTER);
			
		}
		
		
		
		  // für blink-effekt der Buttons
		private void btnBlink (int millis, Node node) {
			  
			  FadeTransition ft = new FadeTransition(Duration.millis(millis), node);
			  ft.setFromValue(1.0);
			  ft.setToValue(0.75);
			  ft.setCycleCount(Timeline.INDEFINITE);
			  ft.setAutoReverse(true);
			  ft.play();
		}
		
		
		
		
		// Methode die Audio startet (ist nur ein Bsp)
		private void startSoundSample() {
			
			/* hier auch mal Sound-Bsp in JavaFX:
			 * https://www.javatpoint.com/javafx-playing-audio
			 * 
			 */
			
			 //Instantiating Media class  
//	        File f = new File("src/main/resources/jb.mp3");
	        File f = new File("resource/audio/ApostelOfWar.mp3");
	        Media media = new Media(f.toURI().toString());
			
	
	        // der MediaPlayer muss immer neu initialisiert werden (geht vlt besser)
//	        btnStart.setOnMouseClicked(e -> {
//	        	  //Instantiating MediaPlayer class   
//		        MediaPlayer mediaPlayer = new MediaPlayer(media);  
//	        	  mediaPlayer.play();
//	        	
//	        });
			
		}
		
		
		private List<GridPane> launchGameBoard(){
			
			
			 List<GridPane> overlayList = new ArrayList<>(); 
			
			 overlayList.add(launchInlay());
			 overlayList.add(launchCursor());
			 
			return overlayList;
		}
		
		
		private GridPane launchInlay() {
			
			double inlaySize = 1000;
			gamePane = new GridPane();
			gamePane.setPrefSize(inlaySize, inlaySize);
			gamePane.setStyle("-fx-background-color: rgba(100, 200, 0, 0.8);");
			
			// das hier geht besser im Fenster
//			double xPos = root.getPrefWidth()/2-inlaySize/2;
//			double yPos = root.getPrefHeight()/2-inlaySize/2;
			
			// das hier klappt besser bei FullScreen
			double xPos = width/2-inlaySize/2;
			double yPos = height/2-inlaySize/2;
			
			gamePane.setTranslateX(xPos);
			gamePane.setTranslateY(yPos);
		
			
			/*PADDING
			 * grid.setHgap(10); //horizontal gap in pixels 
				grid.setVgap(10); //vertical gap in pixels
				grid.setPadding(new Insets(10, 10, 10, 10)); //margins around the whole grid (top/right/bottom/left)
				
				Also for gridlines:
				https://stackoverflow.com/questions/37619867/how-to-display-gamePane-object-grid-lines-permanently-and-without-using-the-set#40408598
				und:
				https://stackoverflow.com/questions/32892646/adding-borders-to-gamePane-javafx
				
				for constraints of the grid 
				https://riptutorial.com/javafx/example/8190/gamePane
			 */
			
			gamePane.setHgap(5);
			gamePane.setVgap(5);
			gamePane.setPadding(new Insets(5, 5, 5, 5));

			
//			gamePane.getColumnConstraints().add(new ColumnConstraints(5));
//			gamePane.getRowConstraints().add(new RowConstraints(5));
			
			
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					gamePane.add(createRectangleForTesting(), j,i); // gamePane uses add(Node,x,y)
				}
			}
			
			return gamePane;
		}
		
		
		private GridPane launchCursor() {
		
		/* 
		 * für den Cursor mal versuchen eine "unsichtbare" Pane über die GridPane zu legen (also an die root!)
		 * weil "in" der GridPane geht ja schlecht weil auch der "Cursor" eine Node ist 
		 * und das Grid verschieben würde 
		 * 
		 * vielleicht sollten wir die 8x8 GridPane einfach kopieren bzw nochmal erstellen
		 * und dann einfach die Farbe der Node ändern bzw alle anderen unsichtbar machen
		 * 
		 */
		
			double inlaySize = 1000;
			cursorPane = new GridPane();
			cursorPane.setPrefSize(inlaySize, inlaySize);
			cursorPane.setStyle("-fx-background-color: rgba(100, 100, 100, 0.5);");
			
			cursorPane.setHgap(5);
			cursorPane.setVgap(5);
			cursorPane.setPadding(new Insets(5, 5, 5, 5));
			
			// das hier klappt besser bei FullScreen
			double xPos = width/2-inlaySize/2;
			double yPos = height/2-inlaySize/2;
			
			cursorPane.setTranslateX(xPos);
			cursorPane.setTranslateY(yPos);
			
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					
					String idRow = String.format("%d", i);
					String idCol = String.format("%d", j);
					String id = idRow+idCol;
					
//					System.out.println("id: " + id);
					
					cursorPane.add(createRectangleForCursorPane(id), j,i); // Gridpane uses add(Node,x,y)
				}
			}
			
			// erstellt cursor an position 00 (evtl ein random 0-7 für row und cols?!)
			Rectangle rect = (Rectangle) cursorPane.lookup("#00");
			
			if (rect != null) {
				rect.setFill(Color.DARKORANGE);
			}else {
				System.out.println("is null");
			}
			
			
			return cursorPane;
		}
		
	
		// rectangles only for testing (later ImageViews)
	private Rectangle createRectangleForTesting() {
		
		// GridPane-Adaption has its limits; better calc a little
		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;			
		double rectSize = parentSize/nodeAmount-parentPaddingSize;
					
		Rectangle rect = new Rectangle(rectSize, rectSize);
		rect.setFill(Color.BLUE);
		
		return rect;
	}


	private Rectangle createRectangleForCursorPane(String id) {
				
				// GridPane-Adaption has its limits; better calc a little
				double parentSize = 1000.0;
				double parentPaddingSize = 5;
				double nodeAmount = 8.0;			
				double rectSize = parentSize/nodeAmount-parentPaddingSize;
							
				Rectangle rect = new Rectangle(rectSize, rectSize);
				rect.setFill(Color.DARKGREEN);
				
				rect.setId(id);
				
	//			rect.setStroke(Color.BLACK);
				
				return rect;
			}


		private List<Integer> getCursorPosRowCol(){
			
			List<Integer> rowColPos = new ArrayList<>();
			
			List<Node> opt = cursorPane.getChildren()
					.stream()
					.filter(n -> "Rectangle".equals(n.getClass().getSimpleName()))
					.collect(Collectors.toList());
					
					for (Node node : opt) {
						Rectangle rec = (Rectangle) node;
						
						if (rec.getFill() == Color.DARKORANGE) {
							System.out.println("current cursorPos is: " + rec.getId());
							
							String id = rec.getId();
							
							char rowIs = id.charAt(0);
							char colIs = id.charAt(1);
							
							rowColPos.add(Integer.parseInt(String.valueOf(rowIs)));
							rowColPos.add(Integer.parseInt(String.valueOf(colIs)));
							
							System.out.println("row: " + rowColPos.get(0));
							System.out.println("col: " + rowColPos.get(1));
							
						}
					}
			
			return rowColPos;
		}
		
		
	private String getCursorPosID(){
		
		String id = "";
			
			List<Node> opt = cursorPane.getChildren()
					.stream()
					.filter(n -> "Rectangle".equals(n.getClass().getSimpleName()))
					.collect(Collectors.toList());
					
					for (Node node : opt) {
						Rectangle rec = (Rectangle) node;
						
						if (rec.getFill() == Color.DARKORANGE) {
							System.out.println("current cursorPos is: " + rec.getId());
							
							id = "#"+rec.getId();
							
							System.out.println("current cursor id is: " + id);
						}
					}
			
			return id;
		}
		
	
		
		// Methode um Cursor zu bewegen 
		private void moveCursor(String direction) {
			
			// methode um cursor-position row und column zu bestimmen
			List<Integer> rowColCursor = getCursorPosRowCol();
			
			// methode um cursor id zu bestimmen
			String cursorPosId = getCursorPosID();
			
			// hier cursor-color für die neue position
			Integer rowPos = rowColCursor.get(0);
			Integer colPos = rowColCursor.get(1);
			
			/*
			 * nach unten: if (rowPos <= 6) --> rowPos++;
			 * nach rechts: if (colPos <= 6) --> --> colPos++;
			 * nach oben: if (rowPos > 0) --> rowPos--;
			 * nach links: if (colPos > 0) --> colPos--;
			 *  
			 */
			
			switch (direction) {
				case "UP":
					if (rowPos > 0) {
						// hier Color der alten position zurücksetzen
						Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);					
						if (rectOld != null) {
							rectOld.setFill(Color.DARKGREEN);
						}else {
							System.out.println("is null");
						}
						
						// positions-ID ändern
						rowPos--;					
						String newID = "#"+ rowPos+colPos;
						
						// cursor-farbe setzen 
						Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);					
						if (rectNew != null) {
							rectNew.setFill(Color.DARKORANGE);
						}
					} 
					
					break;
					
				case "DOWN":
							
					if (rowPos <= 6) {
						// hier Color der alten position zurücksetzen
						Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);					
						if (rectOld != null) {
							rectOld.setFill(Color.DARKGREEN);
						}else {
							System.out.println("is null");
						}
						
						// positions-ID ändern
						rowPos++;					
						String newID = "#"+ rowPos+colPos;
						
						// cursor-farbe setzen 
						Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);					
						if (rectNew != null) {
							rectNew.setFill(Color.DARKORANGE);
						}
					} 
							
					break;
							
				case "LEFT":
					
					if (colPos > 0) {
						// hier Color der alten position zurücksetzen
						Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);					
						if (rectOld != null) {
							rectOld.setFill(Color.DARKGREEN);
						}else {
							System.out.println("is null");
						}
						
						// positions-ID ändern
						colPos--;					
						String newID = "#"+ rowPos+colPos;
						
						// cursor-farbe setzen 
						Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);					
						if (rectNew != null) {
							rectNew.setFill(Color.DARKORANGE);
						}
					} 
					
					break;
					
				case "RIGHT":
					
					if (colPos <= 6) {
						// hier Color der alten position zurücksetzen
						Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);					
						if (rectOld != null) {
							rectOld.setFill(Color.DARKGREEN);
						}else {
							System.out.println("is null");
						}
						
						// positions-ID ändern
						colPos++;					
						String newID = "#"+ rowPos+colPos;
						
						// cursor-farbe setzen 
						Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);					
						if (rectNew != null) {
							rectNew.setFill(Color.DARKORANGE);
						}
					} 
					
					
					break;
	
				default:
					break;
			}
		}

		
		@Override
		public void start(Stage stage) throws Exception {
			
			// initialisierung der Fenstergrößen besser hier; dann können wir alle Parents damit bearbeiten 
			screenSize = Screen.getPrimary().getBounds();
			width = screenSize.getWidth();
			height = screenSize.getHeight();
			
			System.out.println("width: " + width + "; height: " + height);
			
			
			scene = new Scene(launchMainScreen());
			
			stage.setScene(scene);
			
			scene.setFill(Color.TRANSPARENT); // !!
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initStyle(StageStyle.TRANSPARENT); // !!
//			stage.setMaximized(true);
			 stage.setFullScreen(true);
			stage.show();
		
			
			scene.setOnKeyPressed(e -> {

				/*
				 * the arrow-keys on the small keypad seem to be recruited by the JavaFX scene!
				 * so we have to overwrite this or change the operational modus?!?! the scene
				 * responds to the arrow keys by choosing the buttons lets try Numpad here first
				 * oO' we read out the key-entries here but NO response to arrow-buttons
				 * 
				 */

//				System.out.println(e.getCode());
//				if (e.getCode() == KeyCode.ESCAPE) {
//					System.out.println("closing");
//					// CAUTION! BIG difference between Platfrom.exit and System.Exit !!!
//					Platform.exit(); // closes JavaFX app (but JVM still running)
//					System.exit(0); // closes JVM
//				}

				switch (e.getCode()) {
				case Q:
//					System.out.println(e.getCode());
//					the command "System.exit(0);" would be enough here..."Platform.exit();" just for training purposes
					Platform.exit(); // closes JavaFX app (but JVM still running)
					System.exit(0); // closes JVM
					break;
					
					
					// hier Modus-wechsel (Menu oder Spiel)
				case T:
					System.out.println("creating new node");
					root.getChildren().addAll(launchGameBoard());
					break;
				case G:
					System.out.println("clear root");
					root.getChildren().clear();
					// hier das main-menu hin? bei solchen optionen auch erstmal ein Dialog ^^'
					
					gamePane = null;
					cursorPane = null;
					
					break;
					
					// hier User-Inputs 
				case I:
					// hier cursor nach oben
					
					if (gamePane != null && cursorPane != null) { // nur wenn Spiel gestartet
						moveCursor("UP");
					} else { // hier MainMenu? 
						System.out.println("no game screen");
					}
					
					
					break;
				case K:
					// hier cursor nach unten
					if (gamePane != null && cursorPane != null) {
						moveCursor("DOWN");
					} else {
						System.out.println("no game screen");
					}
					
					break;
				case J:		
					// hier cursor nach links
					
					if (gamePane != null && cursorPane != null) {
						moveCursor("LEFT");
					} else {
						System.out.println("no game screen");
					}
					
					break;	
				case L:			
					// hier cursor nach rechts
					if (gamePane != null && cursorPane != null) {
						moveCursor("RIGHT");
					} else {
						System.out.println("no game screen");
					}
					
					break;	
				default:
					break;
				}

			});
			
		}

		
		// ------------------------- Hilfs-Methoden --------------------------------
		
		// to generate random Integer
		private int getRandomNumberInRange(int min, int max) {
			if (min >= max) {
				throw new IllegalArgumentException("max must be greater than min");
			}
			Random r = new Random();
			return r.nextInt((max - min) + 1) + min;
		}
		
		
		
		//----------------------- Methoden für import --------------------------------------------------------
		
		// Methode um Folder auszulesen
		private static List<String> getFileNames(String folderWant) {
			File folder = new File(folderWant);
			File[] listOfFiles = folder.listFiles();
			List<String> fileLst = new ArrayList<String>();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					fileLst.add(listOfFiles[i].toString());
				}
			}
			return fileLst;
		}
		
	
		// coolere Methode um Datei-namen einzulesen 
		private static List<Path> findImage(String searchDirectory, PathMatcher matcher) throws IOException {
			try (Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
				return files.filter(matcher::matches).collect(Collectors.toList());
			}
		}
		
		// hier mal was stricken um Images aus source-folder zu importieren		
		private Image importImage(String folder, String imgName) {
			
			Image imageLoad = null;
			
			// a Wildcard at the end of PathMatcher-String seems to work
			PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:.*" + imgName + ".*");
			List<Path> find = null;
			// address images
			try {
				find = findImage(folder, matcher);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			System.out.println("path is: " + find.get(0));
			// add found images to imagesList
			
			
			try {
				InputStream is = Files.newInputStream(Paths.get(find.get(0).toString()));
				imageLoad = new Image(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			return imageLoad;
		}
		
		
		
		// Main
		public static void main(String[] args) {
	   	   launch(args);
		}

}
