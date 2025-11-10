package Model.Service;

import Model.Entities.*;
import View.PixelCell;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.Map;
import static java.lang.Math.sqrt;

public class BoardService {
    private BoardService() {}

    public static final Map<String, String> OPPOSITE_DIRECTIONS = Map.of(
            "SW", "NE",
            "SE", "NW",
            "E", "W",
            "NE", "SW",
            "NW", "SE",
            "W", "E"
    );

    public static void createBoard(Board board) {
        //create first triangle
        String[] directions = {"SW", "E", "NW"};
        createTriangleHex(board, directions, 0, 12, 4, -8);
        //create inverted triangle
        String[] directionsInverted = {"NW", "E", "SW"};
        createTriangleHex(board, directionsInverted, 0, 12, -4, 8);
    }

    public static Board createTriangleHex(Board board, String[] directions, int dirIndex, int stepsMax, int q, int r) {
        if (stepsMax < 0) {
            return board;
        }
        String currentDir = directions[dirIndex];
        if (stepsMax == 12) {
            Coords newCoord = calculateMove(OPPOSITE_DIRECTIONS.get(currentDir), q, r);
            q = newCoord.getX();
            r = newCoord.getY();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            Coords newCoord = calculateMove(currentDir, q, r);
            q = newCoord.getX();
            r = newCoord.getY();
            if (!board.contains(new Coords(q, r))) {
                HexCell cell = new HexCell(q, r, null);
                board.putCell(cell);
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        return createTriangleHex(board, directions, nextDirIndex, stepsMax - 1, q, r);
    }

    public static Coords calculateMove(String direction, int q, int r) {
        return switch (direction) {
            case "NW" -> new Coords(q, r - 1);
            case "NE" -> new Coords(q + 1, r - 1);
            case "W" -> new Coords(q - 1, r);
            case "E" -> new Coords(q + 1, r);
            case "SW" -> new Coords(q - 1, r + 1);
            case "SE" -> new Coords(q, r + 1);
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
    }

    public static ArrayList<Pair<String, HexCell>> getNeighbors(Board board, Coords coords) {
        ArrayList<Pair<String, HexCell>> neighbors = new ArrayList<>();
        String[] directions = {"NW", "NE", "E", "SE", "SW", "W"};
        for (String direction : directions) {
            Coords newCoord = calculateMove(direction, coords.getX(), coords.getY());
            neighbors.add(new Pair<>(
                    direction,
                    board.getCell(newCoord.getX(), newCoord.getY()))
            );
        }
        return neighbors;
    }

    public static Coords pointyHexToPixel(HexCell cell) {
        // hex to cartesian
        double x = sqrt(3) * cell.getQ()  +  sqrt(3)/2 * cell.getR();
        double y = 3.0/2.0 * cell.getR();
        // scale cartesian coordinates
        int size = 25;
        x = x * size;
        y = y * size;
        int roundX = (int) Math.round(x);
        int roundY = (int) Math.round(y);
        return new Coords(roundX, roundY);
    }

    public static Coords adjustPixelOffset(int pixelX, int pixelY) {
        int centerX = 800 / 2; // mismo que BoardPanel
        int centerY = 700 / 2;

        int adjustedX = pixelX - centerX;
        int adjustedY = pixelY - centerY;

       return new Coords(adjustedX, adjustedY);
    }

    public static Coords pixelToPointyHex(int pixelX, int pixelY) {
        Coords point = adjustPixelOffset(pixelX, pixelY);
        // invert the scaling
        int size = 25;
        double x = (double) point.getX() / size;
        double y = (double) point.getY() / size;
        // cartesian to hex
        double q = sqrt(3)/3 * x  -  1.0/3 * y;
        double r = 2.0/3 * y;
        return axialRound(q, r);
    }

    private static Coords axialRound(double fracQ, double fracR) {
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
        return new Coords(q, r);
    }

    public static ArrayList<HexCell> calculateCornerCells(Board board, ArrayList<HexCell> cornerCells, String[] directions,
                                                          int dirIndex, int stepsMax, int q, int r, boolean firstLap) {
        if (stepsMax < 0) {
            return cornerCells;
        }
        String currentDir = directions[dirIndex];
        if (firstLap) {
            Coords newCoord = calculateMove(OPPOSITE_DIRECTIONS.get(currentDir), q, r);
            q = newCoord.getX();
            r = newCoord.getY();
        }
        int stepsCount = 0;
        while (stepsCount <= stepsMax) {
            Coords newCoord = calculateMove(currentDir, q, r);
            q = newCoord.getX();
            r = newCoord.getY();
            if (board.contains(new Coords(q, r))) {
                cornerCells.add(board.getCell(q, r));
            }
            stepsCount++;
        }
        int nextDirIndex = (dirIndex + 1) % directions.length;
        return calculateCornerCells(board, cornerCells, directions, nextDirIndex, stepsMax - 1, q, r, false);
    }

    public static Coords calculateJump(Board board, String direction, Coords position) {
        int q = position.getX();
        int r = position.getY();
        Coords step = calculateMove(direction, q, r);
        if (step == null) {
            return null;
        }
        HexCell intermediateCell = board.getCell(step.getX(), step.getY());
        if (intermediateCell == null) {
            return null;
        }
        if (intermediateCell.getPiece() == null) {
            return null;
        }
        Coords destPos = calculateMove(direction, step.getX(), step.getY());
        if (!board.contains(destPos)) {
            return null;
        }
        HexCell destCell = board.getCell(destPos.getX(), destPos.getY());
        if (destCell.getPiece() != null) {
            return null;
        }
        return new Coords(destPos.getX(), destPos.getY());
    }

    public static Coords getOppositeCorner(String color) {
        Map <String, Coords> cornerPositions = Map.of(
                "GREEN", new Coords(-4, 8),
                "BLUE", new Coords(4, 4),
                "PURPLE", new Coords(8, -4),
                "RED", new Coords(4, -8),
                "ORANGE", new Coords(-4, -4),
                "YELLOW", new Coords(-8, 4)
        );
        return cornerPositions.get(color);
    }

    public static String[] getDirectionsForCorner(int q, int r) {
        Coords corner = new Coords(q, r);
        Map <Coords, String[]> cornerPositions = Map.of(
                new Coords(4, -8), new String[]{"SW", "E", "NW"},
                new Coords(-4, 8), new String[]{"NW", "E", "SW"},
                new Coords(4, 4), new String[]{"W", "NE", "SE"},
                new Coords(-4, -4), new String[]{"E", "SW", "NW"},
                new Coords(8, -4), new String[]{"W", "SE", "NE"},
                new Coords(-8, 4), new String[]{"E", "NW", "SW"}
        );
        return cornerPositions.get(corner);
    }

    public static boolean checkOppositeCorner(Board board, String color, String[] directions, int q , int r) {
        ArrayList<HexCell> initialCells = calculateCornerCells(board,
                new ArrayList<>(), directions, 0, 3, q, r, true);
        for (HexCell cell : initialCells) {
            Piece piece = cell.getPiece();
            if (piece == null) {
                return false;
            }
            if(!piece.getColor().equals(color)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPlayerPiece(Board board, Coords hexCoord, String currentPieceColor) {
        if (!board.contains(hexCoord)) {
            return false;
        }
        if (board.getCell(hexCoord.getX(), hexCoord.getY()).getPiece() == null) {
            return false;
        }
        Piece piece = board.getCell(hexCoord.getX(), hexCoord.getY()).getPiece();
        return piece.getColor().equals(currentPieceColor);
    }

    public static boolean isJumpMove(Board board, Coords prevPosition, Coords newPosition, String direction) {
        if(direction == null) {
            return false;
        }
        String oppositeDirection = OPPOSITE_DIRECTIONS.get(direction);
        Coords jumpBack = calculateJump(board, oppositeDirection, newPosition);
        return prevPosition.equals(jumpBack);
    }

    public static void setupPiecesForOnePlayer(Board board, String color) {
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
            case "RED": {
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
        ArrayList<HexCell> initialCells = calculateCornerCells(board,
                new ArrayList<>(), directions, 0, 3, q, r, true);
        for (HexCell cell : initialCells) {
            cell.setPiece(new Piece(color));
        }
    }

    public static void setupPieces(Board board, ArrayList<Player> players) {
        for (Player player : players) {
            setupPiecesForOnePlayer(board, player.getColor());
        }
    }

    public static ArrayList<PixelCell> getPixelPositions(Board board) {
        ArrayList<PixelCell> pixelBoard = new ArrayList<>();
        for (HexCell cell : board.getCells().values()) {
            Coords pixelCoords = pointyHexToPixel(cell);
            PixelCell pixelCell = new PixelCell(pixelCoords, cell.getPiece());
            pixelBoard.add(pixelCell);
        }
        return pixelBoard;
    }
}