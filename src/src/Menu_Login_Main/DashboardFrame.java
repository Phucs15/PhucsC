package src.Menu_Login_Main;
import src.Functions.CustomerManagementFrame;
import src.Functions.DatabaseConnection;
import src.Functions.ProductManagementFrame;
import src.Functions.WorkerManagementFrame;
import test.MapMain;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class DashboardFrame extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;

    public DashboardFrame() {
        // Cài đặt JFrame
        setTitle("Warehouse Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700); // Kích thước cửa sổ
        setLayout(new BorderLayout());

        // Tạo Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 128)); // Màu xanh đậm
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("Welcome to WareHouse Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        dateLabel = new JLabel("Date: DD-MM-YYYY", SwingConstants.LEFT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);

        timeLabel = new JLabel("Time: HH:MM:SS", SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(Color.WHITE);

        headerPanel.add(dateLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // Cập nhật thời gian và ngày
        startClock();

        // Tạo Summary Panel (chứa thông tin tổng)
        JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 hàng, 3 cột với khoảng cách
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // // Thêm các ô thông tin với góc bo tròn
        summaryPanel.add(createRoundedInfoBox("Total Employee", getTotal("SELECT COUNT(Employee_ID) FROM Employee"), new Color(0, 0, 255)));
        summaryPanel.add(createRoundedInfoBox("Total Complete Transaction", getTotal("SELECT COUNT(*) FROM Transactions WHERE Transaction_status = 'Completed'"), new Color(255, 165, 0)));
        summaryPanel.add(createRoundedInfoBox("Total Order Quantity", getTotal("SELECT SUM(Quantity) FROM Order_Detail"), new Color(0, 128, 128)));
        summaryPanel.add(createRoundedInfoBox("Total Product", getTotal("SELECT COUNT(Product_ID) FROM Product"), new Color(169, 169, 169)));
        summaryPanel.add(createRoundedInfoBox("Total Customer", getTotal("SELECT COUNT(Customer_ID) FROM Customer"), new Color(255, 106, 106)));
        summaryPanel.add(createRevenueInfoBoxWithTable("Total Revenue", revenueQuery,new Color(50, 205, 50)));

        // Tạo buttonPanel với BoxLayout để sắp xếp các nút theo hàng dọc
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Sắp xếp theo trục Y

        // Căn chỉnh các nút vào giữa
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tạo các nút với kích thước cố định
        Dimension buttonSize = new Dimension(150, 400); 

        JButton userButton = createIconButton("Worker", "Images/Worker_Icon.png");
        userButton.setPreferredSize(buttonSize);
        userButton.setMaximumSize(buttonSize);
        userButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton productButton = createIconButton("Product", "Images/Product_Icon.png");
        productButton.setPreferredSize(buttonSize);
        productButton.setMaximumSize(buttonSize);
        productButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton customerButton = createIconButton("Customer", "Images/Customer_Icon.png");
        customerButton.setPreferredSize(buttonSize);
        customerButton.setMaximumSize(buttonSize);
        customerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton mapButton = createIconButton("Map", "Images/Map_Icon.png");
        mapButton.setPreferredSize(buttonSize);
        mapButton.setMaximumSize(buttonSize);
        mapButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutButton = createIconButton("Logout", "Images/Exit_Icon.png");
        logoutButton.setPreferredSize(buttonSize);
        logoutButton.setMaximumSize(buttonSize);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Thêm buttonPanel vào JFrame hoặc JPanel chính
        add(buttonPanel, BorderLayout.WEST); // Đặt panel ở phía trái giao diện


        // Thêm hành động cho nút
        userButton.addActionListener(e -> {
            WorkerManagementFrame manageUserFrame = new WorkerManagementFrame();
            manageUserFrame.setVisible(true);
        });

        productButton.addActionListener(e -> {
            ProductManagementFrame productManagementFrame = new ProductManagementFrame();
            productManagementFrame.setVisible(true);
        });

        customerButton.addActionListener(e -> {
            CustomerManagementFrame customerManagementFrame = new CustomerManagementFrame();
            customerManagementFrame.setVisible(true);
        });

        // Thêm ActionListener cho nút Map
        mapButton.addActionListener(e -> {
            MapMain map = new MapMain();
            map.setVisible(true);
        });

        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                LoginFrame loginFrame = new LoginFrame(); // Assuming LoginFrame exists
                loginFrame.setVisible(true);
                dispose(); // Close the DashboardFrame
            }
        });

        // Thêm nút vào Button Panel
        buttonPanel.add(userButton);
        buttonPanel.add(productButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(mapButton); 
        buttonPanel.add(logoutButton);

        // Tạo MapMain Panel để đặt summaryPanel và buttonPanel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(summaryPanel, BorderLayout.CENTER); // Thông tin tổng ở giữa
        mainPanel.add(buttonPanel, BorderLayout.EAST);    // Nút chức năng ở bên phải

        // Tạo Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(0, 0, 128));
        footerPanel.setPreferredSize(new Dimension(0, 30));
        JLabel footerLabel = new JLabel("Developed by Group2 - HCMIU", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);

        // Thêm các panel vào JFrame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER); // Thêm MapMain Panel vào trung tâm
        add(footerPanel, BorderLayout.SOUTH);

        // Hiển thị JFrame
        setLocationRelativeTo(null);
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date now = new Date();
            timeLabel.setText("Time: " + timeFormat.format(now));
            dateLabel.setText("Date: " + dateFormat.format(now));
        });
        timer.start();
    }

    //Phương thức tạo ô thông tin với góc bo tròn
    private JPanel createRoundedInfoBox(String title, String value, Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Góc bo tròn 30px
            }
        };
        panel.setOpaque(false); // Để vẽ nền bo tròn hoạt động
        panel.setPreferredSize(new Dimension(150, 100)); // Kích thước nhỏ hơn
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding bên trong

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 55));
        valueLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }
  
    private JPanel createRevenueInfoBoxWithTable(String title, String query, Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Góc bo tròn 30px
            }
        };
        panel.setOpaque(false); // Để vẽ nền bo tròn hoạt động
        panel.setPreferredSize(new Dimension(400, 300)); // Kích thước lớn hơn để chứa bảng
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding bên trong

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Phông chữ cho tiêu đề
        titleLabel.setForeground(Color.WHITE);

        // Tạo bảng
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Customer Name", "Total Revenue"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa bất kỳ ô nào
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);

        // Tạo JScrollPane cho bảng
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Tùy chỉnh thanh trượt
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // Thanh trượt dọc nhỏ hơn
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8)); // Thanh trượt ngang nhỏ hơn

        // Thay đổi giao diện thanh trượt dọc
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(150, 150, 150); // Màu thanh trượt
                this.trackColor = new Color(220, 220, 220); // Màu nền thanh trượt
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        // Thay đổi giao diện thanh trượt ngang
        scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(150, 150, 150); // Màu thanh trượt
                this.trackColor = new Color(220, 220, 220); // Màu nền thanh trượt
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        // Nạp dữ liệu vào bảng từ cơ sở dữ liệu
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                    resultSet.getString("Customer_Name"),
                    "$" + String.format("%.2f", resultSet.getDouble("Total_Revenue"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer revenue data: " + e.getMessage());
        }

        // Thêm bảng vào panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    String revenueQuery = """
        SELECT 
            Customer.Customer_Name, 
            SUM(Transactions.Amount) AS Total_Revenue
        FROM Transactions
        INNER JOIN Customer ON Transactions.Customer_ID = Customer.Customer_ID
        GROUP BY Customer.Customer_Name
        ORDER BY Total_Revenue DESC
    """;

    private JButton createIconButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180)); 
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 100)); // Kích thước nút
    
        // Căn chỉnh icon nằm trên văn bản
        button.setHorizontalTextPosition(SwingConstants.CENTER); // Text nằm giữa theo chiều ngang
        button.setVerticalTextPosition(SwingConstants.BOTTOM);   // Text nằm dưới icon
    
        // Thêm icon vào nút
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Kích thước icon
            button.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
    
        return button;
    }


    private String getTotal(String query) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
        });
    }
}
