package Controller;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Entities.Player;
import Model.Service.BoardService;
import Model.Service.GameRulesService;
import View.GameView;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private final BoardService boardService;
    private final GameView gameView;
    private Board board;
    private GameRulesService gameRulesService;

    private Pair<Integer, Integer> currentPiece = null;
    private HashMap<Pair<Integer, Integer>, String> currentValidMoves = new HashMap<>();
    private final ArrayList<Player> players = new ArrayList<>();
    private int currentTurnIndex = 0;
    private boolean isJumpSequence = false;
    private String lastMoveDirection = null;

    public GameController() {
        this.boardService = BoardService.getInstance();
        this.board = boardService.createBoard();
        this.gameRulesService = GameRulesService.getInstance(board, boardService);
        this.gameView = GameView.getInstance(getBoardPositions());

        setInitializeGame();
        setupEventListeners();
    }

    public String getCurrentPlayer() {
        return players.get(currentTurnIndex).getColor();
    }

    public void addPlayer(Player player) {
        int maxPlayers = 6;
        int currentPlayers = players.size();
        if (players.contains(player) && maxPlayers > currentPlayers) {
            return;
        }
        player.setColor(addColor.get(currentPlayers));
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

    private void setInitializeGame() {
        gameRulesService.setInitialPieces("RED");
        gameRulesService.setInitialPieces("GREEN");
        /*gameRulesService.setInitialPieces("BLUE");
        gameRulesService.setInitialPieces("YELLOW");
        gameRulesService.setInitialPieces("PURPLE");
        gameRulesService.setInitialPieces("ORANGE");*/
        updateView();
    }

    private void handleCellClick(Pair<Integer, Integer> pixelPos) {
        Pair<Integer, Integer> selectedCell = boardService.pixelToPointyHex(pixelPos);
        if (!board.contains(selectedCell)) {
            resetSelection();
            return;
        }
        handleTurnClick( selectedCell);
    }

    //controller
    private void handleTurnClick(Pair<Integer, Integer> selectedCell) {
        if (currentPiece != null) {
            if (!currentValidMoves.containsKey(selectedCell) && !isJumpSequence) {
                resetSelection();
                return;
            }
            setLastMoveDirection(selectedCell);
            if (currentValidMoves.containsKey(selectedCell)) {
                gameRulesService.movePiece(this.currentPiece, selectedCell);

                isJumpSequence = setJumpSequence(selectedCell);
                this.currentPiece = selectedCell;
                if (!isJumpSequence) {
                    endTurn();
                }
                checkWinner();
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

    public void setLastMoveDirection(Pair<Integer, Integer> selectedCell){
        this.lastMoveDirection = currentValidMoves.get(selectedCell);
    }

    public boolean setJumpSequence(Pair<Integer, Integer> selectedCell) {
        if (boardService.isJumpMove(this.currentPiece, selectedCell, lastMoveDirection)){
            Pair<Integer, Integer> nextJump = boardService.calculateJump(lastMoveDirection, selectedCell);
            if (nextJump != null) {
                this.currentValidMoves.clear();
                this.currentValidMoves.put(nextJump, lastMoveDirection);
                return true;
            }
            return false;
        }
        return false;
    }

    public void selectPiece(Pair<Integer, Integer> selectedCell) {
        this.currentPiece = selectedCell;
        this.currentValidMoves = gameRulesService.getValidMoves(selectedCell);
        updateView();
    }

    private void setupEventListeners() {
        gameView.setCellClickListener(pos -> {
            handleCellClick(pos);
        });
        gameView.addEndTurnListener(e -> endTurn());
    }

    //GameRulesService
    private void checkWinner() {
        String winner = gameRulesService.won(getCurrentPlayer());
        if (winner != null) {
            gameView.showWinnerPopup(winner);
        }
    }

    //GameRulesService
    private void endTurn() {
        // Rotar jugadores
        nextTurn();
        resetSelection();
        gameView.updateTurnLabel(getCurrentPlayer());
        updateView();
    }

    //GameRulesService
    private void resetSelection() {
        this.currentPiece = null;
        this.currentValidMoves.clear();
        this.isJumpSequence = false;
        updateView();
    }



    private void updateView() {
        // Obtener todas las piezas del tablero
        ArrayList<Pair<Integer, Integer>> piecePositions = new ArrayList<>();
        HashMap<Pair<Integer, Integer>, String> pieceColors = new HashMap<>();

        for (HexCell cell : board.getCells().values()) {
            if (cell.getPiece() != null) {
                Pair<Integer, Integer> pixelPos = BoardService.pointyHexToPixel(cell);
                piecePositions.add(pixelPos);
                pieceColors.put(pixelPos, cell.getPiece().getColor());
            }
        }
        gameView.updatePieces(piecePositions, pieceColors);

        // Actualizar movimientos válidos y selección
        Pair<Integer, Integer> selectedPixel = null;
        if (currentPiece != null) {
            HexCell selectedHexCell = board.getCell(currentPiece.getValue0(), currentPiece.getValue1());
            if (selectedHexCell != null) {
                selectedPixel = BoardService.pointyHexToPixel(selectedHexCell);
            }
        }

        ArrayList<Pair<Integer, Integer>> validMovePixels = new ArrayList<>();
        for (Pair<Integer, Integer> move : currentValidMoves.keySet()) {
            HexCell moveCell = board.getCell(move.getValue0(), move.getValue1());
            if (moveCell != null) {
                validMovePixels.add(BoardService.pointyHexToPixel(moveCell));
            }
        }

        gameView.showValidMoves(selectedPixel, validMovePixels);
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
        Player player1 = new Player("pepe", null);
        Player player2 = new Player("pepito", null);
        addPlayer(player1);
        addPlayer(player2);
        gameView.updateTurnLabel(getCurrentPlayer());
        updateView();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.startGame();
        });
    }
}