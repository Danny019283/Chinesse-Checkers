package Network;

import Controller.GameController;
import Model.Entities.Player;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final GameController gameController;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private boolean gameStarted = false;

    public Server(int port) {
        this.gameController = new GameController(true);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                ClientHandler newClient = new ClientHandler(socket, this);
                clients.add(newClient);
                new Thread(newClient).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    synchronized void broadcastState() {
        if (!gameStarted) return;
        Client.GameState state = gameController.getGameState();
        for (ClientHandler client : clients) {
            client.sendState(state);
        }
    }

    synchronized void handleAction(Object action, ClientHandler source) {
        if (!gameStarted) return;

        // Basic validation to ensure the action is from the correct player
        int playerIndex = clients.indexOf(source);
        if (playerIndex != gameController.getCurrentTurnIndex()) {
            // It's not this player's turn, ignore the action.
            // Maybe send a message back to the client? For now, just log it.
            System.out.println("Action from wrong player ignored. Current turn: " + gameController.getCurrentTurnIndex() + ", player index: " + playerIndex);
            return;
        }

        if (action instanceof Pair) {
            gameController.handleCellClick((Pair<Integer, Integer>) action);
        } else if (action.equals("END_TURN")) {
            gameController.endTurn(gameController.isJumpSequence());
        }
        broadcastState();
    }

    synchronized void broadcastMessage(Object message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    synchronized void addPlayerToGame(ClientHandler client, String playerName) {
        if (gameStarted) {
            // Game already in progress, maybe add as spectator in the future
            return;
        }
        client.setPlayerName(playerName);
        gameController.addPlayer(new Player(playerName, null));
        System.out.println(playerName + " added. Total players: " + gameController.getPlayers().size());

        // Start the game when 2 players have joined.
        if (gameController.getPlayers().size() == 2) {
            System.out.println("Two players have joined. Starting game.");
            gameController.startGame();
            gameStarted = true;
            broadcastState();
        }
    }

    public static void main(String[] args) {
        new Server(12345);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final Server server;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private String playerName;

        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        @Override
        public void run() {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                // First, read the player's name
                String name = (String) ois.readObject();
                this.playerName = name;

                // Add player to the game with the received name
                server.addPlayerToGame(this, this.playerName);

                // Then, send the board layout
                oos.writeObject(server.gameController.getBoardPositions());

                // Main loop for receiving actions from the client
                while (true) {
                    Object action = ois.readObject();
                    server.handleAction(action, this);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client " + playerName + " (" + socket.getInetAddress() + ") disconnected.");
            } finally {
                clients.remove(this);
                // Notify remaining clients that a player has disconnected
                if (gameStarted) {
                    server.broadcastMessage(new ServerMessage(ServerMessage.MessageType.PLAYER_DISCONNECTED, playerName + " has left the game. The game has ended."));
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendState(Client.GameState state) {
            try {
                oos.writeObject(state);
                oos.reset(); // Use reset to prevent caching of the GameState object
            } catch (IOException e) {
                System.out.println("Error sending state to client " + socket.getInetAddress());
            }
        }

        public void sendMessage(Object message) {
            try {
                oos.writeObject(message);
                oos.reset();
            } catch (IOException e) {
                System.out.println("Error sending message to client " + socket.getInetAddress());
            }
        }
    }
}