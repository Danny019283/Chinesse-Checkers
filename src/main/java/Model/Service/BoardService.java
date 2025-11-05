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
        Board board = new Board();
        String[] directions = {"SW", "E", "NW"};
        createTriangle(board, directions, 0, 12, 4, -8, null);
        //create inverted triangle
        String[] directionsInverted = {"NW", "E", "SW"};
        createTriangle(board, directionsInverted, 0, 12, -4, 8, null);
        return board;
    }

    public Board createTriangle(Board board, String[] directions, int dirIndex, int stepsMax, int q, int r, Piece piece) {
        if (stepsMax < 0) {
            return board;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 12) {
            Pair<Integer, Integer> newCoord = move(oppositeDirections.get(currentDir), q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            Pair<Integer, Integer> newCoord = move(currentDir, q, r);
            q = newCoord.getValue0();
            r = newCoord.getValue1();
            if (!board.contains(new Pair<>(q, r))) {
                HexCell cell = new HexCell(q, r, piece);
                board.putCell(cell);
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        return createTriangle(board, directions, nextDirIndex, stepsMax - 1, q, r, null);
    }

    public Pair<Integer, Integer> move(String direction, int q, int r) {
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

    public ArrayList<Pair<String, HexCell>> getNeighbors(Board board, Pair<Integer, Integer> coord) {
        ArrayList<Pair<String, HexCell>> neighbors = new ArrayList<>();
        String[] directions = {"NW", "NE", "E", "SE", "SW", "W"};
        for (String direction : directions) {
            Pair<Integer, Integer> newCoord = move(direction, coord.getValue0(), coord.getValue1());
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
}
