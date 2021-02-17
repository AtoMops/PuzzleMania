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
import java.util.Map;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
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



	/*
	 * hier mal versuchen mehr mit CSS-Files zu arbeiten
	 * 
	 * das Options-Menu braucht schöne Slider für die Audio-Einstellung
	 * 
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

public class PuzzleManiaAppV3 extends Application{
	
	// für JavaFX
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private Scene scene;
	
	
	Pane mainMenuPane;
	Pane optionsMenuPane;
	GridPane gamePane;
	GridPane cursorPane;
	Rectangle cursor;
	
	Timeline timelineBtnBlink;
	MediaPlayer mediaPlayerBeep;
	Media mediaBeep; 
	File fBeepSound;
	
	
	// to use JavaFX-Plotting
		private Parent launchMainScreen() {
			
			/* nicht sicher ob wir für jeden soudn einen player brauchen
			 * eigentlich wäre besser über media zu gehen aber wir haben 
			 * eben auch später überlappende sounds (musik, mehre sounds bei verschiedenen situationen)
			 * Mediplayer kann man stoppen und wieder neu starten; media nicht oO'
			 * 
			 */
			
			fBeepSound = new File("resource/audio/popsound.mp3");
		    mediaBeep = new Media(fBeepSound.toURI().toString());
		    mediaPlayerBeep = new MediaPlayer(mediaBeep);
		    mediaPlayerBeep.setVolume(0.1);
		    
		    
			double parentRelSize = 1.25; 
			
			root = new Pane();
			root.setPrefSize(width/parentRelSize, height/parentRelSize);
			root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);"); 
			
//			root.getChildren().addAll(launchGameBoard());
			
			root.getChildren().add(launchMainMenu());
			
			return root;
		}
		
		// Method to launch MainMenu
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
			
			Group grpStart = createMenuButton("Start", -150);
			Group grpOptions = createMenuButton("Options", 0);
			Group grpHelp = createMenuButton("Help", 150);
			Group grpExit = createMenuButton("Exit", 300);

			
			// -----------------  add blink Method to Buttons -----------------------------
			/*
			 * hier noch reaction-sounds 
			 * see here:
			 * http://www.orangefreesounds.com/
			 */
			
			grpStart.setOnMouseEntered(e -> {
				Rectangle rect = (Rectangle) grpStart.getChildren().get(0);
				addColorFrame(rect);
				System.out.println("first: " + mediaPlayerBeep.getStatus());
				mediaPlayerBeep.play();
			});
			
			grpStart.setOnMouseExited(e -> {
				timelineBtnBlink.stop();
				Rectangle rect = (Rectangle) grpStart.getChildren().get(0);
				rect.setStroke(null);
				mediaPlayerBeep.stop();
			});
			
			
			grpOptions.setOnMouseEntered(e -> {
				Rectangle rect = (Rectangle) grpOptions.getChildren().get(0);
				addColorFrame(rect);
				mediaPlayerBeep.play();
			});
			
			grpOptions.setOnMouseExited(e -> {
				timelineBtnBlink.stop();
				Rectangle rect = (Rectangle) grpOptions.getChildren().get(0);
				rect.setStroke(null);
				mediaPlayerBeep.stop();
			});
			
			
			grpHelp.setOnMouseEntered(e -> {
				Rectangle rect = (Rectangle) grpHelp.getChildren().get(0);
				addColorFrame(rect);
				mediaPlayerBeep.play();
			});
			
			grpHelp.setOnMouseExited(e -> {
				timelineBtnBlink.stop();
				Rectangle rect = (Rectangle) grpHelp.getChildren().get(0);
				rect.setStroke(null);
				mediaPlayerBeep.stop();
			});
			
			grpExit.setOnMouseEntered(e -> {
				Rectangle rect = (Rectangle) grpExit.getChildren().get(0);
				addColorFrame(rect);
				mediaPlayerBeep.play();
			});
			
			grpExit.setOnMouseExited(e -> {
				timelineBtnBlink.stop();
				Rectangle rect = (Rectangle) grpExit.getChildren().get(0);
				rect.setStroke(null);
				mediaPlayerBeep.stop();
			});
			
			
			// --------------------- add Button Pressed response ---------------------------------
			grpStart.setOnMousePressed(e -> {
				// hier Spielstart	
				
				System.out.println("clear root");
				root.getChildren().clear();
				
				System.out.println("creating new node");
				root.getChildren().addAll(launchGameBoard());
				
				
			});
			
			grpOptions.setOnMousePressed(e -> {
				// OptionsMenu; müssen wir noch basteln
				
				root.getChildren().clear();
				root.getChildren().add(launchOptionsMenu());
			});
			
			grpHelp.setOnMousePressed(e -> {
				/* auf jeden Fall Keys erklären!
				 * 
				 * vielleicht noch spiel-regeln?!
				 *  
				 */
				
			});
			
			
			
			grpExit.setOnMousePressed(e -> {
				
				System.exit(0);
				
			});
			
			
			/* Button-Größe noch nicht einheitlich 
			 * --> alle nochmal in eine VBox packen?!
			 * --> nur der Frame von jeweils einem Button soll blinken (als Markierung)
			 * 
			 * 
			 */
			
			VBox btnPack = new VBox();
			btnPack.setPrefWidth(300);
			btnPack.setPrefHeight(500);
			
