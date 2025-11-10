package Controller;

import Model.Entities.GameState;
import Model.Entities.GameStats;
import Model.Entities.Player;
import Model.Entities.Coords;
import Model.Service.GameStateListener;
import Model.Service.GameStateMapper;
import Model.Service.BoardService;
import Model.Service.GameService;
import DTO.GameStateDTO;

import java.util.ArrayList;

public class GameController implements GameStateListener {

    private final GameService gameService;
    private GameStateUpdateCallback updateCallback;

    public GameController() {
        this.gameService = GameService.getInstance();
        this.gameService.setGameStateListener(this);
    }

    // Método para que el Server se registre y reciba notificaciones
    public void setUpdateCallback(GameStateUpdateCallback callback) {
        this.updateCallback = callback;
    }

    public void addPlayer(Player player) {
        gameService.addPlayer(player);
    }

    public void startGame() {
        if (updateCallback != null) {
            updateCallback.onStateUpdated();
        }
    }

    public void createNewGame(ArrayList<Player> players) {
        gameService.createNewGame(players);
    }

    public void handleCellClick(int pixelX, int pixelY) {
        Coords hexCoords = BoardService.pixelToPointyHex(pixelX, pixelY);
        gameService.turn(hexCoords);
    }

    public void endTurn() {
        gameService.endTurn(true);
    }

    public GameStateDTO getGameViewDTO() {
        return GameStateMapper.toDTO(gameService.getGameState());
    }

    public Player getCurrentPlayer() {
        return gameService.getGameState().getCurrentPlayer();
    }

    public ArrayList<Player> getPlayers() {
        return gameService.getGameState().getPlayers();
    }

    public boolean isGameReady() {
        GameState state = gameService.getGameState();
        return state != null && state.getPlayers().size() >= 2;
    }

    @Override
    public void onGameStateUpdated(GameState newState) {
        if (updateCallback != null) {
            updateCallback.onStateUpdated();
        }
    }

    public interface GameStateUpdateCallback {
        void onStateUpdated();
    }
}