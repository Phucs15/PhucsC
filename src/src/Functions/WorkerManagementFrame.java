package src.Functions;

import src.Functions.AddFctsFrame.SearchingFrame;
import src.Functions.AddFctsFrame.SortingFrame;
import src.Functions.AddFctsFrame.UndoRedoFrame;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WorkerManagementFrame extends JFrame {

    private DefaultTableModel employeeTableModel;
    private DefaultTableModel staffTableModel;
    private JTable employeeTable;
    private JTable staffTable;
    private UndoRedoFrame undoRedoFrame;
    private UndoRedoFrame undoRedoFrameStaff;

    public WorkerManagementFrame() {
        // Set up JFrame properties
        setTitle("Employee Management System - Manage Employees and Staff");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800); // Adjusted for two tables
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Header label
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel headerLabel = new JLabel("Manage Employees and Staff", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Employee Table setup
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.setBorder(BorderFactory.createTitledBorder("Employee Table"));
        String[] employeeColumns = {"Employee_ID", "First_Name", "Last_Name", "Employee_Name", "Phone_Number", "Email", "Address", "Hire_Date"};
        employeeTableModel = new DefaultTableModel(employeeColumns, 0);
        JTable employeeTable = new JTable(employeeTableModel);
        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
        employeePanel.add(employeeScrollPane, BorderLayout.CENTER);

        // Staff Table setup
        JPanel staffPanel = new JPanel(new BorderLayout());
        staffPanel.setBorder(BorderFactory.createTitledBorder("Staff Table"));
        String[] staffColumns = {"Employee_ID", "Position", "Shift", "Department", "Hourly_Rate"};
        staffTableModel = new DefaultTableModel(staffColumns, 0);
        JTable staffTable = new JTable(staffTableModel);
        JScrollPane staffScrollPane = new JScrollPane(staffTable);
        staffPanel.add(staffScrollPane, BorderLayout.CENTER);

        // Split the frame into two parts horizontally
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, employeePanel, staffPanel);
        splitPane.setDividerLocation(400); // Set initial divider position
        add(splitPane, BorderLayout.CENTER);

        // Add Undo/Redo Buttons
        undoRedoFrame = new UndoRedoFrame(employeeTableModel);
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton undoButton = undoRedoFrame.createUndoButton();
        JButton redoButton = undoRedoFrame.createRedoButton();
        topLeftPanel.add(undoButton);
        topLeftPanel.add(redoButton);

        // Add Search Button
        SearchingFrame searchingFrame = new SearchingFrame(employeeTableModel, employeeTable);
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = searchingFrame.createSearchButton(employeeTable, employeeTableModel);
        topRightPanel.add(searchButton);

        // Add Sorting Functionality
        SortingFrame sortingFrameEmployee = new SortingFrame(employeeTable, employeeTableModel, undoRedoFrame);
        sortingFrameEmployee.addSortingFunctionality();
        SortingFrame sortingFrameStaff = new SortingFrame(staffTable, staffTableModel, undoRedoFrame);
        sortingFrameStaff.addSortingFunctionality();

        // Add to header panel
        headerPanel.add(topLeftPanel, BorderLayout.WEST);
        headerPanel.add(topRightPanel, BorderLayout.EAST);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton addButton = new JButton("Add Row");
        JButton saveButton = new JButton("Save Row");
        JButton deleteEmployeeButton = new JButton("Delete Employee");
        JButton deleteStaffButton = new JButton("Delete Staff");
        JButton loadEmployeesButton = new JButton("Load Employees");
        JButton loadStaffButton = new JButton("Load Staff");
        JButton addStaffButton = new JButton("Add Staff");
        JButton viewWarehouseButton = new JButton("View working place");

        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteEmployeeButton);
        buttonPanel.add(deleteStaffButton);
        buttonPanel.add(loadEmployeesButton);
        buttonPanel.add(loadStaffButton);
        buttonPanel.add(addStaffButton);
        buttonPanel.add(viewWarehouseButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Button Actions
        addButton.addActionListener(e -> {undoRedoFrame.saveState();
            employeeTableModel.addRow(new Object[]{"", "", "", "", "", "", "", ""});});

            saveButton.addActionListener(e -> {
                try {
                    // Lưu trạng thái hiện tại của bảng vào cơ sở dữ liệu
                    saveAllRowsToDatabase();
            
                    // Hiển thị thông báo thành công
                    JOptionPane.showMessageDialog(this, "Data saved successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
                }
            });
            

        deleteEmployeeButton.addActionListener(e -> {
            undoRedoFrame.saveState();
            try {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = employeeTableModel.getValueAt(selectedRow, 0).toString();
                    deleteEmployeeAndStaff(id);
                    employeeTableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an Employee to delete.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteStaffButton.addActionListener(e -> {
            undoRedoFrame.saveState(); 
            try {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = staffTableModel.getValueAt(selectedRow, 0).toString();
                    deleteStaffFromDatabase(id);
                    staffTableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a Staff to delete.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        

        loadEmployeesButton.addActionListener(e -> loadEmployeeData());
        loadStaffButton.addActionListener(e -> loadStaffData());

        addStaffButton.addActionListener(e -> {
            undoRedoFrame.saveState();
            try {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = employeeTableModel.getValueAt(selectedRow, 0).toString();
                    addStaffToDatabase(id);
                    JOptionPane.showMessageDialog(this, "Employee added to Staff successfully!");
                    loadStaffData();
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an Employee to add to Staff.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        

        viewWarehouseButton.addActionListener(e -> {
            undoRedoFrame.saveState();
            try {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(this, "Please select an employee to view warehouses.");
                    return;
                }

                String employeeId = employeeTableModel.getValueAt(selectedRow, 0).toString();
                viewEmployeeWareHouse(employeeId);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Center the frame on screen
        setLocationRelativeTo(null);
    }

    private void saveAllRowsToDatabase() throws SQLException {
        // Kết nối cơ sở dữ liệu
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String updateQuery = """
                MERGE INTO Employee AS Target
                USING (VALUES (?, ?, ?, ?, ?, ?, ?, ?)) AS Source (Employee_ID, First_Name, Last_Name, Employee_Name, Phone_Number, Email, Address, Hire_Date)
                ON Target.Employee_ID = Source.Employee_ID
                WHEN MATCHED THEN 
                    UPDATE SET 
                        First_Name = Source.First_Name,
                        Last_Name = Source.Last_Name,
                        Employee_Name = Source.Employee_Name,
                        Phone_Number = Source.Phone_Number,
                        Email = Source.Email,
                        Address = Source.Address,
                        Hire_Date = Source.Hire_Date
                WHEN NOT MATCHED THEN
                    INSERT (Employee_ID, First_Name, Last_Name, Employee_Name, Phone_Number, Email, Address, Hire_Date)
                    VALUES (Source.Employee_ID, Source.First_Name, Source.Last_Name, Source.Employee_Name, Source.Phone_Number, Source.Email, Source.Address, Source.Hire_Date);
        """;
    
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            // Duyệt qua tất cả các dòng trong bảng và lưu vào cơ sở dữ liệu
            for (int i = 0; i < employeeTableModel.getRowCount(); i++) {
                statement.setInt(1, Integer.parseInt(employeeTableModel.getValueAt(i, 0).toString())); // Employee_ID
                statement.setString(2, employeeTableModel.getValueAt(i, 1).toString()); // First_Name
                statement.setString(3, employeeTableModel.getValueAt(i, 2).toString()); // Last_Name
                statement.setString(4, employeeTableModel.getValueAt(i, 3).toString()); // Employee_Name
                statement.setString(5, employeeTableModel.getValueAt(i, 4).toString()); // Phone_Number
                statement.setString(6, employeeTableModel.getValueAt(i, 5).toString()); // Email
                statement.setString(7, employeeTableModel.getValueAt(i, 6).toString()); // Address
                statement.setDate(8, Date.valueOf(employeeTableModel.getValueAt(i, 7).toString())); // Hire_Date
    
                statement.addBatch();
            }
    
            // Thực thi lệnh batch để lưu tất cả các dòng
            statement.executeBatch();
        }
    }
    

    private void deleteEmployeeAndStaff(String id) throws SQLException {
        String deleteStaffQuery = "DELETE FROM Staff WHERE Staff_ID = ?";
        String deleteEmployeeQuery = "DELETE FROM Employee WHERE Employee_ID = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement deleteStaffStmt = connection.prepareStatement(deleteStaffQuery);
             PreparedStatement deleteEmployeeStmt = connection.prepareStatement(deleteEmployeeQuery)) {

            // Delete from Staff table
            deleteStaffStmt.setInt(1, Integer.parseInt(id));
            deleteStaffStmt.executeUpdate();

            // Delete from Employee table
            deleteEmployeeStmt.setInt(1, Integer.parseInt(id));
            deleteEmployeeStmt.executeUpdate();
        }
    }

    private void deleteStaffFromDatabase(String id) throws SQLException {
        String deleteQuery = "DELETE FROM Staff WHERE Staff_ID = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            // Set parameter
            preparedStatement.setInt(1, Integer.parseInt(id));

            // Execute query
            preparedStatement.executeUpdate();
        }
    }

    private void loadEmployeeData() {
        employeeTableModel.setRowCount(0); // Clear existing data
        String query = "SELECT * FROM Employee";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("Employee_ID");
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");
                String employeeName = resultSet.getString("Employee_Name");
                String phoneNumber = resultSet.getString("Phone_Number");
                String email = resultSet.getString("Email");
                String address = resultSet.getString("Address");
                Date hireDate = resultSet.getDate("Hire_Date");

                // Add row to the Employee table model
                employeeTableModel.addRow(new Object[]{id, firstName, lastName, employeeName, phoneNumber, email, address, hireDate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }
    }

    private void loadStaffData() {
        staffTableModel.setRowCount(0); // Clear existing data
        String query = """
            SELECT 
                Staff.Staff_ID AS Employee_ID, 
                Staff.Position, 
                Staff.Shift, 
                Staff.Department, 
                Staff.Hourly_Rate 
            FROM 
                Staff
        """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("Employee_ID");
                String position = resultSet.getString("Position");
                String shift = resultSet.getString("Shift");
                String department = resultSet.getString("Department");
                double hourlyRate = resultSet.getDouble("Hourly_Rate");

                // Add row to the Staff table model
                staffTableModel.addRow(new Object[]{employeeId, position, shift, department, hourlyRate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading staff data: " + e.getMessage());
        }
    }

    private void addStaffToDatabase(String employeeId) throws SQLException {
        String insertQuery = "INSERT INTO Staff (Staff_ID, Position, Shift, Department, Hourly_Rate) VALUES (?, 'Default Position', 'Day', 'Default Department', 20.00)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, Integer.parseInt(employeeId));

            // Execute query
            preparedStatement.executeUpdate();
        }
    }

    private void viewEmployeeWareHouse(String employeeId) throws SQLException {
        // Create a dialog for viewing employee details
        JDialog employeeDialog = new JDialog(this, "Warehouses for Employee ID: " + employeeId, true);
        employeeDialog.setSize(800, 400);
        employeeDialog.setLocationRelativeTo(this);
    
        String[] employeeColumns = {"Employee_ID", "Employee_Name", "Phone_Number", "Email", "Warehouse_Name"};
        DefaultTableModel employeeTableModel = new DefaultTableModel(employeeColumns, 0);
        JTable employeeTable = new JTable(employeeTableModel);
        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
    
        employeeDialog.add(employeeScrollPane);
    
        // Load employee details based on the provided employeeId
        String query = """
            SELECT 
                Employee.Employee_ID,
                Employee.Employee_Name,
                Employee.Phone_Number,
                Employee.Email,
                Warehouse.Warehouse_Name
            FROM Work
            INNER JOIN Employee ON Work.Employee_ID = Employee.Employee_ID
            INNER JOIN Warehouse ON Work.Warehouse_ID = Warehouse.Warehouse_ID
            WHERE Employee.Employee_ID = ?
        """;
    
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, employeeId); // Bind employeeId to the query
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                employeeTableModel.addRow(new Object[]{
                        resultSet.getInt("Employee_ID"),
                        resultSet.getString("Employee_Name"),
                        resultSet.getString("Phone_Number"),
                        resultSet.getString("Email"),
                        resultSet.getString("Warehouse_Name")
                });
            }
    
            // If no results, show a message
            if (employeeTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data found for Employee ID: " + employeeId);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }
    
        employeeDialog.setVisible(true);
    }
    


    //TEST FRAME
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WorkerManagementFrame manageUserFrame = new WorkerManagementFrame();
            manageUserFrame.setVisible(true);
        });
    }
}
