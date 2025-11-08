package Controller;

import View.MainMenuView;
import View.GameStatsView;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MainMenuController {
    private final MainMenuView mainMenuView;
    private final GameController gameController;
    private final GameStatsController statsController;

    public MainMenuController() {
        this.mainMenuView = MainMenuView.getInstance();
        this.gameController = new GameController();
        this.statsController = new GameStatsController();
        setupListeners();
    }

    private void setupListeners() {
        // Listener para el botón "Iniciar Juego"
        mainMenuView.addStartGameListener(e -> handleStartGame());

        // Listener para el botón "Ver Estadísticas"
        mainMenuView.addViewStatsListener(e -> handleViewStats());
    }

    private void handleStartGame() {
        try {
            String serverIP = mainMenuView.getServerIP();
            int serverPort = mainMenuView.getServerPort();

            // Validar campos
            if (serverIP.isEmpty()) {
                mainMenuView.showMessage("Por favor ingrese una IP válida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (serverPort <= 0 || serverPort > 65535) {
                mainMenuView.showMessage("Por favor ingrese un puerto válido (1-65535)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mostrar mensaje de conexión
            mainMenuView.showMessage("Conectando a " + serverIP + ":" + serverPort, "Iniciando Juego", JOptionPane.INFORMATION_MESSAGE);

            // Cerrar el menú principal
            mainMenuView.closeMenu();

            // Aquí iniciarías el juego con la conexión al servidor
            startGameConnection(serverIP, serverPort);

        } catch (Exception ex) {
            mainMenuView.showMessage("Error al iniciar el juego: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewStats() {
        try {
            // Inicializar y mostrar la vista de estadísticas
            statsController.initializeView();

            // Opcional: puedes decidir si cerrar el menú principal o no
            // mainMenuView.closeMenu();

        } catch (Exception ex) {
            mainMenuView.showMessage("Error al abrir estadísticas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGameConnection(String serverIP, int serverPort) {
        // Aquí va la lógica para conectar con el servidor y iniciar el juego
        // Por ahora, solo mostramos un mensaje
        System.out.println("Conectando al servidor: " + serverIP + ":" + serverPort);

        // Ejemplo de cómo podrías iniciar el GameController
        // gameController.initializeGame(serverIP, serverPort);

        // Mientras tanto, mostramos un mensaje
        JOptionPane.showMessageDialog(null,
                "Conexión establecida a:\nIP: " + serverIP + "\nPuerto: " + serverPort +
                        "\n\nFuncionalidad del juego en desarrollo...",
                "Conexión Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para mostrar el menú principal
    public void showMainMenu() {
        mainMenuView.setVisible(true);
    }

    // Método para reiniciar el controlador (útil cuando se cierra y se vuelve a abrir)
    public void restartController() {
        setupListeners();
    }

    // Método principal para probar el controlador
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Iniciando MainMenuController...");

            MainMenuController controller = new MainMenuController();

            // El menú principal ya se muestra automáticamente al crear el controlador
            System.out.println("MainMenuController iniciado correctamente");

            // Ejemplo de cómo podrías simular una reconexión después de cerrar estadísticas
            // Esto es opcional, dependiendo de tu flujo de aplicación
            javax.swing.Timer testTimer = new javax.swing.Timer(10000, e -> {
                System.out.println("Prueba: Controller sigue funcionando después de 10 segundos");
            });
            testTimer.setRepeats(false);
            testTimer.start();
        });
    }
}