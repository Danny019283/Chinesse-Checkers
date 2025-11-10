package Model.Service;

import Model.Entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * GameService es el núcleo de la lógica del juego. Implementado como un Singleton,
 * centraliza la gestión del estado del juego (GameState) y orquesta las acciones
 * principales como la creación de partidas, la gestión de turnos y la validación
 * de movimientos.
 */
public class GameService {
    private static GameService instance;
    private GameState gameState;
    private GameStateListener listener;

    /**
     * El constructor es privado para garantizar que solo exista una instancia (Singleton).
     * Inicializa un GameState vacío pero funcional.
     */
    private GameService() {
        // Inicializar gameState con todos los campos necesarios en el ORDEN CORRECTO
        this.gameState = new GameState(
                new Board(),                    // board
                null,                           // currentPlayer
                new ArrayList<>(),              // players
                0,                              // curentTurnIndex
                new HashMap<>(),                // currentValidMoves
                null,                           // winner
                null,                           // selectedPiece
                false,                          // isJumpSequence
                null                           // lastMoveDirection
        );
    }

    /**
     * Proporciona acceso a la única instancia de GameService.
     * @return La instancia Singleton de GameService.
     */
    public static synchronized GameService getInstance() {
        if (instance == null) {
            instance = new GameService();
        }
        return instance;
    }

