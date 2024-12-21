package src.Functions.AddFctsFrame;

import src.Functions.addFuctions.Sorting;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SortingFrame {

    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private UndoRedoFrame undoRedoFrame;

    public SortingFrame(JTable customerTable, DefaultTableModel customerTableModel, UndoRedoFrame undoRedoFrame) {
        this.customerTable = customerTable;
        this.customerTableModel = customerTableModel;
        this.undoRedoFrame = undoRedoFrame;
    }

    public void addSortingFunctionality() {
        JPopupMenu columnMenu = new JPopupMenu();
        JMenuItem sortAscMenuItem = new JMenuItem("Sort Ascending");
        JMenuItem sortDescMenuItem = new JMenuItem("Sort Descending");
        columnMenu.add(sortAscMenuItem);
        columnMenu.add(sortDescMenuItem);

        customerTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int col = customerTable.columnAtPoint(e.getPoint());
                    columnMenu.show(customerTable.getTableHeader(), e.getX(), e.getY());

                    // Xóa các ActionListener cũ
                    for (ActionListener al : sortAscMenuItem.getActionListeners()) {
                        sortAscMenuItem.removeActionListener(al);
                    }
                    for (ActionListener al : sortDescMenuItem.getActionListeners()) {
                        sortDescMenuItem.removeActionListener(al);
                    }

                    // Thêm ActionListener mới cho cột được chọn
                    sortAscMenuItem.addActionListener(event -> {
                        undoRedoFrame.saveState(); 
                        sortColumn(col, true);
                    });

                    sortDescMenuItem.addActionListener(event -> {
                        undoRedoFrame.saveState(); 
                        sortColumn(col, false);
                    });
                }
            }
        });
    }

    private void sortColumn(int columnIndex, boolean ascending) {
        Object[][] tableData = getTableData();

        Sorting.quickSort(tableData, 0, tableData.length - 1, ascending, columnIndex);

        loadState(tableData);
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

    private void loadState(Object[][] state) {
        customerTableModel.setRowCount(0);
        for (Object[] row : state) {
            customerTableModel.addRow(row);
        }
    }
}
