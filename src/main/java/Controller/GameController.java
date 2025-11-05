package Controller;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Service.BoardService;
import Model.Service.GameRulesService;
import View.GameView;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.HashMap;

public class GameController {
    private final BoardService boardService;
    private final GameView gameView;
    private final Board board;
    private GameRulesService gameRulesService;
    private Pair<Integer, Integer> currentPiece = null;
    private ArrayList<Pair<Integer, Integer>> currentValidMoves = new ArrayList<>();
    private String currentPlayer = "RED";

    public GameController() {
        this.boardService = BoardService.getInstance();
        this.board = boardService.createBoard();
        this.gameRulesService = GameRulesService.getInstance(board);
        this.gameView = GameView.getInstance(getBoardPositions());

        setInitializeGame();
        setupEventListeners();
    }

    private void setInitializeGame() {
        gameRulesService.setInitialPieces("RED");
        gameRulesService.setInitialPieces("GREEN");
        gameRulesService.setInitialPieces("BLUE");
        gameRulesService.setInitialPieces("YELLOW");
        gameRulesService.setInitialPieces("PURPLE");
        gameRulesService.setInitialPieces("ORANGE");
        updateView();
    }

    private void setupEventListeners() {
        gameView.setCellClickListener(pos -> {
            handleCellClick(pos);
        });
        gameView.addEndTurnListener(e -> endTurn());
    }

    private void handleCellClick(Pair<Integer, Integer> pixelPos) {
        Pair<Integer, Integer> selectedCell = boardService.pixelToPointyHex(pixelPos);
        //if selecte cell is outside the board, deselect current piece
        if (!board.contains(selectedCell)) {
            this.currentPiece = null;
            currentValidMoves.clear();
            updateView();
            return;
        }
        // Si ya hay una celda seleccionada, verificar si el click es un movimiento válido
        if (this.currentPiece != null) {
            System.out.println("📌 Celda seleccionada previamente: " + this.currentPiece);
            System.out.println("📋 Movimientos válidos disponibles: " + currentValidMoves.size());
            if (currentValidMoves.contains(selectedCell)) {
                System.out.println("🎯 Movimiento válido detectado! Moviendo pieza...");
                movePiece(this.currentPiece, selectedCell);
                this.currentPiece = null;
                currentValidMoves.clear();
                updateView();
                return;
            } else {
                System.out.println("❌ No es un movimiento válido");
            }
        }

        // Si no hay celda seleccionada o el click no fue válido, verificar si es una pieza del jugador
        if (gameRulesService.isPlayerPiece(selectedCell, currentPlayer)) {
            this.currentPiece = selectedCell;
            currentValidMoves = gameRulesService.getValidMoves(selectedCell);
            System.out.println("📍 Movimientos válidos encontrados: " + currentValidMoves.size());
            for (Pair<Integer, Integer> move : currentValidMoves) {
                System.out.println("   - (" + move.getValue0() + "," + move.getValue1() + ")");
            }
            updateView();
        } else {
            System.out.println("💡 Click en celda vacía o pieza de otro jugador");
            this.currentPiece = null;
            currentValidMoves.clear();
            updateView();
        }
    }

    public void playTurn(){

    }

    private boolean isValidMove(Pair<Integer, Integer> hexCoord) {
        for (Pair<Integer, Integer> move : currentValidMoves) {
            if (move.getValue0().equals(hexCoord.getValue0()) &&
                    move.getValue1().equals(hexCoord.getValue1())) {
                return true;
            }
        }
        return false;
    }

    public void movePiece(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        HexCell fromCell = board.getCell(from.getValue0(), from.getValue1());
        HexCell toCell = board.getCell(to.getValue0(), to.getValue1());

        toCell.setPiece(fromCell.getPiece());
        fromCell.setPiece(null);

        System.out.println("✅ Movimiento completado");
        System.out.println("   Celda origen después: " + (fromCell.getPiece() != null ? fromCell.getPiece().getColor() : "vacía"));
        System.out.println("   Celda destino después: " + (toCell.getPiece() != null ? toCell.getPiece().getColor() : "vacía"));

        checkWinner();
    }

    private void checkWinner() {
        String winner = gameRulesService.won(currentPlayer);
        if (winner != null) {
            gameView.showWinnerPopup(winner);
        }
    }

    private void endTurn() {
        switch(currentPlayer) {
            case "RED": currentPlayer = "GREEN"; break;
            case "GREEN": currentPlayer = "BLUE"; break;
            case "BLUE": currentPlayer = "RED"; break;
        }

        currentPiece = null;
        currentValidMoves.clear();
        gameView.updateTurnLabel(currentPlayer);
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
        for (Pair<Integer, Integer> move : currentValidMoves) {
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
        gameView.updateTurnLabel(currentPlayer);
        updateView();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.startGame();
        });
    }
}