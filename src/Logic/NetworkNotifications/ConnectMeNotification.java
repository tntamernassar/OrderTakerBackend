package Logic.NetworkNotifications;

import Logic.Waitress;
import Services.Network.ConnectionHandler;
import Services.Utils;

import java.net.InetAddress;

public class ConnectMeNotification extends NetworkNotification{

    private int port;
    public ConnectMeNotification(String SenderName, int port) {
        super(SenderName);
        this.port = port;
    }

    @Override
    public synchronized void visitUDP(InetAddress address, Waitress waitress) {
        if(!waitress.getName().equals(this.getSenderName())) {
            if(waitress.getNetworkAdapter().getConnections().containsKey(this.getSenderName())){
                ConnectionHandler current = waitress.getNetworkAdapter().getConnections().get(this.getSenderName());
                if(!current.healthCheck()){
                    current.close();
                    ConnectionHandler connectionHandler = waitress.getNetworkAdapter().connect(getSenderName(), address, this.port);
                    if(connectionHandler == null){
                        Utils.writeToLog("[ERROR] in ConnectMe.visitTCP Can't Reconnect to " + address.getHostAddress());
                    }else{
                        waitress.getNetworkAdapter().getConnections().put(this.getSenderName(), connectionHandler);
                        Utils.writeToLog("Reconnecting to dead host " + address.getHostAddress());
                    }
                }
            }else{
                ConnectionHandler connectionHandler = waitress.getNetworkAdapter().connect(getSenderName(), address, this.port);
                if(connectionHandler == null){
                    Utils.writeToLog("[ERROR] in ConnectMe.visitTCP Can't connect to " + address.getHostAddress());
                }else{
                    waitress.getNetworkAdapter().getConnections().put(this.getSenderName(), connectionHandler);
                    Utils.writeToLog("Connecting to host via ConnectMe " + address.getHostAddress());
                }
            }
        }
    }

    @Override
    public String toString() {
        return getSenderName() + " : ConnectMe on port " + this.port;
    }
}
