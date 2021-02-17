package com.goregoblin.subTest;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/* diese Klasse nur als Test-Umgebung für SubElemente
 * hier nur Darstellung auf einer leeren Pane
 *  
 *  DAS HIER DIENT ALS TEMPLATE!! ALSO NICHTS REINSCHREIBEN ODER SO 
 *  also einfach c/p wenn du was neues ausprobieren willst
 *  exit auf q 
 *  
 */

public class TestFrameTemplate extends Application {

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
			
			
			
//			root.getChildren().addAll(launchGameBoard());
			
			return root;
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

	public static void main(String[] args) {
		launch(args);
	}
}


	
