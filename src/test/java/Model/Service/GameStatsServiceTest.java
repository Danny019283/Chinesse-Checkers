package Model.Service;

import DataAccess.GameStatsDAO;
import Model.Entities.GameStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameStatsServiceTest {

    @Mock
    private GameStatsDAO gameStatsDAO;

    @InjectMocks
    private GameStatsService gameStatsService;

    @Test
    void insertStats() throws Exception {
        // Given
        GameStats gameStats = new GameStats();
        gameStats.setNamePlayers(new String[]{"player1", "player2"});

        // When
        boolean result = gameStatsService.insertStats(gameStats);

        // Then
        assertTrue(result);
        verify(gameStatsDAO, times(1)).insertGameStat(gameStats);
    }

    @Test
    void updateStats() throws Exception {
        // Given
        GameStats gameStats = new GameStats();
        gameStats.setNamePlayers(new String[]{"player1", "player2"});

        // When
        boolean result = gameStatsService.updateStats(gameStats);

        // Then
        assertTrue(result);
        verify(gameStatsDAO, times(1)).updateGameStat(gameStats);
    }

    @Test
    void deleteStats() throws Exception {
        // Given
        int gameId = 1;

        // When
        boolean result = gameStatsService.deleteStats(gameId);

        // Then
        assertTrue(result);
        verify(gameStatsDAO, times(1)).deleteGameStat(gameId);
    }

    @Test
    void findStats() throws Exception {
        // Given
        int gameId = 1;
        GameStats gameStats = new GameStats();
        when(gameStatsDAO.findGameStat(gameId)).thenReturn(gameStats);

        // When
        GameStats result = gameStatsService.findStats(gameId);

        // Then
        assertEquals(gameStats, result);
    }

    @Test
    void findAllStats() throws Exception {
        // Given
        ArrayList<GameStats> gameStatsList = new ArrayList<>();
        when(gameStatsDAO.findAllGameStats()).thenReturn(gameStatsList);

        // When
        ArrayList<GameStats> result = gameStatsService.findAllStats();

        // Then
        assertEquals(gameStatsList, result);
    }
}
