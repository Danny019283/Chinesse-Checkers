package Controller;

import Model.Entities.GameState;
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

    /**
     * Registra un callback para notificar a un observador (como el servidor)
     * cuando el estado del juego ha sido actualizado.
     * @param callback La implementación del callback a ejecutar.
     */
    public void setUpdateCallback(GameStateUpdateCallback callback) {
        this.updateCallback = callback;
    }

    /**
     * Añade un nuevo jugador al estado del juego a través del GameService.
     * @param player El jugador a añadir.
     */
    public void addPlayer(Player player) {
        gameService.addPlayer(player);
    }

    /**
     * Solicita al GameService la creación de una nueva partida,
     * configurando el tablero y los jugadores.
     * @param players La lista de jugadores que participarán.
     */
    public void createNewGame(ArrayList<Player> players) {
        gameService.createNewGame(players);
    }

    /**
     * Gestiona un clic del usuario en el tablero. Convierte las coordenadas
     * de píxeles a coordenadas hexagonales y las pasa al GameService para
     * procesar el turno del jugador.
     * @param pixelX Coordenada X del clic.
     * @param pixelY Coordenada Y del clic.
     */
    public void handleCellClick(int pixelX, int pixelY) {
        Coords hexCoords = BoardService.pixelToPointyHex(pixelX, pixelY);
        gameService.turn(hexCoords);
    }

    /**
     * Finaliza el turno del jugador actual y notifica al GameService
     * para que el siguiente jugador pueda tomar el control.
     */
    public void endTurn() {
        gameService.endTurn(true);
    }

    /**
     * Obtiene el estado actual del juego y lo convierte en un objeto DTO
     * (Data Transfer Object) para que la vista pueda renderizarlo sin
     * acoplarse directamente al modelo.
     * @return El DTO con la información necesaria para la vista.
     */
    public GameStateDTO getGameViewDTO() {
        return GameStateMapper.toDTO(gameService.getGameState());
    }

    public Player getCurrentPlayer() {
        return gameService.getGameState().getCurrentPlayer();
    }

    public ArrayList<Player> getPlayers() {
        return gameService.getGameState().getPlayers();
    }

    /**
     * Verifica si el juego está listo para comenzar, usualmente comprobando
     * si se ha alcanzado el número mínimo de jugadores.
     * @return true si el juego está listo, false en caso contrario.
     */
    public boolean isGameReady() {
        GameState state = gameService.getGameState();
        return state != null && state.getPlayers().size() >= 2;
    }

    /**
     * Método del callback GameStateListener. Se invoca automáticamente
     * cuando el GameService notifica un cambio en el estado del juego.
     * A su vez, notifica a otros observadores (como el servidor).
     * @param newState El nuevo estado del juego.
     */
    @Override
    public void onGameStateUpdated(GameState newState) {
        if (updateCallback != null) {
            updateCallback.onStateUpdated();
        }
    }

    /**
     * Interfaz funcional para implementar un callback que se activa
     * cuando el estado del juego cambia.
     */
    public interface GameStateUpdateCallback {
        void onStateUpdated();
    }
}