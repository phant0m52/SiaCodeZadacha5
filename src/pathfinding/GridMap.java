package pathfinding;

import java.util.*;

public class GridMap {
    private final int rows;
    private final int cols;
    private final Cell[][] cells;

    public GridMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell(r, c, CellType.EMPTY);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
        return cells[row][col];
    }

    public Cell getStart() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c].getType() == CellType.START) return cells[r][c];
            }
        }
        return null;
    }

    public Cell getGoal() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c].getType() == CellType.GOAL) return cells[r][c];
            }
        }
        return null;
    }

    /**
     * Устанавливаем тип клетки с учётом того, что START и GOAL должны быть единственными.
     */
    public void setCellType(int row, int col, CellType type, int teleportId) {
        Cell cell = getCell(row, col);
        if (cell == null) return;

        // Если ставим START — убираем старый START
        if (type == CellType.START) {
            clearType(CellType.START);
        }
        // Если ставим GOAL — убираем старый GOAL
        if (type == CellType.GOAL) {
            clearType(CellType.GOAL);
        }

        cell.setType(type);
        if (type == CellType.TELEPORT_ENTRANCE || type == CellType.TELEPORT_EXIT) {
            cell.setTeleportId(teleportId);
        } else {
            cell.setTeleportId(-1);
        }
    }

    private void clearType(CellType type) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c].getType() == type) {
                    cells[r][c].setType(CellType.EMPTY);
                    cells[r][c].setTeleportId(-1);
                }
            }
        }
    }

    /**
     * Собираем пары телепортов по teleportId.
     * Предполагаем, что для каждого id есть максимум один вход и один выход.
     */
    public Map<Integer, TeleportLink> buildTeleportLinks() {
        Map<Integer, Cell> entrances = new HashMap<>();
        Map<Integer, Cell> exits = new HashMap<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell.getType() == CellType.TELEPORT_ENTRANCE && cell.getTeleportId() >= 0) {
                    entrances.put(cell.getTeleportId(), cell);
                } else if (cell.getType() == CellType.TELEPORT_EXIT && cell.getTeleportId() >= 0) {
                    exits.put(cell.getTeleportId(), cell);
                }
            }
        }

        Map<Integer, TeleportLink> result = new HashMap<>();
        for (Integer id : entrances.keySet()) {
            Cell entrance = entrances.get(id);
            Cell exit = exits.get(id);
            if (entrance != null && exit != null) {
                result.put(id, new TeleportLink(entrance, exit));
            }
        }

        return result;
    }
}
