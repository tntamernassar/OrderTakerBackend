package Services;

public class Constants {


    /**
     * UDP BUFFER SIZE
     * **/
    public static final int UDP_BUFFER_SIZE = 10000;

    /**
     * UDP MultiCast Host
     * **/
    public static final String UDP_MULTICAST_HOST = "230.0.0.0";

    /**
     * UDP port
     * **/
    public static final int UDP_PORT = 4446;

    /**
     * UDP repeating time,
     * While udp is not guarantying that the message will be received,
     * we send the message multiple time to lower the chances for loss
     * we assume that the receiver can deal with repeated messages with the version algorithm for example
     * **/
    public static final int UDP_REPEAT = 3;


    /**
     * TCP PORT
     * **/
    public static int TCP_PORT = 2000;


    /**
     * The Time Syncing process is on.
     * While syncing, the device only receive syncing responses but don't respond on requests
     * **/
    public static final int SYNCING_TIME = 30 * 1000;


    /**
     * Log file.
     * **/
    public static String LOG_FILE = "/home/tamer/IdeaProjects/OrderTakerBackend/files/log.txt";

    /**
     * Order History File,
     * The Order History File holds all the orders from all the devices
     * **/
    public static String ORDER_HISTORY_FILE = "/home/tamer/IdeaProjects/OrderTakerBackend/files/orders";


    /**
     * Demon Delay Time.
     * **/
    public static final int DEMON_DELAY = 5 * 1000;


}
