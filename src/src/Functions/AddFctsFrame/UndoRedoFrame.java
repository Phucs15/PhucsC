package src.Functions.AddFctsFrame;

import src.Functions.addFuctions.UndoRedo;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UndoRedoFrame {

    private UndoRedo undoRedoManager;
    private DefaultTableModel customerTableModel;

    public UndoRedoFrame(DefaultTableModel customerTableModel) {
        this.customerTableModel = customerTableModel;
        this.undoRedoManager = new UndoRedo();
    }

    public JButton createUndoButton() {
        // Tạo nút Undo với biểu tượng
        ImageIcon undoIcon = new ImageIcon(new ImageIcon("Images/Undo_Icon.png")
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton undoButton = new JButton(undoIcon);
        undoButton.setPreferredSize(new Dimension(30, 30));
        undoButton.setToolTipText("Undo");
        undoButton.setBorderPainted(false); 
        undoButton.setContentAreaFilled(false);
        undoButton.setFocusPainted(false); 


        // Thêm ActionListener cho nút Undo
        undoButton.addActionListener(e -> {
            Object[][] previousState = undoRedoManager.undo(customerTableModel);
            if (previousState != null) {
                loadState(previousState);
            } else {
                JOptionPane.showMessageDialog(null, "No more undo steps.");
            }
        });

        return undoButton;
    }

    public JButton createRedoButton() {
        // Tạo nút Redo với biểu tượng
        ImageIcon redoIcon = new ImageIcon(new ImageIcon("Images/Redo_Icon.png")
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton redoButton = new JButton(redoIcon);
        redoButton.setPreferredSize(new Dimension(30, 30));
        redoButton.setToolTipText("Redo");
        redoButton.setBorderPainted(false); 
        redoButton.setContentAreaFilled(false);
        redoButton.setFocusPainted(false); 

        // Thêm ActionListener cho nút Redo
        redoButton.addActionListener(e -> {
            Object[][] nextState = undoRedoManager.redo(customerTableModel);
            if (nextState != null) {
                loadState(nextState);
            } else {
                JOptionPane.showMessageDialog(null, "No more redo steps.");
            }
        });

        return redoButton;
    }

    private void loadState(Object[][] state) {
        customerTableModel.setRowCount(0);
        for (Object[] row : state) {
            customerTableModel.addRow(row);
        }
    }
    
    public void saveState() {
        undoRedoManager.saveState(customerTableModel); 
    }
    
}
