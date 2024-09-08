import java.util.Stack;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;

public class CardGridPane extends GridPane {

    /**
	 * Standard Poker card width/height is 635/889.
	 */
	private static final double ASPECT_RATIO = 635.0/889.0;

    private StringProperty[][] cardGrid;

    // Card move stack is a 6 digit string with the digits being
    // the source card suit and rank, target card suit and rank,
    // and the covered card value respectively.
    private Stack<String> cardMoveStrings = new Stack<>();

    private Scene scene = null;

    public CardGridPane(StringProperty[][] cardgrid) {
        this.cardGrid = cardgrid;

        widthProperty().addListener((observableValue, oldWidth, newWidth) -> generateCardGrid());
        heightProperty().addListener((observableValue, oldHeight, newHeight) -> generateCardGrid());

        // final KeyCombination CTRL_Z
        //     = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        // final KeyCombination CTRL_Z_FOR_MAC
        //     = new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN);
        final KeyCombination CTRL_Z_FOR_ALL
            = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination U_KEY
            = new KeyCodeCombination(KeyCode.U);
        sceneProperty().addListener((o, oldVal, newVal) -> { 
            // setup undo listener
            if (getScene() != null) {
                getScene().setOnKeyPressed(ev -> {
                    // // Print "Key pressed.".
                    // System.out.println("Key pressed.");

                    if (CTRL_Z_FOR_ALL.match(ev) || U_KEY.match(ev)) {
                        // // Print "Undo.".
                        // System.out.println("Undo.");

                        if (!cardMoveStrings.isEmpty()) {
                            String undoMove = cardMoveStrings.pop();

                            // Undo the move
                            StringProperty coveredCard = new SimpleStringProperty(undoMove.substring(4, 6));
                            cardGrid[Integer.valueOf(undoMove.substring(0, 1))][Integer.valueOf(undoMove.substring(1, 2))]
                                = cardGrid[Integer.valueOf(undoMove.substring(2, 3))][Integer.valueOf(undoMove.substring(3, 4))];
                            cardGrid[Integer.valueOf(undoMove.substring(2, 3))][Integer.valueOf(undoMove.substring(3, 4))]
                                = coveredCard;

                            generateCardGrid();
                        }
                    }
                });
            }
        });
        
        generateCardGrid();
    }

    private void generateCardGrid() {
        getChildren().clear();

        // Generate GridPane containing CardPanes
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
		gridPane.setVgap(10);

        for (int i = 0; i < cardGrid.length; i++) {
            for (int j = 0; j < cardGrid[i].length; j++) {
                CardPane cardPane = new CardPane(cardGrid[i][j]);
                cardPane.setOnDragDetected(mouseEvent -> cardPane.startFullDrag());
                cardPane.setOnMouseDragReleased(mouseEvent -> {
                    int sourceRow = CardGridPane.getRowIndex((CardPane) mouseEvent.getGestureSource());
                    int sourceCol = CardGridPane.getColumnIndex((CardPane) mouseEvent.getGestureSource());
                    int targetRow = CardGridPane.getRowIndex((CardPane) mouseEvent.getSource());
                    int targetCol = CardGridPane.getColumnIndex((CardPane) mouseEvent.getSource());

                    if (moveIsLegal(sourceRow, sourceCol, targetRow, targetCol)) {
                        // Save the move
                        String cardString = "" + sourceRow + sourceCol + targetRow + targetCol + cardGrid[targetRow][targetCol].get();
                        cardMoveStrings.push(cardString);

                        // Make the move
                        StringProperty temp = new SimpleStringProperty("00");
                        cardGrid[targetRow][targetCol] = cardGrid[sourceRow][sourceCol];
                        cardGrid[sourceRow][sourceCol] = temp;

                        // Save the sceen if scene is null
                        if (scene == null) {
                            scene = getScene();
                        }

                        generateCardGrid();
                    }
                });
                gridPane.add(cardPane, j, i);
            }
        }

        // Centering of the gridPane
        double paneHeight = getHeight();
		double paneWidth = paneHeight * ASPECT_RATIO;
        if (paneWidth > getWidth()) {
            paneWidth = getWidth();
            paneHeight = paneWidth / ASPECT_RATIO;
        }

        setPrefSize(paneWidth, paneHeight);
        setAlignment(Pos.CENTER);
        getChildren().add(gridPane);
    }

    private boolean moveIsLegal(int sourceRow, int sourceCol, int targetRow, int targetCol) {
        String sourceCard = cardGrid[sourceRow][sourceCol].get();
        String targetCard = cardGrid[targetRow][targetCol].get();

        // Prevent index out of bounds for the 4 variables below
        if (sourceCard.length() != 2 || targetCard.length() != 2) {
            // System.out.println("Source or target card is not a valid card.");
            return false;
        }

        String sourceCardRank = sourceCard.substring(0, 1);
        String sourceCardSuit = sourceCard.substring(1, 2);

        String targetCardRank = targetCard.substring(0, 1);
        String targetCardSuit = targetCard.substring(1, 2);

        String ranks = "A23456789TJQK";

        if (sourceRow == targetRow && sourceCol == targetCol) {
            // System.out.println("Source and target are the same.");
            return false;
        }
        else if (sourceRow != targetRow && sourceCol != targetCol) {
            // System.out.println("Source and target are not in the same row or column.");
            return false;
        }
        else if (sourceCard.length() != 2
        || !("A23456789TJQK".contains(sourceCardRank))
            || !("CHSD".contains(sourceCardSuit))) {
            // System.out.println("Source card is not a valid card.");
            return false;
        }
        else if (targetCard.length() != 2
        || !("A23456789TJQK".contains(targetCardRank))
            || !("CHSD".contains(targetCardSuit))) {
            // System.out.println("Target card is not a valid card.");
            return false;
        }
        else if (!(sourceCardSuit.equals(targetCardSuit)) && Math.abs(ranks.indexOf(sourceCardRank) - ranks.indexOf(targetCardRank)) != 1 && Math.abs(ranks.indexOf(sourceCardRank) - ranks.indexOf(targetCardRank)) != 0) {
            // System.out.println("Source and target cards are not of the same suit and not of adjacent ranks.");
            return false;
        }

        return true;
    }

    private static int getRowIndex(CardPane cardPane) {
        return GridPane.getRowIndex(cardPane);
    }

    private static int getColumnIndex(CardPane cardPane) {
        return GridPane.getColumnIndex(cardPane);
    }

}

