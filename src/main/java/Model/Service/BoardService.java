package Model.Service;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Entities.Piece;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.sqrt;

public class BoardService {
    private static BoardService instance;
    private final Board board;

    BoardService() {
        this.board = new Board();
    }

    public static BoardService getInstance() {
        if (instance == null) {
            instance = new BoardService();
        }
        return instance;
    }

    public final Map<String, String> oppositeDirections = Map.of(
            "SW", "NE",
            "SE", "NW",
            "E", "W",
            "NE", "SW",
            "NW", "SE",
            "W", "E"
    );

    public Board createBoard() {
        //create first triangle
        String[] directions = {"SW", "E", "NW"};
        createTriangle(directions, 0, 12, 4, -8, null);
        //create inverted triangle
        String[] directionsInverted = {"NW", "E", "SW"};
        createTriangle(directionsInverted, 0, 12, -4, 8, null);
        return board;
    }

    public Board createTriangle(String[] directions, int dirIndex, int stepsMax, int q, int r, Piece piece) {
        if (stepsMax < 0) {
            return board;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 12) {
            Pair<Integer, Integer> newCoord = calculateMove(oppositeDirections.get(currentDir), q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            Pair<Integer, Integer> newCoord = calculateMove(currentDir, q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
            if (!board.contains(new Pair<>(q, r))) {
                HexCell cell = new HexCell(q, r, piece);
                board.putCell(cell);
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        return createTriangle(directions, nextDirIndex, stepsMax - 1, q, r, null);
    }

    public Pair<Integer, Integer> calculateMove(String direction, int q, int r) {
        return switch (direction) {
            case "NW" -> new Pair<>(q, r - 1);
            case "NE" -> new Pair<>(q + 1, r - 1);
            case "W" -> new Pair<>(q - 1, r);
            case "E" -> new Pair<>(q + 1, r);
            case "SW" -> new Pair<>(q - 1, r + 1);
            case "SE" -> new Pair<>(q, r + 1);
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
    }

    public ArrayList<Pair<String, HexCell>> getNeighbors(Pair<Integer, Integer> coord) {
        ArrayList<Pair<String, HexCell>> neighbors = new ArrayList<>();
        String[] directions = {"NW", "NE", "E", "SE", "SW", "W"};
        for (String direction : directions) {
            Pair<Integer, Integer> newCoord = calculateMove(direction, coord.getValue0(), coord.getValue1());
            if (board.contains(newCoord)) {
                neighbors.add(new Pair<>(
                        direction,
                        board.getCell(newCoord.getValue0(), newCoord.getValue1()))
                );
            }
        }
        return neighbors;
    }

    public static Pair<Integer, Integer> pointyHexToPixel(HexCell cell) {
        // hex to cartesian
        double x = sqrt(3) * cell.getQ()  +  sqrt(3)/2 * cell.getR();
        double y = 3.0/2.0 * cell.getR();
        // scale cartesian coordinates
        int size = 25;
        x = x * size;
        y = y * size;
        int roundX = (int) Math.round(x);
        int roundY = (int) Math.round(y);
        return new Pair<>(roundX, roundY);
    }

    public Pair<Integer, Integer> pixelToPointyHex(Pair<Integer, Integer> point) {
        // invert the scaling
        int size = 25;
        double x = (double) point.getValue0() / size;
        double y = (double) point.getValue1() / size;
        // cartesian to hex
        double q = sqrt(3)/3 * x  -  1.0/3 * y;
        double r = 2.0/3 * y;
        return axialRound(q, r);
    }

    private Pair<Integer, Integer> axialRound(double fracQ, double fracR) {
        double fracS = -fracQ - fracR;
        int q = (int) Math.round(fracQ);
        int r = (int) Math.round(fracR);
        int s = (int) Math.round(fracS);

        double qDiff = Math.abs(q - fracQ);
        double rDiff = Math.abs(r - fracR);
        double sDiff = Math.abs(qDiff - rDiff);

        if (qDiff > rDiff && qDiff > sDiff) {
            q = -r-s;
        }
        else {
            r = -q-s;
        }
        return new Pair<>(q, r);
    }

    public void calculateInitialCornerForPiece(String[] directions, int dirIndex, int stepsMax, int q, int r, Piece piece) {
        if (stepsMax < 0) {
            return;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 3) {
            Pair<Integer, Integer> newCoord = calculateMove(oppositeDirections.get(currentDir), q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            Pair<Integer, Integer> newCoord = calculateMove(currentDir, q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
            if (board.contains(new Pair<>(q, r))) {
                board.getCell(q, r).setPiece(piece);
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        calculateInitialCornerForPiece(directions, nextDirIndex, stepsMax - 1, q, r, piece);
    }

    public Pair<Integer, Integer> calculateJump(String direction, Pair<Integer, Integer> position) {
        int q = position.getValue0();
        int r = position.getValue1();
        Pair<Integer, Integer> step = calculateMove(direction, q, r);
        if (step == null) {
            return null;
        }
        HexCell intermediateCell = board.getCell(step.getValue0(), step.getValue1());
        if (intermediateCell.getPiece() == null) {
            return null;
        }
        Pair<Integer, Integer> destPos = calculateMove(direction, step.getValue0(), step.getValue1());
        if (!board.contains(destPos)) {
            return null;
        }
        HexCell destCell = board.getCell(destPos.getValue0(), destPos.getValue1());
        if (destCell.getPiece() != null) {
            return null;
        }
        return new Pair<>(destPos.getValue0(), destPos.getValue1());
    }

    public Pair<Integer, Integer> getOppositeCorner(String color) {
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

    public String[] getDirectionsForCorner(int q, int r) {
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

    public boolean checkCorner(String color, int q, int r, String[] directions, int dirIndex, int stepsMax) {
        if (stepsMax < 0) {
            return true;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 12) {
            Pair<Integer, Integer> newCoord = calculateMove(oppositeDirections.get(currentDir), q, r);
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

    public boolean isJumpMove(Pair<Integer, Integer> prevPosition, Pair<Integer, Integer> newPosition, String direction) {
        if(direction == null) {
            return false;
        }
        String oppositeDirection = oppositeDirections.get(direction);
        Pair<Integer, Integer> jumpBack = calculateJump(oppositeDirection, newPosition);
        return prevPosition.equals(jumpBack);
    }

}
