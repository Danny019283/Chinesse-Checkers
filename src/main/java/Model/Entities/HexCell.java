package Model.Entities;

public class HexCell {
    private int q;
    private int r;
    private Piece piece;

    public HexCell(int q, int r, Piece piece) {
        this.q = q;
        this.r = r;
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getQ() {
        return q;
    }

    public void setQR(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getR() {
        return r;
    }
}
