package Services.Network;

import Logic.NetworkNotifications.NetworkNotification;
import Services.Constants;
import Services.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class UDPHandler extends Thread{

    private MulticastSocket socket;
    private String host;
    private int port;
    private boolean connected;
    private boolean running;
    private byte[] buf;

    public UDPHandler(String host, int port){
        this.host = host;
        this.port = port;
        this.running = true;
        this.connected = false;
        this.buf = new byte[Constants.UDP_BUFFER_SIZE];

    }


    public abstract void onNotification(InetAddress address, NetworkNotification notification);


    public boolean send(InetAddress address, NetworkNotification notification) {
        for (int i = 0; i < Constants.UDP_REPEAT; i++) {
            try {
                DatagramSocket socket = new DatagramSocket();

                ByteArrayOutputStream baos = new ByteArrayOutputStream(Constants.UDP_BUFFER_SIZE);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(notification);
                byte[] data = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(data, data.length, address, this.port);
                socket.send(packet);
                socket.close();
                baos.close();
                oos.close();
            } catch (Exception e) {
                Utils.writeToLog("[ERROR] in UDPHandler.send " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean multiCast(NetworkNotification notification, int timesToRepeat){
        for(int i = 0; i < timesToRepeat; i++){
            try{
                DatagramSocket socket = new DatagramSocket();
                InetAddress group = InetAddress.getByName(this.host);

                final ByteArrayOutputStream baos = new ByteArrayOutputStream(Constants.UDP_BUFFER_SIZE);
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(notification);
                final byte[] data = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(data, data.length, group, this.port);
                socket.send(packet);
                socket.close();
                baos.close();
                oos.close();
            }catch (Exception e){
                Utils.writeToLog("[ERROR] in UDPHandler.multicast " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        try {
            socket = new MulticastSocket(this.port);
            InetAddress group = InetAddress.getByName(this.host);
            socket.joinGroup(group);
            while (this.running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                NetworkNotification networkNotification = (NetworkNotification) ois.readObject();
                this.onNotification(packet.getAddress(), networkNotification);
                bais.close();
                ois.close();
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            this.connected = false;
        }

    }
}
