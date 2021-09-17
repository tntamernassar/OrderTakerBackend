package Services.Network;

import Logic.NetworkNotifications.HealthCheck;
import Logic.NetworkNotifications.NetworkNotification;
import Services.Constants;
import Services.Utils;


import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
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

    ConcurrentHashMap<String, ConnectionHandler> connections;
    CopyOnWriteArrayList<ConnectionHandler> readOnlyConnections;

    public NetworkAdapter(){
        this.observers = new LinkedList<>();
        this.connections = new ConcurrentHashMap<>();
        readOnlyConnections = new CopyOnWriteArrayList<>();
    }

    public synchronized void cleanConnections(){
        CopyOnWriteArrayList<ConnectionHandler> activeConnections = new CopyOnWriteArrayList<>();
        for(ConnectionHandler connectionHandler: this.readOnlyConnections){
            if(!connectionHandler.isRunning()){
                connectionHandler.close();
            }else{
                activeConnections.add(connectionHandler);
            }
        }
        this.readOnlyConnections = activeConnections;

        for(String name: this.connections.keySet()){
            ConnectionHandler connectionHandler = this.connections.get(name);
            if (!connectionHandler.isRunning()){
                connectionHandler.close();
                this.connections.remove(name);
            }else if(!connectionHandler.send(new HealthCheck())){
                this.connections.remove(name);
                connectionHandler.close();
            }
        }
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
                    readOnlyConnections.add(connectionHandler);
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

    public ConcurrentHashMap<String, ConnectionHandler> getConnections() {
        return connections;
    }

    public boolean sendUDP(InetAddress address, NetworkNotification notification){
        return this.udpHandler.send(address, notification);
    }

    public synchronized boolean sendTCPToAll(NetworkNotification notification){
        if(connections.size() > 0) {
            Utils.writeToLog("Sending TCP message to " + this.connections.size() + " Connection");
            boolean success = true;
            for (String name : this.connections.keySet()) {
                ConnectionHandler connectionHandler = this.connections.get(name);
                if (connectionHandler.isRunning()) {
                    success &= connectionHandler.send(notification);
                    if (!success) {
                        Utils.writeToLog("Can't send to " + connectionHandler.socket.getInetAddress().getHostAddress());
                        this.connections.remove(name);
                        connectionHandler.close();
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

    public synchronized ConnectionHandler connect(String name, InetAddress address, int port){
        Socket socket = this.tcpHandler.connect(address, port);
        if(socket != null){
            ConnectionHandler connectionHandler = makeConnection(socket);
            connections.put(name, connectionHandler);
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
