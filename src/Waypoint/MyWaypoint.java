
package Waypoint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.xml.stream.events.StartDocument;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class MyWaypoint extends DefaultWaypoint{
     
    private String name;
    private JButton button;

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }
    private PointType pointType;
            

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JButton getButton() {
        return button;
    }

    public void setButton(JButton button) {
        this.button = button;
    }
    
    public MyWaypoint(String name, PointType pointType, EventsWaypoint event, GeoPosition coord){
        super(coord);
        this.name = name;
        this.pointType = pointType;
        initButton(event);
    }
    
    private void initButton(EventsWaypoint event){
        button = new ButtonWayPoint();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                event.selected(MyWaypoint.this);
            }
        });
    }
    
    public static enum PointType{
        START,END
    }
    
}
