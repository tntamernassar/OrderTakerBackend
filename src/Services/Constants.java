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
    public static final int WAITRESS_MAGIC = 10000;

    /**
     * The Time Syncing process is on.
     * While syncing, the device only receive syncing responses but don't respond on requests
     * **/
    public static final int SYNCING_TIME = 10 * 1000;


    /**
     * Configuration file,
     * The Configuration file hold object files for the latest version of the restaurant workflow
     * **/
    public static String CONFIG_DIR = "/home/tamer/IdeaProjects/OrderTakerBackend/files/restaurant";

    /**
     * Log file.
     * **/
    public static String LOG_FILE = "/home/tamer/IdeaProjects/OrderTakerBackend/files/log.txt";


    /**
     * Demon Delay Time.
     * **/
    public static final int DEMON_DELAY = 10 * 1000;

    /**
     * Reconnection Delay.
     * **/
    public static final int RECONNECTION_DELAY = 15 * 1000;

}
