package com.goregoblin.subTest;

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
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
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


/* diese Klasse nur als Test-Umgebung für SubElemente
 * hier haben wir so grob das GameBoard ohne Effekte usw..
 * 
 */


public class TestManaSpellInGame extends Application {

	// für JavaFX
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private Scene scene;

	private GridPane gamePane;
	private GridPane cursorPane;
	// this is just for testing so we can get visual response
	private Pane testingPane;

	private Timeline timelineBtnBlink;
	private boolean fixedCursor;

	// Hintergrund-Bild laden
	String folderToSearch = "resource/images/";
	List<String> imgList = Arrays.asList("hydra_red", "hydra_green", "hydra_blue", "hydra_yellow", "alchemy",
			"skull");
	
	
	// to use JavaFX-Plotting
	private Parent launchMainScreen() {

		double parentRelSize = 1.25;

		root = new Pane();
		root.setPrefSize(width / parentRelSize, height / parentRelSize);
		root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");
		
		root.getChildren().addAll(launchGameBoard());
		
		return root;
	}

	// method to launch GameScreen
	private List<Pane> launchGameBoard() {

		List<Pane> overlayList = new ArrayList<>();

		overlayList.add(launchInlay());
		overlayList.add(launchCursorPane());

		return overlayList;
	}

	private GridPane launchInlay() {

		double inlaySize = 1000;
		gamePane = new GridPane();
		gamePane.setPrefSize(inlaySize, inlaySize);
		gamePane.setStyle("-fx-background-color: rgba(120, 255, 0, 0.8);");

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
//		System.out.println("randomCursorPos: " + randomCursorPos);

		setCursorMarker("00");

		return cursorPane;
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

//		System.out.println("rowPos: " + rowPos + "; colPos: " + colPos);

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

			System.out.println("Using fixedCursor Movement");
			fixedCursorMove(direction, cursorPosId, rowPos, colPos);
			// direkt nach fixedCursor-Move testen (weil dann auf jeden fall ein Objekt im Feld verschoben wurde)
			
			
			checkManaConstellation();
			
			//  Method call; getEmptyRows; at each turn
			List<String> emptIDs = getEmptyIDs();
			
//			System.out.println("current EMPTY IDs are: ");
//			emptIDs.stream().forEach(System.out::println);
			
//			System.out.println("refillEmptyImgViews: ");
			refillEmptyImgViews(emptIDs);
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

	// Methode um ID's für rows auszulesen
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

	// Methode um ID's für cols auszulesen
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
//			System.out.println("Row check: " + k);
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
//			System.out.println("Col check: " + k);
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

	private void removeMatches(List<String> matchesToRemove) {
		for (String string : matchesToRemove) {
			HBox currHBox = (HBox) gamePane.lookup(string);
			ImageView imgCurr = (ImageView) currHBox.getChildren().get(0);
			imgCurr.setImage(null);
			// Auch ID der ImageView löschen bzw ändern
			// "EMPTY"+ ID-string damit es kein Match geben kann
			imgCurr.setId("EMPTY" + string);
		}
		// auch Liste löschen nicht vergessen!
		matchesToRemove.clear();
	}

	private void checkManaConstellation() {

		// ID-Listen für rows und cols erstellen (die sind nur zum indexen)
		List<List<String>> lstRowLists = createListIDRows();
		List<List<String>> lstColLists = createListIDCols();

// ------------------ die 2 folgenden loops sind nur zum testen (console output)---------------------

		// test-loop um row-treffer zu printen
//		int p = 0;
//		for (List<String> list : lstRowLists) {
//			System.out.println();
//			System.out.println("row: " + p);
//			list.stream().forEach(System.out::println);
//			p++;
//		}
//
//		// test-loop um col-treffer zu printen
//		p = 0;
//		for (List<String> list : lstColLists) {
//			System.out.println();
//			System.out.println("col: " + p);
//			list.stream().forEach(System.out::println);
//			p++;
//		}
//		
		// ----------- hier matches erkennen und entfernen -------------------

		// ------------- matches für rows erkennen  -------------
		List<String> idMatchesRows = getIDMatchesRows(lstRowLists);
//		System.out.println("found matches ID's rows");
//		idMatchesRows.stream().forEach(System.out::println);
		
		// ------------- matches für cols erkennen -------------
		List<String> idMatchesCols = getIDMatchesCols(lstColLists);
//		System.out.println("found matches ID's cols");
//		idMatchesCols.stream().forEach(System.out::println);
		
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
		int k = 0; // k only for console output
		for (List<String> listRw : lstRowListsToFill) {
//			System.out.println("Row to check for Empty entries: " + k);
				
			for (String string : listRw) {
//				System.out.println(string);
				if (string.contains("#")) {
//					System.out.println("found ID");
					// hier vorsicht! wir entfernen "EMPTY"; danach bleibt NUR die ID! (#xy)
					String currID = string.replace("EMPTY", "");
//					System.out.println("currID is: " + currID);
					idsToReplace.add(currID);
				}
			}
			k++;
		}
		return idsToReplace;
	}

	private void refillEmptyImgViews(List<String> idsToRefill ) {
		
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
		
		
		for (Map<String, String> map : mapListUpdated) {
			
			for (Entry<String, String> entry : map.entrySet()) {
				HBox currHBox = (HBox) gamePane.lookup(entry.getKey());
				ImageView imgCurr = (ImageView) currHBox.getChildren().get(0);
				Image imgLoad = null;
				if (!entry.getValue().contains("EMPTY")) {
					// replace image
					imgLoad = importImage(folderToSearch, entry.getValue());
					imgCurr.setId(entry.getValue());
				}else {
					// load random Image
					int randLoad = getRandomNumberInRange(0, imgList.size() - 1);
					imgLoad = importImage(folderToSearch, imgList.get(randLoad));
					imgCurr.setId(imgList.get(randLoad));
				}
				imgCurr.setImage(imgLoad);
			}
		}
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
//				  System.out.println("running: " + entry.getValue());
				  if (!lstImg.isEmpty()) {
					  mapIDImages.put(entry.getKey(),  lstImg.get(0));
					  lstImg.remove(0);
				  } else {
					  mapIDImages.put(entry.getKey(), "EMPTY");
				  }
			  }
		  }
		  
		  
//		  System.out.println();
//		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){ //using map.entrySet() for iteration  
//			  System.out.println("ID: " + entry.getKey() + ", Image: " + entry.getValue());   
//		  }
		
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
//			System.out.println(i);
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

				
			default:
				break;
			}
		});

	}

	// manaCatastropheTest
	
		
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