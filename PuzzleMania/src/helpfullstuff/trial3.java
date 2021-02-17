package helpfullstuff;

import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class trial3 {

	/*
	 * 
	 * 	for (Map<String, String> map : mapListUpdated) {
			
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
	 * 
	 * 
	System.out.println("anim activated");
	animationPane.setVisible(true);
	
	// diese Liste gibt die aktuellen Images wieder (also nicht die #xx-ID)
	List<List<String>> lstRowList = createListIDRows();
	
	HBox currHBox = (HBox) gamePane.lookup("#05");
	// Hbox verstecken
//	currHBox.setVisible(false);
	
	
	// Id ermitteln (ID meint namen des Import-Bildes; nicht position)
	String idIs = currHBox.getChildren().get(0).getId();
	// hier wird eine NEUE HBox erstellt 
	HBox currHBoxToMove = createHBoxForAnimationPane(idIs);
	
	// das hier ist die Goal-HBox; wir brauchen die um die Position zu bestimmen
	HBox goalHBox = (HBox) gamePane.lookup("#15");

	
	animationPane.getChildren().add(currHBoxToMove);

	currHBox.setVisible(false);
	
	Timeline myTimeLine = new Timeline();
	KeyValue kvLeft = new KeyValue(currHBoxToMove.translateYProperty(),goalHBox.getLocalToParentTransform().getTy(), Interpolator.EASE_BOTH);
	KeyValue kvRight = new KeyValue(currHBoxToMove.translateXProperty(),goalHBox.getLocalToParentTransform().getTx(), Interpolator.EASE_BOTH);
	KeyFrame kfLeft = new KeyFrame(Duration.millis(250), kvLeft);
	KeyFrame kfRight = new KeyFrame(Duration.millis(.1), kvRight);
	myTimeLine.getKeyFrames().addAll(kfLeft,kfRight);
	
	myTimeLine.setOnFinished(e -> {
		currHBox.setVisible(true);
		animationPane.setVisible(false);
	    });
	
	myTimeLine.play();
	
	 */	
}
