package helpfullstuff;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SampleTimeOptions extends Application  {
	
	/* from here:
	 * https://stackoverflow.com/questions/19549852/javafx-binding-timelines-duration-to-a-property
	 * 
	 * also:
	 * 
		All animations have a rateProperty() which can be changed on a running animation!
		This seems like a much cleaner solution:
		
		private void createTimer(ComboBox<Double> timeOptions) {
		    Timeline timer = new Timeline(
		            new KeyFrame(Duration.seconds(1),
		            evt-> System.out.println(
		                        "This is called every "
		                                + timeOptions.getValue()
		                                + " seconds"
		                ));
		
		    timer.setCycleCount(Timeline.INDEFINITE);
		    timer.rateProperty()
		         .bind(new SimpleDoubleProperty(1.0)
		         .divide(timeOptions.valueProperty()));
		
		    timeOptions.getSelectionModel().selectFirst();
		}
	 * 
	 */
	
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group g = new Group();
        Scene scene = new Scene(g, 150, 100);

        ComboBox<Double> timerOptions = createTimerOptions(
                0.5, 1.0, 1.5, 2.0
        );
        g.getChildren().addAll(timerOptions);

        createTimer(timerOptions);

        stage.setScene(scene);
        stage.show();
    }

    private ComboBox<Double> createTimerOptions(double... options) {
        ObservableList<Double> data = FXCollections.observableArrayList();

        for (Double option: options) {
            data.add(option);
        }

        return new ComboBox<Double>(data);
    }

    private void createTimer(ComboBox<Double> timeOptions) {
        final Timeline timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);

        timeOptions.valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                resetTimer(timer, newValue);
            }
        });

        timeOptions.getSelectionModel().selectFirst();
    }

    private void resetTimer(Timeline timer, final double timerInterval) {
        KeyFrame keyFrame = new KeyFrame(
            Duration.seconds(timerInterval),
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println(
                            "This is called every "
                                    + timerInterval
                                    + " seconds"
                    );
                }
            }
        );

        timer.stop();
        timer.getKeyFrames().setAll(
                keyFrame
        );
        timer.play();
    }
}