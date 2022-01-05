package io.github.toberocat.core.utility.data;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.json.JsonUtility;
import io.github.toberocat.core.utility.sql.MySQL;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DataAccess {
    private static MySQL sql;

    public static boolean init() {
        if (MainIF.getConfigManager().getValue("general.useSQL")) {
            sql = new MySQL();

            try {
                sql.connect();
            } catch (SQLException | ClassNotFoundException e) {
                MainIF.getIF().SaveShutdown("&cDatabase not connected. Please check if your login information are right");
                return false;
            }

            if (!sql.isConnected()) return false;

            MainIF.LogMessage(Level.INFO, "&aSuccessfully &fconnected to database");
        } else {
            String defPath = MainIF.getIF().getDataFolder().getPath() + "/";
            Utility.mkdir(defPath + "Data/Factions");
            Utility.mkdir(defPath + "Data/History");
            Utility.mkdir(defPath + "Data/Chunks");
            Utility.mkdir(defPath + "Data/Players");

        }
        return true;
    }

    public static String getRawFile(String folder, String filename, Class clazz) {
        if (sql != null) {
            //ToDo: Add the method for storing the file in mySQL
            return "";
        } else {
            String filePath = MainIF.getIF().getDataFolder().getPath() + "/Data/" + folder + "/" + filename;
            File file = new File(filePath);

            try {
                return Files.asCharSource(file, Charsets.UTF_8).read();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void clearFolder(String folder) {
        if (sql != null) {
            //ToDo: Add the method for clearing the file in mySQL
        } else {
            for (File file : new File(
                    MainIF.getIF().getDataFolder().getPath() + "/Data/" + folder).listFiles()) {
                file.delete();
            }
        }
    }

    public static <T> T getFile(String folder, String filename, Class clazz) {
        if (sql != null) {
            //ToDo: Add the method for storing the file in mySQL
            return null;
        } else {
            String filePath = MainIF.getIF().getDataFolder().getPath() + "/Data/" + folder + "/" + filename + ".json";
            File file = new File(filePath);

            return (T) JsonUtility.ReadObject(file, clazz);
        }
    }

    public static void disable() {
        if (sql != null) sql.disconnect();
    }

    public static <T> boolean AddFile(String folder, String filename, T object) {
        if (sql != null) {
            //ToDo: Add the method for storing the file in mySQL
            return true;
        } else {
            String filePath = MainIF.getIF().getDataFolder().getPath() + "/Data/" + folder + "/" + filename + ".json";
            File file = new File(filePath);

            return JsonUtility.SaveObject(file, object);
        }
    }

    public static String[] listFiles(String folder) {
        if (sql != null) {
            // ToDO: Add method for sql table listing
            return new String[] {""};
        } else {
            String filePath = MainIF.getIF().getDataFolder().getPath() + "/Data/" + folder;
            File file = new File(filePath);

            File[] listed =file.listFiles();
            String[] fileNames = new String[listed.length];

            for (int i = 0; i < listed.length; i++) {
                fileNames[i] = listed[i].getName();
            }

            return fileNames;
        }
    }

    public static boolean exists(String folder, String filename) {
        if (sql != null) {
            return false;
        } else {
            String filePath = MainIF.getIF().getDataFolder().getPath() + "/Data/"
                    + folder + "/" + filename + ".json";
            File file = new File(filePath);

            return file.exists();
        }
    }

    public void emptyTable() {
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("TRUNCATE mobpoints");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(UUID uuid) {
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("DELETE FROM mobpoints WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPoints(UUID uuid, int points) {
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("UPDATE mobpoints SET POINTS=? WHERE UUID=?");
            ps.setInt(1, points);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getPoints(UUID uuid) {
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT POINTS FROM mobpoints WHERE  UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int points = 0;
            if (rs.next()) {
                points = rs.getInt("POINTS");
                return points;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
