package Services;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class Utils {

    public static String MapToString(Map<Integer, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{\n");
        for (Integer key : map.keySet()) {
            mapAsString.append("\t"+ key + "=" + map.get(key).toString() + ", \n");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("\n}");
        return mapAsString.toString();
    }

    public static String dateToString(LocalDateTime date){
        return date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear() + " " +
                date.getHour() + ":" + date.getMinute();
    }

    public static LocalDateTime stringToDate(String date){
        String[] comps = date.split(" ");
        String[] day_month_year = comps[0].split("-");
        String[] hour_minute = comps[1].split(":");
        return LocalDateTime.of(
                Integer.parseInt(day_month_year[2]),
                Integer.parseInt(day_month_year[1]),
                Integer.parseInt(day_month_year[0]),
                Integer.parseInt(hour_minute[0]),
                Integer.parseInt(hour_minute[1])
        );
    }

    public static boolean writeConfig(Object object){
        return FileManager.writeObject(object, Constants.CONFIG_DIR);
    }

    public static Object readRestaurant(){
        return FileManager.readObject(Constants.CONFIG_DIR);
    }

    public synchronized static boolean writeToLog(String logEvent){
        try {
            File yourFile = new File(Constants.LOG_FILE);
            yourFile.createNewFile(); // if file already exists will do nothing
            return FileManager.appendToFile(Constants.LOG_FILE, logEvent + " At " + Utils.dateToString(LocalDateTime.now()) + "\n");
        }catch (Exception e){
            return false;
        }
    }

    public static String getTimeStamp(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    /**
     * true if t1 > t2
     * **/
    public static boolean isBigger(String t1, String t2){
        Timestamp timestamp1 = new Timestamp(Long.parseLong(t1));
        Timestamp timestamp2 = new Timestamp(Long.parseLong(t1));
        return timestamp1.compareTo(timestamp2) > 0;
    }
}
