package Logic.NetworkNotifications;

public class HealthCheck extends NetworkNotification {

    public HealthCheck(){
        super("");
    }

    @Override
    public boolean ignore() {
        return true;
    }

    @Override
    public String toString() {
        return "HealthCheck";
    }
}
