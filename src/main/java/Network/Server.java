package Network;

import Controller.GameController;
import Controller.GameStatsController;
import Model.Entities.Player;
import DTO.GameStateDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements GameController.GameStateUpdateCallback {

    private final GameController gameController;
    private final GameStatsController statsController = new GameStatsController();
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Gson gson;

    public Server(int port) {
        this.gameController = new GameController();
        this.gameController.setUpdateCallback(this);
        this.gson = new GsonBuilder().create();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");
                ClientHandler newClient = new ClientHandler(socket, this);
                clients.add(newClient);
                new Thread(newClient).start();
            }
        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onStateUpdated() {
        System.out.println("Server received state update. Broadcasting to clients...");
        broadcastGameState();
    }

    private synchronized void broadcastGameState() {
        GameStateDTO gameStateDTO = gameController.getGameViewDTO();
        if (gameStateDTO == null) {
            System.err.println("Cannot broadcast a null game state.");
            return;
        }
        if (gameStateDTO.getWinnerName() != null && !gameStateDTO.getWinnerName().isEmpty()) {
            String[] players = gameStateDTO.getPlayers().stream().map(Player::getName).toArray(String[]::new);
            statsController.addStatsGame(
                    gameStateDTO.getWinnerName(),
                    gameStateDTO.getCurrentPlayerColor(),
                    players
            );
        }
        System.out.println(gameStateDTO.getPiecePositions().toString());
        String jsonState = gson.toJson(gameStateDTO);
        for (ClientHandler client : clients) {
            client.sendMessage(jsonState);
        }
        System.out.println("Broadcasted game state to " + clients.size() + " clients.");
    }

    private synchronized void handleClientMessage(String messageJson, ClientHandler source) {
        Player sender = source.getPlayer();
        Player currentPlayer = gameController.getCurrentPlayer();

        if (sender == null || !sender.equals(currentPlayer)) {
            System.out.println("Action from wrong player or game not ready. Action ignored.");
            return;
        }

        try {
            if ("END_TURN".equals(messageJson) || "\"END_TURN\"".equals(messageJson)) {
                gameController.endTurn();
            } else {
                Map<String, Double> pixelPos = gson.fromJson(messageJson, Map.class);
                int pixelX = pixelPos.get("value0").intValue();
                int pixelY = pixelPos.get("value1").intValue();
                gameController.handleCellClick(pixelX, pixelY);
                System.out.println("Click recibido: " + pixelX + ", " + pixelY);
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON received from client: " + messageJson);
        }
    }

    private int targetPlayerCount = 0;

    private synchronized void addPlayer(String playerName, int playerCount, ClientHandler clientHandler) {
        if (gameController.getPlayers().size() >= 6) {
            System.out.println("Game is full, rejecting player: " + playerName);
            clientHandler.close();
            return;
        }
        //if is first joined player set max player in the game
        if (gameController.getPlayers().isEmpty()) {
            targetPlayerCount = playerCount;
            System.out.println("Partida configurada para " + targetPlayerCount + " jugadores.");
        }

        // Verify players limit
        if (gameController.getPlayers().size() >= targetPlayerCount) {
            System.out.println("Game is full (max " + targetPlayerCount + "), rejecting player: " + playerName);
            clientHandler.sendMessage("GAME_FULL");
            clientHandler.close();
            return;
        }

        Player newPlayer = new Player(playerName, "");
        clientHandler.setPlayer(newPlayer);
        gameController.addPlayer(newPlayer);
        //show assigned color
        clientHandler.sendMessage("COLOR_ASSIGNED:" + newPlayer.getColor());
        //if all players are joined, start
        if (gameController.getPlayers().size() == targetPlayerCount) {
            System.out.println("Todos los jugadores conectados. Creando juego...");
            gameController.createNewGame(new ArrayList<>(gameController.getPlayers()));
        } else {
            clientHandler.sendMessage("ESPERANDO_JUGADORES");
        }
    }

    private void removeClient(ClientHandler client) {
        clients.remove(client);
        if (client.getPlayer() != null) {
            System.out.println(client.getPlayer().getName() + " disconnected.");
        }
    }

    public static void main(String[] args) {
        new Server(12345);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final Server server;
        private PrintWriter out;
        private BufferedReader in;
        private Player player;

        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
        }

        public void setPlayer(Player player) { this.player = player; }
        public Player getPlayer() { return this.player; }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String name = in.readLine();
                int playerCount = Integer.parseInt(in.readLine()); // ← Agregá esta línea

                if (name != null && !name.trim().isEmpty()) {
                    server.addPlayer(name.trim(), playerCount, this); // ← Ahora con 3 parámetros
                } else {
                    System.err.println("Client connected without a name. Closing connection.");
                    close();
                    return;
                }

                String line;
                while ((line = in.readLine()) != null) {
                    server.handleClientMessage(line, this);
                }
            } catch (IOException e) {
                System.out.println("Connection lost with " + (player != null ? player.getName() : "client"));
            } finally {
                close();
            }
        }

        public void sendMessage(String message) {
            if (out != null && !out.checkError()) {
                out.println(message);
            }
        }

        public void close() {
            server.removeClient(this);
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
