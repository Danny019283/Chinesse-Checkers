package View;

import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

class BoardPanel extends JPanel {
    private final int HEX_SIZE = 25;
    private final int PANEL_WIDTH = 800;
    private final int PANEL_HEIGHT = 700;

    private CellClickListener cellClickListener;
    private final ArrayList<Pair<Integer, Integer>> boardPositions;
    private Pair<Integer, Integer> selectedCell = null;
    private ArrayList<Pair<Integer, Integer>> validMoves = new ArrayList<>();
    private ArrayList<Pair<Integer, Integer>> piecePositions = new ArrayList<>();
    private HashMap<Pair<Integer, Integer>, String> pieceColors = new HashMap<>();

    private final Color boardBgColor = new Color(0xFFFFFF);
    private final Color hexColor = new Color(0xE0E0E0);
    private final Color hexBorderColor = new Color(0x000000);
    private final Color validMoveColor = new Color(0x92F196);
    private final Color selectedHexColor = new Color(0x6EC3E5);

    public BoardPanel(ArrayList<Pair<Integer, Integer>> boardPositions) {
        this.boardPositions = boardPositions;
        this.setBackground(boardBgColor);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
    }

    public void setHighlights(Pair<Integer, Integer> selectedCell, ArrayList<Pair<Integer, Integer>> validMoves) {
        this.selectedCell = selectedCell;
        this.validMoves = (validMoves != null) ? validMoves : new ArrayList<>();
        repaint();
    }

    public void setCellClickListener(CellClickListener listener) {
        this.cellClickListener = listener;
    }

    private void handleMouseClick(int mouseX, int mouseY) {
        int centerX = PANEL_WIDTH / 2;
        int centerY = PANEL_HEIGHT / 2;

        for (Pair<Integer, Integer> pos : boardPositions) {
            int x = centerX + pos.getValue0();
            int y = centerY + pos.getValue1();

            double distance = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
            if (distance <= HEX_SIZE * 0.9) {
                if (cellClickListener != null) {
                    cellClickListener.onCellClick(pos);
                }
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = PANEL_WIDTH / 2;
        int centerY = PANEL_HEIGHT / 2;

        // Dibujar celdas válidas primero (fondo)
        if (validMoves != null && !validMoves.isEmpty()) {
            for (Pair<Integer, Integer> move : validMoves) {
                int x = centerX + move.getValue0();
                int y = centerY + move.getValue1();
                HexagonDrawer.draw(g2d, x, y, HEX_SIZE, validMoveColor, true);
                HexagonDrawer.draw(g2d, x, y, HEX_SIZE, validMoveColor.darker(), false);
            }
        }

        // Dibujar tablero normal
        for (Pair<Integer, Integer> pixelPos : boardPositions) {
            int x = centerX + pixelPos.getValue0();
            int y = centerY + pixelPos.getValue1();
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, hexColor, true);
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, hexBorderColor, false);
        }

        // Dibujar piezas
        for (Pair<Integer, Integer> piecePos : piecePositions) {
            int x = centerX + piecePos.getValue0();
            int y = centerY + piecePos.getValue1();
            String color = pieceColors.get(piecePos);
            if (color != null) {
                PieceDrawer.draw(g2d, x, y, HEX_SIZE, color);
            }
        }

        // Dibujar celda seleccionada
        if (selectedCell != null) {
            g2d.setStroke(new BasicStroke(3));
            int x = centerX + selectedCell.getValue0();
            int y = centerY + selectedCell.getValue1();
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, selectedHexColor, false);
        }
    }

    // En el método updatePieces de BoardPanel:
    public void updatePieces(ArrayList<Pair<Integer, Integer>> positions, HashMap<Pair<Integer, Integer>, String> colors) {
        this.piecePositions = positions != null ? positions : new ArrayList<>();
        this.pieceColors = colors != null ? colors : new HashMap<>();
        repaint();
    }

    // Agregar MouseListener para detectar clicks
    {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    // 👇 interfaz para notificar al controlador
    public interface CellClickListener {
        void onCellClick(Pair<Integer, Integer> pos);
    }


}
