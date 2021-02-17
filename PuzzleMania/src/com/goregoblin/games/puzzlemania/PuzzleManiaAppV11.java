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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
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
 * --> Animation jetzt ganz ok (braucht noch etwas fine-tuning weil manchmal ruckelig aber geht)
 * 		--> ein Geräusch wäre noch ganz gut(vielleicht auch unterschiedliche bei größeren Matches e.g 4,5, usw) 
 * 
 * Guck mal im Package "helpfullstuff/Bejeweled"
 * 			wie der die Reihen mit stream() testet; das könnte etwas effizienter als unsere Lösung sein ^^'
 * 
 * 
 * Priorität 1: Movement Rules 
 * wenn < 3 gleiche Symbole in Reihe dann keinen Move erlauben (gibt Penalty und move return)
 * --> also einfach testen ob NACH dem "FixedMove" ein Match enstanden ist
 * 		--> wenn ja ok
 * 		--> wenn nein --> Penalty!(also Hinweis + Geräusch) 
 * 			Dann die Mana zurücksetzen!
 * 
 * danach müssen wir nach JEDEM move die gesamte "Matrix" auslesen
 * wenn keine Move mehr möglich ist --> Mana-Katastrophe (kompletter reset)
 * 
 * Mana-Katastrophen-Test
 * --> das wird übel weil wir ALLE möglichen nächsten Zug-Möglichkeiten
 *     darauf testen müssen ob es überhaupt eine Lösung im nächsten Zug geben kann oO'
 *     JEDE Mana kann ja immer nur EINEN Schritt weit von ihrer Position weiter (Movement Rules)
 *     das muss nach jedem Zug getestet werden
 *     --> hier gibt es ja noch keine "Spezial-Aktionen" sonst müssten wir die auch
 *     		mit einbeziehen (also sowas wie: "Sprenge komplette Reihe" oder "Alle gelbe Mana wird Rot" etc)
 *     
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

public class PuzzleManiaAppV11 extends Application {

	// für JavaFX
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private Scene scene;

	private Pane mainMenuPane;
	private Pane optionsMenuPane;
	private GridPane gamePane;
	private GridPane cursorPane;
	private Pane animationPane;

	private Timeline timelineBtnBlink;
	private MediaPlayer mediaPlayerBeep;
	private Media mediaBeep;
	private File fBeepSound;

	private boolean fixedCursor;
	List<Map<String, String>> manaReplaceForAnim; 
	List<String> manaMatchesToRemoveForAnim; 

	// Hintergrund-Bild laden
	String folderToSearch = "resource/images/";
	List<String> imgList = Arrays.asList("hydra_red", "hydra_green", "hydra_blue", "hydra_yellow", "alchemy",
										 "skull");
	
	
	// to use JavaFX-Plotting
	private Parent launchMainScreen() {

		fBeepSound = new File("resource/audio/popsound.mp3");
		mediaBeep = new Media(fBeepSound.toURI().toString());
		mediaPlayerBeep = new MediaPlayer(mediaBeep);
		mediaPlayerBeep.setVolume(0.1);

		double parentRelSize = 1.25;

		root = new Pane();
		root.setPrefSize(width / parentRelSize, height / parentRelSize);
		root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");

		root.getChildren().add(launchMainMenu());

		return root;
	}

	// Method to launch MainMenu
	private Pane launchMainMenu() {

		double parentRelSize = 1.0;
		mainMenuPane = new Pane();
		mainMenuPane.setPrefSize(width / parentRelSize, height / parentRelSize);
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
		double xPos = width / 2 - imgWidth / 2;
		double yPos = height / 2 - imgHeight / 2;

		imgView.setTranslateX(xPos);
		imgView.setTranslateY(yPos);

		imgView.setScaleX(2);
		imgView.setScaleY(2);

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

		mainMenuPane.getChildren().add(imgView);
		mainMenuPane.getChildren().add(btnPack);

		btnPack.setTranslateX(width / 2 - btnPack.getPrefWidth() / 2);
		btnPack.setTranslateY(height / 2 - btnPack.getPrefHeight() / 2);

		// show font-types
//		List<String> allFonts = javafx.scene.text.Font.getFamilies();			 
//		allFonts.stream().forEach(System.out::println);

		return mainMenuPane;
	}

	// method to launch GameScreen
	private List<Parent> launchGameBoard() {

		List<Parent> overlayList = new ArrayList<>();

		overlayList.add(launchInlay());
		overlayList.add(launchCursorPane());
		overlayList.add(launchAnimationPane());
		
		//  hier AnimationPane start 
		
		
		return overlayList;
	}

