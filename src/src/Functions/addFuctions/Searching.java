package src.Functions.addFuctions;

import java.util.ArrayList;
import java.util.List;

public class Searching {
    public static List<Object[]> linearSearch(Object[][] data, String keyword) {
        List<Object[]> results = new ArrayList<>();

        if (keyword == null || keyword.isEmpty()) {
            return results; 
        }

        for (Object[] row : data) {
            for (Object cell : row) {
                if (cell != null && cell.toString().toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(row);
                    break;
                }
            }
        }

        return results;
    }
}
