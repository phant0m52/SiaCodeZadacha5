package pathfinding;

import java.util.Objects;

class Node implements Comparable<Node> {
    final Cell cell;
    double g;
    double h;
    Node parent;

    Node(Cell cell) {
        this.cell = cell;
        this.g = Double.POSITIVE_INFINITY;
        this.h = 0;
    }

    double f() {
        return g + h;
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(this.f(), o.f());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(cell, node.cell);
    }

    @Override
    public int hashCode() {
        return cell.hashCode();
    }
}
