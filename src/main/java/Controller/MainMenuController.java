package Controller;

import Network.Client;
import View.GameView;
import View.MainMenuView;

import javax.swing.*;
import java.util.ArrayList;

public class MainMenuController {
    private final MainMenuView mainMenuView;
    private final GameStatsController statsController;

    public MainMenuController() {
        this.mainMenuView = new MainMenuView();
        this.statsController = new GameStatsController();
        setupListeners();
    }

    /**
     * Configura los listeners para los botones del menú principal,
     * asignando las acciones correspondientes a cada uno.
     */
    private void setupListeners() {
        mainMenuView.addStartGameListener(e -> handleStartGame());
        mainMenuView.addViewStatsListener(e -> handleViewStats());
    }

    /**
     * Procesa el inicio del juego. Valida los datos de entrada del usuario
     * (nombre, IP, etc.) y, si son correctos, cierra el menú e invoca
     * el método para establecer la conexión de red.
     */
    private void handleStartGame() {
        try {
            String playerName = mainMenuView.getPlayerName();
            int playerCount = mainMenuView.getSelectedPlayerCount();
            String serverIP = mainMenuView.getServerIP();
            int serverPort = mainMenuView.getServerPort();

            if (playerName.isEmpty()) {
                mainMenuView.showMessage("Por favor ingrese un nombre de jugador.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (serverIP.isEmpty()) {
                mainMenuView.showMessage("Por favor ingrese una IP válida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (serverPort <= 0 || serverPort > 65535) {
                mainMenuView.showMessage("Por favor ingrese un puerto válido (1-65535)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            mainMenuView.showMessage("Conectando a " + serverIP + ":" + serverPort, "Iniciando Juego", JOptionPane.INFORMATION_MESSAGE);
            mainMenuView.closeMenu();

            // Inicia la conexión de red para el juego.
            startGameConnection(playerName, playerCount, serverIP, serverPort);

        } catch (Exception ex) {
            mainMenuView.showMessage("Error al iniciar el juego: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja la solicitud de ver estadísticas, delegando la tarea
     * al GameStatsController para que inicialice y muestre la vista correspondiente.
     */
    private void handleViewStats() {
        try {
            statsController.initializeView();
        } catch (Exception ex) {
            mainMenuView.showMessage("Error al abrir estadísticas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicia la conexión del cliente con el servidor. Crea la vista del juego (GameView)
     * y un hilo de cliente (Client), pasando la vista al cliente para que este
     * la gestione y se encargue de la comunicación de red.
     * @param playerName Nombre del jugador.
     * @param playerCount Cantidad de jugadores esperados.
     * @param serverIP IP del servidor.
     * @param serverPort Puerto del servidor.
     */
    private void startGameConnection(String playerName, int playerCount, String serverIP, int serverPort) {
        // La instancia de GameView será gestionada por el hilo del cliente.
        GameView gameView = new GameView();

        // Crea e inicia el hilo del cliente, pasándole la vista.
        Client client = new Client(playerName, playerCount, serverIP, serverPort, gameView);
        client.start();
    }

    /**
     * Hace visible el menú principal de la aplicación.
     */
    public void showMainMenu() {
        mainMenuView.setVisible(true);
    }

    /**
     * Punto de entrada de la aplicación. Inicia el controlador del menú principal
     * en el hilo de despacho de eventos de Swing.
     */
    public static void main(String[] args) {
        // The application entry point
        SwingUtilities.invokeLater(() -> {
            MainMenuController controller = new MainMenuController();
            controller.showMainMenu();
        });
    }
}
