package Network;

import View.GameView;
import org.javatuples.Pair;

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
    private GameView gameView;

    public Client(String host, int port) {
        setupView();

        try {
            this.socket = new Socket(host, port);
            thisos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void setupView() {
        this.gameView = GameView.getInstance(new ArrayList<>()); // Start with empty positions
        gameView.setCellClickListener(this::handleCellClick);
        gameView.addEndTurnListener(e -> sendActionToServer("END_TURN"));
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
            // The first object from the server should be the board positions
            ArrayList<Pair<Integer, Integer>> boardPositions = (ArrayList<Pair<Integer, Integer>>) ois.readObject();
            gameView.getBoardPanel().setHexagonPositions(boardPositions);
            gameView.repaint();

            while (true) {
                // Subsequent objects are game state updates
                GameState gameState = (GameState) ois.readObject();
                updateView(gameState);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Disconnected from server.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateView(GameState state) {
        if (state.winner != null) {
            gameView.showWinnerPopup(state.winner);
        }
        gameView.updateTurnLabel(state.currentPlayer);
        gameView.updatePieces(state.piecePositions, state.pieceColors);
        gameView.showValidMoves(state.selectedPixel, state.validMovePixels);
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
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 12345);
        client.start();
    }
}