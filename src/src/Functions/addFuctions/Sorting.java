package src.Functions.addFuctions;

public class Sorting {
    public static void quickSort(Object[][] data, int low, int high, boolean ascending, int columnIndex) {
        if (low < high) {
            int pi = partition(data, low, high, ascending, columnIndex);

            quickSort(data, low, pi - 1, ascending, columnIndex);
            quickSort(data, pi + 1, high, ascending, columnIndex);
        }
    }

    private static int partition(Object[][] data, int low, int high, boolean ascending, int columnIndex) {
        Object pivot = data[high][columnIndex];
        int i = (low - 1);
    
        for (int j = low; j < high; j++) {
            if (data[j][columnIndex] == null) continue; 
    
            boolean condition = ascending
                    ? (pivot == null || data[j][columnIndex].toString().compareTo(pivot.toString()) <= 0)
                    : (pivot == null || data[j][columnIndex].toString().compareTo(pivot.toString()) >= 0);
    
            if (condition) {
                i++;
                Object[] temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }
    
        Object[] temp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = temp;
    
        return i + 1;
    }
    
}