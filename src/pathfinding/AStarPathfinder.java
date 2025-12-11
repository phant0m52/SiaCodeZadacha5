package pathfinding;

import java.util.*;

public class AStarPathfinder {

    public List<Cell> findPath(GridMap map) {
        Cell start = map.getStart();
        Cell goal = map.getGoal();

        if (start == null || goal == null) {
            return null; 
        }

        Map<Integer, TeleportLink> teleports = map.buildTeleportLinks();

        PriorityQueue<Node> open = new PriorityQueue<>();
        Map<Cell, Node> nodeByCell = new HashMap<>();
        Set<Cell> closed = new HashSet<>();

        Node startNode = new Node(start);
        startNode.g = 0;
        startNode.h = heuristic(start, goal);
        open.add(startNode);
        nodeByCell.put(start, startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (current.cell.equals(goal)) {
                return reconstructPath(current);
            }

            closed.add(current.cell);

            for (Cell neighbor : getNeighbors(map, current.cell, teleports)) {
                if (closed.contains(neighbor)) continue;
                if (neighbor.getType() == CellType.OBSTACLE) continue;

                Node neighborNode = nodeByCell.get(neighbor);
                if (neighborNode == null) {
                    neighborNode = new Node(neighbor);
                    nodeByCell.put(neighbor, neighborNode);
                }

                double moveCost = movementCost(current.cell, neighbor, teleports);
                double tentativeG = current.g + moveCost;

                if (tentativeG < neighborNode.g) {
                    neighborNode.parent = current;
                    neighborNode.g = tentativeG;
                    neighborNode.h = heuristic(neighbor, goal);

                    if (!open.contains(neighborNode)) {
                        open.add(neighborNode);
                    } else {
                        
                        open.remove(neighborNode);
                        open.add(neighborNode);
                    }
                }
            }
        }

        return null;
    }

    private double heuristic(Cell a, Cell b) {
        int dx = Math.abs(a.getRow() - b.getRow());
        int dy = Math.abs(a.getCol() - b.getCol());
        // Ěŕíőýňňĺí
        return dx + dy;
    }

    private double movementCost(Cell from, Cell to, Map<Integer, TeleportLink> teleports) {
       
        if (from.getType() == CellType.TELEPORT_ENTRANCE &&
                to.getType() == CellType.TELEPORT_EXIT &&
                from.getTeleportId() >= 0 &&
                from.getTeleportId() == to.getTeleportId()) {
            return 0; 
        }
        return 1; 
    }

    private List<Cell> getNeighbors(GridMap map, Cell cell, Map<Integer, TeleportLink> teleports) {
        List<Cell> neighbors = new ArrayList<>();

        int r = cell.getRow();
        int c = cell.getCol();

        int[][] dirs = {
                {1, 0}, {-1, 0},
                {0, 1}, {0, -1}
        };

        for (int[] d : dirs) {
            Cell n = map.getCell(r + d[0], c + d[1]);
            if (n != null) {
                neighbors.add(n);
            }
        }

       
        if (cell.getType() == CellType.TELEPORT_ENTRANCE && cell.getTeleportId() >= 0) {
            TeleportLink link = teleports.get(cell.getTeleportId());
            if (link != null) {
                neighbors.add(link.getExit());
            }
        }

        return neighbors;
    }

    private List<Cell> reconstructPath(Node goalNode) {
        List<Cell> path = new ArrayList<>();
        Node current = goalNode;
        while (current != null) {
            path.add(current.cell);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
