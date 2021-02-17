package com.goregoblin.effects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PixelExplosion  extends Application {

	
	/* hier mal einen guten "Explosion-Effect" für PuzzleMania basteln
	 * 
	 * GraphicsContext ist hier wichtig:
	 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html
	 * 
	 * 
	 */
	
	private double screenFac = 1.15;
	private Rectangle2D screenSize;
	private double width;
	private double height;
	private Pane root;
	private GraphicsContext gC;
	
	private List<Particle> particles = new ArrayList<>();
	private double time = 0.0D;
	private int fullsize; 
	

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
		
//		Image imgIn = new Image(getClass().getResource("hydra_blue.png").toExternalForm());
//		Image imgIn = new Image(getClass().getResource("expl1.gif").toExternalForm());
		
		
		String folderToSearch = "resource/images/";
		Image imgIn = importImage(folderToSearch, "hydra_green.png");
		
		
		/* drawImage kann noch mehr:
		 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html#drawImage-javafx.scene.image.Image-double-double-
		 * 
		 */
		gC.drawImage(imgIn, 600, 300, 200, 200); // image;x;y;h;w

	    root.getChildren().add(canvasRoot);
	    
	    // diese Methode packt einfach pixel in eine Liste (List<Particle> particles)
//	    disintegrate(imgIn);
//		AnimationTimer timer = new AnimationTimer() {
//			@Override
//			public void handle(long now) {
//				time+= 0.017;
//				if (time >= 2) { // this is for delay
//					update();
//				}
//			}
//		};
//		timer.start();
	    
//		List<KeyValue> values = new ArrayList<>();
//
//		particles.forEach(p ->{
////			values.add(new KeyValue(p.xProperty(), p.getX()-700+100)); // -700 is the shift in update()-Method +100 is the shift on the screen we want
//			// setting the Interpolator.DISCRETE means the continuous function is now interpreted binary; so you will not see any pixels moving around 
//			values.add(new KeyValue(p.xProperty(), p.getX()-700+100, Interpolator.DISCRETE)); 
//		});
		
//		Collections.shuffle(values);
	    		  
//	    ImageView imgV = new ImageView(imgIn);
//	      root.getChildren().addAll(canvasRoot, imgV);
		return root;
	}
	
	private void update() {
		
		gC.clearRect(0, 0, width / screenFac - 50, height / screenFac - 50); // this is to clear the screen at every step
		
		particles.removeIf(Particle::isDead);

		/*  Add some random for +/- here
		 * by this we can get complete random pixel movement directions
		 */
		
		// this stream defines how the disintegration-process appears
		particles.parallelStream()
				 .filter(p -> !p.isActive())
//				 .sorted((p1,p2)->  (int)(p1.getX()-p2.getX())) 
//				 .limit((long) (fullsize/60/ 1)) // it's listsize/FramesPerSec/TimeItShouldTakeInSec
				 .limit((long) (fullsize/60/ .01)) 
				 .forEach(p -> p.activate(new Point2D(Math.random()*10,Math.random()*10))); // +/-  define the direction of the disintegration process 
//				 .forEach(p -> p.activate(new Point2D(Math.random()*100,Math.random()*100))); 
		
		particles.forEach(p -> { 
			p.update();
			p.draw(gC);
		});
	}
	
	
	private void disintegrate(Image image) {

		PixelReader pixelreader = image.getPixelReader();

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {

				Color color = pixelreader.getColor(x, y);

				// next line is to ignore transparent pixels
				if (!color.equals(Color.TRANSPARENT)) {
					Particle p = new Particle(x, y, color);
					particles.add(p);
				}
			}
		}
		fullsize = particles.size();
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
