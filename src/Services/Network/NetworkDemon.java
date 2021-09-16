package Services.Network;

import Logic.NetworkNotifications.*;
import Logic.Table;
import Logic.Waitress;
import Services.Constants;
import Services.Utils;

import java.util.LinkedList;

public class NetworkDemon extends Thread{


    private NetworkAdapter networkAdapter;
    private Waitress waitress;

    public NetworkDemon(Waitress waitress){
        this.networkAdapter = NetworkAdapter.getInstance();
        this.waitress = waitress;
    }


    private boolean networkHealthy(){
        return this.networkAdapter.multicast(new HealthCheck());
    }

    /**
     * send connect me request
     * Sync Changed Tables
     * **/
    @Override
    public void run() {
        Utils.writeToLog("Running Demon");
        while (true){
            try{
                waitress.getNetworkAdapter().multicast(new ConnectMeNotification(waitress.getName(), Constants.TCP_PORT));
                LinkedList<Table> tables = new LinkedList<>();
                for(Integer i : waitress.getChangedTables()){
                    tables.add(waitress.getRestaurant().getTable(i));
                }
                if(tables.size() > 0){
                    waitress.getNetworkAdapter().sendTCPToAll(new SyncTablesNotification(waitress.getName(), tables));
                    waitress.setChangedTables(new LinkedList<>());
                }else{

                }
            }catch (Exception e){
                e.printStackTrace();
                Utils.writeToLog("[ERROR] in Demon.run " + e.getMessage());
            }

            try {
                sleep(Constants.DEMON_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
