package pathfinding;

public class TeleportLink {
    private final Cell entrance;
    private final Cell exit;

    public TeleportLink(Cell entrance, Cell exit) {
        this.entrance = entrance;
        this.exit = exit;
    }

    public Cell getEntrance() {
        return entrance;
    }

    public Cell getExit() {
        return exit;
    }
}
