package test;

import data.RoutingData;
import data.RoutingService;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import Waypoint.MyWaypoint;
import Waypoint.MyWaypoint.PointType;
import Waypoint.WaypointRender;
import Waypoint.EventsWaypoint;
import Waypoint.Warehouse_Position;

public class MapMain extends JFrame {

    private final Set<MyWaypoint> waypoints = new HashSet<>();
    private List<RoutingData> routingData = new ArrayList<>();
    private EventsWaypoint event;
    private Point mousePosition;
    
    public MapMain() {
        this.initComponents();
        this.init();
    }
    
    private void init(){
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        this.jXMapViewer.setTileFactory(tileFactory);
        GeoPosition geo = new GeoPosition(10.8020094,106.6645009);
        this.jXMapViewer.setAddressLocation(geo);
        this.jXMapViewer.setZoom(10);
        
        MouseInputListener mm = new PanMouseInputListener(this.jXMapViewer);
        this.jXMapViewer.addMouseListener(mm);
        this.jXMapViewer.addMouseMotionListener(mm);
        this.jXMapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(this.jXMapViewer));
        this.event = this.getEvent();
    }

//    private void addWaypoint(MyWaypoint waypoint) {
//        Iterator iter = this.waypoints.iterator();
//
//        while(iter.hasNext()) {
//            MyWaypoint d = (MyWaypoint)iter.next();
//            this.jXMapViewer.remove(d.getButton());
//        }
//
//        iter = this.waypoints.iterator();
//
//        while(iter.hasNext()) {
//            if (((MyWaypoint)iter.next()).getPointType() == waypoint.getPointType()) {
//                iter.remove();
//            }
//        }
//
//        this.waypoints.add(waypoint);
//        this.initWaypoint();
//    }
    
    private void addWaypoint(MyWaypoint waypoint) {
        // Xóa phần code xóa waypoint cũ dựa trên PointType
        Iterator iter = this.waypoints.iterator();

        while(iter.hasNext()) {
            MyWaypoint d = (MyWaypoint) iter.next();
            this.jXMapViewer.remove(d.getButton());
        }

        // Thêm waypoint mới vào danh sách
        this.waypoints.add(waypoint);
        this.initWaypoint();
    }

    
//    private void initWaypoint() {
//        WaypointPainter<MyWaypoint> wp = new WaypointRender();
//        ((WaypointPainter)wp).setWaypoints(this.waypoints);
//        this.jXMapViewer.setOverlayPainter(wp);
//        Iterator var2 = this.waypoints.iterator();
//
//        while(var2.hasNext()) {
//            MyWaypoint d = (MyWaypoint)var2.next();
//            this.jXMapViewer.add(d.getButton());
//        }
//
//        if (this.waypoints.size() == 2) {
//            GeoPosition start = null;
//            GeoPosition end = null;
//            Iterator var4 = this.waypoints.iterator();
//
//            while(var4.hasNext()) {
//                MyWaypoint w = (MyWaypoint)var4.next();
//                if (w.getPointType() == PointType.START) {
//                    start = w.getPosition();
//                } else if (w.getPointType() == PointType.END) {
//                    end = w.getPosition();
//                }
//            }
//
//            if (start != null && end != null) {
//                this.routingData = RoutingService.getInstance().routing(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());
//            } else {
//                this.routingData.clear();
//            }
//
//            this.jXMapViewer.setRoutingData(this.routingData);
//        }
//    }
    
private void initWaypoint() {
    WaypointPainter<MyWaypoint> wp = new WaypointRender();
    wp.setWaypoints(this.waypoints);
    this.jXMapViewer.setOverlayPainter(wp);

    for (MyWaypoint d : waypoints) {
        this.jXMapViewer.add(d.getButton());
    }

    // Tìm đường đi ngắn nhất nếu có điểm START
    GeoPosition start = null;
    List<GeoPosition> destinations = new ArrayList<>();

    for (MyWaypoint w : waypoints) {
        if (w.getPointType() == MyWaypoint.PointType.START) {
            start = w.getPosition();
        } else {
            destinations.add(w.getPosition());
        }
    }

    if (start != null && !destinations.isEmpty()) {
        findShortestPaths(start, destinations);
    }
}

