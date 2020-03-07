package kernbeisser.Config;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;

public class ConfigManager{
    //Static only class
    private ConfigManager(){}

    private static final File file = new File("config.json");
    private static final JSONObject config = new JSONObject(fileToString(StandardCharsets.UTF_8));

    public static JSONObject getHeader(){
        return config;
    }

    private static String fileToString(Charset charset) {
        try {
            createFileIfNotExists();
            StringBuilder sb = new StringBuilder();
            Files.readAllLines(file.toPath(), charset).forEach(sb.append("\n")::append);
            return sb.toString();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getDBAccessData(){
        JSONObject obj = getDBAccess();
        return new String[]{obj.getString("URL"),obj.getString("Username"),obj.getString("Password")};
    }

    public static JSONObject getDBAccess(){
        return getHeader().getJSONObject("DBAccess");
    }

    public static void updateFile(){
        createFileIfNotExists();
        if(!file.delete())return;
        try {
            if(file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(config.toString());
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFileIfNotExists(){
        if(file.exists())return;
        JSONObject object = new JSONObject();
        JSONObject dbAccess = new JSONObject();
        dbAccess.put("URL","");
        dbAccess.put("Username","");
        dbAccess.put("Password","");
        object.put("DBAccess",dbAccess);
        object.put("dbIsInitialized",false);
        object.put("ImagePath","");
        try {
            if(file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(object.toString());
                fw.close();
            }
        }catch (IOException e){
            file.delete();
            e.printStackTrace();
        }

    }
}
