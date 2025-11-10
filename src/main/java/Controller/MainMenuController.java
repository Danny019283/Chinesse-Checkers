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

    private void setupListeners() {
        mainMenuView.addStartGameListener(e -> handleStartGame());
        mainMenuView.addViewStatsListener(e -> handleViewStats());
    }

    private void handleStartGame() {
        try {
            String playerName = mainMenuView.getPlayerName();
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

            // Start the network game connection
            startGameConnection(playerName, serverIP, serverPort);

        } catch (Exception ex) {
            mainMenuView.showMessage("Error al iniciar el juego: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewStats() {
        try {
            statsController.initializeView();
        } catch (Exception ex) {
            mainMenuView.showMessage("Error al abrir estadísticas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGameConnection(String playerName, String serverIP, int serverPort) {
        // Create the GameView instance. It will be managed by the Client thread.
        GameView gameView = new GameView();

        // Create and start the client network thread, passing the view to it.
        Client client = new Client(playerName, serverIP, serverPort, gameView);
        client.start();
    }

    public void showMainMenu() {
        mainMenuView.setVisible(true);
    }

    public static void main(String[] args) {
        // The application entry point
        SwingUtilities.invokeLater(() -> {
            MainMenuController controller = new MainMenuController();
            controller.showMainMenu();
        });
    }
}
