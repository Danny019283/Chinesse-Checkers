package View;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * Handles drawing the hexagonal cells.
 */
public class
HexagonDrawer {
    public static void draw(Graphics2D g2d, int x, int y, int size, Color color, boolean fill) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i + 30);
            hexagon.addPoint((int) (x + size * Math.cos(angle)), (int) (y + size * Math.sin(angle)));
        }
        g2d.setColor(color);
        if (fill) {
            g2d.fillPolygon(hexagon);
        } else {
            g2d.drawPolygon(hexagon);
        }
    }
}
