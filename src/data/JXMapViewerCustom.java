
package data;

import com.graphhopper.util.shapes.GHPoint3D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

public class JXMapViewerCustom extends JXMapViewer {
    private List<RoutingData> routingData;
    private boolean first = true;

    public JXMapViewerCustom() {
    }

    public List<RoutingData> getRoutingData() {
        return this.routingData;
    }

    public void setRoutingData(List<RoutingData> routingData) {
        this.routingData = routingData;
        this.repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.routingData != null && !this.routingData.isEmpty()) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D p2 = new Path2D.Double();
            this.first = true;
            Iterator var4 = this.routingData.iterator();

            while(var4.hasNext()) {
                RoutingData d = (RoutingData)var4.next();
                this.draw(p2, d);
            }

            g2.setColor(new Color(28, 23, 255));
            g2.setStroke(new BasicStroke(5.0F, 1, 1));
            g2.draw(p2);
            g2.dispose();
        }

    }

    private void draw(final Path2D p2, RoutingData d) {
        d.getPointList().forEach(new Consumer<GHPoint3D>() {
            public void accept(GHPoint3D t) {
                Point2D point = JXMapViewerCustom.this.convertGeoPositionToPoint(new GeoPosition(t.getLat(), t.getLon()));
                if (JXMapViewerCustom.this.first) {
                    JXMapViewerCustom.this.first = false;
                    p2.moveTo(point.getX(), point.getY());
                } else {
                    p2.lineTo(point.getX(), point.getY());
                }

            }
        });
    }
}
