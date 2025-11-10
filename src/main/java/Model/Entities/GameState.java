package Model.Entities;

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {

    private Board board;
    private Player currentPlayer;
    private ArrayList<Player> players;
    private int curentTurnIndex;
    private Player winner;
    private Coords selectedPiece;
    private boolean isJumpSequence;
    private HashMap<Coords, String> currentValidMoves;
    private String lastMoveDirection;

    public GameState(Board board, Player currentPlayer, ArrayList<Player> players, int curentTurnIndex,
                     HashMap<Coords, String> currentValidMoves, Player winner, Coords selectedPiece,
                     boolean isJumpSequence, String lastMoveDirection) {
        this.setBoard(board);
        this.setCurrentPlayer(currentPlayer);
        this.setPlayers(players);
        this.setCurentTurnIndex(curentTurnIndex);
        this.setCurrentValidMoves(currentValidMoves);
        this.setWinner(winner);
        this.setSelectedPiece(selectedPiece);
        this.setJumpSequence(isJumpSequence);
        this.setLastMoveDirection(lastMoveDirection);
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getCurentTurnIndex() {
        return curentTurnIndex;
    }

    public Player getWinner() {
        return winner;
    }

    public Coords getSelectedPiece() {
        return selectedPiece;
    }

    public boolean isJumpSequence() {
        return isJumpSequence;
    }

    public String getLastMoveDirection() {
        return lastMoveDirection;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setCurentTurnIndex(int curentTurnIndex) {
        this.curentTurnIndex = curentTurnIndex;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setSelectedPiece(Coords selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public void setJumpSequence(boolean jumpSequence) {
        isJumpSequence = jumpSequence;
    }

    public void setLastMoveDirection(String lastMoveDirection) {
        this.lastMoveDirection = lastMoveDirection;
    }

    public HashMap<Coords, String> getCurrentValidMoves() {
        return currentValidMoves;
    }

    public void setCurrentValidMoves(HashMap<Coords, String> currentValidMoves) {
        this.currentValidMoves = currentValidMoves;
    }
}