//			btnPack.setStyle("-fx-border-color: red"); // nur zum testen
			btnPack.setAlignment(Pos.CENTER);
			
			 //Setting the space between the nodes of a VBox pane 
			btnPack.setSpacing(20);   
			
			btnPack.getChildren().addAll(grpStart, grpOptions, grpHelp, grpExit);
			
			
			System.out.println("btnPack.getPrefWidth(): " + btnPack.getPrefWidth());
			
			mainMenuPane.getChildren().add(imgView);
			mainMenuPane.getChildren().add(btnPack);
			
			btnPack.setTranslateX(width/2 - btnPack.getPrefWidth()/2);
			btnPack.setTranslateY(height/2 - btnPack.getPrefHeight()/2);
			
			
			// show font-types
//			 List<String> allFonts = javafx.scene.text.Font.getFamilies();			 
//			 allFonts.stream().forEach(System.out::println);
			
			
			return mainMenuPane;
		}
		

		// method to launch GameScreen
		private List<GridPane> launchGameBoard(){
			
			
			 List<GridPane> overlayList = new ArrayList<>(); 
			
			 overlayList.add(launchInlay());
			 overlayList.add(launchCursor());
			 
			return overlayList;
		}
		
		
		// Method to launch OptionsMenu
		private Pane launchOptionsMenu() {
			
			/* eine Lautstärke-Regelung wäre gut
			 * auch Sounds on/Off
			 * see here:
			 * https://www.geeksforgeeks.org/javafx-building-a-media-player/
			 */
//			mediaPlayerSoundEffect.setVolume(arg0);
			
			
			double parentRelSize = 1.0; 
			optionsMenuPane = new Pane();
			optionsMenuPane.setPrefSize(width/parentRelSize, height/parentRelSize);
			optionsMenuPane.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);"); 
			
			
			// Hintergrund-Bild laden
			String folderToSearch = "resource/images/";
			String imageToLoad = "Godzilla";
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
			
			// Btn to return to Mainmenu
			Group grpBack = createMenuButton("Back", 150);
			
			grpBack.setOnMouseEntered(e -> {
				Rectangle rect = (Rectangle) grpBack.getChildren().get(0);
				addColorFrame(rect);
				mediaPlayerBeep.play();
			});
			
			grpBack.setOnMouseExited(e -> {
				timelineBtnBlink.stop();
				Rectangle rect = (Rectangle) grpBack.getChildren().get(0);
				rect.setStroke(null);
				mediaPlayerBeep.stop();
			});
			
			grpBack.setOnMouseClicked(e -> {
				System.out.println("clear root");
				root.getChildren().clear();
				
				System.out.println("creating new node");
				root.getChildren().addAll(launchMainMenu());				
				
			});

			
			/* Slider noch hübsch machen
			 * gut wäre auch eine Sound-Resonanz damit man weis wie laut/leise das ist
			 * 
			 * hier was zum editieren:
			 * https://stackoverflow.com/questions/22304300/implementing-css-on-javafx-slider
			 * 
			 * Slider ist nicht so leicht zu händeln wie z.B. Rectangle
			 * wir sollten lieber *.css-files anlegen anstatt über die .setStyle(...) -Methode
			 * zu arbeiten
			 *  
			 * --> ist allgemein eh besser mit css-files zu arbeiten weil es mehr styling-Möglichkeiten bietet

			 * hier basics dazu:
			 * https://www.tutorialspoint.com/javafx/javafx_css.htm
			 * 
			 * auch: --> am besten hier starten!! 
			 * https://www.vojtechruzicka.com/javafx-css/ 
			 * 
			 * 
			 * maybe better try a combo of Slider and progressBar
			 * from here:
			 * https://stackoverflow.com/questions/14157161/hybrid-of-slider-with-progress-bar-javafx
			 * 
			 * für editierungs-optionen des sliders mit CSS:
			 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html#slider
			 * 
			 *  CSS-files an besten dahin packen wo sie wirken sollen
			 *  z.B. so:
			 *  File f = new File("resource/css/style.css");
				optionsMenuPane.getStylesheets().add(f.toURI().toString()); // 
			 *  
			 *  also wenn du CSS auf "Scene" packst wirkt der CSS-File auf ALLE angesprochenen
			 *  Elemente in der SCENE (also auf ALLE Slider die in der Scene auftauchen)
			 *  
			 *  wir haben den jetzt auf die Pane gepackt (unser options-menu)
			 *  daher wirkt der CSS-Style nur in der Pane  (also lokal)
			 *  --> getestet ist so ^^'
			 * 
			 */
			
			
			/* wir verbinden Slider und ProgressBar zu einem Element
			 * in dem wir die einfach übereinander legen
			 * der Slider ist unsichtbar und die ProgressBar 
			 * reagiert auf die Veränderung des Sliders
			 * WICHTIG!! Wir verwenden den File "style.css" für die Editierung!
			 * Den *.css-File haben wir an die Pane(!) gepackt damit dieser nur hier gilt 
			 * 
			 * CSS-Farben für die ProgressBar hier (der Slider ist ja unsichtbar):
			 * https://www.javatpoint.com/css-colors
			 * am besten Hexadecimal-Werte verwenden wie #EE82EE --> rgb(238,130,238)
			 * 
			 * am besten mit einem Converter:
			 * https://www.rapidtables.com/convert/color/rgb-to-hex.html
			 * 
			 */
			double elemsWidth = 200;
			double elemsHeight = 50;

			double maxValSet = 1;
			double initValue = 0.75;
			
	        final Slider slider = new Slider();
	        slider.setMin(0);
	        slider.setValue(initValue);
	        slider.setMax(maxValSet);
	        slider.setMinWidth(elemsWidth);
	        slider.setMaxWidth(elemsWidth);
	        slider.setMinHeight(elemsHeight);
	        slider.setMaxHeight(elemsHeight);

	        /* eine ProgressBar geht immer von 0-1 weil 1 meint der Prozess ist beendet
	         * d.h. wir müssen die übergebenen Werte des Sliders umrechnen
	         * haben wir im Listener (unten) gemacht
	         * 
	         */
	        final ProgressBar pb = new ProgressBar(0);
	        pb.setMinWidth(elemsWidth);
	        pb.setMaxWidth(elemsWidth);	        
	        pb.setMinHeight(elemsHeight);
	        pb.setMaxHeight(elemsHeight);
	        pb.setProgress(initValue);
	        
			slider.valueProperty().addListener((observable, oldValue, newValue) -> {
				pb.setProgress(newValue.doubleValue() / maxValSet);
//				System.out.println("Slider Value Changed (newValue: " + newValue.doubleValue() + ")");
				mediaPlayerBeep.setVolume(newValue.doubleValue());
			});
	        
			// sound response for current setting
			slider.setOnMousePressed(e -> { // player erst stoppen! 
				mediaPlayerBeep.stop();
			});
			
			// hier Kontroll-sound
			slider.setOnMouseReleased(e -> {
				mediaPlayerBeep.play();
			});
			
			// wichtig! wenn wir den Player nicht stoppen kann er auch nicht neu gestartet werden!
			slider.setOnMouseExited(e -> {
				mediaPlayerBeep.stop();
			});
			

	        Group grpBar = new Group(pb, slider);
			
			Text lblTxt = new Text("Volume:");
			txtEdit(lblTxt); 
			
			// auf Mitte setzen 
			double xPosMid = width/2;
			double yPosMid = height/2;
	        
			double xShiftBar = -250;
			double yShiftBar = -200;

			HBox hboxVol = new HBox(lblTxt, grpBar); 
			hboxVol.setAlignment(Pos.CENTER); // !!
			hboxVol.setTranslateX(xPosMid+xShiftBar);
			hboxVol.setTranslateY(yPosMid+yShiftBar);
