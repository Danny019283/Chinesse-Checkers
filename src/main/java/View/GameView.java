package View;

import DTO.GameStateDTO;
import Model.Entities.Coords;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameView extends JFrame {
    private BoardPanel pnlBoard;
    private final JButton btnEndTurn = new JButton("Finalizar Turno");
    private final JLabel lblTurn = new JLabel("Turno de: JUGADOR");

    public GameView() {
        setTitle("Chinese Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        buildUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        pnlBoard = new BoardPanel();
        add(pnlBoard, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlPanel.setBackground(new Color(0xF5F5F5));

        lblTurn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEndTurn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnEndTurn.setBackground(new Color(0x6200EE));
        btnEndTurn.setForeground(Color.WHITE);

        controlPanel.add(lblTurn);
        controlPanel.add(btnEndTurn);
        add(controlPanel, BorderLayout.SOUTH);
    }

    // Método principal para actualizar la vista con datos del DTO
    public void updateView(GameStateDTO state) {
        if (state == null) return;
        updateUI(state);
        updateBoard(state);
        checkGameEnd(state);
    }

    private void updateUI(GameStateDTO state) {
        lblTurn.setText("Turno de: " + state.getCurrentPlayerName());
        btnEndTurn.setEnabled(state.isJumpSequence());
    }

    private void updateBoard(GameStateDTO state) {
        // La lista completa de celdas que viene en el DTO
        ArrayList<PixelCell> allCells = state.getPiecePositions() != null ?
                state.getPiecePositions() : new ArrayList<>();
        // Creamos una nueva lista que contendrá SOLO las celdas con piezas
        ArrayList<PixelCell> pieceOnlyCells = new ArrayList<>();
        for (PixelCell cell : allCells) {
            if (cell.getPiece() != null) {
                pieceOnlyCells.add(cell);
            }
        }
        Coords selectedPixel = state.getSelectedPiecePixel();
        Set<Coords> validMovePixels = new HashSet<>();
        if (state.getValidMovePixels() != null) {
            for (Coords hexCoord : state.getValidMovePixels()) {
                for (PixelCell cell : allCells) {
                    if (cell.getCoords().equals(hexCoord)) {
                        validMovePixels.add(cell.getCoords()); // Usar la coordenada en píxeles
                        break;
                    }
                }
            }
        }
        pnlBoard.updateBoard(allCells);
        pnlBoard.updatePieces(pieceOnlyCells);
        pnlBoard.setHighlights(selectedPixel, validMovePixels);
        pnlBoard.repaint();
    }

    private void checkGameEnd(GameStateDTO state) {
        if (state.getWinnerName() != null && !state.getWinnerName().isEmpty()) {
            showWinnerPopup(state.getWinnerName());
        }
    }

    public void showWinnerPopup(String winnerName) {
        JOptionPane.showMessageDialog(this, "Ganó " + winnerName, "Juego Terminado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void addEndTurnListener(ActionListener listener) {
        btnEndTurn.addActionListener(listener);
    }

    public void setCellClickListener(BoardPanel.CellClickListener listener) {
        pnlBoard.setCellClickListener(listener);
    }

}