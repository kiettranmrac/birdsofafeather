import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CardGridPaneTest extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        new JFXPanel(); // this will prepare JavaFX toolkit and environment

        // shuffle cards
        long seed = getPositiveInteger("Deal Seed", "Please enter a positive deal seed integer:");
		Random random = new Random(seed);
		Stack<String> deck = new Stack<String>();
		String suitChars = "CHSD";
		String rankChars = "A23456789TJQK";
		for (int i = 0; i < suitChars.length(); i++)
			for (int j = 0; j < rankChars.length(); j++)
				deck.push("" + rankChars.charAt(j) + suitChars.charAt(i));
		Collections.shuffle(deck, random);
		
		// create StringProperty grid of cards
		final int SIZE = 4;
		StringProperty[][] cardGrid = new StringProperty[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				cardGrid[i][j] = new SimpleStringProperty(deck.pop());
		
		// create layout with CardGridPane
		Pane rootPane = new StackPane(); // Create a new pane. 
		rootPane.setStyle("-fx-background: #005500;");
		rootPane.getChildren().add(new CardGridPane(cardGrid));		
		Scene scene = new Scene(rootPane, 550, 750); // Create a scene with the pane
		primaryStage.setTitle("CardGridPane Test"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
//		primaryStage.setResizable(false);
		primaryStage.show(); // Display the stage
	}
	
	public static int getPositiveInteger(String headerText, String prompt)  {
		int retVal = 0;
		do {		
			// request positive integer
			TextInputDialog dialog = new TextInputDialog("" + ((int) (Math.random() * 1000000) + 1));
	        dialog.initStyle(StageStyle.UTILITY);
			dialog.setTitle("Input Positive Integer");
			dialog.setHeaderText(headerText);			
			dialog.setContentText(prompt);
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent())
				retVal = Integer.parseInt(result.get());
			if (retVal > 0)
				return retVal;
			else {
				// alert to invalid input
				Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.initStyle(StageStyle.UTILITY);
		        alert.setTitle("Invalid Input");
		        alert.setHeaderText(null); // uncomment to see default error header
		        alert.setGraphic(null); // uncomment to see default error header
		        alert.setContentText("Invalid positive integer. Please try again.");
		        alert.showAndWait();
			}
		} while (true);
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
