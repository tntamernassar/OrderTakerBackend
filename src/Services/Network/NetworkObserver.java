package Services.Network;

import Logic.NetworkNotifications.NetworkNotification;

import java.net.InetAddress;

public interface NetworkObserver {

    void UDPNotification(InetAddress address, NetworkNotification notification);
    void TCPNotification(ConnectionHandler handler, NetworkNotification notification);

}
