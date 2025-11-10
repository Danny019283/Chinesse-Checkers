package Model.Service;
import Model.Entities.GameState;

/**
 * Define una interfaz para actuar como un mecanismo de callback (retrollamada).
 * Cualquier clase que implemente esta interfaz puede registrarse en el GameService
 * para ser notificada automáticamente cada vez que el estado del juego (GameState)
 * sea modificado. Esto permite desacoplar la lógica del juego de los componentes
 * que necesitan reaccionar a sus cambios, como la interfaz de usuario o el servidor.
 */
public interface GameStateListener {
    /**
     * Método que será invocado por el GameService cuando el estado del juego cambie.
     * @param newState El nuevo estado del juego con las actualizaciones.
     */
    void onGameStateUpdated(GameState newState);
}
