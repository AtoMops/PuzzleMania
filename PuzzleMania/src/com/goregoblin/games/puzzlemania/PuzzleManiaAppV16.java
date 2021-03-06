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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


/* 
 * --> das hier später!! für eine executable müssen wir die libaries evtl völlig anders einbinden..also mit Maven etc..
 * hier auch mal was über JavaFX und executable *.jar
 * https://stackoverflow.com/questions/23117541/how-to-make-a-runnable-jar-for-an-application-that-uses-javafx-without-native-in
 * wir verwenden ja runtime-argumente 
 * 
 */

/* Kiste läuft jetzt stabil (was ein Horror ^^')
 * 
 * wird auch langsam Zeit für GitHub: ^^'
 * https://stackoverflow.com/questions/17552457/how-do-i-upload-eclipse-projects-to-github
 * https://www.youtube.com/watch?v=BH4OqYHoHC0
 * 
 * 
 * hier mal was zu "AI"
 * https://www.youtube.com/watch?v=da1uzaj549A
 * verwendet den "MiniMax-Algorithmus"
 * --> könnte für das hier schon reichen..
 * 
 *  TODO: SOUNDS and EFFECTS
 *  	  Sound geht schon gut; müssen wir nur noch etwas anpassen vom Timing her
 *  	  der Sound ended zu früh; vielleicht können wir die Länge des Media-Files selbst 
 *  	  verwenden um zu bestimmen wann der MediaPlayer gestoppt wird
 *  		--> sound geht gut jetzt mit:
 *  			player.setOnEndOfMedia(() -> {
    				player.stop();
				});    
 *  		ABER wir haben dann keine ÜBERLAGERUNG. Also der spielt nur den sound für EINE Reihe ab
 *  		aber nicht den gleichen sound für eine kurz darauf folgende 2. Reihe
 *  		dazu müssten wir evtl jedes mal einen neuen Player initialisieren?!?
 *  		könnte speicher fressen aber müsste gehen.. 
 *  		--> Sounds jetzt halbwegs ok; Überlagerung geht aber stockt manchmal doch noch wenn zuviel oO'
 *  			fine-tuning später
 *  
 *  
 *   	  Für Effects müssen wir noch den ORT bestimmen können
 *   	  wir wissen zwar wohin mit der Animation aber wir müssen auch 3,4,5er etc
 *   	  auseinander halten können
 *   	  Auch mal an der Größe des Effekts arbeiten; fettere Reihen sollen auch fettere Effekte bringen
 *   		--> erstmal 3er und 4er+ Effekte; geht gut jetzt

 *
 *	TODO: Player-Opponent-Boards mit Image, Name, Mana-Progress-Bars und HP
 *			Über und unter der Screen Menu, Options und Help 
 *			Die Progress-Bar können wir ähnlich machen wie die Volume-Bar
 *			Für den Anfang mal 1/50 Mana
 *			Die Bars VERTIKAL; Glitzer-Bars wären gut    
 *			
 * 
 * TODO: Movement Rules (die später weil es dann aufwändiger wird zu testen)
 * * wenn < 3 gleiche Symbole in Reihe dann keinen Move erlauben (gibt Penalty und move return)
 * --> also einfach testen ob NACH dem "FixedMove" ein Match enstanden ist
 * 		--> wenn ja ok
 * 		--> wenn nein --> Penalty!(also Hinweis + Geräusch) 
 * 			Dann die Mana zurücksetzen!
 * 
 * dann müssen wir nach JEDEM move die gesamte "Matrix" auslesen
 * wenn keine Move mehr möglich ist --> Mana-Katastrophe (kompletter reset)
 * 
 * TODO: Mana-Katastrophen-Test
 * --> das wird übel weil wir ALLE möglichen nächsten Zug-Möglichkeiten
 *     darauf testen müssen ob es überhaupt eine Lösung im nächsten Zug geben kann oO'
 *     JEDE Mana kann ja immer nur EINEN Schritt weit von ihrer Position weiter (Movement Rules)
 *     das muss nach jedem Zug getestet werden
 *     --> hier gibt es ja noch keine "Spezial-Aktionen" sonst müssten wir die auch
 *     		mit einbeziehen (also sowas wie: "Sprenge komplette Reihe" oder "Alle gelbe Mana wird Rot" etc)
 *     
 * TODO: Methode für Sounds mit MediaPlayer    
 *     	--> wir haben ja nur 1-2 Sounds bisher aber muss man für jeden
 *     		eine neuen MediaPlayer erstellen? Mal sehen ob es nicht eine bessere lösung
 *     		dafür gibt oO'
 *      --> Timing für sounds noch nicht gut (aber erträglich für den Anfang ^^')
 *      
 * TODO: Sound-Volume für BG Music und Spiel-Effekte!!
 * 			--> Natürlich getrennt für Music und Effects!!  
 *        
 *        
 * TODO: Game-Title on main-Screen ("PuzzleMania"??! "Mana Battle"?! "Mana Battle Mania"?!?! ^^')       
 * 			Oder noch besser: 
 * 				Ein Intro wie: Mana --> Battle --> Mania      
 */

/* Object-Switch-Effekt
 * hier brauchen wir sowas wie eine TranslateTransition
 * wir können die Werte der alten und der neuen Postion austauschen
 * also diese für KeyValues für KeyFrames verwenden
 * 
 * --> den Effekt auf die "animationPane" setzen
 * 
 *   in etwa so:
 *   	List<KeyValue> kvLeft = Arrays.asList(
		new KeyValue(leftPT.ulxProperty(), rightPT.getUlx(), Interpolator.LINEAR),
		...);
		KeyValue kvTransXLeft = new KeyValue(currPos.get(0).translateXProperty(),
		currPos.get(2).translateXProperty().get());
		
		--> siehe "vonMTGPercScrollMenu" ~ Zeile 800
 *   
 *    * --> möglichweise brauchen wir das garnicht
 * 		das Gehirn sorgt eigentlich schon für den "Switch-Effekt" ^^'
 * 
 *      PseudoCode: Node und Nachbar-Node über ID ansteuern (OK)
 *      			Inhalte austauschen (in "Richtung" der Cursor-Bewegung) (OK)
 *      			Das austauschen der Inhalte aber "unsichtbar" machen (nicht nötig)
 *      			Die Animation auf einer sonst nicht sichtbaren weitern Pane zeigen (vielleicht später)
 *      			nach Ende der Animation wieder GamePane zeigen (also die Noden die wir verteckt haben)
 *      
 *      --> wir brauchen eine "AnimationPane" (möglicherweise sogar mehrere oO')
 *      	erstmal eine für Movements im Spielfeld
 * 
 * die ImageViews haben ja keine "feste" Position da wir die ständig verschieben
 * die Inhalte ändern etc
 * 
 * 
 * 	// nur zum antesten (das können wir hier NICHT machen --> das muss auf die "animationPane")
//	Timeline myTimeLine = new Timeline();
//	KeyValue kv = new KeyValue(rectOld.translateXProperty(),rectOld.getWidth()+10, Interpolator.EASE_BOTH);
//	KeyFrame kf = new KeyFrame(Duration.millis(250), kv);
//	myTimeLine.getKeyFrames().add(kf);
//	myTimeLine.play();
 * 
 * nice effekt for build up of screen:
 * Splash screen:
 * https://www.genuinecoder.com/javafx-splash-screen-loading-screen/
 * 
 * für Initialisierbare Klassen (brauchen wir hier wahrscheinlich nicht aber ist interessant)
 * public class MyClass implements Initializable
	https://stackoverflow.com/questions/42942505/how-to-run-a-method-in-javafx-upon-the-opening-of-a-new-scene
 * 
 */

public class PuzzleManiaAppV16 extends Application {

	// für JavaFX
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private Scene scene;

	private Pane introPane;
	private Pane mainMenuPane;
	private Pane optionsMenuPane;
	private GridPane gamePane;
	private GridPane cursorPane;
	private Pane animationPane;
	private Pane playerPaneLeft;
	private Pane playerPaneRight;

	private Timeline timelineBtnBlink;
	
	// für MenueSounds nochmal eigene Liste?!?
	private MediaPlayer mediaPlayerBeep;
	private Media mediaBeep;
	private File fBeepSound;
	
	MediaPlayer fManaExplSound; 
	MediaPlayer bgMusic; 
	
	// Liste mit Background-Music 
	private List<String> lstBGMusic = Arrays.asList(
				"SunriseOnTheTrain.mp3",			
				"DDNeon_GladIAm.mp3",
				"PMTHardbassJoRoRemix.mp3"
			);
	
	// Liste mit Explosion-sound für mana-matches
	private List<String> lstExplSounds = Arrays.asList(
				"Explosion1.mp3",
				"Explosion2.mp3",
				"Explosion3.mp3"
			);
	
	private boolean fixedCursor;
	// Listen für Animation
	List<Map<String, String>> manaReplaceForAnim; 
	List<String> manaMatchesToRemoveForAnim; 
	// diese Listen brauchen wir um Mana-Match-Längen auszulesen
	List<List<Integer>> lstManaLenRow = new ArrayList<List<Integer>>();
	List<List<Integer>> lstManaLenCol = new ArrayList<List<Integer>>();
	
	// Liste für ManaBarFill (enthält gematchte ImageViewIDs; also "hydra_blue","hydra_green" etc)
	List<String> lstForManaBarFill;
	
	
	// Hintergrund-Bild laden
	String folderToSearch = "resource/images/";
	List<String> imgList = Arrays.asList("hydra_red", "hydra_green", "hydra_blue", "hydra_yellow", "alchemy",
										 "skull");
	
	// Liste mit IDs für PlayerBoard Left and Right
	List<String> lstHpPlayerTxtId = Arrays.asList("playerHPLeft","playerHPRight");
	
	// total HP for both players; maybe change approach later?! for this we need diff. Opponent-Types etc
	int hpTotal = 100;
	// to switch between Players; start at random? see Method manaTest()
	boolean playerTurn;
	// to determine weather its first turn; then we give turn for one Player at random
	boolean isFirstTurn = true;
	
	
	// for testing only
	boolean bgMusicOnOff = true; 
	boolean usingIntro = true;
	
	
	private IntegerProperty initValueProperty = new SimpleIntegerProperty(0);
	private IntegerProperty finalValueProperty = new SimpleIntegerProperty(1);
	private BooleanProperty completedProperty = new SimpleBooleanProperty();
	
	
	// Lists with ManaBarIDs to address them
	List<String> lstManaBarIDsLeft = Arrays.asList(
			"#pbRedLeft",
			"#pbGreenLeft",
			"#pbYellowLeft",
			"#pbBlueLeft"
			);
	
