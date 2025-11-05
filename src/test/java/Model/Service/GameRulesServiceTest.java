package Model.Service;

import Model.Entities.Board;
import Model.Entities.HexCell;
import Model.Entities.Piece;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameRulesServiceTest {

    @Mock
    private BoardService boardService;

    @Mock
    private Board board;

    private GameRulesService gameRulesService;

    @BeforeEach
    void setUp() {
        gameRulesService = GameRulesService.getInstance(board);
    }

    @Test
    void setInitialPieces() {
        // Given
        String color = "GREEN";

        // When
        gameRulesService.setInitialPieces(color);

        // Then
        verify(boardService, times(1)).createTriangle(any(Board.class), any(String[].class), anyInt(), anyInt(), anyInt(), anyInt(), any(Piece.class));
    }

    @Test
    void validateNumOfPlayers() {
        // Given
        int maxPlayers = 2;
        int connectedPlayers = 2;

        // When
        boolean result = gameRulesService.validateNumOfPlayers(maxPlayers, connectedPlayers);

        // Then
        assertTrue(result);
    }

    @Test
    void validateJump() {
        // Given
        String direction = "NE";
        Pair<Integer, Integer> position = new Pair<>(0, 0);
        Pair<Integer, Integer> step = new Pair<>(1, -1);
        Pair<Integer, Integer> jump = new Pair<>(2, -2);
        HexCell jumpCell = new HexCell(jump.getValue0(), jump.getValue1(), null);
        jumpCell.setPiece(new Piece("GREEN"));

        when(boardService.move(direction, position.getValue0(), position.getValue1())).thenReturn(step);
        when(boardService.move(direction, step.getValue0(), step.getValue1())).thenReturn(jump);
        when(board.contains(jump)).thenReturn(true);
        when(board.getCell(jump.getValue0(), jump.getValue1())).thenReturn(jumpCell);

        // When
        Pair<Integer, Integer> result = gameRulesService.validateJump(direction, position);

        // Then
        assertEquals(jump, result);
    }

    @Test
    void getValidMoves() {
        // Given
        Pair<Integer, Integer> position = new Pair<>(0, 0);
        ArrayList<Pair<String, HexCell>> neighbors = new ArrayList<>();
        HexCell neighborCell = new HexCell(1, -1, null);
        neighbors.add(new Pair<>("NE", neighborCell));

        when(boardService.getNeighbors(board, position)).thenReturn(neighbors);

        // When
        ArrayList<Pair<Integer, Integer>> validMoves = gameRulesService.getValidMoves(position);

        // Then
        assertFalse(validMoves.isEmpty());
        assertEquals(new Pair<>(1, -1), validMoves.get(0));
    }

    @Test
    void won() {
        // Given
        String color = "GREEN";
        Pair<Integer, Integer> targetCorner = new Pair<>(-4, 8);
        HexCell targetCell = new HexCell(targetCorner.getValue0(), targetCorner.getValue1(), null);
        targetCell.setPiece(new Piece(color));

        when(board.getCell(targetCorner.getValue0(), targetCorner.getValue1())).thenReturn(targetCell);

        // When
        String result = gameRulesService.won(color);

        // Then
        // This test is incomplete as the won method has complex logic that is hard to test without more information.
        // A more complete test would require mocking the private methods or refactoring the code to make it more testable.
        assertNull(result);
    }
}
