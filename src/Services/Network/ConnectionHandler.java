package Services.Network;

import Logic.NetworkNotifications.HealthCheck;
import Logic.NetworkNotifications.NetworkNotification;
import Services.Constants;
import Services.Utils;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public abstract class ConnectionHandler extends Thread{

    protected Socket socket;
    private boolean running;

    public ConnectionHandler(Socket socket){
        this.socket = socket;
        this.running = true;
    }


    public boolean send(NetworkNotification notification){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(Constants.UDP_BUFFER_SIZE);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(notification);
            byte[] data = baos.toByteArray();
            socket.getOutputStream().write(data);
            return true;
        }catch (Exception e){
            Utils.writeToLog(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean healthCheck(){
        return send(new HealthCheck());
    }

    public abstract void onNotification(NetworkNotification notification);

    @Override
    public void run() {
        while (this.running){
            try{
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                NetworkNotification networkNotification = (NetworkNotification) ois.readObject();
                this.onNotification(networkNotification);
            }catch (EOFException eofException){
                this.running = false;
            }catch (Exception e){
                try{
                    socket = new Socket(socket.getInetAddress(), socket.getPort());
                }catch (Exception ex){
                    Utils.writeToLog("[ERROR] in ConnectionHandler.run " + ex.getMessage());
                    this.running = false;
                    continue;
                }
                Utils.writeToLog("Can't read from client " + e.getMessage());
            }

        }
    }

    public boolean isRunning(){
        try{
            return this.running && socket.getInetAddress().isReachable(1);
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionHandler that = (ConnectionHandler) o;
        return socket.getInetAddress().equals(that.socket.getInetAddress());
    }

    public void close(){
        try{
            this.socket.close();
        }catch (Exception e){

        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }
}