	List<String> lstManaBarIDsRight = Arrays.asList(
			"#pbRedRight",
			"#pbGreenRight",
			"#pbYellowRight",
			"#pbBlueRight"
			);
	
	
	// to use JavaFX-Plotting
	private Parent launchRoot() {

		fBeepSound = new File("resource/audio/popsound.mp3");
		mediaBeep = new Media(fBeepSound.toURI().toString());
		mediaPlayerBeep = new MediaPlayer(mediaBeep);
		mediaPlayerBeep.setVolume(0.1);

		double parentRelSize = 1.25;

		root = new Pane();
		root.setPrefSize(width / parentRelSize, height / parentRelSize);
		root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");

		/* besser hier die Intro-Screen weil wir dann auf "root" arbeiten und nicht auf Scene
		 * hier gutes Beispiel wie man mit Observeable-boolean umgeht:
		 * https://stackoverflow.com/questions/37785689/javafx-listener-to-check-for-a-boolean-value
		 * 
		 * das ist hier vielleicht ein wenig overkill aber in anderen Situationen kann das noch hilfreich sein 
		 * 
		 */
		
		
		/* hier wir das BooleanProperty beobachtet
		 * das IntegerProperty "finalValueProperty" setzen wir in der Methode "launchIntroScreen()"
		 * nach Ablauf des FadeOuts auf "1"
		 * da finalValueProperty dann Equal zu initValueProperty = 1 ist 
		 * bekommen wir "true"
		 * 
		 * 
		 */
		completedProperty.bind(initValueProperty.isEqualTo(finalValueProperty));
		
		if (usingIntro) { // das hier ist nur ob überhaupt ein Intro verwendet werden soll
			
			root.getChildren().add(launchIntroScreen()); // launch MainMenu
			
			completedProperty.addListener((observable, oldValue, newValue) -> {
			    // Only if completed
			    if (newValue) {
			    	scene.setCursor(Cursor.HAND);
			    	// was immer auch passiern soll...
					System.out.println("launching MainMenu");
					root.getChildren().clear(); // clear root (remove introPane)
					root.getChildren().add(launchMainMenu());
			    }
			});
			
		}else {
			root.getChildren().add(launchMainMenu());
		}

		return root;
	}

	// to use JavaFX-Plotting
		private Parent launchIntroScreen() {
	
			double parentRelSize = 1.0;
			introPane = new Pane();
			introPane.setPrefSize(width / parentRelSize, height / parentRelSize);
			introPane.setStyle("-fx-background-color: rgba(0, 0, 0, 1.0);");
			
			//TODO:  Intro Screen mit Timer und Fadeout 
			String folderToSearch = "resource/BGImages/";
			String imageToLoad = "MeisenbergV1.png";
			Image imgLoad = importImage(folderToSearch, imageToLoad);
			
			double imgWidth = imgLoad.getWidth();
			double imgHeight = imgLoad.getHeight();
			
			ImageView imgView = new ImageView();
			imgView.setImage(imgLoad);
	
			// das hier klappt besser bei FullScreen
			double xPos = width / 2 - imgWidth / 2;
			double yPos = height / 2 - imgHeight / 2;
	
			imgView.setTranslateX(xPos);
			imgView.setTranslateY(yPos);
			
			
			String mbProd = "Meisenberg Productions";
			Text txtTop = new Text();
			// Setting the text to be added.
			txtTop.setText(mbProd);
			// text-Editierung
			txtTop.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 50));
			txtTop.setFill(Color.BLACK);
			txtTop.setStrokeWidth(2);
			txtTop.setStroke(Color.WHITE);
			txtTop.setTextAlignment(TextAlignment.CENTER);
			
			
			String presents = "presents";
			Text txtBottom = new Text();
			// Setting the text to be added.
			txtBottom.setText(presents);
			// text-Editierung
			txtBottom.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 30));
			txtBottom.setFill(Color.BLACK);
			txtBottom.setStrokeWidth(2);
			txtBottom.setStroke(Color.WHITE);
			txtBottom.setTextAlignment(TextAlignment.CENTER);
			
	//		txtTop.setTranslateX(xPos-txtTop.getBoundsInLocal().getWidth());
			txtTop.setTranslateX(xPos);
			txtTop.setTranslateY(yPos);
			
	//		txtBottom.setTranslateX(xPos-txtBottom.getBoundsInLocal().getWidth()/4);
			txtBottom.setTranslateX(xPos);
			txtBottom.setTranslateY(yPos);
			
			VBox vBoxIntro = new VBox();
			vBoxIntro.setAlignment(Pos.CENTER);
			
			vBoxIntro.getChildren().addAll(txtTop,imgView, txtBottom);
			
			
			// here FadeIn of IntroScreen
			FadeTransition fadeInIntro = new FadeTransition();  
	          
	        //setting the duration for the Fade transition   
			fadeInIntro.setDuration(Duration.seconds(4));  
	        //setting the initial and the target opacity value for the transition   
			fadeInIntro.setFromValue(0.0);  
			fadeInIntro.setToValue(1.0);  
			fadeInIntro.setNode(vBoxIntro);  
			fadeInIntro.play();  
			
			fadeInIntro.setOnFinished(e -> {
				// here FadeOut of IntroScreen
				FadeTransition fadeOutIntro = new FadeTransition();  
				fadeOutIntro.setDuration(Duration.seconds(3));  
				fadeOutIntro.setFromValue(1.0);  
				fadeOutIntro.setToValue(0.0);  
				fadeOutIntro.setNode(vBoxIntro);  
		        //playing the transition   
				fadeOutIntro.play();  

				// at end of FadeOut communicate to root to initialize MainMenu
				fadeOutIntro.setOnFinished(c ->{
					initValueProperty.set(1);
				});
				
			});
			
			introPane.getChildren().add(vBoxIntro);
			
			return introPane;
		}



	// Method to launch MainMenu
	private Pane launchMainMenu() {

		double parentRelSize = 1.0;
		mainMenuPane = new Pane();
		mainMenuPane.setPrefSize(width / parentRelSize, height / parentRelSize);
		mainMenuPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

		//TODO:  Hintergrund-Bild laden --> sieht unmöglich aus xD
		String folderToSearch = "resource/BGImages/";
		String imageToLoad = "pq17_background.gif";
		Image imgLoad = importImage(folderToSearch, imageToLoad);
		
		String imageToLoad2 = "pq20_background.gif";
		Image imgLoad2 = importImage(folderToSearch, imageToLoad2);
		
		String imageToLoad3 = "pq19_background.gif";
		Image imgLoad3 = importImage(folderToSearch, imageToLoad3);

		double imgWidth = imgLoad.getWidth();
		double imgHeight = imgLoad.getHeight();

		ImageView imgView = new ImageView();
		imgView.setImage(imgLoad);
		
		ImageView imgView2 = new ImageView();
		imgView2.setImage(imgLoad2);
		
		ImageView imgView3 = new ImageView();
		imgView3.setImage(imgLoad3);

		// das hier klappt besser bei FullScreen
		double xPos = width / 2 - imgWidth / 2;
		double yPos = height / 2 - imgHeight / 2;

		imgView.setTranslateX(xPos);
		imgView.setTranslateY(yPos);
		
		imgView2.setTranslateX(xPos+400);
		imgView2.setTranslateY(yPos);
		
		imgView3.setTranslateX(xPos-400);
		imgView3.setTranslateY(yPos);
		
		imgView.setScaleX(6);
		imgView.setScaleY(6);

		imgView2.setScaleX(4);
		imgView2.setScaleY(4);
		
		imgView3.setScaleX(2.5);
		imgView3.setScaleY(2.5);

		Group grpStart = createMenuButton("Start", -150);
		Group grpOptions = createMenuButton("Options", 0);
		Group grpHelp = createMenuButton("Help", 150);
		Group grpExit = createMenuButton("Exit", 300);

		// ----------------- add blink Method to Buttons -----------------------------

		/*
		 * hier noch reaction-sounds see here: http://www.orangefreesounds.com/
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

		// --------------------- add Button Pressed response
		// ---------------------------------
		grpStart.setOnMousePressed(e -> {
			
			// stop MediaPlayer and restart
			if (bgMusicOnOff) {
				bgMusic.stop();
				playBackgroundSound(lstBGMusic.get(0));
			}
			
			// hier Spielstart
			root.getChildren().clear();
			root.getChildren().addAll(launchGameBoard());
			
			// check mana at startup
			manaTest();
			
		});

		grpOptions.setOnMousePressed(e -> {
			// OptionsMenu; müssen wir noch basteln

			root.getChildren().clear();
			root.getChildren().add(launchOptionsMenu());
		});

		grpHelp.setOnMousePressed(e -> {
			/*
			 * auf jeden Fall Keys erklären!
			 * 
			 * vielleicht noch spiel-regeln?!
			 * 
			 */

		});

		grpExit.setOnMousePressed(e -> {
			System.exit(0);
		});

		VBox btnPack = new VBox();
		btnPack.setPrefWidth(300);
		btnPack.setPrefHeight(500);
		btnPack.setAlignment(Pos.CENTER);
		// Setting the space between the nodes of a VBox pane
		btnPack.setSpacing(20);
		btnPack.getChildren().addAll(grpStart, grpOptions, grpHelp, grpExit);

		mainMenuPane.getChildren().addAll(imgView, imgView2, imgView3);
		mainMenuPane.getChildren().add(btnPack);

		btnPack.setTranslateX(width / 2 - btnPack.getPrefWidth() / 2);
		btnPack.setTranslateY(height / 2 - btnPack.getPrefHeight() / 2);

		// only start if not already playing (otherwise you'll get overlay of music)
		
		if (bgMusicOnOff) {
			if (mediaPlayerBeep.getStatus() != MediaPlayer.Status.PLAYING) {
				playBackgroundSound(lstBGMusic.get(1));
			}
		}
		
		// show font-types
