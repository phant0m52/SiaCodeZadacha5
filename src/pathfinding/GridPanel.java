package pathfinding;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridPanel extends JPanel {

    private final GridMap map;
    private CellType currentBrush = CellType.OBSTACLE;
    private int currentTeleportId = 0;
    private List<Cell> currentPath;

    private static final int CELL_SIZE = 25;

    public GridPanel(GridMap map) {
        this.map = map;
        setPreferredSize(new Dimension(map.getCols() * CELL_SIZE, map.getRows() * CELL_SIZE));
        setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                applyBrush(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                applyBrush(e);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void applyBrush(MouseEvent e) {
        int col = e.getX() / CELL_SIZE;
        int row = e.getY() / CELL_SIZE;
        if (row < 0 || row >= map.getRows() || col < 0 || col >= map.getCols()) return;

        map.setCellType(row, col, currentBrush, currentTeleportId);
        currentPath = null;
        repaint();
    }

    public void setCurrentBrush(CellType brush) {
        this.currentBrush = brush;
    }

    public void setCurrentTeleportId(int id) {
        this.currentTeleportId = id;
    }

    public void setPath(List<Cell> path) {
        this.currentPath = path;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Set<Cell> pathSet = new HashSet<>();
        if (currentPath != null) {
            pathSet.addAll(currentPath);
        }

        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                Cell cell = map.getCell(r, c);

                Color color;
                switch (cell.getType()) {
                    case EMPTY:
                        color = Color.WHITE;
                        break;
                    case OBSTACLE:
                        color = Color.DARK_GRAY;
                        break;
                    case START:
                        color = Color.GREEN;
                        break;
                    case GOAL:
                        color = Color.RED;
                        break;
                    case TELEPORT_ENTRANCE:
                        color = new Color(100, 149, 237); 
                        break;
                    case TELEPORT_EXIT:
                        color = new Color(218, 165, 32); 
                        break;
                    default:
                        color = Color.WHITE;
                }

                g.setColor(color);
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);


                if (pathSet.contains(cell) &&
                        cell.getType() != CellType.START &&
                        cell.getType() != CellType.GOAL) {
                    g.setColor(new Color(0, 255, 255, 120));
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);


                if ((cell.getType() == CellType.TELEPORT_ENTRANCE || cell.getType() == CellType.TELEPORT_EXIT)
                        && cell.getTeleportId() >= 0) {
                    g.setColor(Color.BLACK);
                    String txt = String.valueOf(cell.getTeleportId());
                    g.drawString(txt, c * CELL_SIZE + 4, r * CELL_SIZE + 12);
                }
            }
        }
    }
}
