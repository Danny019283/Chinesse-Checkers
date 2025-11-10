package DTO;

import Model.Entities.Coords;
import Model.Entities.Player;
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
    private final String currentPlayerColor;
    private final ArrayList<Player> players;

    public GameStateDTO(ArrayList<PixelCell> piecePositions, Coords selectedPiecePixel,
                        Set<Coords> validMovePixels, String currentPlayerName, String winnerName, boolean isJumpSequence,
                        String currentPlayerColor, ArrayList<Player> players) {
        this.piecePositions = piecePositions;
        this.selectedPiecePixel = selectedPiecePixel;
        this.validMovePixels = validMovePixels;
        this.currentPlayerName = currentPlayerName;
        this.winnerName = winnerName;
        this.isJumpSequence = isJumpSequence;
        this.currentPlayerColor = currentPlayerColor;
        this.players = players;
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

    public String getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
