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

/**
 * Representa el cliente de red del juego. Se ejecuta en un hilo separado para manejar
 * la comunicación con el servidor sin bloquear la interfaz de usuario (UI). Es responsable
 * de enviar las acciones del jugador (clics, fin de turno) al servidor y de recibir
 * las actualizaciones del estado del juego para mostrarlas en la vista (GameView).
 */
public class Client extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final GameView gameView;
    private final String playerName;
    private final int playerCount;
    private final Gson gson;
    private boolean connected = false;

    /**
     * Construye el cliente, establece la conexión con el servidor y configura
     * los listeners en la GameView para capturar las acciones del usuario.
     * @param playerName Nombre del jugador.
     * @param playerCount Número de jugadores en la partida.
     * @param host Dirección IP del servidor.
     * @param port Puerto del servidor.
     * @param gameView La vista del juego que este cliente controlará.
     */
    public Client(String playerName, int playerCount, String host, int port, GameView gameView) {
        this.playerName = playerName;
        this.playerCount = playerCount;
        this.gameView = gameView;
        this.gson = new GsonBuilder().create();


        // Configura listeners en la vista para enviar acciones a este cliente.
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

    /**
     * Serializa las coordenadas de un clic en formato JSON y las envía al servidor.
     * @param pixelPos Par de enteros con las coordenadas (x, y) del clic.
     */
    private void sendClickAction(Pair<Integer, Integer> pixelPos) {
        if (!connected) return;
        // Objeto simple para serializar los datos del clic.
        ClickData clickData = new ClickData(pixelPos.getValue0(), pixelPos.getValue1());
        String jsonAction = gson.toJson(clickData);
        out.println(jsonAction);
    }

    /**
     * Envía un mensaje simple al servidor para indicar que el jugador ha terminado su turno.
     */
    private void sendEndTurnAction() {
        if (!connected) return;
        out.println("END_TURN");
    }

    /**
     * El bucle principal del hilo del cliente. Escucha continuamente los mensajes del servidor.
     * Procesa mensajes simples (como la asignación de color) y objetos JSON complejos
     * (GameStateDTO), actualizando la interfaz de usuario de forma segura en el
     * hilo de despacho de eventos de Swing (EDT).
     */
    @Override
    public void run() {
        try {
            // 1. Enviar nombre y cantidad de jugadores al servidor para el registro.
            out.println(playerName);
            out.println(playerCount);

            String serverLine;
            while ((serverLine = in.readLine()) != null) {
                final String line = serverLine;

                // Manejo de mensajes simples de texto (no JSON).
                if (line.startsWith("COLOR_ASSIGNED:")) {
                    String color = line.split(":")[1];
                    SwingUtilities.invokeLater(() -> gameView.setPlayerColor(color));
                    continue;
                }

                if ("ESPERANDO_JUGADORES".equals(line)) {
                    SwingUtilities.invokeLater(() -> gameView.showWaitingMessage());
                    continue;
                }

                // El resto de mensajes se asume que son JSON con el estado del juego.
                // Se usa SwingUtilities.invokeLater para actualizar la UI de forma segura.
                SwingUtilities.invokeLater(() -> {
                    try {
                        GameStateDTO gameStateDTO = gson.fromJson(line, GameStateDTO.class);
                        if (gameStateDTO != null) {
                            gameView.updateView(gameStateDTO);
                        }
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

    /**
     * Maneja errores de conexión inicial, mostrando un diálogo de error al usuario.
     */
    private void handleConnectionError(IOException e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(gameView,
                        "Could not connect to the server: " + e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE)
        );
    }

    /**
     * Cierra el socket y los flujos de comunicación de forma segura.
     */
    private void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Record simple para serializar los datos de un clic.
        private record ClickData(int value0, int value1) {
    }
}