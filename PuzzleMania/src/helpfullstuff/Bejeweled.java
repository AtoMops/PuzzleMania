package helpfullstuff;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Bejeweled extends Application{

	
	private static final int W = 6;
	private static final int H = 6;
	private static final int SIZE = 100;
	// Array of fixed colors for the circles
	private Color[] myColors = new Color[] {
			Color.BLACK,
			Color.RED,
			Color.BLUE,
			Color.GREEN,
			Color.YELLOW,
	};
	
	
	private Jewel selectedJewel = null;
	private List<Jewel> jewels; 
	
	// this is to add the score
	private IntegerProperty score = new SimpleIntegerProperty();
	
	private Parent getContent() {
		
		Pane root = new Pane();
		root.setPrefSize(W * SIZE + 150, H * SIZE);
		
		jewels = IntStream.range(0, W*H) // stream of int (6*6 -> 0-35)
							.mapToObj(i -> new Point2D(i % W, i/H)) // each of 36 ints added to Point 
							.map(point -> new Jewel(point)) // create new Jewel at position point 
							.collect(Collectors.toList()); // collect all Jewel-objects and add to List
		
		
		root.getChildren().addAll(jewels);
		
		// Text to show current score
		Text textScore = new Text();
		textScore.setTranslateX(W*SIZE);
		textScore.setTranslateY(100);
		textScore.textProperty().bind(score.asString("Score: [%d]"));
		textScore.setFont(Font.font(25));
		
		root.getChildren().add(textScore);
		
		return root;
	}
	
	// to check state of board if user swapped a jewel
	private void checkState() {
		// here grab and sort Jewel-Objects from rows and columns
		Map<Integer, List<Jewel>> jewelsFromRow = jewels.stream().collect(Collectors.groupingBy(Jewel::getRow));
		Map<Integer, List<Jewel>> jewelsFromCol = jewels.stream().collect(Collectors.groupingBy(Jewel::getColumn));
		
		jewelsFromRow.values().forEach(this::checkCombo);
		jewelsFromCol.values().forEach(this::checkCombo);
		
	}
	
	private void checkCombo(List<Jewel> jewelLine) {
		Jewel jewel = jewelLine.get(0);
		
		long count = jewelLine.stream().filter(i -> i.getColor() != jewel.getColor()).count();
		
	if (count == 0) { // according to our stream above this means all elements have the same color
		
		score.set(score.get()+10); // add 10 to current score
		jewelLine.forEach(Jewel::randomizeJewels);
	}
		
	}
	
	
	// for swap of position we just change the color of the jewel
	// but an animated standard movement would look nicer ^^'
	private void swapPos(Jewel a, Jewel b){
		Paint color = a.getColor();
		a.setColor(b.getColor());
		b.setColor(color);
 	}
	
	@Override
	public void start(Stage stage) throws Exception {

		stage.setScene(new Scene(getContent()));
		stage.show();
		
	}
	
	
	private class Jewel extends Parent{

		private Circle circle = new Circle(SIZE/2, SIZE/2, SIZE/2); // here you can add also images
		
		public Jewel(Point2D point) { // point is where we place the jewels
		
			circle.setFill(myColors[new Random().nextInt(myColors.length)]);
			// here the whole Parent is translated (not the circle object within)
			setTranslateX(point.getX()*SIZE);
			setTranslateY(point.getY()*SIZE);
			
			getChildren().add(circle); // we can do this because we extend Parent
			
			setOnMouseClicked(e -> {
				if (selectedJewel == null) { // if no jewel is selected
					selectedJewel = this; // 		
				}else {
					// here swapPos-Method 
					swapPos(selectedJewel, this); 
					checkState();
					selectedJewel = null; // if swapPos-Method is done un-select the jewel
				}
				
				
			});
		}
		
		public void randomizeJewels() {
			
			circle.setFill(myColors[new Random().nextInt(myColors.length)]);
			
		}
		
		public int getColumn() {
			return (int) (getTranslateX()/SIZE);
		}
		
		public int getRow(){
			return (int) (getTranslateY()/SIZE);
		}
		
		
		public void setColor(Paint col) {
			circle.setFill(col);
		}
		
		public Paint getColor() {
			return circle.getFill();
		}
		
		
	}
	

	public static void main(String[] args) {
		launch(args);
	}
	
}
