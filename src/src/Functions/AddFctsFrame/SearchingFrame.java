package src.Functions.AddFctsFrame;

import src.Functions.addFuctions.Searching;

import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SearchingFrame extends JFrame {

    private final JTextField searchTextField;
    private final JTable customerTable;
    private final DefaultTableModel customerTableModel;
    private final JTable resultTable; 
    private final DefaultTableModel resultTableModel; 
    private int lastFoundIndex = -1; 

    public SearchingFrame(DefaultTableModel customerTableModel, JTable customerTable) {
        this.customerTableModel = customerTableModel;
        this.customerTable = customerTable;
    
        setTitle("Search Frame");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    
        // Create a panel for the search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
    
        // Add search text label and text field
        JLabel searchLabel = new JLabel("Find What:");
        searchPanel.add(searchLabel);
    
        searchTextField = new JTextField(20);
        searchPanel.add(searchTextField);
    
        // Add search panel to frame
        add(searchPanel, BorderLayout.NORTH);
    
        // Create result table to display search results
        Vector<String> columnIdentifiers = new Vector<>();
        for (int i = 0; i < customerTableModel.getColumnCount(); i++) {
            columnIdentifiers.add(customerTableModel.getColumnName(i));
        }
        resultTableModel = new DefaultTableModel(columnIdentifiers, 0);
        resultTable = new JTable(resultTableModel);
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        add(resultScrollPane, BorderLayout.CENTER);
    
        // Add button panel for options
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
    
        JButton findAllButton = new JButton("Find All");
        JButton exploreNextButton = new JButton("Explore Next");
        JButton closeButton = new JButton("Close");
    
        buttonPanel.add(findAllButton);
        buttonPanel.add(exploreNextButton);
        buttonPanel.add(closeButton);
    
        add(buttonPanel, BorderLayout.SOUTH);
    
        // Action listener for Find All button
        findAllButton.addActionListener(e -> {
            String keyword = searchTextField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search keyword.");
                return;
            }
    
            Object[][] tableData = getTableData();
            List<Object[]> results = Searching.linearSearch(tableData, keyword);
    
            resultTableModel.setRowCount(0); // Clear previous results
    
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matches found for: " + keyword);
            } else {
                for (Object[] row : results) {
                    resultTableModel.addRow(row);
                }
                JOptionPane.showMessageDialog(this, "Found " + results.size() + " matching rows.");
            }
        });
    
        // Action listener for Explore Next button
        exploreNextButton.addActionListener(e -> {
            String keyword = searchTextField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search keyword.");
                return;
            }
    
            Object[][] tableData = getTableData();
            int totalRows = tableData.length;
    
            for (int i = lastFoundIndex + 1; i < totalRows; i++) {
                for (Object cell : tableData[i]) {
                    if (cell != null && cell.toString().toLowerCase().contains(keyword.toLowerCase())) {
                        customerTable.setRowSelectionInterval(i, i);
                        lastFoundIndex = i;
                        JOptionPane.showMessageDialog(this, "Found at row: " + (i + 1));
                        return;
                    }
                }
            }
    
            JOptionPane.showMessageDialog(this, "No more matches found.");
            lastFoundIndex = -1; // Reset for future searches
        });
    
        closeButton.addActionListener(e -> dispose());
    }
    

    public JButton createSearchButton(JTable customerTable, DefaultTableModel customerTableModel) {
        ImageIcon searchIcon = new ImageIcon(new ImageIcon("Images/Searching_Icon.png")
                .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        JButton searchButton = new JButton(searchIcon);
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.setToolTipText("Search Customers");
        searchButton.setBorderPainted(false); 
        searchButton.setContentAreaFilled(false); 
        searchButton.setFocusPainted(false);
    
        // Thêm ActionListener để mở cửa sổ SearchingFrame
        searchButton.addActionListener(e -> {
            SearchingFrame searchingFrame = new SearchingFrame(customerTableModel, customerTable);
            searchingFrame.setVisible(true);
        });
    
        return searchButton;
    }
    

    private Object[][] getTableData() {
        int rowCount = customerTableModel.getRowCount();
        int colCount = customerTableModel.getColumnCount();
        Object[][] data = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i][j] = customerTableModel.getValueAt(i, j);
            }
        }
        return data;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SearchingFrame(null, null); // For standalone testing
        });
    }
}
