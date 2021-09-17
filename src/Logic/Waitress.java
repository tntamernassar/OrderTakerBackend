package Logic;

import Logic.NetworkNotifications.NetworkNotification;
import Services.Constants;
import Services.FileManager;
import Services.Network.ConnectionHandler;
import Services.Network.NetworkAdapter;
import Services.Network.NetworkObserver;
import Services.OrderDistribution.OrderDistributionRequest;
import Services.OrderDistribution.OrderDistributor;
import Services.Utils;

import java.net.InetAddress;
import java.util.LinkedList;

public abstract class Waitress implements NetworkObserver {


    private Restaurant restaurant;
    private boolean isSyncing;
    private NetworkAdapter networkAdapter;
    private String name;
    private LinkedList<Integer> changedTables;
    /**
     *  Waitress class is responsible for the interaction
     *  with the restaurant tables
     *  the functionalities that you can ask a waitress in a restaurant should be here
     * **/
    public Waitress(String name, Restaurant restaurant){
        this.restaurant = restaurant;
        this.isSyncing = true;
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        doneSyncing();
                    }
                },
                Constants.SYNCING_TIME
        );
        this.name = name;
        this.changedTables = new LinkedList<>();
    }

    public void setNetworkAdapter(NetworkAdapter networkAdapter) {
        this.networkAdapter = networkAdapter;
    }


    public String getName() {
        return name;
    }

    public LinkedList<Integer> getChangedTables() {
        return changedTables;
    }

    public void setChangedTables(LinkedList<Integer> changedTables) {
        this.changedTables = changedTables;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
    public NetworkAdapter getNetworkAdapter() {
        return networkAdapter;
    }

    public boolean isSyncing() {
        return isSyncing;
    }
    public void doneSyncing(){
        this.isSyncing = false;
    }

    private void tableChanged(int table){
        FileManager.writeObject(this.getRestaurant(), Constants.RESTAURANT_STATE_FILE);
        if(!changedTables.contains(table)){
            changedTables.push(table);
        }
    }
    /**
     * open the table {@param table}
     *
     * label the table as opened and notify rest of the observers
     * **/
    public void openTable(int table){
        Table theTable = restaurant.getTable(table);
        if (!theTable.isActive()){
            theTable.startOrder(getName());
            theTable.nextVersion();
            tableChanged(table);
            Utils.writeToLog("Opened Table " + table);
        }else {
            Utils.writeToLog("Tried to open Table " + table + " While it Was opened");
        }
    }

    /**
     * order the given item for table {@param table}
     *
     * add the given item to the table order
     * if the table is not active, then treat it as the first order and start and order
     *
     * returns the index of the item in the order
     * **/
    public int orderItem(int table, Product product, int quantity, String notes){
        if(!this.restaurant.getTable(table).isActive()){
            this.restaurant.getTable(table).startOrder(getName());
        }
        Utils.writeToLog("Table " + table + " Ordered Item : name : " + product.getName() + " quantity : " + quantity + " notes : " + notes);
        return this.restaurant.getTable(table).getCurrentOrder().addItem(this.getName(), product, quantity, notes);
    }


    /**
     * remove the item with index {#param orderItem} from the order of  {@param table}
     *
     * if the table is active remove the given item from the order*
     * **/
    public void removeItem(int table, int orderItem){
        Table theTable = restaurant.getTable(table);
        if(theTable.isActive()){
            theTable.getCurrentOrder().removeItem(orderItem);
        }
        Utils.writeToLog("Table " + table + " Removed Item number : " + orderItem);
    }

    /**
     * edit the given item index in the order of table {@param table}
     *
     * set the item with index {@param orderItem} to be
     * the item given in the 3-most right params
     * **/
    public void editOrder(int table, int orderItem, Product product, int quantity, String notes){
        restaurant.getTable(table).getCurrentOrder().editOrder(orderItem, product, quantity, notes);
        Utils.writeToLog("Table " + table + " Edited Item number : " + orderItem + " new name : " + product.getName() + " new quantity : " + quantity + " new notes : " + notes);
    }

    /**
     * submits order of table {@param table}
     *
     * notify rest of observers for table changes
     * update local config
     *
     * if the order is not distributed yet
     * distribute the order over OrderDistributor, mark as distributed and set the distribution version
     *
     * if the order is already distribute,
     * distribute an edit with the last and new versions
     *
     * returns the submitted order if table is active, otherwise null
     * **/
    public Order submitOrder(int table){
        if(restaurant.getTable(table).isActive()){
            Table theTable = restaurant.getTable(table);
            Order currentOrder = theTable.getCurrentOrder();
            if(currentOrder.getOrderItems().size() > 0){
                theTable.nextVersion();
                if(currentOrder.isDistributed()){
                    // the order already distributed
                    Order distributedVersion = currentOrder.getDistributeVersion();
                    OrderDistributor.distributeEdit(distributedVersion, new OrderDistributionRequest(table, currentOrder));
                    currentOrder.setDistributeVersion(currentOrder.clone());
                    Utils.writeToLog("Table " + table + " ReSubmitted an order");
                }else{
                    // this is the first order from this table
                    OrderDistributor.distribute(new OrderDistributionRequest(table, currentOrder));
                    currentOrder.setDistributed(true);
                    currentOrder.setDistributeVersion(currentOrder.clone());
                    Utils.writeToLog("Table " + table + " Submitted the order");
                }
                currentOrder.distributeItems();
                tableChanged(table);
                return currentOrder;
            }else{
                Utils.writeToLog("Order contains no item");
                return null;
            }
        }else{
            Utils.writeToLog("Table " + table + " Tried to submit a closed table !");
            return null;
        }
    }


    /**
     * close order of table {@param table}
     *
     * notify rest of observers for table changes
     *
     * close the order of the table
     * and add the order to the restaurant order history
     *
     * returns the closed order if table is active, otherwise null
     * **/
    public Order closeOrder(int table){
        Table theTable = restaurant.getTable(table);
        if(theTable.isActive()){
            if(theTable.getCurrentOrder().getOrderItems().size() > 0){
                boolean added = this.restaurant.getOrderHistory().addToHistory(table, theTable.getCurrentOrder());
                if(added) {
                    this.restaurant.getOrderHistory().write();
                }
            }
            Order closedOrder = theTable.closeOrder();
            theTable.nextVersion();
            tableChanged(table);
            Utils.writeToLog("Table " + table + " Closed the order");
            return closedOrder;
        }else{
            Utils.writeToLog("Table " + table + " Tried to close a closed table");
            return null;
        }
    }


    /**
     * cancel the order of table {@param table}
     *
     * notify rest of observers for table changes
     *
     * close the order of the table without saving it to history
     *
     * returns the canceled order if table is active, otherwise null
     * **/
    public Order cancelOrder(int table){
        Table theTable = restaurant.getTable(table);
        if(restaurant.getTable(table).isActive()){
            Order closedOrder = theTable.closeOrder();
            theTable.nextVersion();
            tableChanged(table);
            Utils.writeToLog("Table " + table + " Canceled the order");
            return closedOrder;
        }else{
            Utils.writeToLog("Table " + table + " Tried to cancel a closed table");
            return null;
        }
    }

    @Override
    public void UDPNotification(InetAddress address, NetworkNotification notification) {
        if(!notification.getSenderName().equals(getName())){ // prevent loopback
            notification.visitUDP(address,this);
            onUDPNotification(address, notification);
        }
    }

    @Override
    public void TCPNotification(ConnectionHandler handler, NetworkNotification notification) {
        if(!notification.getSenderName().equals(getName())){ // prevent loopback
            notification.visitTCP(handler,this);
            onTCPNotification(handler, notification);
        }
    }

    public abstract void onUDPNotification(InetAddress address, NetworkNotification notification);
    public abstract void onTCPNotification(ConnectionHandler handler, NetworkNotification notification);
}
