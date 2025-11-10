package Model.Service;

import DTO.GameStateDTO;
import Model.Entities.*;
import View.PixelCell;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class GameStateMapper {
    public static GameStateDTO toDTO(GameState gameState) {
        if (gameState == null) {
            return createEmptyDTO();
        }

        ArrayList<PixelCell> pixelBoard = BoardService.getPixelPositions(gameState.getBoard());

        // Verificar que currentValidMoves no sea null
        Set<Coords> validMoves = new HashSet<>();
        for (Coords coords : gameState.getCurrentValidMoves().keySet()) {
            HexCell cell = gameState.getBoard().getCell(coords.getX(), coords.getY());
            validMoves.add(BoardService.pointyHexToPixel(cell));
        }

        // Verificar cuidadosamente selectedPiece
        Coords selectedPixel = null;
        Coords selectedPiece = gameState.getSelectedPiece();

        if (selectedPiece != null && gameState.getBoard() != null) {
            HexCell selectedCell = gameState.getBoard().getCell(selectedPiece.getX(), selectedPiece.getY());
            if (selectedCell != null) {
                selectedPixel = BoardService.pointyHexToPixel(selectedCell);
            }
        }

        String currentPlayerName = (gameState.getCurrentPlayer() != null)
                ? gameState.getCurrentPlayer().getName()
                : "Waiting...";

        String currentPlayerColor = (gameState.getCurrentPlayer() != null)
                ? gameState.getCurrentPlayer().getColor()
                : "Waiting...";

        String winnerName = (gameState.getWinner() != null)
                ? gameState.getWinner().getName()
                : null;
        ArrayList<Player> players = gameState.getPlayers();

        return new GameStateDTO(
                pixelBoard,
                selectedPixel,
                validMoves,
                currentPlayerName,
                winnerName,
                gameState.isJumpSequence(),
                currentPlayerColor,
                players
        );
    }

    private static GameStateDTO createEmptyDTO() {
        return new GameStateDTO(
                new ArrayList<>(),
                null,
                new HashSet<>(),
                "Waiting for players...",
                null,
                false,
                "Waiting for players...",
                new ArrayList<>()
        );
    }
}