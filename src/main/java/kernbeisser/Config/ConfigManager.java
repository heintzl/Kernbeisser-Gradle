package kernbeisser.Config;

import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    public static final int CONFIG_FILE_INDENT_FACTOR = 2;

    private static final File file = new File("config.json");
    private static final JSONObject config = new JSONObject(fileToString(StandardCharsets.UTF_8));

    //Static only class
    private ConfigManager() {
    }

    public static JSONObject getHeader() {
        return config;
    }

    private static String fileToString(Charset charset) {
        try {
            StringBuilder sb = new StringBuilder();
            Files.readAllLines(file.toPath(), charset).forEach(sb.append("\n")::append);
            return sb.toString();
        } catch (IOException e) {
            createFileIfNotExists();
            return fileToString(charset);
        }
    }


    public static String[] getDBAccessData() {
        JSONObject obj = getDBAccess();
        return new String[]{obj.getString("URL"), obj.getString("Username"), obj.getString("Password")};
    }

    public static JSONObject getDBAccess() {
        return getConfigSub("DBAccess");
    }

    public static JSONObject getConfigSub(String subcategoryName) {
        return getHeader().getJSONObject(subcategoryName);
    }

    public static void updateFile() {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(config.toString());
            fw.close();
        } catch (IOException e) {
            createFileIfNotExists();
            updateFile();
        }
    }

    private static void createFileIfNotExists() {
        if (file.exists()) {
            return;
        }
        JSONObject object = new JSONObject();
        JSONObject dbAccess = new JSONObject();
        dbAccess.put("URL", "");
        dbAccess.put("Username", "");
        dbAccess.put("Password", "");
        JSONObject reports = new JSONObject();
        reports.put("reportDirectory", "");
        reports.put("outputDirectory", "");
        reports.put("invoiceFileName", "");
        object.put("DBAccess", dbAccess);
        object.put("Reports", reports);
        object.put("dbIsInitialized", false);
        object.put("ImagePath", "");
        try {
            if (file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(object.toString(CONFIG_FILE_INDENT_FACTOR));
                fw.close();
            }else {
                throw new IOException("ConfigManager cannot create config file at File");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Das Programm kann keine Config-Datei erstellen:\n"+e);
            e.printStackTrace();
        }

    }

    public static String getUsername() {
        return getDBAccess().getString("Username");
    }

    public static String getPassword() {
        return getDBAccess().getString("Password");
    }

    public static Path getPath(String subCategory, String key) {
        return Paths.get(getConfigSub(subCategory).getString(key));
    }

    public static Path getDirectory(String subCategory, String key) {
        Path path = getPath(subCategory, key);
        if (Files.isDirectory(path)) {
            return path;
        } else {
            throw new IllegalArgumentException(String.format("%s is not a directory", path.toString()));
        }
    }
}
