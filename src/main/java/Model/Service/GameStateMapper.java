package Model.Service;

import DTO.GameStateDTO;
import Model.Entities.*;
import View.PixelCell;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class GameStateMapper {

    /**
     * Convierte un objeto `GameState` (el estado interno del juego) en un
     * `GameStateDTO` (un objeto de transferencia de datos para la vista/red).
     *
     * @param gameState El estado del juego del modelo.
     * @return Un DTO que contiene la información necesaria para ser renderizada o enviada por red.
     */
    public static GameStateDTO toDTO(GameState gameState) {
        if (gameState == null) {
            return createEmptyDTO();
        }

        // 1. Transformación principal: Convierte el tablero de coordenadas lógicas (HexCell)
        //    a una lista de coordenadas de píxeles (PixelCell) que la vista puede dibujar directamente.
        ArrayList<PixelCell> pixelBoard = BoardService.getPixelPositions(gameState.getBoard());

        // 2. Convierte las coordenadas de los movimientos válidos a píxeles.
        Set<Coords> validMoves = new HashSet<>();
        for (Coords coords : gameState.getCurrentValidMoves().keySet()) {
            HexCell cell = gameState.getBoard().getCell(coords.getX(), coords.getY());
            validMoves.add(BoardService.pointyHexToPixel(cell));
        }

        // 3. Convierte la coordenada de la pieza seleccionada a píxeles.
        Coords selectedPixel = null;
        Coords selectedPiece = gameState.getSelectedPiece();

        if (selectedPiece != null && gameState.getBoard() != null) {
            HexCell selectedCell = gameState.getBoard().getCell(selectedPiece.getX(), selectedPiece.getY());
            if (selectedCell != null) {
                selectedPixel = BoardService.pointyHexToPixel(selectedCell);
            }
        }

        // 4. Extrae datos simples como nombres de jugadores y ganador.
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

        // 5. Construye y devuelve el DTO con todos los datos transformados.
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

    /**
     * Crea un DTO vacío para situaciones donde el estado del juego aún no está
     * inicializado, asegurando que la vista no reciba un objeto nulo.
     * @return Un GameStateDTO con valores por defecto.
     */
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