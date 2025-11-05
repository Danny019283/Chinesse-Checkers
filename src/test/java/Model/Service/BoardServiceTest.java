package Model.Service;

import Model.Entities.Board;
import Model.Entities.HexCell;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = BoardService.getInstance();
    }

    @Test
    void createBoard() {
        // When
        Board board = boardService.createBoard();

        // Then
        assertNotNull(board);
        assertFalse(board.getCells().isEmpty());
    }

    @Test
    void createTriangle() {
        // Given
        Board board = new Board();
        String[] directions = {"SW", "E", "NW"};

        // When
        boardService.createTriangle(board, directions, 0, 3, 4, -8, null);

        // Then
        assertFalse(board.getCells().isEmpty());
    }

    @Test
    void move() {
        // When
        Pair<Integer, Integer> result = boardService.move("NE", 0, 0);

        // Then
        assertEquals(new Pair<>(1, -1), result);
    }

    @Test
    void getNeighbors() {
        // Given
        Board board = boardService.createBoard();
        Pair<Integer, Integer> coord = new Pair<>(0, 0);

        // When
        ArrayList<Pair<String, HexCell>> neighbors = boardService.getNeighbors(board, coord);

        // Then
        assertFalse(neighbors.isEmpty());
    }

    @Test
    void pointyHexToPixel() {
        // Given
        HexCell cell = new HexCell(0, 0, null);

        // When
        Pair<Integer, Integer> result = BoardService.pointyHexToPixel(cell);

        // Then
        assertEquals(new Pair<>(0.0, 0.0), result);
    }

    @Test
    void pixelToPointyHex() {
        // Given
        Pair<Integer, Integer> point = new Pair<>(0, 0);

        // When
        Pair<Integer, Integer> result = boardService.pixelToPointyHex(point);

        // Then
        assertEquals(new Pair<>(0, 0), result);
    }
}
