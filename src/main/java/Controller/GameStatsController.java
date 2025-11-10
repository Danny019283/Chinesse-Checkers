package Controller;

import DataAccess.Exceptions.NoDataException;
import Model.Entities.GameStats;
import Model.Service.GameStatsService;
import View.GameStatsView;

import javax.swing.*;
import java.util.ArrayList;

public class GameStatsController {
    final GameStatsService serviceGameStats;
    private GameStatsView statsView;

    public GameStatsController() {
        this.serviceGameStats = GameStatsService.getInstance();
    }

    /**
     * Inicializa y muestra la vista de estadísticas. Si la vista no ha sido creada,
     * la instancia, configura los listeners para los eventos de la UI y carga
     * todas las estadísticas disponibles.
     */
    public void initializeView() {
        if (statsView == null) {
            statsView = GameStatsView.getInstance();
            setupListeners();
            loadAllStats(); // Cargar todas las estadísticas al iniciar
        }
        statsView.setVisible(true);
    }

    /**
     * Configura los manejadores de eventos para los componentes de la vista,
     * como el botón de búsqueda y la selección de filas en la tabla.
     */
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

    /**
     * Procesa el evento de búsqueda. Obtiene el ID del campo de texto,
     * lo valida y, si es un número válido, busca las estadísticas de ese juego.
     * Si el campo está vacío, recarga todas las estadísticas.
     */
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

    /**
     * Maneja la selección de una fila en la tabla de la vista,
     * solicitando a la vista que muestre los detalles del juego seleccionado.
     */
    private void handleTableSelection() {
        statsView.showSelectedGameDetails();
    }

    /**
     * Solicita al servicio todas las estadísticas de los juegos almacenados
     * y actualiza la vista para que las muestre en la tabla. Maneja el caso
     * en que no se encuentren datos.
     */
    public void loadAllStats() {
        try {
            ArrayList<GameStats> allStats = serviceGameStats.findAllStats();
            statsView.updateTable(allStats);
        } catch (NoDataException e) {
            statsView.updateTable(new ArrayList<>()); // Clear the table
            statsView.showMessage("No hay estadísticas de juegos disponibles.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            statsView.showMessage("Error al cargar las estadísticas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca las estadísticas de un juego específico por su ID a través del servicio
     * y actualiza la tabla en la vista para mostrar solo ese resultado.
     * @param gameId El ID del juego a buscar.
     */
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

    /**
     * Crea un nuevo registro de estadísticas de juego y solicita al servicio
     * que lo inserte en la base de datos.
     * @param winner El nombre del ganador.
     * @param winnerColor El color del ganador.
     * @param namePlayers Los nombres de todos los jugadores.
     */
    public void addStatsGame(String winner, String winnerColor, String[] namePlayers) {
        GameStats gameStats = new GameStats(0, winner, winnerColor, namePlayers);
        serviceGameStats.insertStats(gameStats);
    }
}