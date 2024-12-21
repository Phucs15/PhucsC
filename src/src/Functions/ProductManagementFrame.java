package src.Functions;

import src.Functions.AddFctsFrame.SearchingFrame;
import src.Functions.AddFctsFrame.SortingFrame;
import src.Functions.AddFctsFrame.UndoRedoFrame;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductManagementFrame extends JFrame {

    private JTable productTable;
    private DefaultTableModel productTableModel;
    private UndoRedoFrame undoRedoFrame;

    public ProductManagementFrame() {
        setTitle("Product Management System - Manage Products");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Manage Products", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Create Product Table
        String[] productColumns = {"Product_ID", "Product_Name", "Category", "Price", "Supplier_ID", "Description"};
        productTableModel = new DefaultTableModel(productColumns, 0);
        productTable = new JTable(productTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        mainPanel.add(productScrollPane, BorderLayout.CENTER);

        // Add Header Panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Add Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add Undo/Redo Buttons
        undoRedoFrame = new UndoRedoFrame(productTableModel);
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton undoButton = undoRedoFrame.createUndoButton();
        JButton redoButton = undoRedoFrame.createRedoButton();
        topLeftPanel.add(undoButton);
        topLeftPanel.add(redoButton);
        headerPanel.add(topLeftPanel, BorderLayout.WEST);

        // Add Search Button
        SearchingFrame searchingFrame = new SearchingFrame(productTableModel, productTable);
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = searchingFrame.createSearchButton(productTable, productTableModel);
        topRightPanel.add(searchButton);
        headerPanel.add(topRightPanel, BorderLayout.EAST);

        // Add Sorting Functionality
        SortingFrame sortingFrame = new SortingFrame(productTable, productTableModel, undoRedoFrame);
        sortingFrame.addSortingFunctionality();

        // Add Additional Buttons
        JButton loadProductsButton = new JButton("Load Products");
        JButton viewSuppliersButton = new JButton("View Supplier");
        JButton viewWarehouseButton = new JButton("View Warehouse");
        JButton viewShipmentStatusButton = new JButton("View Shipment Status");
        buttonPanel.add(loadProductsButton);
        buttonPanel.add(viewSuppliersButton);
        buttonPanel.add(viewWarehouseButton);
        buttonPanel.add(viewShipmentStatusButton);

        // Button Functionalities
        loadProductsButton.addActionListener(e -> {
            undoRedoFrame.saveState(); // Save state before loading
            loadProductData();
        });

        viewSuppliersButton.addActionListener(e -> viewSupplierForSelectedProduct());
        viewWarehouseButton.addActionListener(e -> viewWarehouseForSelectedProduct());
        viewShipmentStatusButton.addActionListener(e -> viewShipmentStatusForSelectedProduct());

        setVisible(true);
    }

    // Load Product Data
    private void loadProductData() {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Product")) {

            productTableModel.setRowCount(0); // Clear existing rows
            while (resultSet.next()) {
                productTableModel.addRow(new Object[]{
                        resultSet.getInt("Product_ID"),
                        resultSet.getString("Product_Name"),
                        resultSet.getString("Category"),
                        resultSet.getBigDecimal("Price"),
                        resultSet.getInt("Supplier_ID"),
                        resultSet.getString("Description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading product data: " + e.getMessage());
        }
    }

    // View Supplier for Selected Product
    private void viewSupplierForSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to view its supplier.");
            return;
        }

        int supplierId = (int) productTableModel.getValueAt(selectedRow, 4);
        String query = "SELECT Supplier_ID, Supplier_Name, Contact_Number, Address FROM Supplier WHERE Supplier_ID = ?";
        showDataDialog("Supplier", query, new String[]{"Supplier_ID", "Supplier_Name", "Contact_Number", "Address"}, supplierId);
    }

    // View Warehouse for Selected Product
    private void viewWarehouseForSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to view its warehouse.");
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
        String query = """
            SELECT w.Warehouse_Name, w.Location, p.Product_Name, SUM(s.Quantity) AS Total_Quantity
            FROM Store s
            INNER JOIN Warehouse w ON s.Warehouse_ID = w.Warehouse_ID
            INNER JOIN Product p ON s.Product_ID = p.Product_ID
            WHERE p.Product_ID = ? 
            GROUP BY w.Warehouse_Name, w.Location, p.Product_Name;
        """;
        showDataDialog("Warehouse Details", query, new String[]{"Warehouse_Name", "Location", "Product_Name", "Total_Quantity"}, productId);
    }

    // View Shipment Status for Selected Product
    private void viewShipmentStatusForSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to view its shipment status.");
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
        String query = """
            SELECT o.Order_ID, s.Delivery_Status, s.Recipent_Date, s.Delivery_Date
            FROM Orders o
            JOIN Order_Detail od ON o.Order_ID = od.Order_ID
            JOIN Shipment s ON o.Shipment_ID = s.Shipment_ID
            WHERE od.Product_ID = ?
        """;
        showDataDialog("Shipment Status", query, new String[]{"Order_ID", "Delivery_Status", "Recipent_Date", "Delivery_Date"}, productId);
    }

    // Helper to Show Data in Dialog
    private void showDataDialog(String title, String query, String[] columnNames, int parameter) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, parameter);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    rowData[i] = resultSet.getObject(columnNames[i]);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }

        dialog.setVisible(true);
    }

    // Main Method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductManagementFrame());
    }
}
