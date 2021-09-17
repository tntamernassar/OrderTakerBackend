package Services.Network;

import Logic.NetworkNotifications.NetworkNotification;
import Services.Constants;
import Services.Utils;


import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class NetworkAdapter{

    private static NetworkAdapter instance;
    public static NetworkAdapter getInstance(){
        return instance;
    }

    public static void init(NetworkAdapter adapter){
        instance = adapter;
    }

    private TCPHandler tcpHandler;
    private UDPHandler udpHandler;

    LinkedList<NetworkObserver> observers;

    CopyOnWriteArrayList<ConnectionHandler> connections;

    public NetworkAdapter(){
        this.observers = new LinkedList<>();
        this.connections = new CopyOnWriteArrayList<>();
    }

    public synchronized void cleanConnections(){
        CopyOnWriteArrayList<ConnectionHandler> connectionHandlers = new CopyOnWriteArrayList<>();
        for(ConnectionHandler connectionHandler: this.connections){
            if (connectionHandler.isAlive()){
                connectionHandlers.add(connectionHandler);
            }
        }

        this.connections = connectionHandlers;
    }

    public abstract void onConnectionEstablished(ConnectionHandler connectionHandler);

    public void start(){
        this.tcpHandler = new TCPHandler(Constants.TCP_PORT) {
            /**
             * Logically, this triggers whenever other tablet want to connect for notifying
             * so there is no need to hold the connection handler in the connections list
             * since a connectionHandler for this should have been stored in the connections list while responding
             * to ConnectMe notification from the same client
             * **/
            @Override
            public void onConnectionRequest(Socket socket) {
                ConnectionHandler connectionHandler = makeConnection(socket);
                if(connectionHandler != null){
//                    connections.add(connectionHandler);
                    onConnectionEstablished(connectionHandler);
                }
            }
        };

        this.udpHandler = new UDPHandler(Constants.UDP_MULTICAST_HOST, Constants.UDP_PORT) {
            @Override
            public void onNotification(InetAddress address, NetworkNotification notification) {
                if(! notification.ignore()){
                    notifyUDPObservers(address, notification);
                }
            }
        };

        this.tcpHandler.start();
        this.udpHandler.start();
    }

    public boolean sendUDP(InetAddress address, NetworkNotification notification){
        return this.udpHandler.send(address, notification);
    }

    public synchronized boolean sendTCPToAll(NetworkNotification notification){
        if(connections.size() > 0) {
            Utils.writeToLog("Sending TCP message to " + this.connections.size() + " Connection");
            boolean success = true;
            for (ConnectionHandler connectionHandler : this.connections) {
                if (connectionHandler.isAlive()) {
                    success &= connectionHandler.send(notification);
                    if (!success) {
                        Utils.writeToLog("Can't send to " + connectionHandler.socket.getInetAddress().getHostAddress());
                    }
                }
            }
            return success;
        }else{
            return false;
        }
    }

    public boolean multicast(NetworkNotification notification){
        return this.udpHandler.multiCast(notification, Constants.UDP_REPEAT);
    }

    private ConnectionHandler makeConnection(Socket socket){
        ConnectionHandler handler = new ConnectionHandler(socket) {
            @Override
            public void onNotification(NetworkNotification notification) {
                if(!notification.ignore()) {
                    notifyTCPObservers(this, notification);
                }
            }
        };
        handler.start();
        return handler;
    }

    public synchronized ConnectionHandler connect(InetAddress address, int port){
        Socket socket = this.tcpHandler.connect(address, port);
        if(socket != null){
            ConnectionHandler connectionHandler = makeConnection(socket);
            connections.add(connectionHandler);
            return connectionHandler;
        }else {
            return null;
        }
    }

    public void register(NetworkObserver observer){
        this.observers.add(observer);
    }

    public void notifyUDPObservers(InetAddress address, NetworkNotification notification){
        for(NetworkObserver observer : this.observers){
            observer.UDPNotification(address, notification);
        }
    }

    public void notifyTCPObservers(ConnectionHandler connectionHandler, NetworkNotification notification){
        for(NetworkObserver observer : this.observers){
            observer.TCPNotification(connectionHandler, notification);
        }
    }

}
