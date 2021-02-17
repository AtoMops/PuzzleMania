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
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
 * wir brauchen mal einen code-clean-up oO'
 * die Sache ist zu unübersichtlich geworden 
 *
 * 
 * wir müssen die Keys an die Panes setzen (an Scene haben wir nur "q" für Exit)
 * sonst bekommen wir einen überladung an Keys
 * 
 *  zudem brauchen wir auch mal einen neuen Ansatz für die Kommunikation zwischen der
 *  GamePane und der CursorPane
 *  
 *  --> wir können mal versuchen die Keys an die CursorPane zu packen
 *  	und die GamePane komplett passiv zur CursorPane zu setzen 
 *  --> probiert.. ist nicht so gute Idee oO' unser Ansatz für die keys war schon ganz ok so
 *  --> aber wir müssen die move-Methode überarbeiten 
 * 
 * 
 * --> die Key-logic können wir aus V4 übernehmen (dort ist sie noch an der Scene..)
 * 
 * 
 * --> das hier später!! für eine executable müssen wir die libaries evtl völlig anders einbinden..also mit Maven etc..
 * hier auch mal was über JavaFX und executable *.jar
 * https://stackoverflow.com/questions/23117541/how-to-make-a-runnable-jar-for-an-application-that-uses-javafx-without-native-in
 * wir verwenden ja runtime-argumente 
 * 
 */

public class PuzzleManiaAppV5 extends Application {

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
	private List<GridPane> launchGameBoard() {

		List<GridPane> overlayList = new ArrayList<>();

		overlayList.add(launchInlay());
		overlayList.add(launchCursorPane());

		return overlayList;
	}

	// Method to launch OptionsMenu
	private Pane launchOptionsMenu() {

		double parentRelSize = 1.0;
		optionsMenuPane = new Pane();
		optionsMenuPane.setPrefSize(width / parentRelSize, height / parentRelSize);
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
		gamePane.setStyle("-fx-background-color: rgba(100, 200, 0, 0.8);");

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
//				gamePane.add(createRectangleForTesting(), j,i); // gamePane uses add(Node,x,y)
//				gamePane.add(createCircleForTesting(), j, i); // gamePane uses add(Node,x,y)
			}
		}

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
		cursorPane.setStyle("-fx-background-color: rgba(255, 100, 100, 0.5);");

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

//						System.out.println("id: " + id);

				cursorPane.add(createRectangleForCursorPane(id), j, i); // Gridpane uses add(Node,x,y)
			}
		}

		// erstellt cursor an position 00 (evtl ein random 0-7 für row und cols?!)

		int randomRow = getRandomNumberInRange(0, 7);
		int randomCol = getRandomNumberInRange(0, 7);
		String randomCursorPos = String.valueOf(randomRow) + String.valueOf(randomCol);
		System.out.println("randomCursorPos: " + randomCursorPos);

		setCursorMarker("00");
		
		return cursorPane;
	}

	// Methode um Cursor zu bewegen
	private void moveCursor(String direction) {

		/*
		 * Methode ist in V4 aber die hat nicht so gefunzt wie gedacht (ungewollte
		 * veränderung der Feldgröße usw)
		 * 
		 * mal versuchen die ID's der CursorPane auszulesen und diese zu verwenden
		 * eventuell brauchen wir sogar noch eine 3. Pane trouble macht uns die
		 * Veränderliche Größe der GridPane die ja auf der Größe der beinhalteten
		 * Elemente basiert
		 *
		 * das Code-Gerüst können wir übernehmen
		 * 
		 * aber die Art wie wir die Position des Cursors ermitteln müssen wir ändern!!
		 * wir haben eigenschaften wie "Color" oder "Stroke" ermittelt aber wir
		 * brauchen da klar was anderes!!
		 * 
		 * der switch-case auch nochmal überarbeiten 
		 * 
		 * --> oder völlig neuer Ansatz oO' 
		 * 
		 */

		// methode um cursor-position row und column zu bestimmen
		List<Integer> rowColCursor = getCursorPosRowCol();

		// methode um cursor id zu bestimmen
		String cursorPosId = getCursorPosID();

		// hier cursor-color für die neue position
		Integer rowPos = rowColCursor.get(0);
		Integer colPos = rowColCursor.get(1);

		System.out.println("rowPos: " + rowPos + "; colPos: " + colPos);
		
		/*
		 * nach unten: if (rowPos <= 6) --> rowPos++;
		 * nach rechts: if (colPos <= 6) --> colPos++; 
		 * nach oben: if (rowPos > 0) --> rowPos--;
		 * nach links: if (colPos > 0) --> colPos--;
		 * 
		 */
		
		switch (direction) {
		case "UP":
			if (rowPos > 0) {
				System.out.println("UP");
				// hier Color der alten position zurücksetzen
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);					
				if (rectOld != null) {
					resetRectangle(rectOld);
				}else {
					System.out.println("is null");
				}
				
				// positions-ID ändern
				rowPos--;					
				String newID = "#"+ rowPos+colPos;
				
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
				}else {
					System.out.println("is null");
				}
				// positions-ID ändern
				rowPos++;					
				String newID = "#"+ rowPos+colPos;
				
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
				}else {
					System.out.println("is null");
				}
				
				// positions-ID ändern
				colPos--;					
				String newID = "#"+ rowPos+colPos;
				
				// cursor-farbe setzen 
				Rectangle rectNew = (Rectangle) cursorPane.lookup(newID);					
				if (rectNew != null) {
					rectNew = changeCursorApperance(rectNew);
				}
				
			} 
			break;
		case "RIGHT":
			if (colPos <= 6) {
			
				System.out.println("Moving right");
				// hier Color der alten position zurücksetzen
				Rectangle rectOld = (Rectangle) cursorPane.lookup(cursorPosId);					
				if (rectOld != null) {
					resetRectangle(rectOld);
				}else {
					System.out.println("is null");
				}
				
				// positions-ID ändern
				colPos++;					
				String newID = "#"+ rowPos+colPos;
				
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
	
	/*
	 * wir "korrigieren" ständig die größe des rectangles
	 * aber dadurch wird es auch immer kleiner
	 * wir müssen beim ZURÜCKSETZEN des Rectangles nochmal schauen
	 * wir nehmen ja nur die Border raus; ändern dadurch aber die gesamtgröße
	 * 
	 * --> Hier jetzt nur noch verhindern das sich die Größe ändert
	 * identifikation hängt jetzt nicht mehr from Stroke sondern von isFocusTranversable ab
	 * 
	 */
	private Rectangle changeCursorApperance(Rectangle rectNew) {
			
		double borderSize = 5;
		rectNew.setHeight(rectNew.getHeight()-borderSize);
		rectNew.setWidth(rectNew.getWidth()-borderSize);
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

		rect.setHeight(rectSize-5.0);
		rect.setWidth(rectSize-5.0);
		rect.setFill(null);
		rect.setStroke(Color.BLUE);
		rect.setStrokeWidth(5.0);
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
				System.out.println("current cursorPos is: " + rec.getId());
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

	// for testing only
	private Circle createCircleForTesting() {

		// GridPane-Adaption has its limits; better calc a little
		double parentSize = 1000.0;
		double parentPaddingSize = 5;
		double nodeAmount = 8.0;
		double rectSize = parentSize / nodeAmount - parentPaddingSize;

		Circle rect = new Circle(rectSize / 2);
		rect.setFill(Color.BLUE);

		return rect;
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