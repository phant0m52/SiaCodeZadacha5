package pathfinding;

import java.util.Objects;

public class Cell {
    private final int row;
    private final int col;
    private CellType type;
    private int teleportId = -1; 

    public Cell(int row, int col, CellType type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public int getTeleportId() {
        return teleportId;
    }

    public void setTeleportId(int teleportId) {
        this.teleportId = teleportId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
