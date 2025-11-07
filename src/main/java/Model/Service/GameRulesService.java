package Model.Service;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Entities.Piece;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.HashMap;

public class GameRulesService {
    private static GameRulesService instance;
    private final BoardService boardService;
    private final Board board;

    public GameRulesService(Board board, BoardService boardService) {
        this.board = board;
        this.boardService = boardService;
    }

    public static GameRulesService getInstance(Board board, BoardService boardService) {
        if (instance == null) {
            instance = new GameRulesService(board,boardService);
        }
        return instance;
    }

    public void setInitialPieces(String color) {
        int q, r;
        String[] directions;
        switch(color) {
            case "GREEN": {
                q = 4; r = -8;
                directions = new String[]{"SW", "E", "NW"};
                break;
            }
            case "BLUE": {
                q = -4; r = -4;
                directions = new String[]{"E", "SW", "NW"};
                break;
            }
            case "PURPLE": {
                q = -8; r = 4;
                directions = new String[]{"E", "NW", "SW"};
                break;
            }
            case "RED" : {
                q = -4; r = 8;
                directions = new String[]{"NW", "E", "SW"};
                break;
            }
            case "ORANGE": {
                q = 4; r = 4;
                directions = new String[]{"W", "NE", "SE"};
                break;
            }
            case "YELLOW": {
                q = 8; r = -4;
                directions = new String[]{"W", "SE", "NE"};
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid color: " + color);
            }
        }
        Piece piece = new Piece(color);
        boardService.calculateInitialCornerForPiece(directions, 0, 3, q, r, piece);
    }

    public boolean validateNumOfPlayers(int maxPlayers, int connectedPlayers) {
        return connectedPlayers == maxPlayers;
    }


    public HashMap<Pair<Integer, Integer>, String> getValidMoves(Pair<Integer, Integer> currentPos) {
        int q = currentPos.getValue0();
        int r = currentPos.getValue1();
        ArrayList<Pair<String, HexCell>> neighbors = boardService.getNeighbors(new Pair<>(q, r));
        HashMap<Pair<Integer, Integer>, String> validMoves = new HashMap<>();
        for (Pair<String, HexCell> neighbor : neighbors) {
            int neighborQ = neighbor.getValue1().getQ();
            int neighborR = neighbor.getValue1().getR();
            String direction = neighbor.getValue0();
            if (neighbor.getValue1().getPiece() == null) {
                validMoves.put(new Pair<>(neighborQ, neighborR), direction);
                continue;
            }
            Pair<Integer, Integer> jump = boardService.calculateJump(direction, currentPos);
            if (jump != null) {
                validMoves.put(jump, direction);
            }
        }
        return validMoves;
    }

    public String won(String color) {
        Pair<Integer, Integer> oppositeCorner = boardService.getOppositeCorner(color);
        int q = oppositeCorner.getValue0();
        int r = oppositeCorner.getValue1();
        if (board.getCell(q, r).getPiece() == null) {
            return null;
        }
        if (!board.getCell(q, r).getPiece().getColor().equals(color)) {
            return null;
        }
        String[] directionesForCorner = boardService.getDirectionsForCorner(q, r);
        if (!boardService.checkCorner(color, q, r, directionesForCorner, 0, 3)) {
            return null;
        }
        return color;
    }

    public void movePiece(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        HexCell fromCell = board.getCell(from.getValue0(), from.getValue1());
        HexCell toCell = board.getCell(to.getValue0(), to.getValue1());
        toCell.setPiece(fromCell.getPiece());
        fromCell.setPiece(null);
    }
}
