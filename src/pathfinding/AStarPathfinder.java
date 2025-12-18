package pathfinding;

import java.util.*;

public class AStarPathfinder {

    public List<Cell> findPath(GridMap map) {
        Cell start = map.getStart();
        Cell goal = map.getGoal();
        if (start == null || goal == null) return null;

        Map<Integer, TeleportLink> teleports = map.buildTeleportLinks();

        PriorityQueue<Node> open = new PriorityQueue<>();
        Map<Cell, Node> nodes = new HashMap<>();
        Set<Cell> closed = new HashSet<>();

        Node startNode = new Node(start);
        startNode.g = 0;
        startNode.h = heuristic(start, goal);

        open.add(startNode);
        nodes.put(start, startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.cell.equals(goal))
                return reconstruct(current);

            closed.add(current.cell);

            for (Cell n : getNeighbors(map, current.cell, teleports)) {
                if (n.getType() == CellType.OBSTACLE || closed.contains(n))
                    continue;

                Node node = nodes.computeIfAbsent(n, Node::new);
                double gNew = current.g + cost(current.cell, n, teleports);

                if (gNew < node.g) {
                    node.parent = current;
                    node.g = gNew;
                    node.h = heuristic(n, goal);
                    open.remove(node);
                    open.add(node);
                }
            }
        }
        return null;
    }

    private double heuristic(Cell a, Cell b) {
        return Math.max(
                Math.abs(a.getRow() - b.getRow()),
                Math.abs(a.getCol() - b.getCol())
        );
    }

    private double cost(Cell a, Cell b, Map<Integer, TeleportLink> t) {
        if (a.getType() == CellType.TELEPORT_ENTRANCE &&
                b.getType() == CellType.TELEPORT_EXIT &&
                a.getTeleportId() == b.getTeleportId())
            return 0;

        int dr = Math.abs(a.getRow() - b.getRow());
        int dc = Math.abs(a.getCol() - b.getCol());
        return (dr == 1 && dc == 1) ? Math.sqrt(2) : 1;
    }

    private List<Cell> getNeighbors(GridMap map, Cell c, Map<Integer, TeleportLink> t) {
        List<Cell> res = new ArrayList<>();
        int[][] d = {
                {1,0},{-1,0},{0,1},{0,-1},
                {1,1},{1,-1},{-1,1},{-1,-1}
        };

        for (int[] x : d) {
            int r = c.getRow() + x[0];
            int col = c.getCol() + x[1];
            Cell n = map.getCell(r, col);
            if (n == null) continue;

            // запрет срезания углов
            if (Math.abs(x[0]) == 1 && Math.abs(x[1]) == 1) {
                Cell a = map.getCell(c.getRow(), col);
                Cell b = map.getCell(r, c.getCol());
                if (a == null || b == null) continue;
                if (a.getType() == CellType.OBSTACLE ||
                        b.getType() == CellType.OBSTACLE)
                    continue;
            }

            res.add(n);
        }

        if (c.getType() == CellType.TELEPORT_ENTRANCE) {
            TeleportLink link = t.get(c.getTeleportId());
            if (link != null) res.add(link.getExit());
        }

        return res;
    }

    private List<Cell> reconstruct(Node n) {
        List<Cell> path = new ArrayList<>();
        while (n != null) {
            path.add(n.cell);
            n = n.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
