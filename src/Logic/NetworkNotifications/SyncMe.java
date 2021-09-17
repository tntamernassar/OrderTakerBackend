package Logic.NetworkNotifications;

import Logic.Table;
import Logic.Waitress;
import Services.Network.ConnectionHandler;

import java.util.LinkedList;

public class SyncMe extends NetworkNotification{

    public SyncMe(String SenderName) {
        super(SenderName);
    }


    @Override
    public void visitTCP(ConnectionHandler handler, Waitress waitress) {
        LinkedList<Table> tables = new LinkedList<>();
        for (Integer t : waitress.getRestaurant().getTables()) {
            tables.add(waitress.getRestaurant().getTable(t));
        }
        handler.send(new SyncTablesNotification(this.getSenderName(), tables));
    }

    @Override
    public String toString() {
        return "Sync me from " + this.getSenderName();
    }
}