private void findShortestPaths(GeoPosition start, List<GeoPosition> destinations) {
    RoutingService routingService = RoutingService.getInstance(); // Giả sử bạn có dịch vụ tìm đường
    List<RoutingData> allRoutes = new ArrayList<>();

    for (GeoPosition destination : destinations) {
        // Tính đường đi từ điểm xuất phát đến từng kho
        List<RoutingData> route = routingService.routing(
            start.getLatitude(), start.getLongitude(),
            destination.getLatitude(), destination.getLongitude()
        );

        if (route != null) {
            allRoutes.addAll(route); // Thêm dữ liệu đường đi
        }
    }

    // Hiển thị tất cả các tuyến đường
    this.jXMapViewer.setRoutingData(allRoutes);
    JOptionPane.showMessageDialog(this, "Shortest paths to warehouses have been calculated.");
}
   
    
    
    private void clearWaypoint(){
        for(MyWaypoint d : waypoints){
            jXMapViewer.remove(d.getButton());
        }
        routingData.clear();
        waypoints.clear();
        initWaypoint();
    }
    
    private EventsWaypoint getEvent(){
        return new EventsWaypoint() {
            @Override
            public void selected(MyWaypoint waypoint) {
                JOptionPane.showMessageDialog(MapMain.this, waypoint.getName());
            }
        };
    }
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        menuEnd = new javax.swing.JMenuItem();
        menuStart = new javax.swing.JMenuItem();
        jXMapViewer = new data.JXMapViewerCustom();
        cmdAdd = new javax.swing.JButton();
        cmdClear = new javax.swing.JButton();
        comboMapType = new javax.swing.JComboBox<>();
        CloseButton = new javax.swing.JButton();
        ShowWareHouse = new javax.swing.JButton();
        WHFindShortestWay = new javax.swing.JButton();

        menuEnd.setText("End");
        menuEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEndActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuEnd);

        menuStart.setText("Start");
        menuStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuStartActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuStart);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jXMapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jXMapViewerMouseReleased(evt);
            }
        });

        cmdAdd.setText("Add Waypoint");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdClear.setText("Clear Waypoint");
        cmdClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearActionPerformed(evt);
            }
        });

        comboMapType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Road Map", "Hybrid", "Satellite" }));
        comboMapType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMapTypeActionPerformed(evt);
            }
        });

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        ShowWareHouse.setText("Show WareHouse");
        ShowWareHouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowWareHouseActionPerformed(evt);
            }
        });

        WHFindShortestWay.setText("Show The Ways");
        WHFindShortestWay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WHFindShortestWayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXMapViewerLayout = new javax.swing.GroupLayout(jXMapViewer);
        jXMapViewer.setLayout(jXMapViewerLayout);
        jXMapViewerLayout.setHorizontalGroup(
            jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXMapViewerLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(cmdAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ShowWareHouse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WHFindShortestWay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 307, Short.MAX_VALUE)
                .addComponent(comboMapType, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CloseButton)
                .addGap(12, 12, 12))
        );
        jXMapViewerLayout.setVerticalGroup(
            jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXMapViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboMapType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CloseButton))
                    .addGroup(jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdClear)
                        .addComponent(cmdAdd)
                        .addComponent(ShowWareHouse)
                        .addComponent(WHFindShortestWay)))
                .addContainerGap(537, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXMapViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXMapViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void comboMapTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMapTypeActionPerformed
        TileFactoryInfo info;
        int index = comboMapType.getSelectedIndex();
        if (index == 0){
            info = new OSMTileFactoryInfo();
        }
//        else if (index == 1){
//            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
//        }
        else if (index == 2){
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
        }else{
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        }
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapViewer.setTileFactory(tileFactory);
    }//GEN-LAST:event_comboMapTypeActionPerformed

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
//        addWaypoint(new MyWaypoint("Test 001", event, new GeoPosition(10.800370, 106.662923)));
//        initWaypoint();
    }//GEN-LAST:event_cmdAddActionPerformed

    private void cmdClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdClearActionPerformed
        clearWaypoint();
    }//GEN-LAST:event_cmdClearActionPerformed

    private void menuEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEndActionPerformed
//        GeoPosition geop = jXMapViewer.convertPointToGeoPosition(mousePosition);
//        MyWaypoint wayPoint = new MyWaypoint("End Location", MyWaypoint.PointType.END, event, new GeoPosition(geop.getLatitude(), geop.getLongitude()));
//        addWaypoint(wayPoint);
    }//GEN-LAST:event_menuEndActionPerformed

    private void menuStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuStartActionPerformed
//        GeoPosition geop = jXMapViewer.convertPointToGeoPosition(mousePosition);
//        MyWaypoint wayPoint = new MyWaypoint("Start Location", MyWaypoint.PointType.START, event, new GeoPosition(geop.getLatitude(), geop.getLongitude()));
//        addWaypoint(wayPoint);
    }//GEN-LAST:event_menuStartActionPerformed

    private void jXMapViewerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jXMapViewerMouseReleased
