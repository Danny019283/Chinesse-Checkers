package Network;

import View.GameView;
import org.javatuples.Pair;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client extends Thread {

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final GameView gameView;
    private final String playerName;

    public Client(String playerName, String host, int port, GameView gameView) {
        this.playerName = playerName;
        this.gameView = gameView;
        // Set up listeners on the already-created view
        this.gameView.setCellClickListener(this::handleCellClick);
        this.gameView.addEndTurnListener(e -> sendActionToServer("END_TURN"));

        try {
            this.socket = new Socket(host, port);
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gameView, "Could not connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void handleCellClick(Pair<Integer, Integer> pixelPos) {
        sendActionToServer(pixelPos);
    }

    private void sendActionToServer(Object action) {
        try {
            oos.writeObject(action);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // First, send the player name to the server
            oos.writeObject(playerName);

            // The first object from the server should be the board positions
            ArrayList<Pair<Integer, Integer>> boardPositions = (ArrayList<Pair<Integer, Integer>>) ois.readObject();
            SwingUtilities.invokeLater(() -> {
                gameView.setHexagonPositions(boardPositions);
                gameView.repaint();
            });


            while (true) {
                // Subsequent objects can be GameState or ServerMessage
                Object receivedObject = ois.readObject();

                if (receivedObject instanceof GameState) {
                    GameState gameState = (GameState) receivedObject;
                    // UI updates must run on the Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> updateView(gameState));
                } else if (receivedObject instanceof ServerMessage) {
                    ServerMessage serverMessage = (ServerMessage) receivedObject;
                    // Show the message in a popup
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(gameView, serverMessage.getPayload(), "Server Notification", JOptionPane.INFORMATION_MESSAGE)
                    );
                    // If a player disconnects, the game might be over, so we can break the loop.
                    if (serverMessage.getType() == ServerMessage.MessageType.PLAYER_DISCONNECTED) {
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Disconnected from server.");
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(gameView, "Disconnected from the server.", "Connection Lost", JOptionPane.ERROR_MESSAGE)
            );
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateView(GameState state) {
        // Check for winner and handle end of game flow
        if (state.winner != null && !state.winner.isEmpty()) {
            gameView.showWinnerPopup(state.winner);
            gameView.dispose(); // Close the game window
            Controller.MainMenuController.main(null); // Relaunch the main menu
            return; // Stop further processing of this state
        }

        gameView.updateTurnLabel(state.currentPlayer);
        gameView.updatePieces(state.piecePositions, state.pieceColors);
        gameView.showValidMoves(state.selectedPixel, state.validMovePixels);
        gameView.setEndTurnButtonEnabled(state.isJumpSequence);
        gameView.repaint();
    }

    // A simple class to encapsulate game state for serialization
    public static class GameState implements Serializable {
        public ArrayList<Pair<Integer, Integer>> piecePositions;
        public HashMap<Pair<Integer, Integer>, String> pieceColors;
        public Pair<Integer, Integer> selectedPixel;
        public ArrayList<Pair<Integer, Integer>> validMovePixels;
        public String currentPlayer;
        public String winner;
        public boolean isJumpSequence;
    }
}
