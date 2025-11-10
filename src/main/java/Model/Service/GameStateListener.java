package Model.Service;
import Model.Entities.GameState;

public interface GameStateListener {
    void onGameStateUpdated(GameState newState);
}
