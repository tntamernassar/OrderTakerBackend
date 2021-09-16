package Services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager {

    public synchronized static boolean writeObject(Object object, String path){
        try {

            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            objectOut.close();
            fileOut.close();
            return true;
        } catch (Exception e) {
            Utils.writeToLog("[ERROR] in FileManager.writeObject " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Object readObject(String path){
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream stream = new ObjectInputStream(fileIn);
            Object o = stream.readObject();
            fileIn.close();
            stream.close();
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean appendToFile(String path, String logEvent){
        try {
            Files.write(Paths.get(path), logEvent.getBytes(), StandardOpenOption.APPEND);
            return true;
        }catch (IOException e) {
            return false;
        }
    }
}
