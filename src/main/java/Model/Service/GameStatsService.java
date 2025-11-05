package Model.Service;

import DataAccess.GameStatsDAO;
import Model.Entities.GameStats;

import java.util.ArrayList;

public class GameStatsService {
    private static GameStatsService instance;
    private final GameStatsDAO gameStatsDAO = GameStatsDAO.getInstance();
    public static GameStatsService getInstance() {
        if (instance == null) {
            instance = new GameStatsService();
        }
        return instance;
    }

    public boolean insertStats(GameStats gameStats) {
        if (gameStats == null) {
            return false;
        }
        if (gameStats.getNamePlayers().length == 0) {
            return false;
        }
        gameStatsDAO.insertGameStat(gameStats);
        return true;
    }

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

    public boolean deleteStats(int gameId) {
        gameStatsDAO.deleteGameStat(gameId);
        return true;
    }

    public GameStats findStats(int gameId) {
        return gameStatsDAO.findGameStat(gameId);
    }

    public ArrayList<GameStats> findAllStats() {
        return gameStatsDAO.findAllGameStats();
    }
}