//			hboxVol.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null , null))); // nut test
			
			
			// set CSS-Style
			File f = new File("resource/css/style.css");
			optionsMenuPane.getStylesheets().add(f.toURI().toString());
				
			optionsMenuPane.getChildren().addAll(imgView, hboxVol, grpBack);
			
			return optionsMenuPane;
		} 
		


		// Methode um menu-button zu erstellen
		private Group createMenuButton(String btntxt, double yPos) {
			
			Group grp = new Group();
			
			
			  // Text-Fields
			  Text txt = new Text();      
			  
		      //Setting the text to be added. 
			  txt.setText(btntxt);
			  
			  // text-Editierung
			  txtEdit(txt);
			  
			  // Container für Text; Text ohne Container zu platzieren ist echt fies ^^'
			  HBox hbox = new HBox();			  
			  
			  hbox.getChildren().add(txt);
			  
			  Rectangle txtFrame = createFrame(txt);
			  
			  btnPosition(hbox, txtFrame, txt, yPos);
			  addColorBG(txtFrame);
//			  addColorFrame(txtFrame);
			  
			  grp.getChildren().addAll(txtFrame,hbox);
			  
			  grp.setId(btntxt);
			  
			  
			return grp;
			
		}
		
		
		
		// Methode um textframe zu erstellen
		private Rectangle createFrame(Text txt) {
			
			double txtWidth = txt.getLayoutBounds().getWidth();
			double txtHeight = txt.getLayoutBounds().getHeight();
			  
			double widthAdd = 50;
			Rectangle txtFrame = new Rectangle(txtWidth+widthAdd, txtHeight, Color.TRANSPARENT);
			txtFrame.setStrokeWidth(5);
			
			   /*Setting the height and width of the arc
		       * see also here how Arc works:
		       * https://www.tutorialspoint.com/javafx/2dshapes_rounded_rectangle.htm 
		       */
			txtFrame.setArcWidth(30.0); 
			txtFrame.setArcHeight(20.0);  
			
			return txtFrame;
		}
		
		
		// Method to create Background-Gradient color for Textframe
		private void addColorBG(Rectangle rect) {
			Stop[] stops = new Stop[] { new Stop(0, Color.INDIGO), new Stop(1, Color.ORANGE)};
			  
			  // gradient top-down
			  LinearGradient lngnt = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
			  rect.setFill(lngnt);
		}
		
		// LinearGradient Color für Textframe
		private void addColorFrame(Rectangle rect) {
			// ein Basis-Farbobjekt dem wir einen Listener zuordenen können 
			ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();
			  
			//KeyValues definieren 
 	        KeyValue keyValue1 = new KeyValue(baseColor, Color.RED, Interpolator.LINEAR);
            KeyValue keyValue2 = new KeyValue(baseColor, Color.YELLOW, Interpolator.LINEAR);
            
            // KeyValues den Keyframes zuordnen
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);
            
            // keyframes an die Timeline (hier haben wir nur 2 können aber auch mehr reinpacken)
            timelineBtnBlink = new Timeline(keyFrame1, keyFrame2);
			  
            //  Listener an baseColor; baseColor ist mit den KeyValues verbunden
            baseColor.addListener((obs, oldColor, newColor) -> {  // changed(ObservableValue<? extends Color>, Color, Color)
          	  // hier eine Color setzen reicht (wir könnte auch oldColor setzen; der Effekt wäre der gleiche)
            	rect.setStroke(newColor); 
            });
     
            timelineBtnBlink.setAutoReverse(true);
            timelineBtnBlink.setCycleCount(Animation.INDEFINITE);
            timelineBtnBlink.play();	
			
		}
		
		// Methode um Button-elemente zu positionieren(HBox, Rectangle)
		private void btnPosition(HBox hbox, Rectangle rect, Text txt, double yPos) {
			
			// erst auf Mitte positionieren dann y-Position bestimmen
			hbox.setTranslateX(width/2 - txt.getBoundsInLocal().getWidth()/2);
			hbox.setTranslateY((height/2 - txt.getBoundsInLocal().getHeight()/2) + yPos);
//			btnStart.setStyle("-fx-border-color: red;");// nur zum testen
			
			rect.setTranslateX(width/2 -rect.getWidth()/2);
			rect.setTranslateY((height/2 -rect.getHeight()/2) + yPos); 
			
		}
		
		
		// Methode für die Text-Editierung (Schriftart, Größe, usw); soll ja einheitlich sein im Menü
		private void txtEdit(Text text) {
			text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 80));
			text.setFill(Color.NAVAJOWHITE);
			text.setStrokeWidth(2); 
			text.setStroke(Color.BLACK);
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
			
//  		File f = new File("resource/css/style.css");
//			scene.getStylesheets().add(f.toURI().toString());
			
			stage.setScene(scene);
			
			scene.setFill(Color.TRANSPARENT); // !!
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initStyle(StageStyle.TRANSPARENT); // !!
//			stage.setMaximized(true);
			
			/* to remove or change the FS-hint:
			 * https://stackoverflow.com/questions/16713554/how-to-change-scene-when-in-fullscreen-in-javafx-and-avoid-press-esc-to-exit-fu
			 */
			
			stage.setFullScreenExitHint(""); // remove label
			/* you can change the exit-key (if you type none then also the message is gone)
			 * CAUTION! Also Escape does not work anymore ^^'
			 */
			
			stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); 
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
