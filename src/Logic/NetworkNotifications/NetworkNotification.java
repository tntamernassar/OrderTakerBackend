package Logic.NetworkNotifications;

import Logic.Waitress;
import Services.Network.ConnectionHandler;

import java.io.Serializable;
import java.net.InetAddress;

public class NetworkNotification implements Serializable {

    private String SenderName;

    public NetworkNotification(String SenderName){
        this.SenderName = SenderName;
    }

    public String getSenderName() {
        return SenderName;
    }


    /**
     *
     * {@param address}: The InetAddress of the sender
     * {@param waitress}: The system waitress for visitor pattern
     *
     * **/
    public void visitUDP(InetAddress address, Waitress waitress){}

    public void visitTCP(ConnectionHandler handler, Waitress waitress){}

    public void print() {
        System.out.println(this.toString());
    }
}
