package Logic.NetworkNotifications;

import Logic.OrderItem;
import Logic.Table;
import Logic.Waitress;
import Services.Network.ConnectionHandler;

import java.util.LinkedList;


public class SyncTablesNotification extends NetworkNotification{

    private LinkedList<Table> tables;

    public SyncTablesNotification(String SenderName, LinkedList<Table> tables){
        super(SenderName);
        this.tables = tables;
    }


    /**
     * Accepted Initial Sync Table Response notification over TCP
     *
     * update the tables that have higher version than what is defined
     * * **/
    @Override
    public synchronized void visitTCP(ConnectionHandler handler, Waitress waitress) {
        for(Table networkTable : this.tables){
            Table localTable = waitress.getRestaurant().getTable(networkTable.getNumber());

            if(localTable.isActive() && networkTable.isActive()){
                for(Integer io : networkTable.getCurrentOrder().getOrderItems().keySet()){
                    OrderItem networkOrderItem = networkTable.getCurrentOrder().getOrderItems().get(io);
                    if(!localTable.containsOrder(networkOrderItem.getWaiterName(), networkOrderItem.getTimestamp())){
                        localTable.getCurrentOrder().addItem(networkOrderItem.getWaiterName(), networkOrderItem.getTimestamp(), networkOrderItem.getProduct(), networkOrderItem.getQuantity(), networkOrderItem.getNotes(), networkOrderItem.isDistributed());
                        System.out.println("Adding " + networkOrderItem.getProduct());
                    }else{
                        // update current item
                        OrderItem localOrderItem = localTable.getOrderItem(networkOrderItem.getWaiterName(), networkOrderItem.getTimestamp());

                        localOrderItem.setQuantity(networkOrderItem.getQuantity());
                        localOrderItem.setProduct(networkOrderItem.getProduct());
                        localOrderItem.setNotes(networkOrderItem.getNotes());
                        localOrderItem.setDistributed(networkOrderItem.isDistributed());
                    }
                }
            }else {
                waitress.getRestaurant().addTable(networkTable);
            }

        }
        waitress.doneSyncing();
    }

    @Override
    public String toString() {
        return "SyncTablesNotification for tables";
    }
}
