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
            gameController.endTurn(true);
        }
        broadcastState();
    }

    synchronized void addPlayerToGame(ClientHandler client) {
        if (gameStarted) {
            // Game already in progress, maybe add as spectator in the future
            return;
        }
        gameController.addPlayer(new Player("Player " + (clients.size()), null));
        System.out.println("Player added. Total players: " + gameController.getPlayers().size());

        // Start the game when 2 players have joined.
        // This can be changed to a "start game" button in a lobby.
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

        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                // First, send the board layout
                oos.writeObject(server.gameController.getBoardPositions());

                // Add player to the game
                server.addPlayerToGame(this);

                // Main loop for receiving actions from the client
                while (true) {
                    Object action = ois.readObject();
                    server.handleAction(action, this);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client " + socket.getInetAddress() + " disconnected.");
            } finally {
                clients.remove(this);
                // Handle player disconnection during a game if necessary
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
    }
}