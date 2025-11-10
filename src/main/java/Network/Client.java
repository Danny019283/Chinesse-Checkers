package Network;

import View.GameView;
import DTO.GameStateDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.javatuples.Pair;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final GameView gameView;
    private final String playerName;
    private final Gson gson;
    private boolean connected = false;

    public Client(String playerName, String host, int port, GameView gameView) {
        this.playerName = playerName;
        this.gameView = gameView;
        this.gson = new GsonBuilder().create();

        // Set up listeners on the view to send actions to this client
        this.gameView.setCellClickListener(this::sendClickAction);
        this.gameView.addEndTurnListener(e -> sendEndTurnAction());

        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.connected = true;
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    private void sendClickAction(Pair<Integer, Integer> pixelPos) {
        if (!connected) return;
        //simple object for serializate
        ClickData clickData = new ClickData(pixelPos.getValue0(), pixelPos.getValue1());
        String jsonAction = gson.toJson(clickData);
        out.println(jsonAction);
    }

    private void sendEndTurnAction() {
        if (!connected) return;
        out.println("END_TURN");
    }

    @Override
    public void run() {
        try {
            // First, send the player name to the server
            out.println(playerName);

            String serverLine;
            while ((serverLine = in.readLine()) != null) {
                final String line = serverLine;
                SwingUtilities.invokeLater(() -> {
                    try {
                        GameStateDTO gameStateDTO = gson.fromJson(line, GameStateDTO.class);
                        gameView.updateView(gameStateDTO);
                    } catch (JsonSyntaxException e) {
                        System.err.println("Received non-JSON or malformed JSON message: " + line);
                    }
                });
            }
        } catch (IOException e) {
        System.out.println("Disconnected from server.");
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(gameView, "Disconnected from the server.", "Connection Lost", JOptionPane.ERROR_MESSAGE);
            gameView.dispose();
        });
    } finally {
            connected = false;
            closeConnection();
        }
    }

    private void handleConnectionError(IOException e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(gameView, "Could not connect to the server: " + e.getMessage(),
                        "Connection Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //serialize data clicks
        private record ClickData(int value0, int value1) {
    }
}