package Controller;

import Model.Entities.GameStats;
import Model.Service.GameStatsService;
import View.GameStatsView;

import javax.swing.*;
import java.util.ArrayList;

public class GameStatsController {
    private final GameStatsService serviceGameStats;
    private GameStatsView statsView;

    public GameStatsController() {
        this.serviceGameStats = GameStatsService.getInstance();
    }

    // Método para inicializar la vista y conectar todos los listeners
    public void initializeView() {
        if (statsView == null) {
            statsView = GameStatsView.getInstance();
            setupListeners();
            loadAllStats(); // Cargar todas las estadísticas al iniciar
        }
        statsView.setVisible(true);
    }

    // Configurar todos los listeners de la vista
    private void setupListeners() {
        // Listener para el botón de búsqueda
        statsView.addSearchListener(e -> handleSearch());

        // Listener para la selección de la tabla
        statsView.addTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }

    // Manejar la búsqueda por ID de juego
    private void handleSearch() {
        String searchText = statsView.getSearchText();
        if (!searchText.isEmpty()) {
            try {
                int gameId = Integer.parseInt(searchText);
                searchStats(gameId);
            } catch (NumberFormatException ex) {
                statsView.showMessage("Por favor ingrese un ID de juego válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si el campo de búsqueda está vacío, recargar todas las stats
            loadAllStats();
        }
    }

    // Manejar la selección de una fila en la tabla
    private void handleTableSelection() {
        statsView.showSelectedGameDetails();
    }

    // Cargar todas las estadísticas en la tabla
    public void loadAllStats() {
        try {
            ArrayList<GameStats> allStats = serviceGameStats.findAllStats();
            if (allStats != null && !allStats.isEmpty()) {
                statsView.updateTable(allStats);
            } else {
                statsView.showMessage("No hay estadísticas de juegos disponibles", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            statsView.showMessage("Error al cargar las estadísticas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Buscar estadísticas por ID de juego
    public void searchStats(int gameId) {
        try {
            GameStats stats = serviceGameStats.findStats(gameId);
            if (stats != null) {
                // Mostrar solo el juego encontrado en la tabla
                ArrayList<GameStats> singleStat = new ArrayList<>();
                singleStat.add(stats);
                statsView.updateTable(singleStat);
                statsView.showMessage("Juego encontrado: " + stats.getWinner(), "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statsView.showMessage("No se encontró ningún juego con ID: " + gameId, "Búsqueda", JOptionPane.WARNING_MESSAGE);
                // Recargar todas las stats si no se encuentra el juego específico
                loadAllStats();
            }
        } catch (Exception e) {
            statsView.showMessage("Error en la búsqueda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Agregar nuevas estadísticas de juego (desde el GameController)
    public void addStatsGame(String winner, String winnerColor, String[] namePlayers) {
        int gameId = serviceGameStats.getGameId();
        GameStats gameStats = new GameStats(gameId, winner, winnerColor, namePlayers);
        serviceGameStats.insertStats(gameStats);
    }
}