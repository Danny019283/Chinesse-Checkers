package View;
import Model.Entities.Coords;
import Model.Entities.Piece;

public class PixelCell {
    private Coords coords;
    private Piece piece;
    public PixelCell(Coords coords, Piece piece) {
        this.coords = coords;
        this.piece = piece;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
