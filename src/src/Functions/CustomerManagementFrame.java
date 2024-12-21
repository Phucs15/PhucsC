package src.Functions;

import src.Functions.AddFctsFrame.SearchingFrame;
import src.Functions.AddFctsFrame.SortingFrame;
import src.Functions.AddFctsFrame.UndoRedoFrame;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class CustomerManagementFrame extends JFrame {

    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private UndoRedoFrame undoRedoFrame;
    public CustomerManagementFrame() {
        setTitle("Customer Management System - Manage Customers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // // Create a label
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Manage Customers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER); // Căn giữa tiêu đề
        // Create customer table
        String[] customerColumns = {
                "Customer_ID", "First_Name", "Last_Name", "Customer_Name", "Phone_Number", "Address"
        };
        customerTableModel = new DefaultTableModel(customerColumns, 0);
        customerTable = new JTable(customerTableModel);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);  

        // Add table to panel
        mainPanel.add(customerScrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tạo Panel cho nút tìm kiếm và đặt ở phía phải
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); 
        // Tạo nút tìm kiếm
        SearchingFrame searchingFrame = new SearchingFrame(customerTableModel, customerTable);
        JButton searchButton = searchingFrame.createSearchButton(customerTable, customerTableModel);
        // Thêm nút tìm kiếm vào topRightPanel
        topRightPanel.add(searchButton);
        headerPanel.add(topRightPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);


        // Tạo Panel cho nút Undo/Redo và đặt ở phía trái
        JPanel topLeftPanel = new JPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        undoRedoFrame = new UndoRedoFrame(customerTableModel);
        // Tạo nút Undo/Redo và thêm vào topLeftPanel
        JButton undoButton = undoRedoFrame.createUndoButton();
        JButton redoButton = undoRedoFrame.createRedoButton();
        // Thêm hai nút này vào góc trên bên trái
        topLeftPanel.add(undoButton);
        topLeftPanel.add(redoButton);
        headerPanel.add(topLeftPanel, BorderLayout.WEST);



        // Buttons
        JButton loadCustomersButton = new JButton("Load Customers");
        JButton viewOrdersButton = new JButton("View Detail");
        SortingFrame sortingFrame = new SortingFrame(customerTable, customerTableModel, undoRedoFrame);
        sortingFrame.addSortingFunctionality();

        // Apear the Button
        buttonPanel.add(loadCustomersButton);
        buttonPanel.add(viewOrdersButton);

        // Add button functionality
        loadCustomersButton.addActionListener(e -> {
            undoRedoFrame.saveState(); 
            loadCustomerData();
        });

        viewOrdersButton.addActionListener(e -> viewCustomerDetails());

        setVisible(true);
    }

    private void loadCustomerData() {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Customer")) {

            customerTableModel.setRowCount(0); // Clear existing rows
            while (resultSet.next()) {
                customerTableModel.addRow(new Object[]{
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("First_Name"),
                        resultSet.getString("Last_Name"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Phone_Number"),
                        resultSet.getString("Address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void viewCustomerDetails() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to view details.");
            return;
        }

        int customerId = Integer.parseInt(customerTableModel.getValueAt(selectedRow, 0).toString());

        // Create a dialog to display transactions and orders
        JDialog detailDialog = new JDialog(this, "Details for Customer ID: " + customerId, true);
        detailDialog.setSize(1200, 700);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setLayout(new BorderLayout());

        // Create split pane for transactions and orders
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        detailDialog.add(splitPane, BorderLayout.CENTER);

        // Panel for Transactions
        String[] transactionColumns = {"Product_Name", "Transaction_Date", "Transaction_Time", "Transaction_Status", "Payment_Method", "Amount"};
        DefaultTableModel transactionTableModel = new DefaultTableModel(transactionColumns, 0);
        JTable transactionTable = new JTable(transactionTableModel);
        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        transactionPanel.add(transactionScrollPane, BorderLayout.CENTER);

        // Panel for Orders
        String[] orderColumns = {"Order_ID", "Order_Date", "Expected_Delivery", "Product_Name", "Category", "Price", "Supplier_ID", "Description"};
        DefaultTableModel orderTableModel = new DefaultTableModel(orderColumns, 0);
        JTable orderTable = new JTable(orderTableModel);
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createTitledBorder("Orders"));
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);

        // Add panels to split pane
        splitPane.setTopComponent(transactionPanel);
        splitPane.setBottomComponent(orderPanel);

        // Load transactions into the Transactions table
        String transactionQuery = """
            SELECT 
                p.Product_Name,
                t.Transaction_Date,
                t.Transaction_Time,
                t.Transaction_Status,
                t.Payment_Method,
                t.Amount
            FROM Transactions t
            JOIN Orders o ON t.Customer_ID = o.Customer_ID
            JOIN Order_Detail od ON o.Order_ID = od.Order_ID
            JOIN Product p ON od.Product_ID = p.Product_ID
            WHERE t.Customer_ID = ?
        """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement transactionStmt = connection.prepareStatement(transactionQuery)) {
            transactionStmt.setInt(1, customerId);
            ResultSet transactionResult = transactionStmt.executeQuery();

            while (transactionResult.next()) {
                transactionTableModel.addRow(new Object[]{
                    transactionResult.getString("Product_Name"),
                    transactionResult.getDate("Transaction_Date"),
                    transactionResult.getTime("Transaction_Time"),
                    transactionResult.getString("Transaction_Status"),
                    transactionResult.getString("Payment_Method"),
                    transactionResult.getBigDecimal("Amount")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transaction data: " + e.getMessage());
        }

        // Load orders into the Orders table
        String orderQuery = """
            SELECT 
                o.Order_ID,
                o.Order_Date,
                o.Expected_Delivery,
                p.Product_Name,
                p.Category,
                p.Price,
                p.Supplier_ID,
                p.Description
            FROM Orders o
            JOIN Order_Detail od ON o.Order_ID = od.Order_ID
            JOIN Product p ON od.Product_ID = p.Product_ID
            WHERE o.Customer_ID = ?
        """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement orderStmt = connection.prepareStatement(orderQuery)) {
            orderStmt.setInt(1, customerId);
            ResultSet orderResult = orderStmt.executeQuery();

            while (orderResult.next()) {
                orderTableModel.addRow(new Object[]{
                    orderResult.getInt("Order_ID"),
                    orderResult.getDate("Order_Date"),
                    orderResult.getDate("Expected_Delivery"),
                    orderResult.getString("Product_Name"),
                    orderResult.getString("Category"),
                    orderResult.getDouble("Price"),
                    orderResult.getInt("Supplier_ID"),
                    orderResult.getString("Description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }

        detailDialog.setVisible(true);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerManagementFrame manageCustomerFrame = new CustomerManagementFrame();
            manageCustomerFrame.setVisible(true);
        });
    }
}
