package Model.Service;

import Model.Entities.Board;
import Model.Entities.Coords;
import Model.Entities.HexCell;
import Model.Entities.Piece;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.HashMap;

public class GameRulesService {
    private GameRulesService() {}

    /**
     * Calcula todos los movimientos válidos para una pieza desde una posición dada.
     * Un movimiento válido puede ser un paso a una celda adyacente vacía o un
     * salto sobre una pieza adyacente a una celda vacía.
     * @param board El tablero de juego actual.
     * @param currentPos Las coordenadas de la pieza a mover.
     * @return Un HashMap donde las claves son las coordenadas de destino válidas
     * y los valores son la dirección del movimiento.
     */
    public static HashMap<Coords, String> getValidMoves(Board board, Coords currentPos) {
        int q = currentPos.getX();
        int r = currentPos.getY();
        ArrayList<Pair<String, HexCell>> neighbors = BoardService.getNeighbors(board, new Coords(q, r));
        HashMap<Coords, String> validMoves = new HashMap<>();
        for (Pair<String, HexCell> neighbor : neighbors) {
            if(neighbor == null) continue;
            if(neighbor.getValue1() == null) continue;
            String direction = neighbor.getValue0();
            // Movimiento simple a una celda adyacente vacía
            if (neighbor.getValue1().getPiece() == null) {
                HexCell cell = neighbor.getValue1();
                Coords coords = new Coords(cell.getQ(), cell.getR());
                validMoves.put(coords, direction);
            }
            // Salto sobre una pieza
            Coords jump = BoardService.calculateJump(board, direction, currentPos);
            if (jump != null) {
                validMoves.put(jump, direction);
            }
        }
        return validMoves;
    }

    /**
     * Verifica si un jugador ha ganado la partida. La condición de victoria se cumple
     * cuando todas las piezas de un jugador ocupan las celdas de la esquina
     * opuesta a su esquina de inicio.
     * @param board El tablero de juego actual.
     * @param playerColor El color del jugador a verificar.
     * @return true si el jugador ha ganado, false en caso contrario.
     */
    public static boolean hasWon(Board board, String playerColor) {
        Coords oppositeCorner = BoardService.getOppositeCorner(playerColor);
        int q = oppositeCorner.getX();
        int r = oppositeCorner.getY();
        Piece pieceInOppositeCorner = board.getCell(q, r).getPiece();
        if (pieceInOppositeCorner == null) {
            return false;
        }
        if (!pieceInOppositeCorner.getColor().equals(playerColor)) {
            return false;
        }
        String[] directionsForCorner = BoardService.getDirectionsForCorner(q, r);
        return BoardService.checkOppositeCorner(board, playerColor, directionsForCorner, q, r);
    }
}