package pathfinding;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private final GridMap map;
    private final GridPanel gridPanel;
    private final AStarPathfinder pathfinder = new AStarPathfinder();

    private final JLabel statusLabel = new JLabel("Готово.");

    public MainFrame() {
        super("Поиск пути A* с телепортами");

        this.map = new GridMap(20, 30);
        this.gridPanel = new GridPanel(map);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(gridPanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel cellTypeLabel = new JLabel("Тип клетки:");
        panel.add(cellTypeLabel);

        JComboBox<CellType> cellTypeCombo = new JComboBox<>(CellType.values());
        cellTypeCombo.setSelectedItem(CellType.OBSTACLE);
        cellTypeCombo.addActionListener(e -> {
            CellType type = (CellType) cellTypeCombo.getSelectedItem();
            if (type != null) {
                gridPanel.setCurrentBrush(type);
            }
        });
        panel.add(cellTypeCombo);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel teleportLabel = new JLabel("ID телепорта:");
        panel.add(teleportLabel);

        JSpinner teleportSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        teleportSpinner.addChangeListener(e -> {
            int id = (int) teleportSpinner.getValue();
            gridPanel.setCurrentTeleportId(id);
        });
        panel.add(teleportSpinner);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton runButton = new JButton("Запустить A*");
        runButton.addActionListener(e -> runPathfinding());
        panel.add(runButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton clearPathButton = new JButton("Очистить путь");
        clearPathButton.addActionListener(e -> {
            gridPanel.setPath(null);
            statusLabel.setText("Путь очищен.");
        });
        panel.add(clearPathButton);

        JButton clearAllButton = new JButton("Очистить карту");
        clearAllButton.addActionListener(e -> {
            for (int r = 0; r < map.getRows(); r++) {
                for (int c = 0; c < map.getCols(); c++) {
                    map.setCellType(r, c, CellType.EMPTY, -1);
                }
            }
            gridPanel.setPath(null);
            gridPanel.repaint();
            statusLabel.setText("Карта очищена.");
        });
        panel.add(clearAllButton);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void runPathfinding() {
        Cell start = map.getStart();
        Cell goal = map.getGoal();

        if (start == null || goal == null) {
            JOptionPane.showMessageDialog(this,
                    "Необходимо задать START и GOAL.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Cell> path = pathfinder.findPath(map);
        if (path == null) {
            statusLabel.setText("Путь не найден.");
            gridPanel.setPath(null);
        } else {
            gridPanel.setPath(path);
            double length = computePathLength(path);
            statusLabel.setText(String.format("Путь найден. Длина: %.2f", length));
        }
    }

    private double computePathLength(List<Cell> path) {
        // Используем ту же стоимость, что и в AStarPathfinder
        double len = 0.0;
        for (int i = 1; i < path.size(); i++) {
            Cell prev = path.get(i - 1);
            Cell cur = path.get(i);
            if (prev.getType() == CellType.TELEPORT_ENTRANCE &&
                    cur.getType() == CellType.TELEPORT_EXIT &&
                    prev.getTeleportId() >= 0 &&
                    prev.getTeleportId() == cur.getTeleportId()) {
                len += 0; // телепорт
            } else {
                len += 1;
            }
        }
        return len;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}
