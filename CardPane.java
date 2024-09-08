import java.io.File;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class CardPane extends Pane {

    /**
	 * Standard Poker card width/height is 635/889.
	 */
	public static final double ASPECT_RATIO = 635.0/889.0;
	private static final String RANK[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};
	private static final String SUIT[] = {"S", "C", "D", "H"};
	private static Image[] images;
	private	static ImageView[] imageViews;

	static{
		String path = "/Users/mracat/Downloads/CS112 Downloads/hw8/";
		String[] imageFilenames = {"suit-black-spade-100.png", "suit-green-club-100.png", "suit-blue-diamond-100.png", "suit-pink-heart-100.png"};
		int numImages = imageFilenames.length;
		images = new Image[numImages];
		imageViews = new ImageView[numImages];
		for (int i = 0; i < numImages; i++) {
			images[i] = new Image(new File(path + imageFilenames[i]).toURI().toString());
			imageViews[i] = new ImageView(images[i]);
		}
	}

	private StringProperty cardStr;

	// Constructor for class
    public CardPane(StringProperty cardStr) {
		this.cardStr = cardStr;
		setPrefSize(635, 889);
		
		changeCard();
		
		// Listen for width and height changes and adjust accordingly
		widthProperty().addListener((observableValue, oldWidth, newWidth) -> changeCard());
        heightProperty().addListener((observableValue, oldHeight, newHeight) -> changeCard());

		// TextField changes and adjust accordingly
        cardStr.addListener((observableValue, oldValue, newValue) -> changeCard());
	}

    private void createCardPane(int rank, int suit) {
        // Calculate paneHeight and paneWidth based on the aspect ratio and the given preference 
		// height and width.
        double paneHeight = getHeight();
		double paneWidth = paneHeight * ASPECT_RATIO;
        if (paneWidth > getWidth()) {
            paneWidth = getWidth();
            paneHeight = paneWidth / ASPECT_RATIO;
        }

		// Make sure paneHeight and paneWidth are at least the prefHeight and prefWidth
		// paneHeight = Math.max(paneHeight, getPrefHeight());
		// paneWidth = Math.max(paneWidth, getPrefWidth());

        // Create Rectangle white rectangle as a child
		Rectangle cardBackground = new Rectangle(paneWidth, paneHeight, javafx.scene.paint.Color.WHITE);
		cardBackground.setArcWidth(paneWidth * 0.1);
		cardBackground.setArcHeight(paneHeight * 0.1);

		// Create suits as a child
		GridPane suits = new GridPane();
		suits.setAlignment(Pos.CENTER);
		double suitWidth = paneWidth / 5;
        double suitHeight = suitWidth * (ASPECT_RATIO * 1.4);
        suits.setHgap(paneWidth * 0.03);
		suits.setVgap(paneHeight * 0.03);
		// Generate suit images and add to GridPane
		int numCols = 3;
		for (int i = 0; i < rank; i++) {
			ImageView view = new ImageView(images[suit]);
            view.setFitWidth(suitWidth);
		    view.setFitHeight(suitHeight);
			// System.out.printf("%d, %d\n", i % numCols, i / numCols);
			suits.add(view, i % numCols, i / numCols);
		}
		// Generate transparent suit images and add to GridPane (for allignment purposes)
		if (rank < 13) {
			for (int i = 0; i < 13 - rank; i++){
				ImageView view = new ImageView(images[suit]);
				view.setFitWidth(suitWidth);
		   		view.setFitHeight(suitHeight);
				view.setOpacity(0);
				suits.add(view, (i + rank) % numCols, (i + rank) / numCols);
			}
		}

		// Create StackPane and add childs
		StackPane stackpane = new StackPane();
		stackpane.layoutXProperty().bind(widthProperty().subtract(paneWidth).divide(2));
		stackpane.layoutYProperty().bind(heightProperty().subtract(paneHeight).divide(2));
		stackpane.setAlignment(Pos.TOP_CENTER);
		stackpane.getChildren().add(cardBackground);
		stackpane.getChildren().add(suits);

		getChildren().add(stackpane);

    }
	
	private void changeCard() {
		String cardCode = cardStr.get().toUpperCase();

		getChildren().clear();
		
        if (cardCode.length() == 2
			&& "A23456789TJQK".contains(cardCode.substring(0, 1))
				&& "CHSD".contains(cardCode.substring(1, 2))) {
					int rank = getRank(cardCode.substring(0, 1));
					int suit = getSuit(cardCode.substring(1, 2));
					createCardPane(rank, suit);
		}
	}

	private int getRank(String rank) {
		for (int i = 0; i < RANK.length; i++){
			if (rank.equals(RANK[i])) {
				return i + 1;
			}
		}
		return 0;
	}

	private int getSuit(String suit) {
		for (int i = 0; i < SUIT.length; i++){
			if (suit.equals(SUIT[i])) {
				return i;
			}
		}
		return 0;
	}

}