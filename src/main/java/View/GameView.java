package View;

import org.javatuples.Pair;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class GameView extends JFrame {
    private BoardPanel pnlBoard;
    private final JButton btnEndTurn = new JButton("Finalizar Turno");
    private final JLabel lblTurn = new JLabel("Turno de: JUGADOR");
    private static GameView instance;

    public static GameView getInstance(ArrayList<Pair<Integer, Integer>> positions) {
        if (instance == null) {
            instance = new GameView(positions);
        }
        return instance;
    }

    public GameView(ArrayList<Pair<Integer, Integer>> positions) {
        setTitle("Chinese Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        buildUI(positions);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void buildUI(ArrayList<Pair<Integer, Integer>> positions) {
        pnlBoard = new BoardPanel(positions);
        add(pnlBoard, BorderLayout.CENTER);

        // Panel de control
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

    // Popup de ganador
    public void showWinnerPopup(String winnerColor) {
        JOptionPane.showMessageDialog(this, "Ganó " + winnerColor, "Juego Terminado", JOptionPane.INFORMATION_MESSAGE);
    }

    // Actualiza el turno
    public void updateTurnLabel(String playerName) {
        this.lblTurn.setText("Turno de: " + playerName);
    }
    public void updatePieces(ArrayList<Pair<Integer, Integer>> positions, HashMap<Pair<Integer, Integer>, String> colors) {
        pnlBoard.updatePieces(positions, colors);
    }

    // Actualiza celdas seleccionadas y movimientos válidos (solo recibe datos)
    public void showValidMoves(Pair<Integer, Integer> selectedPos, ArrayList<Pair<Integer, Integer>> validMoves) {
        pnlBoard.setHighlights(selectedPos, validMoves);
    }

    // Listener del botón
    public void addEndTurnListener(ActionListener accion) {
        btnEndTurn.addActionListener(accion);
    }
    public void setEndTurnButtonEnabled(boolean enabled) {
        btnEndTurn.setEnabled(enabled);
    }

    public void setCellClickListener(BoardPanel.CellClickListener listener) {
        pnlBoard.setCellClickListener(listener);
    }

    public void setHexagonPositions(ArrayList<Pair<Integer, Integer>> positions) {
        pnlBoard.setHexagonPositions(positions);
    }
}
