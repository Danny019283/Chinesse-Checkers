package Controller;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Entities.Player;
import Model.Service.BoardService;
import Model.Service.GameRulesService;
import Network.Client;
import View.GameView;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private final BoardService boardService;
    private GameView gameView; // Can be null
    private Board board;
    private final GameRulesService gameRulesService;

    private Pair<Integer, Integer> currentPiece = null;
    private final HashMap<Pair<Integer, Integer>, String> currentValidMoves = new HashMap<>();
    private final ArrayList<Player> players = new ArrayList<>();
    private int currentTurnIndex = 0;
    private boolean isJumpSequence = false;
    private String lastMoveDirection = null;
    private String winner = null;

    // Constructor for server or single-player mode
    public GameController(boolean isServer) {
        this.boardService = BoardService.getInstance();
        this.board = boardService.createBoard();
        this.gameRulesService = GameRulesService.getInstance(board, boardService);

        if (!isServer) {
            this.gameView = GameView.getInstance(getBoardPositions());
            updateView();
            setupEventListeners();
        }
    }

    // Default constructor for local play
    public GameController() {
        this(false);
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getCurrentPlayer() {
        if (players.isEmpty() || currentTurnIndex >= players.size()) {
            return "No Players";
        }
        return players.get(currentTurnIndex).getColor();
    }

    public void addPlayer(Player player) {
        int maxPlayers = 6;
        int currentPlayers = players.size();
        if (players.contains(player) || currentPlayers >= maxPlayers) {
            return;
        }
        String color = addColor.get(currentPlayers);
        player.setColor(color);
        players.add(player);
    }

    public final Map<Integer, String> addColor = Map.of(
            0, "RED",
            1, "GREEN",
            2, "YELLOW",
            3, "PURPLE",
            4, "ORANGE",
            5, "BLUE"
    );

    void nextTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % players.size();
    }

    public void handleCellClick(Pair<Integer, Integer> pixelPos) {
        Pair<Integer, Integer> selectedCell = boardService.pixelToPointyHex(pixelPos);
        if (!board.contains(selectedCell)) {
            resetSelection();
            return;
        }
        handleTurnClick(selectedCell);
    }

    private void handleTurnClick(Pair<Integer, Integer> selectedCell) {
        if (currentPiece != null) {
            if (!currentValidMoves.containsKey(selectedCell) && !isJumpSequence) {
                resetSelection();
                return;
            }
            setLastMoveDirection(selectedCell);
            if (currentValidMoves.containsKey(selectedCell)) {
                gameRulesService.movePiece(this.currentPiece, selectedCell);
                checkWinner();
                isJumpSequence = setJumpSequence(selectedCell);
                this.currentPiece = selectedCell;
                if (!isJumpSequence) {
                    endTurn(true);
                }
                updateView();
            }
        }
        if (isJumpSequence) {
            return;
        }
        if (boardService.isPlayerPiece(selectedCell, getCurrentPlayer())) {
            selectPiece(selectedCell);
        } else {
            resetSelection();
        }
    }

    public void setLastMoveDirection(Pair<Integer, Integer> selectedCell) {
        this.lastMoveDirection = currentValidMoves.get(selectedCell);
    }

    public boolean setJumpSequence(Pair<Integer, Integer> selectedCell) {
        if (boardService.isJumpMove(this.currentPiece, selectedCell, lastMoveDirection)) {
            Pair<Integer, Integer> nextJump = boardService.calculateJump(lastMoveDirection, selectedCell);
            if (nextJump != null) {
                this.currentValidMoves.clear();
                this.currentValidMoves.put(nextJump, lastMoveDirection);
                return true;
            }
        }
        return false;
    }

    public void selectPiece(Pair<Integer, Integer> selectedCell) {
        this.currentPiece = selectedCell;
        this.currentValidMoves.clear();
        this.currentValidMoves.putAll(gameRulesService.getValidMoves(selectedCell));
        updateView();
    }

    private void checkWinner() {
        if (this.winner == null && gameRulesService.won(getCurrentPlayer())) {
            this.winner = getCurrentPlayer();
            updateView();

            // Save game statistics
            try {
                GameStatsController statsController = new GameStatsController();
                String winnerName = players.get(currentTurnIndex).getName();
                String[] playerNames = players.stream().map(Player::getName).toArray(String[]::new);
                statsController.addStatsGame(winnerName, this.winner, playerNames);
            } catch (Exception e) {
                // Log or handle the exception if stats saving fails
                e.printStackTrace();
                System.err.println("Failed to save game statistics.");
            }
        }
    }

    public void endTurn(boolean canEndTurn) {
        if (canEndTurn) {
            nextTurn();
            resetSelection();
            if (gameView != null) {
                gameView.updateTurnLabel(getCurrentPlayer());
            }
            updateView();
        }
    }

    private void resetSelection() {
        this.currentPiece = null;
        this.currentValidMoves.clear();
        this.isJumpSequence = false;
        updateView();
    }

    private void updateView() {
        if (gameView != null) {
            Client.GameState state = getGameState();
            gameView.updatePieces(state.piecePositions, state.pieceColors);
            gameView.showValidMoves(state.selectedPixel, state.validMovePixels);
            if (state.winner != null) {
                gameView.showWinnerPopup(state.winner);
            }
        }
    }

    public ArrayList<Pair<Integer, Integer>> getBoardPositions() {
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        for (HexCell cell : board.getCells().values()) {
            Pair<Integer, Integer> cartesianPos = BoardService.pointyHexToPixel(cell);
            positions.add(cartesianPos);
        }
        return positions;
    }

    public void startGame() {
        this.board = boardService.createBoard();
        for(Player player : players){
            gameRulesService.setInitialPieces(player.getColor());
        }
        if (gameView != null) {
            gameView.updateTurnLabel(getCurrentPlayer());
        }
        updateView();
    }

    private void setupEventListeners() {
        if (gameView != null) {
            gameView.setCellClickListener(this::handleCellClick);
            gameView.addEndTurnListener(e -> endTurn(!isJumpSequence));
        }
    }

    public Client.GameState getGameState() {
        Client.GameState state = new Client.GameState();

        ArrayList<Pair<Integer, Integer>> piecePositions = new ArrayList<>();
        HashMap<Pair<Integer, Integer>, String> pieceColors = new HashMap<>();
        for (HexCell cell : board.getCells().values()) {
            if (cell.getPiece() != null) {
                Pair<Integer, Integer> pixelPos = BoardService.pointyHexToPixel(cell);
                piecePositions.add(pixelPos);
                pieceColors.put(pixelPos, cell.getPiece().getColor());
            }
        }
        state.piecePositions = piecePositions;
        state.pieceColors = pieceColors;

        Pair<Integer, Integer> selectedPixel = null;
        if (currentPiece != null) {
            HexCell selectedHexCell = board.getCell(currentPiece.getValue0(), currentPiece.getValue1());
            if (selectedHexCell != null) {
                selectedPixel = BoardService.pointyHexToPixel(selectedHexCell);
            }
        }
        state.selectedPixel = selectedPixel;

        ArrayList<Pair<Integer, Integer>> validMovePixels = new ArrayList<>();
        for (Pair<Integer, Integer> move : currentValidMoves.keySet()) {
            HexCell moveCell = board.getCell(move.getValue0(), move.getValue1());
            if (moveCell != null) {
                validMovePixels.add(BoardService.pointyHexToPixel(moveCell));
            }
        }
        state.validMovePixels = validMovePixels;
        state.currentPlayer = getCurrentPlayer();
        state.winner = this.winner;
        state.isJumpSequence = this.isJumpSequence;

        return state;
    }

    public boolean isJumpSequence() {
        return isJumpSequence;
    }
}