//		List<String> allFonts = javafx.scene.text.Font.getFamilies();			 
//		allFonts.stream().forEach(System.out::println);

		return mainMenuPane;
	}
	
	
	private void playBackgroundSound(String soundWant) {
		
		String path = "resource/audio/";
		String soundToLoad = path+soundWant;
		File loadSound = new File(soundToLoad);
		Media mediaManaExpl = new Media(loadSound.toURI().toString());
		bgMusic = new MediaPlayer(mediaManaExpl);
		bgMusic.setVolume(0.25); // ?! somehow connect this to options-menu
		bgMusic.play();
		
		bgMusic.setOnEndOfMedia(() -> {
			// TODO: Fix music replay; klappt so nicht oO'
			bgMusic.play(); // here replay if music end we have to stop it if you change menu e.g if you enter game etc
		});
		
	}

	// method to launch GameScreen
	private List<Parent> launchGameBoard() {
		
		List<Parent> overlayList = new ArrayList<>();
		
		overlayList.add(launchInlay());
		overlayList.add(launchCursorPane());
		overlayList.add(launchAnimationPane());
		overlayList.add(launchPlayerBoardLeft());
		overlayList.add(launchPlayerBoardRight());
		
//		bgMusic.stop();
//		playBackgroundSound(lstBGMusic.get(0));
		
		return overlayList;
	}
	
	// Method to launch Player board (left) (Player Image; HP; Mana-Overview etc)
	private Pane launchPlayerBoardLeft() {
		
		/* TODO: PlayerPane
		 * die PlayerPane soll links vom Spielfeld sein und ein Image des Players zeigen
		 * zudem eine Übersicht über die Mana die gesammelt wurde
		 * auch eine HP- Anzeige
		 * Vielleicht auch sowas wie Rows und Cols (3er 4er 5er..)die man geschafft hat
		 * Später MiniMax-Algorithmus aber dafür brauchen wir erst Movement-Rules
		 * Spezielle "Mana-Zauber" haben wir auch nicht oO' 
		 * also sowas wie "Alle Grüne Mana wird Rot" etc..
		 * 
		 *  --> wir sollten die Methode auch für die OpponentPane verwenden können
		 *  also mit Argumenten wie ImageView, HP, String Name etc
		 *  
		 *  --> später übergreifende Methode für HumanPlayer und PCPlayer (MiniMax-Algo)
		 */
		
		
		double inlayWidth = width/2-500;
		double inlayHeight = height;
//		playerPane = new Pane();
		playerPaneLeft = new VBox(); // try vertical box because this is how the inlay is sorted
		
		playerPaneLeft.setPrefSize(inlayWidth, inlayHeight);
		playerPaneLeft.setStyle("-fx-background-color: rgba(0, 0, 200, 0.9);");

		// für linke Seite brauchen wir keine translate; für die rechte seite später aber schon
		double xPos = 0;
		double yPos = 0;
		
		playerPaneLeft.setTranslateX(xPos);
		playerPaneLeft.setTranslateY(yPos);
		
		
		// das müssen wir später noch anders machen; die HP müssen ja immer aktualisiert werden
//		int hpTotal = 100;
		int hpCurrent = 100;

		String hpLabelTxt = "HP: "+hpCurrent+"/"+hpTotal;
		
		// PlayerName muss auch optional sein
		String playerName = "Dakota";
		
		// create Labels
		Group grpHPLabel = createLabelForPlayerMenu(hpLabelTxt, inlayWidth, lstHpPlayerTxtId.get(0));
		Group grpNameLabel = createLabelForPlayerMenu(playerName, inlayWidth, "playerName");
		
		// create Player Image
		String pathToSearch = "resource/playerImages/";
		String imageToLoad = "Dakota_lvl1.png";
		Image imgLoad = importImage(pathToSearch, imageToLoad);
		
		ImageView playerImageLeft = new ImageView(imgLoad);
		// hier breite und höhe FIX! Wenn wir größere oder kleinere Images verwenden fallen die aus dem Rahmen
		
		playerImageLeft.setFitWidth(inlayWidth);
		playerImageLeft.setFitHeight(inlayWidth);
		playerImageLeft.setPreserveRatio(true); // !!
		
		/* TODO: Rahmen und Image immer mittig der PlayerPane!
		 * 	unsere ImageView ist auf Quadrat gesetzt; 
		 *  viele Images sind aber eher Rechtecke
		 * 	deswegen das ganze etwas adaptiver; 
		 *   extreme Fälle wie VH 1:2 sehen dann eben blöd aus.. bei mir kann jeder machen was er will.. 
		 *   		dann gibts eben viereckige Haie.. ^^' 
		 */
		
		
		HBox hBoxForPlayerImage = new HBox();
		hBoxForPlayerImage.getChildren().add(playerImageLeft);
		
		// hier noch ein Label für die ManaBar "Mana Power"
		
		Group grpManaPowerLabel = createLabelForPlayerMenu("Mana Power", inlayWidth, "manaPower");
		
		/* TODO: create mana-box; jetzt wirds tricky oO'
		 * 
		 * so wie hier:
		 * https://stackoverflow.com/questions/23668273/vertical-progressbar-javafx
		 * 
		 * auch: Klasse "StarCounter" in package "helpfullstuff"
		 * 
		 *		--> erst noch mit reagierenden ManaBars?!
		 *				--> besser erst noch Design
		 *					die ManaBars zu füllen ist nicht so krass; die richtigen Daten abzugreifen aber evtl schon
		 *					wir müssen ja erst klarstellen wie die Bars reagieren sollen
		 *					also z.B. 4 Grün auch 4 in die ManaBar?
		 *					und was passiert wenn die voll sind?
		 *					Was sollen die Alchemy-Symbole machen?
		 *					HP-Abzug bei Schädeln? etc  *AAHHHH *Spass ^^'
		 * 
		 */
		
		List<ProgressBar> lstManaBars = new ArrayList<ProgressBar>();
		for (int i = 0; i < 4; i++) {
			lstManaBars.add(createManaBar(i, true));
		}

		// oder Group?!
		HBox hBoxManaBars = new HBox();
		hBoxManaBars.getChildren().addAll(lstManaBars);
		
		hBoxManaBars.setTranslateX(20);
		hBoxManaBars.setSpacing(10);
		
		Rectangle manaBoxFrame = createFrameForPlayerMenu(inlayWidth, 100);
		manaBoxFrame.setStroke(Color.CYAN);
		manaBoxFrame.setTranslateY(-100);
		
		addColorBGForManaBox(manaBoxFrame);
		
		Group grpManaBox = new Group();
		grpManaBox.getChildren().addAll(manaBoxFrame,hBoxManaBars);
		
		playerPaneLeft.getChildren().addAll(grpHPLabel, grpNameLabel, 
											hBoxForPlayerImage,
											grpManaPowerLabel,
											grpManaBox);
		
		return playerPaneLeft;
	}
	
	
	private Pane launchPlayerBoardRight() {
		
		double inlayWidth = width/2-500;
		double inlayHeight = height;
		playerPaneRight = new VBox(); // try vertical box because this is how the inlay is sorted
		
		playerPaneRight.setPrefSize(inlayWidth, inlayHeight);
		playerPaneRight.setStyle("-fx-background-color: rgba(0, 0, 200, 0.9);");

		/* für linke Seite brauchen wir keine translate; für die rechte seite später aber schon
		 * der Translate müsste der xTranslate vom Spieldfeld + dessen Breite sein
		 */
		double xPos = 1460; // so ungefähr ^^'		
		double yPos = 0;
		
		playerPaneRight.setTranslateX(xPos);
//		playerPaneRight.setTranslateY(yPos);
		
		
		// das müssen wir später noch anders machen; die HP müssen ja immer aktualisiert werden
//		int hpTotal = 100;
		int hpCurrent = 100;

		String hpLabelTxt = "HP: "+hpCurrent+"/"+hpTotal;
		
		// PlayerName muss auch optional sein
		String playerName = "PopeInnocentX";
		
		// create Labels
		Group grpHPLabel = createLabelForPlayerMenu(hpLabelTxt, inlayWidth, lstHpPlayerTxtId.get(1));
		Group grpNameLabel = createLabelForPlayerMenu(playerName, inlayWidth, "playerName");
		
		// create Player Image
		String pathToSearch = "resource/playerImages/";
		String imageToLoad = "PlayerImage_Right_V1.png";
		Image imgLoad = importImage(pathToSearch, imageToLoad);
		
		ImageView playerImageLeft = new ImageView(imgLoad);
		// das hier geht aber später mal wegen der höhe gucken; die Breite wird automatisch angepasst (nee.. breite auch anpassen)
		HBox hBoxForPlayerImage = new HBox();
		hBoxForPlayerImage.getChildren().add(playerImageLeft);
		
		
		// hier noch ein Label für die ManaBar "Mana Power"
		
		Group grpManaPowerLabel = createLabelForPlayerMenu("Mana Power", inlayWidth, "manaPower");
		
		List<ProgressBar> lstManaBars = new ArrayList<ProgressBar>();
		for (int i = 0; i < 4; i++) {
			lstManaBars.add(createManaBar(i, false));
		}

		// oder Group?!
		HBox hBoxManaBars = new HBox();
		hBoxManaBars.getChildren().addAll(lstManaBars);
		
		hBoxManaBars.setTranslateX(20);
//		hBoxManaBars.setPadding(new Insets(10, 10, 10, 10));
		hBoxManaBars.setSpacing(10);
		
		Rectangle manaBoxFrame = createFrameForPlayerMenu(inlayWidth, 100);
		manaBoxFrame.setStroke(Color.CYAN);
		
		addColorBGForManaBox(manaBoxFrame);

		manaBoxFrame.setTranslateY(-100);
		
		Group grpManaBox = new Group();
		grpManaBox.getChildren().addAll(manaBoxFrame,hBoxManaBars);
		
		playerPaneRight.getChildren().addAll(grpHPLabel, grpNameLabel, 
										hBoxForPlayerImage,
										grpManaPowerLabel,
										grpManaBox);
		
		return playerPaneRight;
	}

	
	// Methode um ManaBars aufzufüllen; Aufruf in Methode "manaTest()" !!
	private void updateManaBar(boolean loadManaLeft) {
		/*
		 * hilfreiche listen
		 *    lstManaLenRow (muss nicht)
			  lstManaLenCol (muss nicht)
			  lstForManaBarFill!! (enthält ImageView IDS aller matches)
		 */
		
		// ManaTypen
		List<String> manaTypesMatches = lstForManaBarFill.stream().distinct().collect(Collectors.toList());
		// Menge der ManaTypen (auch andere Symbole wie Schädel oder Alchemy) --> das können wir auch noch verwenden e.g. für HP
		List<Integer> manaTypeMatchAmount = new ArrayList<Integer>();
		for (String string : manaTypesMatches) {
			manaTypeMatchAmount.add((int)lstForManaBarFill.stream().filter(e -> e.equals(string)).count());
		}

		double manaBarSize = 20.0; // Wert der bestimmt wieviel Mana die bar aufnehmen kann
		double manaSingleStep = 1.0/manaBarSize; // einzelner ManaStep
		
		List<String> lstPbarToUse;
		Pane paneToUseForCurrentPlayer;
		Pane paneToUseForCurrentOpponent;
		String hpToReduce;
		if (loadManaLeft) {
			lstPbarToUse = lstManaBarIDsLeft;
			paneToUseForCurrentPlayer = playerPaneLeft;
			paneToUseForCurrentOpponent = playerPaneRight;
			// using # to address Node-ID via lookUp()-Method
			hpToReduce = "#"+lstHpPlayerTxtId.get(1); // get RightPlayerHP for Left-handed player move
		}else {
			lstPbarToUse = lstManaBarIDsRight;
			paneToUseForCurrentPlayer = playerPaneRight;
			paneToUseForCurrentOpponent = playerPaneLeft;
			hpToReduce = "#"+lstHpPlayerTxtId.get(0); // get LeftPlayerHP for Rightt-handed player move
		}
		
		
		int manaGet = 0;
		for (String manaType : manaTypesMatches) {
			ProgressBar currPB;
			switch (manaType) {
				// loading Mana
				case "hydra_red":
					currPB = (ProgressBar) paneToUseForCurrentPlayer.lookup(lstPbarToUse.get(0));
					currPB.setProgress(currPB.getProgress()+manaSingleStep*manaTypeMatchAmount.get(manaGet));
					break;
				case "hydra_green":
					currPB = (ProgressBar) paneToUseForCurrentPlayer.lookup(lstPbarToUse.get(1));
					currPB.setProgress(currPB.getProgress()+manaSingleStep*manaTypeMatchAmount.get(manaGet));
					break;
				case "hydra_yellow":
					currPB = (ProgressBar) paneToUseForCurrentPlayer.lookup(lstPbarToUse.get(2));
					currPB.setProgress(currPB.getProgress()+manaSingleStep*manaTypeMatchAmount.get(manaGet));
					break;
				case "hydra_blue":
					 currPB = (ProgressBar) paneToUseForCurrentPlayer.lookup(lstPbarToUse.get(3));
					 currPB.setProgress(currPB.getProgress()+manaSingleStep*manaTypeMatchAmount.get(manaGet));
					break;
				// Special: Skulls reduce HP; Alchemy??! block manaLoad of opponent? power up own ManaLoad?! ähh ^^'
				case "alchemy":
					
					break;
				case "skull":
					// here address HP of other player
				updateHP(manaTypeMatchAmount, paneToUseForCurrentOpponent, hpToReduce, manaGet);
					break;
				default:
					break;
				}
			manaGet++;
		}
	}

	private void updateHP(List<Integer> manaTypeMatchAmount, Pane paneToUseForCurrentOpponent, String hpToReduce, int manaGet) {
		Text currTxt = (Text) paneToUseForCurrentOpponent.lookup(hpToReduce);
		String currHP = currTxt.getText();
		currHP = currHP.substring(4,currHP.indexOf("/"));
		Integer currHPNum = Integer.parseInt(currHP);
		currHPNum-=manaTypeMatchAmount.get(manaGet);
		String hpLabelTxt = "HP: "+currHPNum+"/"+hpTotal;
		currTxt.setText(hpLabelTxt);
	}
	
	
	// Methode um ManaBars zu erstellen
	private ProgressBar createManaBar(int manaColor, boolean sideLeft) {
		
		double elemsWidth = 100;
		double elemsHeight = 75;
		double initValue = 0.0;

		/*
		 * eine ProgressBar geht immer von 0-1 weil 1 meint der Prozess ist beendet d.h.
		 */
		final ProgressBar pb = new ProgressBar(0);
		pb.setMinWidth(elemsWidth);
		pb.setMaxWidth(elemsWidth);
		pb.setMinHeight(elemsHeight);
		pb.setMaxHeight(elemsHeight);
		pb.setProgress(initValue);
		
		pb.getTransforms().setAll(
	                new Rotate(-90, 0, 0)
	        );
		
		/* CSS-Colors: https://www.javatpoint.com/css-colors
		 * wir haben mal basic genommen aber da geht noch mehr..
		 * ManaFarben:
		 * Rot, Grün, Gelb, Blau
		 * 
		 * --> evtl man von rgb zu hexa-methode.. müssten wir noch irgendwo haben..oder aus dem netz fischen
		 * 
		 * direkt RGBA geht aber auch ^^'
		 * 
		 * css style auch:
		 * https://stackoverflow.com/questions/13357077/javafx-progressbar-how-to-change-bar-color/13372086#13372086
		 * 
		 * https://docs.oracle.com/javafx/2/css_tutorial/jfxpub-css_tutorial.htm
		 * https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html
		 * 
		 * das hier ist auch krass: oO'
		 * http://hg.openjdk.java.net/openjfx/2.2/master/rt/raw-file/tip/javafx-ui-controls/src/com/sun/javafx/scene/control/skin/caspian/caspian.css
		 * 
		 */
			
			String side; 
			String manaBarID;
			
			if (sideLeft) { 
				 side = "Left";
			}else { 
				 side = "Right";
			}
			
			
			switch (manaColor) {
				case 0:
					pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
							  + "border-style: dashed;"
							  + "border-width: medium; "
							  + "-fx-base: rgba(255, 255, 255, 1.0);"
							  + "-fx-accent: rgba(255, 0, 0, 1.0);");
					manaBarID = "pbRed"+side;
					pb.setId(manaBarID);
					break;
				case 1:
					pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
							  + "-fx-border-radius: 5"
							  + "-fx-base: rgba(255, 255, 255, 1.0);"
							  + "-fx-accent: rgba(0, 255, 0, 1.0);");
					manaBarID = "pbGreen"+side;
					pb.setId(manaBarID);
					break;
				case 2:
					pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
							  + "border-style: solid;"
							  + "border-width: 10px; "
							  + "-fx-base: rgba(255, 255, 255, 1.0);"
							  + "-fx-accent: rgba(255, 200, 0, 1.0);"); 
					manaBarID = "pbYellow"+side;
					pb.setId(manaBarID);
					break;
				case 3:
					pb.setStyle("-fx-border-color: rgba(0, 0, 0, 1.0);"
							  + "border-style: solid;"
							  + "border-width: 10px; "
							  + "-fx-base: rgba(255, 255, 255, 1.0);"
							  + "-fx-accent: rgba(0, 0, 255, 1.0);");
					manaBarID = "pbBlue"+side;
					pb.setId(manaBarID);
					break;
				default:
					break;
			}
			
		return pb;
	}

	private Group createLabelForPlayerMenu(String txtLabel,double inlayWidth, String labelID) {
		Text txt = createTextForPlayerMenu(txtLabel);
		txt.setId(labelID);
		
		// HBox as container vor text
		HBox hBox = new HBox();
		hBox.getChildren().add(txt);
		hBox.setAlignment(Pos.CENTER);
		hBox.setTranslateX(inlayWidth/2 - hBox.getBoundsInLocal().getWidth()/2);
		
		// Container für Text; Text ohne Container zu platzieren ist echt fies ^^'
		Rectangle txtFrame = createFrameForPlayerMenu(inlayWidth, txt.getLayoutBounds().getHeight()+10);
		txtFrame.setStroke(Color.CYAN);
		
		addColorBGForPlayerMenu(txtFrame);
		
		Group grp = new Group();
		grp.getChildren().addAll(txtFrame,hBox);
		
//		grp.setId(labelID);
		return grp;
	}

	// Method to create Background-Gradient color for ManaBox
	private void addColorBGForManaBox(Rectangle rect) {
		Stop[] stops = new Stop[] { new Stop(0, Color.STEELBLUE), new Stop(1, Color.WHITE) };
		// gradient radial from center
		
		/*public RadialGradient​(double focusAngle,
                      double focusDistance,
                      double centerX,
                      double centerY,
                      double radius,
                      boolean proportional,
                      CycleMethod cycleMethod,
                      Stop... stops)
                      
          --> you have 2 centers in this; interesting for effects but not here 
		 */
		
		RadialGradient rdgnt = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);
		
		rect.setFill(rdgnt);
	}
	
	
	// Method to create Background-Gradient color for Textframe
	private void addColorBGForPlayerMenu(Rectangle rect) {
		Stop[] stops = new Stop[] { new Stop(0, Color.STEELBLUE), new Stop(1, Color.WHITE) };
		// gradient radial from center
		
		/*public RadialGradient​(double focusAngle,
                      double focusDistance,
                      double centerX,
                      double centerY,
                      double radius,
                      boolean proportional,
                      CycleMethod cycleMethod,
                      Stop... stops)
                      
          --> you have 2 centers in this; interesting for effects but not here 
		 */
		
		RadialGradient rdgnt = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);
		
		rect.setFill(rdgnt);
	}

	private Text createTextForPlayerMenu(String hpString) {
		Text txt = new Text();
		// Setting the text to be added.
		txt.setText(hpString);
		// text-Editierung
		txt.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 30));
		txt.setFill(Color.BLACK);
		txt.setStrokeWidth(2);
		txt.setStroke(Color.WHITE);
		txt.setTextAlignment(TextAlignment.CENTER);
		return txt;
	}
	
	
	// Methode um textframe zu erstellen
	private Rectangle createFrameForPlayerMenu(double bWidth, double bHeight) {
		Rectangle txtFrame = new Rectangle(bWidth, bHeight, Color.TRANSPARENT);
		txtFrame.setStrokeWidth(5);
		txtFrame.setArcWidth(10.0);
		txtFrame.setArcHeight(10.0);
		return txtFrame;
	}
	
	

	// Method to launch OptionsMenu
	private Pane launchOptionsMenu() {

		double parentRelSize = 1.0;
		optionsMenuPane = new Pane();
		optionsMenuPane.setPrefSize(width / parentRelSize, height / parentRelSize);
		optionsMenuPane.setStyle("-fx-background-color: rgba(120, 125, 0, 0.4);");

		// Hintergrund-Bild laden
		String folderToSearch = "resource/BGImages/";
		String imageToLoad = "pq14_background.gif";
		Image imgLoad = importImage(folderToSearch, imageToLoad);

		double imgWidth = imgLoad.getWidth();
		double imgHeight = imgLoad.getHeight();

		ImageView imgView = new ImageView();
		imgView.setImage(imgLoad);

		// das hier klappt besser bei FullScreen
		double xPos = width / 2 - imgWidth / 2;
		double yPos = height / 2 - imgHeight / 2;

		imgView.setTranslateX(xPos);
		imgView.setTranslateY(yPos);

		imgView.setScaleX(3);
		imgView.setScaleY(3);

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

		/*
		 * eine ProgressBar geht immer von 0-1 weil 1 meint der Prozess ist beendet d.h.
		 * wir müssen die übergebenen Werte des Sliders umrechnen haben wir im Listener
		 * (unten) gemacht
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
//						System.out.println("Slider Value Changed (newValue: " + newValue.doubleValue() + ")");
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

		// wichtig! wenn wir den Player nicht stoppen kann er auch nicht neu gestartet
		// werden!
		slider.setOnMouseExited(e -> {
			mediaPlayerBeep.stop();
		});

		Group grpBar = new Group(pb, slider);

		Text lblTxt = new Text("Volume:");
		txtEdit(lblTxt);

		// auf Mitte setzen
		double xPosMid = width / 2;
		double yPosMid = height / 2;

		double xShiftBar = -250;
		double yShiftBar = -200;

		HBox hboxVol = new HBox(lblTxt, grpBar);
		hboxVol.setAlignment(Pos.CENTER); // !!
		hboxVol.setTranslateX(xPosMid + xShiftBar);
		hboxVol.setTranslateY(yPosMid + yShiftBar);

		// set CSS-Style
		File f = new File("resource/css/style.css");
		optionsMenuPane.getStylesheets().add(f.toURI().toString());

		optionsMenuPane.getChildren().addAll(imgView, hboxVol, grpBack);

		return optionsMenuPane;
	}

	private GridPane launchInlay() {
		
		double inlaySize = 1000;
		gamePane = new GridPane();
		gamePane.setPrefSize(inlaySize, inlaySize);
		gamePane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);");

		// das hier klappt besser bei FullScreen
		double xPos = width / 2 - inlaySize / 2;
		double yPos = height / 2 - inlaySize / 2;

		gamePane.setTranslateX(xPos);
		gamePane.setTranslateY(yPos);

		gamePane.setHgap(5);
		gamePane.setVgap(5);
		gamePane.setPadding(new Insets(5, 5, 5, 5));

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				String idRow = String.format("%d", i);
				String idCol = String.format("%d", j);
				String id = idRow + idCol;
//				System.out.println("id: " + id);
				gamePane.add(createHBoxForGamePane(id), j, i); // gamePane uses add(Node,x,y)
			}
		}
		
		// TODO: hier gamePane Visibility
//		gamePane.setVisible(false);

		return gamePane;
	}

	// Methode die die Cursor-Pane initialisiert
	private GridPane launchCursorPane() {

		/*
		 * der Cursor soll frei verschiebar sein und eine Resonanz geben wenn SPACE
		 * gedrückt wird die Resonanz soll mit SPACE auch wieder deaktiert werden können
		 * also erstmal sowas wie Farbe1--> Farbe2--> Farbe1
		 * 
		 */

		double inlaySize = 1000;
		cursorPane = new GridPane();
		cursorPane.setPrefSize(inlaySize, inlaySize);
		cursorPane.setStyle("-fx-background-color: rgba(255, 255, 255, .2);");

		cursorPane.setHgap(5);
		cursorPane.setVgap(5);
		cursorPane.setPadding(new Insets(5, 5, 5, 5));

		// das hier klappt besser bei FullScreen
		double xPos = width / 2 - inlaySize / 2;
		double yPos = height / 2 - inlaySize / 2;

		cursorPane.setTranslateX(xPos);
		cursorPane.setTranslateY(yPos);

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				String idRow = String.format("%d", i);
				String idCol = String.format("%d", j);
				String id = idRow + idCol;
//				System.out.println("id: " + id);

				cursorPane.add(createRectangleForCursorPane(id), j, i); // Gridpane uses add(Node,x,y)
			}
		}

		// erstellt cursor an position 00 (evtl ein random 0-7 für row und cols?!)

		int randomRow = getRandomNumberInRange(0, 7);
		int randomCol = getRandomNumberInRange(0, 7);
		String randomCursorPos = String.valueOf(randomRow) + String.valueOf(randomCol);

		setCursorMarker("00");

		return cursorPane;
	}

	private Pane launchAnimationPane() {

		/* TODO: Animation-Pane for advanced Animations
		 * die animationPane ist "einfach nur da"
		 * sie dient als Contaienr um Animationen zu einem bestimmten zeitpunkt darzustellen
		 * evtl können wir die auch zeitweise unsichtbar machen
		 * vielleicht brauchen wir sogar mehrere animationPanes um diverse Effekte überlagern zu können
		 * 
		 * wir probieren erstmal eine ^^'
		 * 
		 */
		double inlaySize = 1000;
		animationPane = new Pane();
		animationPane.setPrefSize(inlaySize, inlaySize);
		animationPane.setStyle("-fx-background-color: rgba(0, 0, 120, 0);");

		double xPos = width / 2 - inlaySize / 2;
		double yPos = height / 2 - inlaySize / 2;

		animationPane.setTranslateX(xPos);
		animationPane.setTranslateY(yPos);
		
		// animationPane nur zeigen wenn Effekt aktiviert ist
		animationPane.setVisible(false);
		
		return animationPane;
	}
		
	
	
	private void startColRefillAnim2() {
		
		if (!manaReplaceForAnim.isEmpty()) {
//			System.out.println("matches to remove: ");
//			manaReplaceForAnim.stream().forEach(System.out::println);
	
			animationPane.setVisible(true);
			
			List<HBox> lstCurrBoxesToHide = new ArrayList<HBox>();
			List<HBox> lstBoxesToMove = new ArrayList<HBox>();
			List<KeyValue> lstKValuesVertical = new ArrayList<KeyValue>();
			List<KeyValue> lstKValuesHorizontal = new ArrayList<KeyValue>();
			List<KeyFrame> lstKFrames = new ArrayList<KeyFrame>();
			
			// TODO: see here if we get intel that we can use for Mana-Match-size
			// here get IDs which are supposed to move within the field (Mana which is NOT new generated)
			List<Map<String, String>> manaToMove = new ArrayList<Map<String,String>>();
			for (int i = 0; i < manaReplaceForAnim.size(); i++) {
				Map<String, String>  map = manaReplaceForAnim.get(i);
				System.out.println(map);
				for (String string : manaMatchesToRemoveForAnim) {
				Iterator<Entry<String, String>> itr = map.entrySet().iterator();
					while(itr.hasNext()){
					   Entry<String, String> entry = itr.next();
					   if (entry.getKey().equals(string) ) {
//						 System.out.println("Key : "+ entry.getKey()+" Removed.");
						 itr.remove();  // Call Iterator's remove method.
					   }
					}
				}
					
				manaToMove.add(map);
//				System.out.println("cleared ManaList: " + i);
//				manaToMove.stream().forEach(System.out::println);
			}
			
			
			// here prepare movement of present mana which is supposed to move
			for (int i = 0; i < manaToMove.size(); i++) {
				Map<String, String>  map = manaToMove.get(i);
				for (Entry<String, String> entry : map.entrySet()) {
					// diese HBox müssen wir "verstecken"
					lstCurrBoxesToHide.add((HBox) gamePane.lookup(entry.getKey()));
					// hier wird eine NEUE HBox erstellt 
					lstBoxesToMove.add(createHBoxForAnimationPane(entry.getValue()));
				}
			}
			
			moveMana(lstCurrBoxesToHide, lstBoxesToMove, lstKValuesVertical, lstKValuesHorizontal, lstKFrames);
			
			
		} else {
			System.out.println("manaReplaceForAnim- List empty");
		}
	}

	// method to animated movement of mana 
	private void moveMana(List<HBox> lstCurrBoxesToHide, List<HBox> lstBoxesToMove,
			List<KeyValue> lstKValuesVertical,List<KeyValue> lstKValuesHorizontal, List<KeyFrame> lstKFrames) {
		
		// KValues for vertical position
		for (int i = 0; i < lstBoxesToMove.size(); i++) {
			lstKValuesVertical.add(new KeyValue(lstBoxesToMove.get(i).translateYProperty(),
					lstCurrBoxesToHide.get(i).getLocalToParentTransform().getTy(),
									    Interpolator.EASE_BOTH));
		}
			
		for (int i = 0; i < lstKValuesVertical.size(); i++) {
			lstKFrames.add(new KeyFrame(Duration.millis(250), lstKValuesVertical.get(i)));
		}
		
		// KValues for horizontal position
		for (int i = 0; i < lstBoxesToMove.size(); i++) {
			lstKValuesHorizontal.add(new KeyValue(lstBoxesToMove.get(i).translateXProperty(),
					lstCurrBoxesToHide.get(i).getLocalToParentTransform().getTx(),
									    Interpolator.EASE_BOTH));
		}
		
		// we just move at super-fast speed to the goal-position.. might not be the best approach oO'
		for (int i = 0; i < lstKValuesHorizontal.size(); i++) {
			lstKFrames.add(new KeyFrame(Duration.millis(.1), lstKValuesHorizontal.get(i)));
		}
		
		for (HBox hBox : lstCurrBoxesToHide) {
			hBox.setVisible(false);
		}
		
		animationPane.getChildren().addAll(lstBoxesToMove);
		
		Timeline myTimeLine = new Timeline();
		myTimeLine.getKeyFrames().addAll(lstKFrames);
		
		myTimeLine.setOnFinished(e -> {
			animationPane.setVisible(false);
			animationPane.getChildren().clear();
			for (HBox hBox : lstCurrBoxesToHide) {
				hBox.setVisible(true);
			}
			
			// mana-test after animation		
			manaTest();
		  });
		
		myTimeLine.play();
	}
	
	// Methode die Animation für Reihen-matches auf der Animation-Pane zeigt
	private void matchDeleteAnim() {
		
		if (!manaMatchesToRemoveForAnim.isEmpty()) { // // diese Liste wird in der Methode manaTest() aktualisiert

			
			/* mit den Listen 
			 *  lstManaLenRow und
			 *  lstManaLenCol
			 *  manaMatchesToRemoveForAnim
			 *  
			 *  können wir die Anzahl und die Größe der Mana-Matches bestimmen
			 */
	
			animationPane.setVisible(true);
			
			List<HBox> lstCurrBoxes = new ArrayList<HBox>();
			
			for (String entry : manaMatchesToRemoveForAnim) {
				// diese HBox müssen wir "verstecken"
				lstCurrBoxes.add((HBox) gamePane.lookup(entry));
			}
		
			List<ImageView> lstAnimObj = new ArrayList<ImageView>();	
			for (int i = 0; i < lstCurrBoxes.size(); i++) {
				
				
				 double posX = 0.0D; 
				 double posY = 0.0D; 
				
				ImageView imgView = new ImageView();
				
				String img2Load = "";
				if(lstManaLenRow.stream().anyMatch(e -> e.size() == 3)
						|| lstManaLenCol.stream().anyMatch(e -> e.size() == 3)	) {
					 img2Load = "expl3.gif";
					 
					 posX = lstCurrBoxes.get(i).getLocalToParentTransform().getTx()-lstCurrBoxes.get(i).getWidth()*1.25;
					 posY = lstCurrBoxes.get(i).getLocalToParentTransform().getTy()-lstCurrBoxes.get(i).getHeight()*1.5;
					 
					 Image imgLoad = importImage(folderToSearch, img2Load);
					 imgView.setImage(imgLoad);
					 
					 imgView.setScaleX(.25);
					 imgView.setScaleY(.25);
					 
					}
				
				// hier erstmal einen Effect für >= 4; später vielleicht noch einen für >4 ?!
				if(lstManaLenRow.stream().anyMatch(e -> e.size() >= 4)
						|| lstManaLenCol.stream().anyMatch(e -> e.size() >= 4)	) {
					 img2Load = "expl5.gif";
					 
					  posX = lstCurrBoxes.get(i).getLocalToParentTransform().getTx()
							  -lstCurrBoxes.get(i).getWidth()*.25;
					  posY = lstCurrBoxes.get(i).getLocalToParentTransform().getTy()
							  -lstCurrBoxes.get(i).getHeight()*.5;
					 
					 
					 Image imgLoad = importImage(folderToSearch, img2Load);
					 imgView.setImage(imgLoad);
					 
					 imgView.setScaleX(2);
					 imgView.setScaleY(2);
					 
					}
				
				// position
				imgView.setTranslateX(posX);
				imgView.setTranslateY(posY);
				
				lstAnimObj.add(imgView); 
				
			}
					
			for (HBox hBox : lstCurrBoxes) {
				hBox.setVisible(false);
			}
					
			animationPane.getChildren().addAll(lstAnimObj);
			
			Timeline myTimeLine = new Timeline();
			double timeWin = 500.0;
		    KeyFrame keyFrameExpl = new KeyFrame( Duration.millis(timeWin), 
		            new EventHandler<ActionEvent>() {
		                @Override
		                public void handle(ActionEvent event) {
		                    System.out.println("Event");
		                }
		            }
		        );
			   
			myTimeLine.getKeyFrames().setAll(keyFrameExpl);			
			
			myTimeLine.setOnFinished(e -> {
				animationPane.setVisible(false);
				animationPane.getChildren().clear();
				for (HBox hBox : lstCurrBoxes) {
					hBox.setVisible(true);
				}
		
				// die Reihen erst aufüllen wenn Explosions-Animation beendet
				startColRefillAnim2();
				
			  });

			// here start sound
			
			// Play Sound for Match == 3
			if(lstManaLenRow.stream().anyMatch(e -> e.size() == 3)
				|| lstManaLenCol.stream().anyMatch(e -> e.size() == 3)	) {
				playExplSound(lstExplSounds.get(2));
			}
			
			// Play Sound for Match == 4
			if(lstManaLenRow.stream().anyMatch(e -> e.size() == 4)
					|| lstManaLenCol.stream().anyMatch(e -> e.size() == 4)	) {
				playExplSound(lstExplSounds.get(0));
			}
			
			// Play Sound for Match > 4
			if(lstManaLenRow.stream().anyMatch(e -> e.size() > 4)
					|| lstManaLenCol.stream().anyMatch(e -> e.size() > 4)	) {
				playExplSound(lstExplSounds.get(1));
			}
			
			myTimeLine.play();
		
		} else {
			System.out.println("manaMatchesToRemoveForAnim- List empty");
		}
	}
	
	
	private void playExplSound(String soundWant) {
		
		String path = "resource/audio/";
		String soundToLoad = path+soundWant;
		File loadSound = new File(soundToLoad);
		Media mediaManaExpl = new Media(loadSound.toURI().toString());
		fManaExplSound = new MediaPlayer(mediaManaExpl);
		fManaExplSound.setVolume(0.5); // ?! somehow connect this to options-menu
		fManaExplSound.play();
		
		fManaExplSound.setOnEndOfMedia(() -> {
			fManaExplSound.stop();
		});
		
	}
	
	
	private HBox createHBoxForAnimationPane(String id) {

		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;
		double rectSize = parentSize / nodeAmount - parentPaddingSize;

		ImageView imgView = new ImageView();

		// load Image
		Image imgLoad = importImage(folderToSearch, id);

		// Image an ImageView
		imgView.setImage(imgLoad);
		// ImgeView Namen des Image geben
		imgView.setId(id);

		// ImageView-Größe anpassen (die HBox passt sich dann automatisch an)
		imgView.setFitHeight(rectSize);
		imgView.setFitWidth(rectSize);

		// ImageView and die HBox
		HBox hbox = new HBox(imgView);
		hbox.setId(id); // HBox ID geben

		return hbox;
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
		 * nach unten: if (rowPos <= 6) --> rowPos++; nach rechts: if (colPos <= 6) -->
		 * colPos++; nach oben: if (rowPos > 0) --> rowPos--; nach links: if (colPos >
		 * 0) --> colPos--;
		 * 
		 */

		// state of cursor independent of other directions
		switch (direction) {
		case "SPACE":
			System.out.println("SPACE");
			Rectangle rectCurrent = (Rectangle) cursorPane.lookup(cursorPosId);
			if (rectCurrent != null) {
				if (rectCurrent.getStroke() == Color.BLACK) {
					fixedCursor = true;
					rectCurrent.setStroke(Color.RED);
				} else {
					fixedCursor = false;
					rectCurrent.setStroke(Color.BLACK);
				}

			} else {
				System.out.println("is null");
			}
			break;
		default:
			break;
		}

		// normal (unfixed) movement of cursor
		if (!fixedCursor) {
			unFixedCursorMove(direction, cursorPosId, rowPos, colPos);
			
			
		} else if (fixedCursor) {

			fixedCursorMove(direction, cursorPosId, rowPos, colPos);
			// Mana-Test
			manaTest();
			System.out.println("Mana-Matches present at fixedCursorMove: " +
					manaMatchesToRemoveForAnim.isEmpty());
			
			// SWITCH OF PLAYER ; happens if no manaMatches can be found following manaTest()
			if (manaMatchesToRemoveForAnim.isEmpty()) {
				playerTurn = !playerTurn;
				System.out.println("playerTurn: " + playerTurn);
			}
			
			
		}
	}

	private void unFixedCursorMove(String direction, String cursorPosId, int rowPos, int colPos) {

		switch (direction) {
		case "UP":
			if (rowPos > 0) {
				System.out.println("UP");
				// hier Color der alten position zurücksetzen
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				if (rectOld != null) {
					resetRectangle(rectOld);
				} else {
					System.out.println("is null");
				}

				// positions-ID ändern
				rowPos--;
				String newID = "#" + rowPos + colPos;

				// cursor-farbe setzen
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}

			}
			break;
		case "DOWN":
			if (rowPos <= 6) {
				System.out.println("DOWN");

				// hier Color der alten position zurücksetzen
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				if (rectOld != null) {
					resetRectangle(rectOld);
				} else {
					System.out.println("is null");
				}
				// positions-ID ändern
				rowPos++;
				String newID = "#" + rowPos + colPos;

				// cursor-farbe setzen
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}

			}
			break;
		case "LEFT":
			System.out.println("LEFT");
			if (colPos > 0) {

				System.out.println("Moving left");
				// hier Color der alten position zurücksetzen
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				if (rectOld != null) {
					resetRectangle(rectOld);
				} else {
					System.out.println("is null");
				}

				// positions-ID ändern
				colPos--;
				String newID = "#" + rowPos + colPos;

				// cursor-farbe setzen
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}

			}
			break;
		case "RIGHT":
			if (colPos <= 6) {

				System.out.println("RIGHT");
				// hier Color der alten position zurücksetzen
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);

				if (rectOld != null) {
					resetRectangle(rectOld);
				} else {
					System.out.println("is null");
				}

				// positions-ID ändern
				colPos++;
				String newID = "#" + rowPos + colPos;

				// cursor-farbe setzen
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}
			}
			break;
		default:
			break;
		}

	}

	private void fixedCursorMove(String direction, String cursorPosId, int rowPos, int colPos) {

		switch (direction) {
		case "UP":
			if (rowPos > 0) {
				System.out.println("UP switch element");
				// positions-ID ändern
				rowPos--;
				String newID = "#" + rowPos + colPos;

				// HBox auf cursor-Position
				HBox hBoxAtCursorPos = (HBox) gamePane.lookup(cursorPosId);
				// HBox rechts vom Cursor
				HBox hBoxRightFromCursorPos = (HBox) gamePane.lookup(newID);
				// flip images
				flipImages(hBoxAtCursorPos, hBoxRightFromCursorPos);

				// den cursor ändern
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);

				if (rectOld != null) {
					resetRectangle(rectOld);
				}
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}
				// den Cursor nach der Bewegung wieder loslassen!
				fixedCursor = false;

			}
			break;
		case "DOWN":
			if (rowPos <= 6) {
				System.out.println("DOWN switch element");
				// positions-ID ändern
				rowPos++;
				String newID = "#" + rowPos + colPos;

				// HBox auf cursor-Position
				HBox hBoxAtCursorPos = (HBox) gamePane.lookup(cursorPosId);
				// HBox rechts vom Cursor
				HBox hBoxRightFromCursorPos = (HBox) gamePane.lookup(newID);
				// flip images
				flipImages(hBoxAtCursorPos, hBoxRightFromCursorPos);

				// den cursor ändern
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);

				if (rectOld != null) {
					resetRectangle(rectOld);
				}
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}
				// den Cursor nach der Bewegung wieder loslassen!
				fixedCursor = false;

			}
			break;
		case "LEFT":
			System.out.println("LEFT switch element");
			if (colPos > 0) {

				// positions-ID ändern
				colPos--;
				String newID = "#" + rowPos + colPos;

				// HBox auf cursor-Position
				HBox hBoxAtCursorPos = (HBox) gamePane.lookup(cursorPosId);
				// HBox rechts vom Cursor
				HBox hBoxRightFromCursorPos = (HBox) gamePane.lookup(newID);
				// flip images
				flipImages(hBoxAtCursorPos, hBoxRightFromCursorPos);

				// den cursor ändern
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);

				if (rectOld != null) {
					resetRectangle(rectOld);
				}
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}
				// den Cursor nach der Bewegung wieder loslassen!
				fixedCursor = false;

			}
			break;
		case "RIGHT":
			if (colPos <= 6) {
				System.out.println("RIGHT switch element");
				// positions-ID ändern
				colPos++;
				String newID = "#" + rowPos + colPos;

				// HBox auf cursor-Position
				HBox hBoxAtCursorPos = (HBox) gamePane.lookup(cursorPosId);
				// HBox rechts vom Cursor
				HBox hBoxRightFromCursorPos = (HBox) gamePane.lookup(newID);
				// flip images
				flipImages(hBoxAtCursorPos, hBoxRightFromCursorPos);

				// den cursor ändern
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);

				if (rectOld != null) {
					resetRectangle(rectOld);
				}
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}
				// den Cursor nach der Bewegung wieder loslassen!
				fixedCursor = false;

			}
			break;
		default:
			break;
		}

	}

	private void flipImages(HBox hBoxAtCursorPos, HBox hBoxRightFromCursorPos) {

		if (hBoxAtCursorPos != null && hBoxRightFromCursorPos != null) {
			ImageView imgC = (ImageView) hBoxAtCursorPos.getChildren().get(0);
			ImageView imgR = (ImageView) hBoxRightFromCursorPos.getChildren().get(0);

			hBoxAtCursorPos.getChildren().clear();
			hBoxRightFromCursorPos.getChildren().clear();

			if (imgC != null && imgR != null) {
				hBoxAtCursorPos.getChildren().add(imgR);
				hBoxRightFromCursorPos.getChildren().add(imgC);
			}

		}

	}

	private Rectangle changeCursorApperance(Rectangle rectNew) {

		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;
		double rectSize = parentSize / nodeAmount - parentPaddingSize;

		double borderSize = 5;
		rectNew.setHeight(rectSize - borderSize);
		rectNew.setWidth(rectSize - borderSize);
		rectNew.setFill(null);
		rectNew.setStroke(Color.BLACK);
		rectNew.setStrokeWidth(5);
		// das hier um das als Cursor zu identifizieren
		rectNew.setFocusTraversable(true);
		rectNew.setVisible(true);
		return rectNew;
	}

	private void resetRectangle(Rectangle rect) {
		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;
		double rectSize = parentSize / nodeAmount - parentPaddingSize;

		double borderSize = 5;
		rect.setHeight(rectSize - borderSize);
		rect.setWidth(rectSize - borderSize);
		rect.setFill(null);
		rect.setStroke(Color.BLACK);
//		rect.setStrokeWidth(5.0);
		// das hier verwenden wir um zu erkenne ob das der cursor ist oder nicht
		rect.setFocusTraversable(false); // false --> NICHT cursor
		rect.setVisible(false); // wir verstecken das aber es ist für die Pane noch vorhanden
	}

	private List<Integer> getCursorPosRowCol() {

		List<Integer> rowColPos = new ArrayList<>();

		List<Node> opt = cursorPane.getChildren().stream().filter(n -> "Rectangle".equals(n.getClass().getSimpleName()))
				.collect(Collectors.toList());

		for (Node node : opt) {
			Rectangle rec = (Rectangle) node;

			if (rec.isFocusTraversable()) { // nur Cursor hat isFocusTraversable --> TRUE
				String id = rec.getId();
				char rowIs = id.charAt(0);
				char colIs = id.charAt(1);
				rowColPos.add(Integer.parseInt(String.valueOf(rowIs)));
				rowColPos.add(Integer.parseInt(String.valueOf(colIs)));
			}
		}

		return rowColPos;
	}

	private String getCursorPosID() {

		String id = "";
		List<Node> opt = cursorPane.getChildren().stream().filter(n -> "Rectangle".equals(n.getClass().getSimpleName()))
				.collect(Collectors.toList());

		for (Node node : opt) {
			Rectangle rec = (Rectangle) node;

			if (rec.isFocusTraversable()) { // nur der Cursor hat isFocusTraversable --> TRUE
				id = "#" + rec.getId();
			}
		}
		return id;
	}

	// Methode ManaTest umfasst alle methoden die den Status des Spielbretts testen
	private void manaTest() {
		checkManaConstellation();
		
		manaMatchesToRemoveForAnim = getEmptyIDs();
		manaReplaceForAnim = refillEmptyImgViews(getEmptyIDs());
		
		if (!manaMatchesToRemoveForAnim.isEmpty()) {
			// start Animation if Mana-matches are found
			matchDeleteAnim();
			
			if (isFirstTurn) {
				int randTurn = getRandomNumberInRange(0, 9);
				if (randTurn <4) {
					playerTurn = true; // left side begins
					System.out.println("Player begins");
				}else {
					playerTurn = false; // right side begins
					System.out.println("Computer begins");
				}
				isFirstTurn = false;
			}
			
			
			// hier update der ManaBars;
			updateManaBar(playerTurn); // true= left; false = right
			
			
			// nur test
//			manaMatchSize();
		}
		
	}
	
	// Methode um ID's für rows auszulesen (also die NAMEN der aktuellen Images!!)
	private List<List<String>> createListIDRows() {

		List<List<String>> lstRowList = new ArrayList<List<String>>();
		for (int i = 0; i < 8; i++) {
			List<String> lstRow = new ArrayList<>();
			for (int j = 0; j < 8; j++) {

				String rowPos = String.format("%d", i);
				String colPos = String.format("%d", j);
				String getID = "#" + rowPos + colPos;

				HBox currHBox = (HBox) gamePane.lookup(getID);
				String idIs = currHBox.getChildren().get(0).getId();
				lstRow.add(idIs);

			}
			lstRowList.add(lstRow);
		}
		return lstRowList;
	}

	// Methode um ID's für cols auszulesen (also die NAMEN der aktuellen Images!!)
	private List<List<String>> createListIDCols() {

		List<List<String>> lstColList = new ArrayList<List<String>>();
		for (int i = 0; i < 8; i++) {
			List<String> lstCol = new ArrayList<>();
			for (int j = 0; j < 8; j++) {

				String rowPos = String.format("%d", j);
				String colPos = String.format("%d", i);
				String getID = "#" + rowPos + colPos;

				HBox currHBox = (HBox) gamePane.lookup(getID);
				String idIs = currHBox.getChildren().get(0).getId();
				lstCol.add(idIs);

			}
			lstColList.add(lstCol);
		}
		return lstColList;
	}

	// Methode um IDs für Row-Matches auszulesen
	private List<String> getIDMatchesRows(List<List<String>> lstRowLists) {

		List<String> idMatchesRows = new ArrayList<String>();
		
		lstManaLenRow.clear();
		
		int k = 0;
		for (List<String> listRw : lstRowLists) {
			List<List<Integer>> chkmatchListRow = checkForManaMatches(listRw);
			if (!chkmatchListRow.isEmpty()) {

				 // diese Liste brauchen wir um Match-Längen zu verarbeiten
				lstManaLenRow.addAll(chkmatchListRow);
				for (List<Integer> listR : chkmatchListRow) {
					String idRow = String.format("%d", k);
					for (Integer idCol : listR) {
						idMatchesRows.add("#" + idRow + idCol);
					}
				}
			}
			k++;
		}
		return idMatchesRows;
	}

	// Methode um IDs für Col-Matches auszulesen
	private List<String> getIDMatchesCols(List<List<String>> lstColLists) {
		List<String> idMatchesCols = new ArrayList<String>();
		lstManaLenCol.clear();
		
		int k = 0;
		for (List<String> listCl : lstColLists) {
			List<List<Integer>> chkmatchListCol = checkForManaMatches(listCl);
			if (!chkmatchListCol.isEmpty()) {
				// diese Liste brauchen wir um Match-Längen zu verarbeiten
				lstManaLenCol.addAll(chkmatchListCol);
				
				for (List<Integer> listC : chkmatchListCol) {
					String idCol = String.format("%d", k);
					for (Integer idRow : listC) {
						idMatchesCols.add("#" + idRow + idCol);
					}
				}
			}
			k++;
		}
		return idMatchesCols;
	}

	private void removeMatches(List<String> matchesToRem) {
		
		for (String string : matchesToRem) {
			HBox currHBox = (HBox) gamePane.lookup(string);
			ImageView imgCurr = (ImageView) currHBox.getChildren().get(0);
			imgCurr.setImage(null);
			// Auch ID der ImageView löschen bzw ändern
			// "EMPTY"+ ID-string damit es kein Match geben kann
			imgCurr.setId("EMPTY" + string);
		}
		// auch Liste löschen nicht vergessen!
		matchesToRem.clear();
	}

	private void checkManaConstellation() {

		// ID-Listen für rows und cols erstellen (die sind nur zum indexen)
		List<List<String>> lstRowLists = createListIDRows();
		List<List<String>> lstColLists = createListIDCols();
		
		// ----------- hier matches erkennen und entfernen -------------------

		// ------------- matches für rows erkennen  -------------
		List<String> idMatchesRows = getIDMatchesRows(lstRowLists);
		
		// ------------- matches für cols erkennen -------------
		List<String> idMatchesCols = getIDMatchesCols(lstColLists);
		
		List<String> matchesToRemove = new ArrayList<String>();
		for (String string : idMatchesRows) {matchesToRemove.add(string);}		
		for (String string : idMatchesCols) {matchesToRemove.add(string);}
		// remove double ID's
		matchesToRemove = matchesToRemove.stream().distinct().collect(Collectors.toList());

		/* grab intel here!! hier sind wir noch VOR der Methode "removeMatches"
		 * und können hier abgreifen WELCHE Symbole ein match gegeben haben
		 * --> einfach mit Instanz-Liste abgreifen ; nicht elegant aber wir üben ja noch ^^'
		 */
		
		// lstForManaBarFill wird in Methode "updateManaBar()" verwendet
		lstForManaBarFill = new ArrayList<String>();
		for (String string : matchesToRemove) {
			  HBox currHBox = (HBox) gamePane.lookup(string);
			  ImageView imgCurr = (ImageView) currHBox.getChildren().get(0);
			  lstForManaBarFill.add(imgCurr.getId());
		}
		
		// remove all matches
		removeMatches(matchesToRemove);

	}
	
	// Methode um EMPTY IDs zu finden (nur über rows weil wir auch nur über Rows nachfüllen!)
	private List<String> getEmptyIDs() {
		
		List<List<String>> lstRowListsToFill = createListIDRows();
		List<String> idsToReplace = new ArrayList<String>();
		for (List<String> listRw : lstRowListsToFill) {
			for (String string : listRw) {
				if (string.contains("#")) {
					// hier vorsicht! wir entfernen "EMPTY"; danach bleibt NUR die ID! (#xy)
					String currID = string.replace("EMPTY", "");
					idsToReplace.add(currID);
				}
			}
		}
		return idsToReplace;
	}
		
	// Methode um zu ermitteln wo Mana nachrücken soll (für Animation)
	private List<Map<String,String>> refillEmptyImgViews(List<String> idsToRefill ) {
		
		List<List<String>> idsToProgress = colsToProgress(idsToRefill);
		
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (List<String> list : idsToProgress) { // einzelne ID-listen

			Map<String,String> mapIDImages = new HashMap<String,String>();  
			for (String stringID : list) { // id's in aktueller Liste
				  HBox currHBox = (HBox) gamePane.lookup(stringID);
				  ImageView imgCurr = (ImageView) currHBox.getChildren().get(0);
				  mapIDImages.put(stringID, imgCurr.getId());
			  }
			mapList.add(mapIDImages);
		}
		
		List<Map<String,String>> mapListUpdated = new ArrayList<Map<String,String>>();
		for (Map<String, String> map : mapList) {
			mapListUpdated.add(updateMana(map));
		}
		
		// this list for Animation later (we keep #ids and updated images-ids)
		List<Map<String,String>> manaReplaceForAnim = new ArrayList<Map<String,String>>();
		
		for (Map<String, String> map : mapListUpdated) {
			
			Map<String, String> mapForAnim = new HashMap<String,String>();   
			
			for (Entry<String, String> entry : map.entrySet()) {
				
				String idStr = entry.getKey();
				String imgStr = "";
				
				HBox currHBox = (HBox) gamePane.lookup(idStr);
				ImageView imgCurr = (ImageView) currHBox.getChildren().get(0);
				Image imgLoad = null;
				if (!entry.getValue().contains("EMPTY")) {
					// replace image
					imgStr = entry.getValue();
					imgLoad = importImage(folderToSearch, entry.getValue());
					imgCurr.setId(entry.getValue());
				}else {
					// load random Image
					int randLoad = getRandomNumberInRange(0, imgList.size() - 1);
					imgStr = imgList.get(randLoad);
					imgLoad = importImage(folderToSearch, imgStr);
					imgCurr.setId(imgList.get(randLoad));
				}
				imgCurr.setImage(imgLoad);
				
				mapForAnim.put(idStr, imgStr);
			}
			// this is 
			manaReplaceForAnim.add(mapForAnim);
		}
		
		return manaReplaceForAnim;
	}
	
	// Methode die Mana updated
	private Map<String,String> updateMana(Map<String,String> mapIDImages){
		
		 List<String> lstImg = new ArrayList<String>();
		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){ 
			  if (!entry.getValue().contains("EMPTY")) {
				  lstImg.add(entry.getValue());
			}
		  }
		  
		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){
			  if (entry.getValue().contains("EMPTY")) {
				  mapIDImages.replace(entry.getKey(), "EMPTY");
			  }
		  }

		  while (mapIDImages.containsValue("EMPTY") && !lstImg.isEmpty()) {
			  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){
				  if (!lstImg.isEmpty()) {
					  mapIDImages.put(entry.getKey(),  lstImg.get(0));
					  lstImg.remove(0);
				  } else {
					  mapIDImages.put(entry.getKey(), "EMPTY");
				  }
			  }
		  }
		
		return mapIDImages;
	}
	
	
	// Methode um id's für neue Images zu erstellen
	private List<List<String>> colsToProgress(List<String> idList){
		
		List<List<String>> idsToAddImagesTo = new ArrayList<List<String>>();
		
		// get cols  
		List<Integer> subIds = idList.stream()
								     .map(elem -> Integer.parseInt(elem.substring(2)))
								     .distinct().collect(Collectors.toList());
		
		for (int i = 0; i < subIds.size(); i++) {
			idsToAddImagesTo.add(getIdListToProcess(subIds.get(i), idList));
		}
		
		return idsToAddImagesTo;
	}
	
	// Methode um ID's basierend auf cols für neue Images zu erstellen
	private List<String> getIdListToProcess(Integer col, List<String> idList){

		// get ids depending on col
		List<Integer> subIds2 = idList.stream().filter(e -> Integer.parseInt(e.substring(2)) == col)
				   .map(e -> Integer.parseInt(e.substring(1, 2)))
				   .collect(Collectors.toList());
		
		// create new id-list
		List<String> idsToProgress = new ArrayList<String>();
		for (int i = 0; i <= subIds2.get(subIds2.size()-1); i++) {
			idsToProgress.add(new String("#"+i+col));
		}
	
		return idsToProgress;
	}
	

	// Method to test for Mana-Matches (Mana continuous > 2 in one row or col)
	private List<List<Integer>> checkForManaMatches(List<String> inList) {

		List<List<Integer>> allLineMatches = new ArrayList<List<Integer>>();
		
		// this is WHAT appeared more than 2 times
		List<String> opt = inList.stream().filter(i -> Collections.frequency(inList, i) > 2)
									      .distinct()
									      .collect(Collectors.toList());

		// this is HOW often it appeared (we know already that min freq is 3)
		List<Integer> freqCount = new ArrayList<Integer>();
		for (int j = 0; j < opt.size(); j++) {
			Integer cnt = Collections.frequency(inList, opt.get(j));
			freqCount.add(cnt);
		}
		
		// using chkCount-MeThod to get Positions in line
		for (int j = 0; j < opt.size(); j++) {
			List<Integer> cList = chkCount(opt.get(j), inList, freqCount.get(j));
			if (cList.size() >= 3) {
				allLineMatches.add(cList);
			}
		}
		return allLineMatches;
	}
	
	private List<Integer> chkCount(String testString, List<String> testList, Integer freqCount) {

		int pos = 0;
		List<String> conList = new ArrayList<String>();
		List<Integer> posList = new ArrayList<Integer>();
		Iterator<String> itr = testList.listIterator();   

        // way to test if no double rows can appear 
        if (freqCount < 6 || freqCount == 8) { // for <6 no double rows can appear; 8 means its the whole row
			while (itr.hasNext()) {
					if (itr.next() == testString) {
						conList.add(testString);
						posList.add(pos);
					} else if ((conList.size() < 3)) {
						conList.clear();
						posList.clear();
					} else {
						return posList;
					}
					pos++;
			}
        }
        // way to test if doubles rows may appear (this CAN happen but does not have to)
        if (freqCount >= 6 && freqCount != 8) {
        	while (itr.hasNext()) {
				if (itr.next() == testString) {
					conList.add(testString);
					posList.add(pos);
				} else if ((conList.size() < 3)) {
					conList.clear();
					posList.clear();
				} else if(conList.size() == 3 || conList.size() == 4) {  
					// remove already found positions
					for (int i = 0; i < posList.size(); i++) {
						testList.set(posList.get(i), "");
					}
					if (conList.size() == 3) {
						int lastPos = posList.get(posList.size()-1);
						if (lastPos == 2 || lastPos == 3) { // nur in diesen Fällen kann noch eine 2. Reihe der gleichen Farbe vorkommen
							int posSub = 0;
							Iterator<String> itr2 = testList.listIterator();  
							while (itr2.hasNext()) {
								if (itr2.next() == testString) {
									conList.add(testString);
									posList.add(posSub);
								} else if ((conList.size() < 3)) {
									conList.clear();
									posList.clear();
								}
								posSub++;
							}
						} else {
							return posList;
						}
							return posList;
						} // end 3 test
					
					if (conList.size() == 4) {
						int lastPos = posList.get(posList.size()-1);
						if (lastPos == 3) { // nur in diesem Fall kann noch eine 2. Reihe der gleichen Farbe vorkommen
							int posSub = 0;
							Iterator<String> itr2 = testList.listIterator();  
							while (itr2.hasNext()) {
								if (itr2.next() == testString) {
									conList.add(testString);
									posList.add(posSub);
								} else if ((conList.size() < 3)) {
									conList.clear();
									posList.clear();
								}
								posSub++;
							}
							}else {
								return posList;
							}
								return posList;
							} // end 4 test
        		} else if ((conList.size() > 3) && (posList.size() < 6)){
					return posList;
				} else if ((conList.size() > 3) && (posList.size() < 7)){
					return posList;
				}
				pos++;
        	}       	
		}
		return posList;
	} 
	

	// Methode um menu-button zu erstellen
	private Group createMenuButton(String btntxt, double yPos) {

		Group grp = new Group();
		// Text-Fields
		Text txt = new Text();
		// Setting the text to be added.
		txt.setText(btntxt);
		// text-Editierung
		txtEdit(txt);
		// Container für Text; Text ohne Container zu platzieren ist echt fies ^^'
		HBox hbox = new HBox();
		hbox.getChildren().add(txt);
		Rectangle txtFrame = createFrame(txt);

		btnPosition(hbox, txtFrame, txt, yPos);
//		addColorBG(txtFrame);
		grp.getChildren().addAll(txtFrame, hbox);
		grp.setId(btntxt);
		return grp;
	}

	// Methode um textframe zu erstellen
	private Rectangle createFrame(Text txt) {

		double txtWidth = txt.getLayoutBounds().getWidth();
		double txtHeight = txt.getLayoutBounds().getHeight();

		double widthAdd = 50;
		Rectangle txtFrame = new Rectangle(txtWidth + widthAdd, txtHeight, Color.TRANSPARENT);
		txtFrame.setStrokeWidth(5);
		txtFrame.setArcWidth(30.0);
		txtFrame.setArcHeight(20.0);
		return txtFrame;
	}

	// Method to create Background-Gradient color for Textframe
	private void addColorBG(Rectangle rect) {
		Stop[] stops = new Stop[] { new Stop(0, Color.DARKORANGE), new Stop(1, Color.WHITE) };
		// gradient top-down
		LinearGradient lngnt = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
		rect.setFill(lngnt);
	}

	// LinearGradient Color für Textframe
	private void addColorFrame(Rectangle rect) {
		// ein Basis-Farbobjekt dem wir einen Listener zuordenen können
		ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();

		// KeyValues definieren
		KeyValue keyValue1 = new KeyValue(baseColor, Color.RED, Interpolator.LINEAR);
		KeyValue keyValue2 = new KeyValue(baseColor, Color.YELLOW, Interpolator.LINEAR);

		// KeyValues den Keyframes zuordnen
		KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
		KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);

		// keyframes an die Timeline (hier haben wir nur 2 können aber auch mehr
		// reinpacken)
		timelineBtnBlink = new Timeline(keyFrame1, keyFrame2);

		// Listener an baseColor; baseColor ist mit den KeyValues verbunden
		baseColor.addListener((obs, oldColor, newColor) -> { // changed(ObservableValue<? extends Color>, Color, Color)
			// hier eine Color setzen reicht (wir könnte auch oldColor setzen; der Effekt
			// wäre der gleiche)
			rect.setStroke(newColor);
		});

		timelineBtnBlink.setAutoReverse(true);
		timelineBtnBlink.setCycleCount(Animation.INDEFINITE);
		timelineBtnBlink.play();

	}

	// Methode um Button-elemente zu positionieren(HBox, Rectangle)
	private void btnPosition(HBox hbox, Rectangle rect, Text txt, double yPos) {
		// erst auf Mitte positionieren dann y-Position bestimmen
		hbox.setTranslateX(width / 2 - txt.getBoundsInLocal().getWidth() / 2);
		hbox.setTranslateY((height / 2 - txt.getBoundsInLocal().getHeight() / 2) + yPos);
		rect.setTranslateX(width / 2 - rect.getWidth() / 2);
		rect.setTranslateY((height / 2 - rect.getHeight() / 2) + yPos);

	}

	// Methode für die Text-Editierung (Schriftart, Größe, usw); soll ja einheitlich
	// sein im Menü
	private void txtEdit(Text text) {
		text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 80));
		text.setFill(Color.NAVAJOWHITE);
		text.setStrokeWidth(2);
		text.setStroke(Color.BLACK);
		text.setTextAlignment(TextAlignment.CENTER);
	}

	private HBox createHBoxForGamePane(String id) {

		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;
		double rectSize = parentSize / nodeAmount - parentPaddingSize;

		ImageView imgView = new ImageView();

		// load random Image
		int randLoad = getRandomNumberInRange(0, imgList.size() - 1);
		Image imgLoad = importImage(folderToSearch, imgList.get(randLoad));

		// Image an ImageView
		imgView.setImage(imgLoad);
		// ImgeView Namen des Image geben
		imgView.setId(imgList.get(randLoad));

		// ImageView-Größe anpassen (die HBox passt sich dann automatisch an)
		imgView.setFitHeight(rectSize);
		imgView.setFitWidth(rectSize);

		// ImageView and die HBox
		HBox hbox = new HBox(imgView);
		hbox.setId(id); // HBox ID geben

		return hbox;
	}

	private Rectangle createRectangleForCursorPane(String id) {

		// GridPane-Adaption has its limits; better calc a little
		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;
		double rectSize = parentSize / nodeAmount - parentPaddingSize;

		Rectangle rect = new Rectangle(rectSize, rectSize);
		rect.setFill(Color.WHITE);
		rect.setId(id);

		// alles was NICHT Cursor ist auf false!!
		rect.setFocusTraversable(false);
		rect.setVisible(false);
		return rect;
	}

	// Methode die den Marker für die aktuelle Cursor-Position erstellt
	private void setCursorMarker(String id) {

		/*
		 * id ist die Cursor-Position auf der Cursor-Pane (Position: 00-77) wir
		 * erstellen kein neues Rectangle sondern ändern das Rectangle an der Position
		 * die makiert werden soll --> das "sieht dann so aus" als ob da ein cursor wäre
		 * 
		 */

		String idIs = "#" + id;

		Rectangle rect = (Rectangle) cursorPane.lookup(idIs);

		if (rect != null) {
			rect.setFill(null);
			double borderSize = 5;
			rect.setHeight(rect.getHeight() - borderSize);
			rect.setWidth(rect.getWidth() - borderSize);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(borderSize);
			// nur das Cursor-Rectangle TRUE
			rect.setFocusTraversable(true);
			rect.setVisible(true);
		} else {
			System.out.println("is null");
		}

	}

	@Override
	public void start(Stage stage) throws Exception {

		// initialisierung der Fenstergrößen besser hier; dann können wir alle Parents
		// damit bearbeiten
		screenSize = Screen.getPrimary().getBounds();
		width = screenSize.getWidth();
		height = screenSize.getHeight();
		
		scene = new Scene(launchRoot());
		

		stage.setScene(scene);

		scene.setFill(Color.TRANSPARENT); // !!
		
		if (usingIntro) {
			scene.setCursor(Cursor.NONE);
		}
		
//		scene.setCursor(Cursor.NONE); // no Mouse Pointer!! Caution! Pointer is not gone but INVISIBLE!(need to add KEY-Controls for Buttons)
		
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initStyle(StageStyle.TRANSPARENT); // !!

		stage.setFullScreenExitHint(""); // remove label
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.setFullScreen(true);
		stage.show();
		
		/*
		 * besser die Keys für das GamePlay etc auf die einzelnen Panes packen wir haben
		 * hier noch einen exit-key, aber auch das können wir besser über die Menüs
		 * regeln
		 */
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case Q:
				Platform.exit(); // closes JavaFX app (but JVM still running)
				System.exit(0); // closes JVM
				break;
			case I:
				// hier cursor nach oben
				if (gamePane != null) { // nur wenn Spiel gestartet
					moveCursor("UP");
				} else { // hier MainMenu?
					System.out.println("no game screen");
				}
				break;
			case K:
				// hier cursor nach unten
				if (gamePane != null) {
					moveCursor("DOWN");
				} else {
					System.out.println("no game screen");
				}
				break;
			case J:
				// hier cursor nach links

				if (gamePane != null) {
					moveCursor("LEFT");
				} else {
					System.out.println("no game screen");
				}
				break;
			case L:
				// hier cursor nach rechts
				if (gamePane != null) {
					moveCursor("RIGHT");
				} else {
					System.out.println("no game screen");
				}
				break;
			case SPACE:
				if (gamePane != null && cursorPane != null) {
					moveCursor("SPACE");
				} else {
					System.out.println("no game screen");
				}
				break;
			case Z:
				// TODO: here activation of animation pane by using key (only for testing) 
				if (animationPane != null) {

//					matchDeleteAnim();
//					startAnim(); // 
					
				} else {
					System.out.println("no animation screen");
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

	// ----------------------- Methoden für import
	// --------------------------------------------------------

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