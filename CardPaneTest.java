import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class CardPaneTest extends Application {
	
	private String initCard = "KC";
	private StringProperty cardStr = new SimpleStringProperty(initCard);
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FlowPane topPane = new FlowPane();
		topPane.getChildren().add(new Label("2-character card name (e.g. \"TC\" for 10 of Clubs): "));
		TextField textField = new TextField(initCard);
		cardStr.bind(textField.textProperty());
		topPane.getChildren().add(textField);
		
		BorderPane rootPane = new BorderPane(); // Create a new pane. 
		rootPane.setStyle("-fx-background: #005500;");
		rootPane.setTop(topPane);
		rootPane.setCenter(new CardPane(cardStr));
		topPane.setAlignment(Pos.TOP_CENTER);
		
		Scene scene = new Scene(rootPane, 600, 800); // Create a scene with the pane
		primaryStage.setTitle("CardPane Test"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
	
	public static void main(String[] args) {
		launch(args);
	}


}
