package Logic;


import Services.Utils;

import java.io.Serializable;
import java.time.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Order implements Serializable {

    private AtomicInteger itemsCounter;
    private LocalDateTime startedAt;
    private String startedBy;
    private boolean distributed;
    private Order distributeVersion;
    private HashMap<Integer, OrderItem> orderItems;


    public Order(String startedBy){
        this.startedBy = startedBy;
        this.itemsCounter = new AtomicInteger(0);
        this.distributed = false;
        this.distributeVersion = null;
        this.orderItems = new HashMap<>();
        this.startedAt = LocalDateTime.now();
    }

    public void distributeItems(){
        for(OrderItem orderItem : orderItems.values()){
            orderItem.setDistributed(true);
        }
    }

    public boolean isDistributed() {
        return distributed;
    }

    public void setDistributed(boolean distributed) {
        this.distributed = distributed;
    }

    public Order getDistributeVersion() {
        return distributeVersion;
    }

    public void setDistributeVersion(Order distributeVersion) {
        this.distributeVersion = distributeVersion;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public HashMap<Integer, OrderItem> getOrderItems() {
        return orderItems;
    }

    public int addItem(String waiterName, Product product, int quantity, String notes){
        int newItemIndex = itemsCounter.incrementAndGet();
        this.orderItems.put(newItemIndex, new OrderItem(waiterName, product, quantity, notes));
        return newItemIndex;
    }

    public int addItem(String waiterName, String timestamp, Product product, int quantity, String notes, boolean distributed){
        int newItemIndex = itemsCounter.incrementAndGet();
        this.orderItems.put(newItemIndex, new OrderItem(timestamp, waiterName, product, quantity, notes, distributed));
        return newItemIndex;
    }

    public void removeItem(int itemIndex){
        this.orderItems.remove(itemIndex);
    }

    public void editOrder(int itemIndex, Product newProduct, int newQuantity, String newNotes){
        if(this.orderItems.containsKey(itemIndex)){
            this.orderItems.get(itemIndex).setProduct(newProduct);
            this.orderItems.get(itemIndex).setQuantity(newQuantity);
            this.orderItems.get(itemIndex).setNotes(newNotes);
        }
    }

    public Order clone() {
        Order o = new Order(getStartedBy());
        for(Integer oi: this.orderItems.keySet()){
            OrderItem orderItem = this.orderItems.get(oi);
            o.orderItems.put(oi, new OrderItem(orderItem.getWaiterName(), orderItem.getProduct(), orderItem.getQuantity(), orderItem.getNotes()));
        }
        return o;
    }

    @Override
    public String toString() {
        return "Order {" + "\n\t" +
                "startedAt = " + Utils.dateToString(this.startedAt) + "\n\t" +
                "orderItems = " + orderItems + "\n" +
                '}';
    }
}