//        if(SwingUtilities.isRightMouseButton(evt)){
//            mousePosition = evt.getPoint();
//            jPopupMenu1.show(jXMapViewer, evt.getX(), evt.getY());
//        }
    }//GEN-LAST:event_jXMapViewerMouseReleased

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose(); 
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void ShowWareHouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowWareHouseActionPerformed
            // Danh sách kho hàng
        Warehouse_Position [] warehouses = new Warehouse_Position[]{
        new Warehouse_Position("Hà Nội", 21.0285, 105.8542),
        new Warehouse_Position("Hải Phòng", 20.8449, 106.6881),
        new Warehouse_Position("Bắc Ninh", 21.1857, 106.0655),
        new Warehouse_Position("Đà Nẵng", 16.0544, 108.2022),
        new Warehouse_Position("Huế", 16.4637, 107.5909),
        new Warehouse_Position("TP Hồ Chí Minh", 10.7769, 106.7009),
        new Warehouse_Position("Vinh", 18.6796, 105.6810),
        new Warehouse_Position("Thanh Hóa", 19.8075, 105.7764),
        new Warehouse_Position("Nha Trang", 12.2388, 109.1962),
        new Warehouse_Position("Phan Thiết", 10.9264, 108.1056)
        };

       // Thêm các waypoint từ danh sách
       for (Warehouse_Position warehouse : warehouses) {
           addWaypoint(warehouse.toWaypoint(event));
       }

       JOptionPane.showMessageDialog(this, "Warehouses loaded on the map.");   
    }//GEN-LAST:event_ShowWareHouseActionPerformed

    private void WHFindShortestWayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WHFindShortestWayActionPerformed
        // Danh sách các kho hàng và điểm đầu, điểm kết thúc
        Warehouse_Position[] warehouses = new Warehouse_Position[]{
            new Warehouse_Position("Hà Nội", 21.0285, 105.8542),       // Điểm đầu
            new Warehouse_Position("Hải Phòng", 20.8449, 106.6881),
//            new Warehouse_Position("Bắc Ninh", 21.1857, 106.0655),
//            new Warehouse_Position("Đà Nẵng", 16.0544, 108.2022),
//            new Warehouse_Position("Huế", 16.4637, 107.5909),
//            new Warehouse_Position("Vinh", 18.6796, 105.6810),
//            new Warehouse_Position("Thanh Hóa", 19.8075, 105.7764),
//            new Warehouse_Position("Nha Trang", 12.2388, 109.1962),
//            new Warehouse_Position("Phan Thiết", 10.9264, 108.1056),
//            new Warehouse_Position("TP Hồ Chí Minh", 10.7769, 106.7009) // Điểm kết thúc
        };

        // Xác định điểm đầu và điểm cuối
        GeoPosition start = warehouses[0].getPosition(); // Hà Nội
        GeoPosition end = warehouses[warehouses.length - 1].getPosition(); // TP Hồ Chí Minh

        List<GeoPosition> intermediatePoints = new ArrayList<>();
        for (int i = 1; i < warehouses.length - 1; i++) {
            intermediatePoints.add(warehouses[i].getPosition());
        }

        // Tìm đường đi qua tất cả các kho
        findPathThroughWarehouses(start, intermediatePoints, end);
    }//GEN-LAST:event_WHFindShortestWayActionPerformed

private void findPathThroughWarehouses(GeoPosition start, List<GeoPosition> waypoints, GeoPosition end) {
    RoutingService routingService = RoutingService.getInstance();
    List<RoutingData> allRoutes = new ArrayList<>();

    GeoPosition current = start;

    // Tính đường từ điểm hiện tại đến từng waypoint trung gian
    for (GeoPosition waypoint : waypoints) {
        List<RoutingData> route = routingService.routing(
                current.getLatitude(), current.getLongitude(),
                waypoint.getLatitude(), waypoint.getLongitude()
        );
        if (route != null) {
            allRoutes.addAll(route);
        }
        current = waypoint; // Cập nhật điểm hiện tại
    }

    // Tính đường từ điểm trung gian cuối cùng đến điểm kết thúc
    List<RoutingData> finalRoute = routingService.routing(
            current.getLatitude(), current.getLongitude(),
            end.getLatitude(), end.getLongitude()
    );
    if (finalRoute != null) {
        allRoutes.addAll(finalRoute);
    }

    // Hiển thị tất cả các tuyến đường trên bản đồ
    this.jXMapViewer.setRoutingData(allRoutes);
    JOptionPane.showMessageDialog(this, "Route from Hà Nội to TP Hồ Chí Minh through all warehouses has been displayed.");
}

    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MapMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MapMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MapMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MapMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MapMain map = new MapMain();
                map.WHFindShortestWay.doClick(); 
                map.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton ShowWareHouse;
    private javax.swing.JButton WHFindShortestWay;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdClear;
    private javax.swing.JComboBox<String> comboMapType;
    private javax.swing.JPopupMenu jPopupMenu1;
    private data.JXMapViewerCustom jXMapViewer;
    private javax.swing.JMenuItem menuEnd;
    private javax.swing.JMenuItem menuStart;
    // End of variables declaration//GEN-END:variables
}
