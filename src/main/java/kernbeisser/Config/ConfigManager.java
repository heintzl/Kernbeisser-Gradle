package kernbeisser.Config;

import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
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
    private ConfigManager() {}

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

    public static File getCatalogFile(){
        return getFile(getHeader(),"CatalogSource");
    }

    public static String getCatalogInfoLine(){
        try {
            FileInputStream fis = new FileInputStream(getCatalogFile());
            BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
            String infoLine = dis.readLine();
            fis.close();
            dis.close();
            return infoLine;
        } catch (IOException e) {
            Tools.showUnexpectedErrorWarning(e);
        }
        return null;
    }

    private static void createFileIfNotExists() {
        if (file.exists()) {
            return;
        }
        try {
            if (file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(getPattern().toString(CONFIG_FILE_INDENT_FACTOR));
                fw.close();
            }else {
                throw new IOException("ConfigManager cannot create config file at File");
            }
        } catch (IOException e) {
            Main.logger.error("Cannot create config file at "+file.getAbsolutePath());
            JOptionPane.showMessageDialog(null,"Das Programm kann keine Config-Datei erstellen:\n"+e);
            Tools.showUnexpectedErrorWarning(e);
        }

    }

    public static JSONObject getPattern(){
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
        object.put("CatalogSource","");
        object.put("dbIsInitialized", false);
        object.put("ImagePath", "");
        return object;
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

    public static File getFile(JSONObject parent,String key){
        String fileData = parent.getString(key);
        File relative = new File(file.getAbsoluteFile().getParentFile(),fileData);
        if(relative.exists())return relative;
        File absolute = new File(fileData);
        if(absolute.exists()) return absolute;
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle(key+" Dateipfad auswählen");
        jFileChooser.showOpenDialog(null);
        File out = jFileChooser.getSelectedFile().getAbsoluteFile();
        parent.put(key,out.getAbsolutePath());
        updateFile();
        return out;
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
