package src.Functions.addFuctions;

import java.util.Stack;
import javax.swing.table.DefaultTableModel;

public class UndoRedo {
    private final Stack<Object[][]> undoStack = new Stack<>();
    private final Stack<Object[][]> redoStack = new Stack<>();

    public void saveState(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();
        Object[][] tableData = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                tableData[i][j] = model.getValueAt(i, j);
            }
        }
        undoStack.push(tableData);
        redoStack.clear(); 
    }

    public Object[][] undo(DefaultTableModel model) {
        if (!undoStack.isEmpty()) {
            Object[][] currentState = getCurrentState(model);
            redoStack.push(currentState);
            return undoStack.pop();
        } else {
            return null;
        }
    }

    public Object[][] redo(DefaultTableModel model) {
        if (!redoStack.isEmpty()) {
            Object[][] currentState = getCurrentState(model);
            undoStack.push(currentState);
            return redoStack.pop();
        } else {
            return null;
        }
    }

    private Object[][] getCurrentState(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();
        Object[][] currentState = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                currentState[i][j] = model.getValueAt(i, j);
            }
        }
        return currentState;
    }
}