package Model.Service;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Entities.Piece;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Map;

public class GameRulesService {
    private static GameRulesService instance;
    private final BoardService boardService = BoardService.getInstance();
    private final Board board;

    public GameRulesService(Board board) {
        this.board = board;
    }

    public static GameRulesService getInstance(Board board) {
        if (instance == null) {
            instance = new GameRulesService(board);
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
        placeTrianglePieces(board, directions, 0, 3, q, r, piece);
    }

    private void placeTrianglePieces(Board board, String[] directions, int dirIndex, int stepsMax, int q, int r, Piece piece) {
        if (stepsMax < 0) {
            return;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 3) {
            Pair<Integer, Integer> newCoord = boardService.move(boardService.oppositeDirections.get(currentDir), q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            Pair<Integer, Integer> newCoord = boardService.move(currentDir, q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
            if (board.contains(new Pair<>(q, r))) {
                board.getCell(q, r).setPiece(piece);
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        placeTrianglePieces(board, directions, nextDirIndex, stepsMax - 1, q, r, piece);
    }

    public boolean validateNumOfPlayers(int maxPlayers, int connectedPlayers) {
        return connectedPlayers == maxPlayers;
    }

    public Pair<Integer, Integer> validateJump(String direction, Pair<Integer, Integer> position) {
        int q = position.getValue0();
        int r = position.getValue1();
       Pair<Integer, Integer> step = boardService.move(direction, q, r);
       if (board.getCell(step.getValue0(), step.getValue1()).getPiece() == null) {
           return null;
       }
       Pair<Integer, Integer> jump = boardService.move(direction, step.getValue0(), step.getValue1());
       if (!board.contains(jump)) {
           return null;
       }
       Piece pieceAtJump = board.getCell(jump.getValue0(), jump.getValue1()).getPiece();
       if (pieceAtJump != null) {
           return null;
       }
        return new Pair<>(jump.getValue0(), jump.getValue1());
    }

    public ArrayList<Pair<Integer, Integer>> getValidMoves(Pair<Integer, Integer> currentPos) {
        int q = currentPos.getValue0();
        int r = currentPos.getValue1();
        ArrayList<Pair<String, HexCell>> neighbors = boardService.getNeighbors(board, new Pair<>(q, r));
        ArrayList<Pair<Integer, Integer>> validMoves = new ArrayList<>();
        for (Pair<String, HexCell> neighbor : neighbors) {
            int neighborQ = neighbor.getValue1().getQ();
            int neighborR = neighbor.getValue1().getR();
            if (neighbor.getValue1().getPiece() == null) {
                validMoves.add(new Pair<>(neighborQ, neighborR));
                continue;
            }
            String direction = neighbor.getValue0();
            Pair<Integer, Integer> jump = validateJump(direction, currentPos);
            if (jump != null) {
                validMoves.add(new Pair<>(jump.getValue0(), jump.getValue1()));
            }
        }
        return validMoves;
    }

    private Pair<Integer, Integer> getOpositeCorner(String color) {
        Map <String, Pair<Integer, Integer>> cornerPositions = Map.of(
                "GREEN", new Pair<>(-4, 8),
                "BLUE", new Pair<>(4, 4),
                "PURPLE", new Pair<>(8, -4),
                "RED", new Pair<>(4, -8),
                "ORANGE", new Pair<>(-4, -4),
                "YELLOW", new Pair<>(-8, 4)
        );
        return cornerPositions.get(color);
    }

    public String won(String color) {
        Pair<Integer, Integer> targetCorner = getOpositeCorner(color);
        int q = targetCorner.getValue0();
        int r = targetCorner.getValue1();
        if (board.getCell(q, r).getPiece() == null) {
            return null;
        }
        if (!board.getCell(q, r).getPiece().getColor().equals(color)) {
            return null;
        }
        String[] directions = getDirectionsForCorner(q, r);
        if (!checkCorner(color, q, r, directions, 0, 3)) {
            return null;
        }
        return color;
    }

    private String[] getDirectionsForCorner(int q, int r) {
        Pair<Integer, Integer> corner = new Pair<>(q, r);
        Map <Pair<Integer, Integer>, String[]> cornerPositions = Map.of(
                new Pair<>(4, -8), new String[]{"SW", "E", "NW"},
                new Pair<>(-4, 8), new String[]{"NW", "E", "SW"},
                new Pair<>(4, 4), new String[]{"W", "NE", "SE"},
                new Pair<>(-4, -4), new String[]{"E", "SW", "NW"},
                new Pair<>(8, -4), new String[]{"W", "SE", "NE"},
                new Pair<>(-8, 4), new String[]{"E", "NW", "SW"}
        );
        return cornerPositions.get(corner);
    }

    private boolean checkCorner(String color, int q, int r, String[] directions, int dirIndex, int stepsMax) {
        if (stepsMax < 0) {
            return true;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 12) {
            Pair<Integer, Integer> newCoord = boardService.move(boardService.oppositeDirections.get(currentDir), q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            if(!board.getCell(q, r).getPiece().getColor().equals(color)) {
                return false;
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        return checkCorner(color, q, r, directions, nextDirIndex, stepsMax);
    }

    public boolean isPlayerPiece(Pair<Integer, Integer> hexCoord, String currenPlayer) {
        if (!board.contains(hexCoord)) {
            return false;
        }
        if (board.getCell(hexCoord.getValue0(), hexCoord.getValue1()).getPiece() == null) {
            return false;
        }
        Piece piece = board.getCell(hexCoord.getValue0(), hexCoord.getValue1()).getPiece();
        return piece.getColor().equals(currenPlayer);
    }
}
