package View;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Handles drawing the game pieces with a modern look.
 */
public class PieceDrawer {
    public static void draw(Graphics2D g2d, int x, int y, int size, String colorName) {
        int pieceSize = (int) (size * 1.2);
        int ovalX = x - pieceSize / 2;
        int ovalY = y - pieceSize / 2;

        Color baseColor = getColorByName(colorName);
        Color lightColor = baseColor.brighter();
        Color darkColor = baseColor.darker();

        Point2D center = new Point2D.Float(x, y - 2);
        float radius = pieceSize / 2f;
        float[] dist = {0.0f, 0.8f, 1.0f};
        Color[] colors = {lightColor, baseColor, darkColor};
        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(rgp);

        g2d.fillOval(ovalX, ovalY, pieceSize, pieceSize);
    }

    private static Color getColorByName(String colorName) {
        if (colorName == null) return Color.BLACK;
        switch (colorName.toUpperCase()) {
            case "RED": return new Color(0xC0302E);
            case "GREEN": return new Color(0x43A047);
            case "BLUE": return new Color(0x1E88E5);
            case "YELLOW": return new Color(0xFFC107);
            case "PURPLE": return new Color(0x8E24AA);
            case "ORANGE": return new Color(0xFF852A);
            default: return Color.BLACK;
        }
    }
}