	// Method to launch OptionsMenu
	private Pane launchOptionsMenu() {

		double parentRelSize = 1.0;
		optionsMenuPane = new Pane();
		optionsMenuPane.setPrefSize(width / parentRelSize, height / parentRelSize);
		optionsMenuPane.setStyle("-fx-background-color: rgba(120, 125, 0, 0.4);");

		// Hintergrund-Bild laden
		String folderToSearch = "resource/images/";
		String imageToLoad = "Godzilla";
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
		gamePane.setStyle("-fx-background-color: rgba(120, 255, 0, 0.2);");

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
		
		//  hier gamePane Visibility
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

		/*  Animation-Pane for advanced Animations
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
			System.out.println("matches to remove: ");
			manaReplaceForAnim.stream().forEach(System.out::println);
	
			animationPane.setVisible(true);
			
			System.out.println("matches to remove in manaMatchesToRemoveForAnim-list: ");
			manaMatchesToRemoveForAnim.stream().forEach(System.out::println);
		
			// später list in list (erstmal nur eine reihe oO')
			List<HBox> lstCurrBoxesToHide = new ArrayList<HBox>();
			List<HBox> lstBoxesToMove = new ArrayList<HBox>();
			List<KeyValue> lstKValuesVertical = new ArrayList<KeyValue>();
			List<KeyValue> lstKValuesHorizontal = new ArrayList<KeyValue>();
			List<KeyFrame> lstKFrames = new ArrayList<KeyFrame>();
			
			// here get IDs which are supposed to move within the field (Mana which is NOT new generated)
			List<Map<String, String>> manaToMove = new ArrayList<Map<String,String>>();
			for (int i = 0; i < manaReplaceForAnim.size(); i++) {
				Map<String, String>  map = manaReplaceForAnim.get(i);
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
			
		  });
		
		myTimeLine.play();
	}
	
	
	
	/* Methode die Animation für Reihen-Nachrücken auf Animation-pane zeigt 
	 * läuft noch nicht automatisch; starten wir mit "k" --> siehe Scence
	 */
	private void startColRefillAnim() {
		
		if (!manaReplaceForAnim.isEmpty()) {
			System.out.println("matches to remove: ");
			manaReplaceForAnim.stream().forEach(System.out::println);
	
			animationPane.setVisible(true);
		
		
		/*  Progress Map for Animated refill of Mana	
		 * 
		 * --> besser wir erstellen mehrere Listen oder Maps mit Noden und Ids
		 * 		die wir dann abarbeiten
		 * 	
		 * 		die Map-Einträge sehen so aus:
		 * 		{#30=hydra_green, #20=hydra_blue, #10=hydra_red, #00=EMPTY}
		 * 		oder so:
		 * 		{#41=hydra_yellow, #31=hydra_green, #21=hydra_blue, #11=EMPTY, #01=EMPTY, #61=EMPTY, #51=EMPTY}
		 * 		
		 * 		d.h. die Einträge sind nicht sortiert bzw. wir müssen uns an der ID orientieren
		 * 		enthalten ist immer die gleiche col (die 2. ID-Ziffer)
		 * 		
		 * 		siehe "trial3" dort ist die methode die zeigt wie wir die Man ersetzen
		 * 		NICHT die HBoxen verschieben!! Nur NEUE HBoxen mit den gleichen Images belegen!
		 * 		wir müssen nur die ID's top-down addressieren 
		 * 		die row mit der größten Ziffer (max. 7) ist die letzte ZielBox
		 * 		ID: #RowCol 		
		 *  
		 *		könnte knifflig werden ^^' 		
		 */
		
//		  for (Map<String, String> map : manaReplaceForAnim) {
		
		/*
		 * hier mit List<HBox>
		 * auch für KeyValues und Frames
		 * --> Nicht sicher ob wir alles mit einer TimeLine gleichzeitig animieren können wegen dem timing
		 * sonst brauchen wir für jeden Col eine eigene TimeLine oO'
		 * 
		 * --> gut wäre auch wenn die ID's die nur "runterfallen" nicht unsichtbar werden
		 * 		ID's die entfernt werden sind in Liste "manaMatchesToRemoveForAnim"
		 */
		
			System.out.println("matches to remove in manaMatchesToRemoveForAnim-list: ");
			manaMatchesToRemoveForAnim.stream().forEach(System.out::println);
		
		// später list in list (erstmal nur eine reihe oO')
		List<HBox> lstCurrBoxes = new ArrayList<HBox>();
		List<HBox> lstBoxesToMove = new ArrayList<HBox>();
		List<KeyValue> lstKValuesVertical = new ArrayList<KeyValue>();
		List<KeyValue> lstKValuesHorizontal = new ArrayList<KeyValue>();
		List<KeyFrame> lstKFrames = new ArrayList<KeyFrame>();
		
		
		/*  Nachrückende Mana für ALLE Columnen
		 * Wir haben erst eine Columne probiert; das geht jetzt und Effekt sieht auch ok aus
		 * --> es fehlt noch das Nachrücken für Rows
		 */
		
		Map<String, String>  map = manaReplaceForAnim.get(0);
			for (Entry<String, String> entry : map.entrySet()) {
				// diese HBox müssen wir "verstecken"
				lstCurrBoxes.add((HBox) gamePane.lookup(entry.getKey()));
				// hier wird eine NEUE HBox erstellt 
				lstBoxesToMove.add(createHBoxForAnimationPane(entry.getValue()));
				
			}
			
		/*  KValues for TimeLine --> this for a start; there might be better ways oO'
		 * top-down geht
		 * --> weil wir nur EINE Reihe barbeiten ^^' 
		 * --> wir brauchen noch eine if-case wenn Liste leer
		 * 
		 */
			
			moveMana(lstCurrBoxes, lstBoxesToMove, lstKValuesVertical, lstKValuesHorizontal, lstKFrames);
			
		} else {
			System.out.println("manaReplaceForAnim- List empty");
		}
					
	}
	
	// Methode die Animation für Reihen-matches auf der Animation-Pane zeigt
	private void matchDeleteAnim() {
		
		if (!manaMatchesToRemoveForAnim.isEmpty()) { // // diese Liste wird in der Methode manaTest() aktualisiert
			System.out.println("matches to remove: ");
			manaMatchesToRemoveForAnim.stream().forEach(System.out::println);
	
			animationPane.setVisible(true);
			
			List<HBox> lstCurrBoxes = new ArrayList<HBox>();
			
			for (String entry : manaMatchesToRemoveForAnim) {
				// diese HBox müssen wir "verstecken"
				lstCurrBoxes.add((HBox) gamePane.lookup(entry));
			}
			
		
			List<ImageView> lstAnimObj = new ArrayList<ImageView>();	// just for trial 
			for (int i = 0; i < lstCurrBoxes.size(); i++) {
				
				/*  Positions Mana-Explosion-Effect
			     * works but somehow positions that worked for circle do not work for ImageView?!? oO'
				 * we corrected the values and it works but its not "adaptive" 
				 *   
				 */
				double posX = lstCurrBoxes.get(i).getLocalToParentTransform().getTx()-lstCurrBoxes.get(i).getWidth()*1.25;
				double posY = lstCurrBoxes.get(i).getLocalToParentTransform().getTy()-lstCurrBoxes.get(i).getHeight()*1.5;
				
				ImageView imgView = new ImageView();
				
				String img2Load = "expl3.gif";
				// load Image
				Image imgLoad = importImage(folderToSearch, img2Load);
				imgView.setImage(imgLoad);
				
				// size
				imgView.setScaleX(.25);
				imgView.setScaleY(.25);
				
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
//				startColRefillAnim(); // old method (works but different Animation)
				startColRefillAnim2();
			  });
			
			myTimeLine.play();
		
		} else {
			System.out.println("manaMatchesToRemoveForAnim- List empty");
		}
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
			
			manaTest();
			System.out.println("Mana-Matches present at unFixedCursorMove: " +
					manaMatchesToRemoveForAnim.isEmpty());
			
			// if matches could be found start remove-Animation
			if (!manaMatchesToRemoveForAnim.isEmpty()) {
				matchDeleteAnim();
			}
			
		} else if (fixedCursor) {

			fixedCursorMove(direction, cursorPosId, rowPos, colPos);
			// Mana-Test
			manaTest();
			
			System.out.println("Mana-Matches present at fixedCursorMove: " +
					manaMatchesToRemoveForAnim.isEmpty());
			
			// if matches could be found start remove-Animation
			if (!manaMatchesToRemoveForAnim.isEmpty()) {
				matchDeleteAnim();
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

	
	/*  manaTest-Methode aktivieren bis kein mana-Match mehr vorhanden 
	 * diese Methode verwenden um das Spiel-Feld auf Mana-Matches zu testen
	 * --> wir sollten die Animation an die Methode koppeln
	 * 		einfach testen ob  "manaMatchesToRemoveForAnim" empty ist ?!?
	 * 
	 */
	private void manaTest() {
		checkManaConstellation();
		manaMatchesToRemoveForAnim = getEmptyIDs();
		manaReplaceForAnim = refillEmptyImgViews(getEmptyIDs());
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
		int k = 0;
		for (List<String> listRw : lstRowLists) {
			List<List<Integer>> chkmatchListRow = checkForManaMatches(listRw);
			if (!chkmatchListRow.isEmpty()) {
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

		int k = 0;
		for (List<String> listCl : lstColLists) {
			List<List<Integer>> chkmatchListCol = checkForManaMatches(listCl);
			if (!chkmatchListCol.isEmpty()) {
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
	
	
	// Methode um zu ermitteln wo Mana-Matches entfernt werden sollen 
	private List<Map<String,String>> getManaToRemove(List<String> idsToRefill ) {
		/* wir können wie in Methode "refillEmptyImgViews" vorgehen
		 * es müsste reichen rauszufinden welche ID's "EMPTY" sind
		 * wir brauche eigentlich nur Positionen weil wir ja nur
		 *  
		 * wissen nur müssen WO der Effekt auf der AnimationPane erfolgen soll 
		 * das packen wir in eine Liste und geben die zurück
		 * 
		 * Map brauchen wir hier wahrscheinlich nicht 
		 */
		
		
		return null;
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
		addColorBG(txtFrame);
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
		Stop[] stops = new Stop[] { new Stop(0, Color.INDIGO), new Stop(1, Color.ORANGE) };
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

		scene = new Scene(launchMainScreen());

		stage.setScene(scene);

		scene.setFill(Color.TRANSPARENT); // !!
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
				//  here activation of animation pane by using key (only for testing) 
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