package Logic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Restaurant implements Serializable {
    private HashMap<Integer, Table> tables;
    private CopyOnWriteArrayList<Order> orderHistory;

    public Restaurant(){
        this.tables = new HashMap<>();
        this.orderHistory = new CopyOnWriteArrayList<>();
    }

    public int[] getTables() {
        int [] res = new int[tables.size()];
        int index = 0;
        for (Integer i : tables.keySet()){
            res[index++] = i;
        }
        return res;
    }

    public Table getTable(int number) {
        return tables.get(number);
    }



    public CopyOnWriteArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    public void addTable(Table table) {
        this.tables.put(table.getNumber(), table);
    }

    public void addOrderToHistory(Order order) {
        this.orderHistory.add(order);
    }
}
