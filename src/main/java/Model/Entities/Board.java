package Model.Entities;

import org.javatuples.Pair;
import java.util.HashMap;

public class Board {
    private final HashMap<Pair <Integer, Integer>, HexCell> cells;

    public Board() {
        this.cells = new HashMap<>();
    }

    public HexCell getCell(int q, int r) {
        return cells.get(new Pair<>(q, r));
    }

    public void putCell(HexCell hexCell) {
        cells.put(new Pair<>(hexCell.getQ(), hexCell.getR()), hexCell);
    }

    public boolean contains(Pair <Integer, Integer> pos) {
        return cells.containsKey(pos);
    }

    public int size() {
        return cells.size();
    }

    public HashMap<Pair <Integer, Integer>, HexCell> getCells() {
        return cells;
    }
}