    /**
     * Registra un listener (normalmente el GameController o el Servidor) que será
     * notificado cada vez que el estado del juego cambie.
     * @param listener El listener a notificar.
     */
    public void setGameStateListener(GameStateListener listener) {
        this.listener = listener;
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * Configura una nueva partida. Crea un nuevo tablero, coloca las piezas
     * para cada jugador y establece el primer turno.
     * @param players La lista de jugadores que participarán.
     */
    public void createNewGame(ArrayList<Player> players) {
        if (players == null || players.isEmpty()) {
            System.err.println("Attempted to create a game with no players.");
            return;
        }
        Board board = new Board();
        BoardService.createBoard(board);
        BoardService.setupPieces(board, players);


        this.gameState = new GameState(
                board,
                players.getFirst(),
                players,
                0,
                new HashMap<>(),
                null,
                null,
                false,
                null
        );

        //Notifica que el juego ha sido creado.
        if (listener != null) {
            listener.onGameStateUpdated(gameState);
        }
    }

    /**
     * Añade un jugador a la partida actual, asignándole un color disponible.
     * Si el tablero ya está creado, coloca sus piezas.
     * @param player El jugador a añadir.
     */
    public void addPlayer(Player player) {
        int maxPlayers = 6;
        int currentPlayers = gameState.getPlayers().size();
        if (gameState.getPlayers().contains(player) || currentPlayers >= maxPlayers) {
            return;
        }
        String color = addColor.get(currentPlayers);
        player.setColor(color);
        gameState.getPlayers().add(player);

        //Añade las piezas del jugador si el tablero ya existe.
        if (!gameState.getBoard().getCells().isEmpty()) {
            BoardService.setupPiecesForOnePlayer(gameState.getBoard(), color);
            if (listener != null) {
                listener.onGameStateUpdated(gameState);
            }
        }
    }

    public final Map<Integer, String> addColor = Map.of(
            0, "RED",
            1, "GREEN",
            2, "YELLOW",
            3, "PURPLE",
            4, "ORANGE",
            5, "BLUE"
    );

    /**
     * Ejecuta el movimiento de una pieza de una celda a otra en el tablero.
     */
    private void movePiece(Coords from, Coords to) {
        HexCell fromCell = gameState.getBoard().getCell(from.getX(), from.getY());
        HexCell toCell = gameState.getBoard().getCell(to.getX(), to.getY());
        if (fromCell != null && toCell != null) {
            toCell.setPiece(fromCell.getPiece());
            fromCell.setPiece(null);
        }
    }

    /**
     * Avanza al siguiente jugador en la lista de turnos.
     */
    private void nextTurn() {
        gameState.setCurentTurnIndex((gameState.getCurentTurnIndex() + 1) % gameState.getPlayers().size());
        gameState.setCurrentPlayer(gameState.getPlayers().get(gameState.getCurentTurnIndex()));
    }

    /**
     * Procesa la acción principal del jugador en su turno, basada en la celda seleccionada.
     * Puede ser seleccionar una pieza, mover una pieza seleccionada o continuar una
     * secuencia de saltos.
     * @param selectedCell Las coordenadas de la celda donde el usuario hizo clic.
     */
    public void turn(Coords selectedCell) {
        // Si ya hay una pieza seleccionada, intenta moverla.
        if (gameState.getSelectedPiece() != null) {
            // Si el movimiento no es válido, y no estamos en medio de saltos, deselecciona.
            if (!gameState.getCurrentValidMoves().containsKey(selectedCell)) {
                if (!gameState.isJumpSequence()) {
                    resetSelection();
                }
                return;
            }
            // Realiza el movimiento.
            gameState.setLastMoveDirection(gameState.getCurrentValidMoves().get(selectedCell));
            movePiece(gameState.getSelectedPiece(), selectedCell);

            // Comprueba si el movimiento resulta en una victoria.
            boolean hasWon = GameRulesService.hasWon(gameState.getBoard(), gameState.getCurrentPlayer().getColor());
            if (hasWon) {
                gameState.setWinner(gameState.getCurrentPlayer());
            }
            // Verifica si se puede continuar con otro salto.
            boolean canContinueJumping = setJumpSequence(selectedCell);
            gameState.setJumpSequence(canContinueJumping);
            gameState.setSelectedPiece(selectedCell);

            // Si no se puede seguir saltando, termina el turno.
            if (!gameState.isJumpSequence()) {
                endTurn(true);
            }

            if (listener != null) {
                listener.onGameStateUpdated(gameState);
            }
        }
        // Si estamos en una secuencia de saltos, no permite seleccionar otra pieza.
        if (gameState.isJumpSequence()) {
            return;
        }
        // Si no hay pieza seleccionada, intenta seleccionar una.
        if (BoardService.isPlayerPiece(gameState.getBoard(), selectedCell, gameState.getCurrentPlayer().getColor())) {
            selectPiece(selectedCell);
        } else {
            resetSelection();
        }
    }

    /**
     * Determina si el jugador puede y debe continuar una secuencia de saltos.
     * Si el último movimiento fue un salto, calcula si es posible otro salto
     * en la misma dirección y actualiza los movimientos válidos.
     * @param selectedCell La celda a la que se acaba de mover.
     * @return true si es posible continuar saltando, false en caso contrario.
     */
    private boolean setJumpSequence(Coords selectedCell) {
        String lastDirection = gameState.getLastMoveDirection();
        if (BoardService.isJumpMove(gameState.getBoard(), gameState.getSelectedPiece(), selectedCell, lastDirection)) {
            Coords nextJump = BoardService.calculateJump(gameState.getBoard(), lastDirection, selectedCell);
            if (nextJump != null) {
                gameState.getCurrentValidMoves().clear();
                gameState.getCurrentValidMoves().put(nextJump, lastDirection);
                return true;
            }
        }
        return false;
    }

    /**
     * Selecciona una pieza y calcula todos sus movimientos válidos.
     * @param selectedCell Las coordenadas de la pieza a seleccionar.
     */
    private void selectPiece(Coords selectedCell) {
        gameState.setSelectedPiece(selectedCell);
        gameState.getCurrentValidMoves().clear();
        gameState.setCurrentValidMoves(GameRulesService.getValidMoves(gameState.getBoard(), gameState.getSelectedPiece()));
        if (listener != null) {
            listener.onGameStateUpdated(gameState);
        }
    }

    /**
     * Limpia la selección actual de pieza, los movimientos válidos y el estado de salto.
     */
    private void resetSelection() {
        gameState.setSelectedPiece(null);
        gameState.getCurrentValidMoves().clear();
        gameState.setJumpSequence(false);
        if (listener != null) {
            listener.onGameStateUpdated(gameState);
        }
    }

    /**
     * Finaliza el turno del jugador actual, pasa al siguiente y resetea la selección.
     * @param canEndTurn Flag para controlar si el turno realmente puede terminar.
     */
    public void endTurn(boolean canEndTurn) {
        if (canEndTurn) {
            nextTurn();
            resetSelection();
            if (listener != null) {
                listener.onGameStateUpdated(gameState);
            }
        }
    }
}