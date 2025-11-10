package Model.Entities;
import java.util.HashMap;

public class Board {
    private final HashMap<Coords,  HexCell> cells;

    public Board() {
        this.cells = new HashMap<>();
    }

    public HexCell getCell(int q, int r) {
        return cells.get(new Coords(q, r));
    }

    public void putCell(HexCell hexCell) {
        cells.put(new Coords(hexCell.getQ(), hexCell.getR()), hexCell);
    }

    public boolean contains(Coords pos) {
        return cells.containsKey(pos);
    }

    public int size() {
        return cells.size();
    }

    public HashMap<Coords, HexCell> getCells() {
        return cells;
    }
}
