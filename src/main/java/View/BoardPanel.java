package View;

import java.util.ArrayList;
import Model.Entities.Coords;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

class BoardPanel extends JPanel {
    private final int HEX_SIZE = 25;
    private final int PANEL_WIDTH = 800;
    private final int PANEL_HEIGHT = 700;

    private CellClickListener cellClickListener;

    // Data for drawing, provided by GameView
    private ArrayList<PixelCell> boardCells = new ArrayList<>();
    private ArrayList<PixelCell> piecePositions = new ArrayList<>();
    private Coords selectedPixel = null;
    private Set<Coords> validMovePixels = Set.of();

    private final Color boardBgColor = new Color(0xFFFFFF);
    private final Color hexColor = new Color(0xE0E0E0);
    private final Color hexBorderColor = new Color(0x000000);
    private final Color validMoveColor = new Color(0xB0E5B4);
    private final Color selectedHexColor = new Color(0x6EC3E5);

    public BoardPanel() {
        this.setBackground(boardBgColor);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cellClickListener != null) {
                    cellClickListener.onCellClick(new Pair<>(e.getX(), e.getY()));
                }
            }
        });
    }

    public void updatePieces(ArrayList<PixelCell> positions) {
        this.piecePositions = positions != null ? positions : new ArrayList<>();
    }

    public void updateBoard(ArrayList<PixelCell> cells) {
        this.boardCells = cells != null ? cells : new ArrayList<>();
    }

    public void setHighlights(Coords selected, Set<Coords> validMoves) {
        this.selectedPixel = selected;
        this.validMovePixels = validMoves != null ? validMoves : Set.of();
    }

    public void setCellClickListener(CellClickListener listener) {
        this.cellClickListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = PANEL_WIDTH / 2;
        int centerY = PANEL_HEIGHT / 2;

        for (PixelCell cell : boardCells) {
            int x = centerX + cell.getCoords().getX();
            int y = centerY + cell.getCoords().getY();
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, hexColor, true);
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, hexBorderColor, false);
        }

        for (Coords move : validMovePixels) {
            int x = centerX + move.getX();
            int y = centerY + move.getY();
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, validMoveColor, true);
        }

        if (selectedPixel != null) {
            g2d.setStroke(new BasicStroke(3));
            int x = centerX + selectedPixel.getX();
            int y = centerY + selectedPixel.getY();
            HexagonDrawer.draw(g2d, x, y, HEX_SIZE, selectedHexColor, false);
        }

        for (PixelCell pixelCell : piecePositions) {
            if (pixelCell.getPiece() != null) {
                int x = centerX + pixelCell.getCoords().getX();
                int y = centerY + pixelCell.getCoords().getY();
                String color = pixelCell.getPiece().getColor();
                PieceDrawer.draw(g2d, x, y, HEX_SIZE, color);
            }
        }
    }

    public interface CellClickListener {
        void onCellClick(Pair<Integer, Integer> pixelPos);
    }
}