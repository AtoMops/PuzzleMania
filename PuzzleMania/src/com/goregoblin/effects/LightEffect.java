package com.goregoblin.effects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LightEffect  extends Application {

	
	/* hier mal ein Licht-Effekt
	 *  hat irres Potential ^^'
	 *  
	 *  --> könnte mit AnimationTimer gut sein z.B. rotierende Lichtpegel etc 
	 *  
	 */
	
	private double screenFac = 1.15;
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private GraphicsContext gC;

	// my Standard createRoot()-Method;, makes live easier ^^'
	private Pane createRoot() {
		screenSize = Screen.getPrimary().getBounds();
		width = screenSize.getWidth();
		height = screenSize.getHeight();
		root = new Pane(); // Pane is a layout without any grid o.s.e.
		root.setPrefSize(width / screenFac, height / screenFac);
		return root;
	}
	
	private Parent showMyPixelExplosion() {
		root = createRoot();
		root.setStyle("-fx-background-color: rgba(0, 200, 200, 0.4);");
		
		int cXY = 50;
		Pane canvasRoot = new Pane();
		canvasRoot.setPrefSize(width / screenFac - cXY, height / screenFac - cXY);
		canvasRoot.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");
		canvasRoot.setTranslateX(cXY/2);
		canvasRoot.setTranslateY(cXY/2);
		
		// add Canvas; a little bit smaller than Pane
		Canvas canvas = new Canvas(width / screenFac - 50, height / screenFac - 50);
		canvasRoot.getChildren().add(canvas);

		gC = canvas.getGraphicsContext2D();
		
		Image imgIn = new Image(getClass().getResource("hydra_blue.png").toExternalForm());
		
		/* drawImage kann noch mehr:
		 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html#drawImage-javafx.scene.image.Image-double-double-
		 * 
		 */
		gC.drawImage(imgIn, 600, 300, 200, 200); // image;x;y;h;w
		
		
		  //Instantiating the Light.Spot class 
	      Light.Spot light = new Light.Spot(); 
	      
	      //Setting the color of the light 
	      light.setColor(Color.WHITE); 
	      
	      //setting the position of the light 
	      light.setX(50); 
	      light.setY(200); 
	      light.setZ(300); // !! die Höhe ist hier wichtig! wegen dem "Licht-Effekt" haben wir hier auch eine z-ebene 
	       
	      //Instantiating the Lighting class  
	      Lighting lighting = new Lighting(); 
	      
	      //Setting the light source 
	      lighting.setLight(light);
	      

	      ImageView imgV = new ImageView(imgIn);
	
	      imgV.setEffect(lighting);
	      
	      root.getChildren().addAll(canvasRoot, imgV);
		return root;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		// Scene-Settings
		Scene scene = new Scene(showMyPixelExplosion());
		scene.setFill(Color.TRANSPARENT); // !!
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
		
		// Stage-Settings
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initStyle(StageStyle.TRANSPARENT); // !!
		stage.setFullScreenExitHint(""); // remove label
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.setFullScreen(false);
		stage.show();
		
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
