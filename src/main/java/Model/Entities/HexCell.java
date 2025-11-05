package Model.Entities;

public class HexCell {
    private int q;
    private int r;
    private Piece piece;

    public HexCell(int q, int r, Piece piece) {
        this.setQ(q);
        this.setR(r);
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

    public void setQ(int q) {
        this.q = q;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }
}
