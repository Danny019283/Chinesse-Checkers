package Model.Service;

import DataAccess.GameStatsDAO;
import Model.Entities.GameStats;

import java.util.ArrayList;

/**
 * Servicio Singleton que actúa como intermediario entre los controladores y la capa
 * de acceso a datos (DAO) para las estadísticas del juego. Su función es encapsular
 * la lógica de negocio relacionada con las estadísticas antes de persistirlas o
 * recuperarlas de la base de datos.
 */
public class GameStatsService {
    private static GameStatsService instance;
    private final GameStatsDAO gameStatsDAO = GameStatsDAO.getInstance();

    /**
     * Proporciona acceso a la única instancia de GameStatsService.
     * @return La instancia Singleton de GameStatsService.
     */
    public static GameStatsService getInstance() {
        if (instance == null) {
            instance = new GameStatsService();
        }
        return instance;
    }

    /**
     * Procesa y prepara las estadísticas de un juego para su inserción.
     * Asigna un nuevo ID al registro antes de pasarlo al DAO para que lo guarde.
     * @param gameStats El objeto con las estadísticas del juego a insertar.
     */
    public void insertStats(GameStats gameStats) {
        if (gameStats == null) {
            return;
        }
        if (gameStats.getNamePlayers().length == 0) {
            return;
        }
        int gameId = gameStatsDAO.countGameStats() + 1;
        gameStats.setGameId(gameId);
        gameStatsDAO.insertGameStat(gameStats);
    }

    /**
     * Delega la actualización de un registro de estadísticas al DAO.
     * @param gameStats El objeto con los datos a actualizar.
     * @return true si la operación fue exitosa (en este caso, siempre delega).
     */
    public boolean updateStats(GameStats gameStats) {
        if (gameStats == null) {
            return false;
        }
        if (gameStats.getNamePlayers().length == 0) {
            return false;
        }
        gameStatsDAO.updateGameStat(gameStats);
        return true;
    }

    /**
     * Delega la eliminación de un registro de estadísticas al DAO.
     * @param gameId El ID del juego cuyas estadísticas se eliminarán.
     * @return true si la operación fue exitosa (en este caso, siempre delega).
     */
    public boolean deleteStats(int gameId) {
        gameStatsDAO.deleteGameStat(gameId);
        return true;
    }

    /**
     * Delega la búsqueda de un registro de estadísticas por ID al DAO.
     * @param gameId El ID del juego a buscar.
     * @return El objeto GameStats si se encuentra, o null.
     */
    public GameStats findStats(int gameId) {
        return gameStatsDAO.findGameStat(gameId);
    }

    /**
     * Delega la obtención de todos los registros de estadísticas al DAO.
     * @return Una lista con todas las estadísticas de juegos almacenadas.
     */
    public ArrayList<GameStats> findAllStats() {
        return gameStatsDAO.findAllGameStats();
    }

}
