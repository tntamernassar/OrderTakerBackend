package Logic;

import Services.Utils;

import java.io.Serializable;
import java.sql.Timestamp;

public class OrderItem implements Serializable {

    private Product product;
    private int quantity;
    private String notes;
    private String waiterName;
    private String timestamp;
    private boolean isDistributed;

    public OrderItem(String waiterName, Product product, int quantity, String notes) {
        this.waiterName = waiterName;
        this.product = product;
        this.quantity = quantity;
        this.notes = notes;
        this.timestamp = Utils.getTimeStamp();
        this.isDistributed = false;
    }

    public OrderItem(String timestamp, String waiterName, Product product, int quantity, String notes, boolean isDistributed) {
        this.product = product;
        this.quantity = quantity;
        this.notes = notes;
        this.waiterName = waiterName;
        this.timestamp = timestamp;
        this.isDistributed = isDistributed;
    }

    public boolean isDistributed() {
        return isDistributed;
    }

    public void setDistributed(boolean distributed) {
        isDistributed = distributed;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "OrderItem {" + "\n\t" +
                "waiter = " + waiterName +"\n\t" +
                "timestamp = " + timestamp +"\n\t" +
                "product = " + product +"\n\t" +
                "quantity = " + quantity +"\n\t" +
                "notes = " + notes  +"\n" +
                "}\n";
    }
}
