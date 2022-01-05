package io.github.toberocat.core.utility.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toberocat.MainIF;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class JsonUtility {

    private static ObjectMapper om = new ObjectMapper();

    public static boolean SaveObject(File file, Object object) {
        try {
            om.writerWithDefaultPrettyPrinter().writeValue(file, object);
            return true;
        } catch (IOException e) {
            if (MainIF.getConfigManager().getValue("general.printStacktrace")) e.printStackTrace();
            MainIF.getIF().SaveShutdown(e.getMessage());            return false;
        }
    }

    public static String SaveObject(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (IOException e) {
            if (MainIF.getConfigManager().getValue("general.printStacktrace")) e.printStackTrace();
            MainIF.getIF().SaveShutdown(e.getMessage());
            return "";
        }
    }

    public static Object ReadObject(String object, Class clazz) {
        try {
            return om.readValue(object, clazz);
        } catch (IOException e) {
            if (MainIF.getConfigManager().getValue("general.printStacktrace")) e.printStackTrace();
            MainIF.getIF().SaveShutdown(e.getMessage());
        }
        return null;
    }
    public static Object ReadObject(File file, Class clazz) {
        try {
            return om.readValue(file, clazz);
        } catch (IOException e) {
            if (MainIF.getConfigManager().getValue("general.printStacktrace")) e.printStackTrace();
            MainIF.getIF().SaveShutdown(e.getMessage());
        }
        return null;
    }

    public static Object ReadObjectFromURL(URL url, Class clazz) {
        try {
            return om.readValue(url, clazz);
        } catch (IOException e) {
            if (MainIF.getConfigManager().getValue("general.printStacktrace")) e.printStackTrace();
            MainIF.getIF().SaveShutdown(e.getMessage());
        }
        return null;
    }
}