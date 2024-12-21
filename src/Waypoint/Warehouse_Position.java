package Waypoint;

import org.jxmapviewer.viewer.GeoPosition;

public class Warehouse_Position {
    private String name;
    private GeoPosition position;

    // Constructor
    public Warehouse_Position(String name, double latitude, double longitude) {
        this.name = name;
        this.position = new GeoPosition(latitude, longitude);
    }

    // Getter cho tên kho
    public String getName() {
        return name;
    }

    // Getter cho vị trí GeoPosition
    public GeoPosition getPosition() {
        return position;
    }

    // Hàm tạo MyWaypoint từ Warehouse_Position
    public MyWaypoint toWaypoint(EventsWaypoint event) {
        return new MyWaypoint(name, MyWaypoint.PointType.START, event, position);
    }
}
