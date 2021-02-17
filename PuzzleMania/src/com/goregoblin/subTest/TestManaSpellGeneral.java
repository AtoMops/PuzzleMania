package com.goregoblin.subTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/* diese Klasse nur als Test-Umgebung für SubElemente
 * hier nur Darstellung auf einer leeren Pane
 * 
 *  
 */

public class TestManaSpellGeneral extends Application {

	// für JavaFX
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private Scene scene; 
	
	
	
	// to use JavaFX-Plotting
		private Parent launchMainScreen() {

			double parentRelSize = 1.25;

			root = new Pane();
			root.setPrefSize(width / parentRelSize, height / parentRelSize);
			root.setStyle("-fx-background-color: rgba(150, 255, 255, 0.8);");
			

			/* hier ist Spielwiese
			 * 
			 */
			
			VBox manaSpellContainer = new VBox();
			
			manaSpellContainer.setTranslateX(200);
			manaSpellContainer.setTranslateY(400);
			
			List<ManaSpell> lstManaSpellPool = createManaSpellPool();
			
			// this to randomly assign spells for player
			Set<ManaSpell> lstManaSpellsPlayer = new HashSet<ManaSpell>();
			do {
				int currSpell = getRandomNumberInRange(0, 9);
				lstManaSpellsPlayer.add(lstManaSpellPool.get(currSpell));
			} while (lstManaSpellsPlayer.size() < 4);	
			
			lstManaSpellsPlayer.stream().forEach(System.out::println);

			manaSpellContainer.getChildren().addAll(lstManaSpellsPlayer);
			
			
			root.getChildren().addAll(manaSpellContainer);
			
			return root;
		}
	
		
		// Methode die alle 10 möglichen ManaSpells generiert
		private List<ManaSpell> createManaSpellPool(){
			
			List<ManaSpell> lstManaSpells = new ArrayList<ManaSpell>(); 
			
			try {
				// double types
				lstManaSpells.add(new ManaSpell(0, 0));
				lstManaSpells.add(new ManaSpell(1, 1));
				lstManaSpells.add(new ManaSpell(2, 2));
				lstManaSpells.add(new ManaSpell(3, 3));
				// mixed types
				lstManaSpells.add(new ManaSpell(0, 1));
				lstManaSpells.add(new ManaSpell(0, 2));
				lstManaSpells.add(new ManaSpell(0, 3));				
				lstManaSpells.add(new ManaSpell(1, 2));
				lstManaSpells.add(new ManaSpell(1, 3));
				lstManaSpells.add(new ManaSpell(2, 3));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return lstManaSpells;
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
	
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case Q:
				Platform.exit(); // closes JavaFX app (but JVM still running)
				System.exit(0); // closes JVM
				break;
				
			default:
				break;
			}
		});
		

	}

	// to generate random Integer
	private int getRandomNumberInRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}


	
