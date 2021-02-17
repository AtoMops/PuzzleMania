package helpfullstuff;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

class UpwardProgress {
    private ProgressBar progressBar    = new ProgressBar();
    private Group       progressHolder = new Group(progressBar);

    public UpwardProgress(double width, double height) {
        progressBar.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        progressBar.setPrefSize(height, width);
        progressBar.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        progressBar.getTransforms().setAll(
                new Translate(0, height),
                new Rotate(-90, 0, 0)
        );
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Group getProgressHolder() {
        return progressHolder;
    }
}

public class StarCounter extends Application {

    public static final Color INDIA_INK = Color.rgb(35, 39, 50);

    private static final int CANVAS_SIZE      = 400;
    private static final int N_STARS          = 1_000;

    private final Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
    private final Random random = new Random(42);
    private final IntegerProperty visibleStars = new SimpleIntegerProperty(0);
    private       Timeline timeline;

    @Override
    public void start(final Stage stage) {
        Group root = initProgress();
        clearCanvas();

        visibleStars.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                addStars(newValue.intValue() - oldValue.intValue());
            }
        });

        stage.setScene(
            new Scene(
                new HBox(canvas, root),
                INDIA_INK
            )
        );
        stage.show();

        runSimulation();

        stage.getScene().setOnMouseClicked(event -> {
            resetSimulation();
            runSimulation();
        });
    }

    private Group initProgress() {
        UpwardProgress upwardProgress = new UpwardProgress(15, 400);

        ProgressIndicator bar = upwardProgress.getProgressBar();
        bar.setStyle("-fx-base: skyblue; -fx-accent: gold;");
        bar.progressProperty().bind(visibleStars.divide(N_STARS * 1.0));

        return upwardProgress.getProgressHolder();
    }

    private void resetSimulation() {
        clearCanvas();
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private void runSimulation() {
        timeline = new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                new KeyValue(visibleStars, 0)
            ),
            new KeyFrame(
                Duration.seconds(10),
                new KeyValue(visibleStars, N_STARS)
            )
        );
        timeline.play();
    }

    private void clearCanvas() {
        canvas.getGraphicsContext2D().setFill(INDIA_INK);
        canvas.getGraphicsContext2D().fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    }

    private void addStars(int nStarsToAdd) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        PixelWriter writer = context.getPixelWriter();
        for (int i = 0; i < nStarsToAdd; i++) {
            writer.setColor(random.nextInt(CANVAS_SIZE), random.nextInt(CANVAS_SIZE), Color.GOLD);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}