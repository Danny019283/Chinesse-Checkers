package Model.Service;

import Model.Entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameService {
    private static GameService instance;
    private GameState gameState;
    private GameStateListener listener;

    private GameService() {
        // Inicializar gameState con todos los campos necesarios en el ORDEN CORRECTO
        this.gameState = new GameState(
                new Board(),                    // board
                null,                           // currentPlayer
                new ArrayList<>(),              // players
                0,                              // curentTurnIndex
                new HashMap<>(),                // currentValidMoves - IMPORTANTE!
                null,                           // winner
                null,                           // selectedPiece
                false,                          // isJumpSequence
                null                           // lastMoveDirection
        );
    }

    public static synchronized GameService getInstance() {
        if (instance == null) {
            instance = new GameService();
        }
        return instance;
    }

    public void setGameStateListener(GameStateListener listener) {
        this.listener = listener;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void createNewGame(ArrayList<Player> players) {
        if (players == null || players.isEmpty()) {
            System.err.println("Attempted to create a game with no players.");
            return;
        }
        Board board = new Board();
        BoardService.createBoard(board);
        BoardService.setupPieces(board, players);


        this.gameState = new GameState(
                board,                          // board
                players.getFirst(),             // currentPlayer
                players,                        // players
                0,                              // curentTurnIndex
                new HashMap<>(),                // currentValidMoves - IMPORTANTE!
                null,                           // winner
                null,                           // selectedPiece
                false,                          // isJumpSequence
                null                           // lastMoveDirection
        );

        // Notificar que el juego se creó
        if (listener != null) {
            listener.onGameStateUpdated(gameState);
        }
    }

    public void addPlayer(Player player) {
        int maxPlayers = 6;
        int currentPlayers = gameState.getPlayers().size();
        if (gameState.getPlayers().contains(player) || currentPlayers >= maxPlayers) {
            return;
        }
        String color = addColor.get(currentPlayers);
        player.setColor(color);
        gameState.getPlayers().add(player);
        System.out.println("Player added: " + player.getName() + " as " + color);
        if (gameState.getPlayers().size() >= 2 && gameState.getBoard().getCells().isEmpty()) {
            createNewGame(new ArrayList<>(gameState.getPlayers()));
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

    private void movePiece(Coords from, Coords to) {
        HexCell fromCell = gameState.getBoard().getCell(from.getX(), from.getY());
        HexCell toCell = gameState.getBoard().getCell(to.getX(), to.getY());
        if (fromCell != null && toCell != null) {
            toCell.setPiece(fromCell.getPiece());
            fromCell.setPiece(null);
        }
    }

    private void nextTurn() {
        gameState.setCurentTurnIndex((gameState.getCurentTurnIndex() + 1) % gameState.getPlayers().size());
        // Actualizar el jugador actual
        gameState.setCurrentPlayer(gameState.getPlayers().get(gameState.getCurentTurnIndex()));
    }

    public void turn(Coords selectedCell) {
        if (gameState.getSelectedPiece() != null) {
            if (!gameState.getCurrentValidMoves().containsKey(selectedCell)) {
                if (!gameState.isJumpSequence()) {
                    resetSelection();
                }
                return;
            }
            gameState.setLastMoveDirection(gameState.getCurrentValidMoves().get(selectedCell));
            movePiece(gameState.getSelectedPiece(), selectedCell);

            boolean hasWon = GameRulesService.hasWon(gameState.getBoard(), gameState.getCurrentPlayer().getColor());
            if (hasWon) {
                gameState.setWinner(gameState.getCurrentPlayer());
            }
            boolean canContinueJumping = setJumpSequence(selectedCell);
            gameState.setJumpSequence(canContinueJumping);
            gameState.setSelectedPiece(selectedCell);

            if (!gameState.isJumpSequence()) {
                endTurn(true);
            }

            if (listener != null) {
                listener.onGameStateUpdated(gameState);
            }
        }
        if (gameState.isJumpSequence()) {
            return;
        }
        if (BoardService.isPlayerPiece(gameState.getBoard(), selectedCell, gameState.getCurrentPlayer().getColor())) {
            System.out.println("se encontro una pieza");
            selectPiece(selectedCell);
        } else {
            System.out.println("no se encontro una pieza");
            resetSelection();
        }
    }

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

    private void selectPiece(Coords selectedCell) {
        gameState.setSelectedPiece(selectedCell);
        gameState.getCurrentValidMoves().clear();
        gameState.setCurrentValidMoves(GameRulesService.getValidMoves(gameState.getBoard(), gameState.getSelectedPiece()));
        if (listener != null) {
            listener.onGameStateUpdated(gameState);
        }
    }

    private void resetSelection() {
        gameState.setSelectedPiece(null);
        gameState.getCurrentValidMoves().clear();
        gameState.setJumpSequence(false);
        if (listener != null) {
            listener.onGameStateUpdated(gameState);
        }
    }

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