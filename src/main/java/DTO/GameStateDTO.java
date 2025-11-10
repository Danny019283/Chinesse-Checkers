package DTO;

import Model.Entities.Coords;
import View.PixelCell;

import java.util.ArrayList;
import java.util.Set;

/**
 * A Data Transfer Object that holds all the necessary information for the GameView to draw itself.
 * This data is already converted to pixel coordinates and is ready for rendering.
 */
public class GameStateDTO {
    private ArrayList<PixelCell> piecePositions;
    private final Coords selectedPiecePixel;
    private Set<Coords> validMovePixels;
    private final String currentPlayerName;
    private final String winnerName;
    private final boolean isJumpSequence;

    public GameStateDTO(ArrayList<PixelCell> piecePositions, Coords selectedPiecePixel,
                        Set<Coords> validMovePixels, String currentPlayerName, String winnerName, boolean isJumpSequence) {
        this.piecePositions = piecePositions;
        this.selectedPiecePixel = selectedPiecePixel;
        this.validMovePixels = validMovePixels;
        this.currentPlayerName = currentPlayerName;
        this.winnerName = winnerName;
        this.isJumpSequence = isJumpSequence;
    }

    public ArrayList<PixelCell> getPiecePositions() {
        return piecePositions;
    }

    public void setPiecePositions(ArrayList<PixelCell> piecePositions) {
        this.piecePositions = piecePositions;
    }

    public Coords getSelectedPiecePixel() {
        return selectedPiecePixel;
    }

    public Set<Coords> getValidMovePixels() {
        return validMovePixels;
    }

    public void setValidMovePixels(Set<Coords> validMovePixels) {
        this.validMovePixels = validMovePixels;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public boolean isJumpSequence() {
        return isJumpSequence;
    }
}
