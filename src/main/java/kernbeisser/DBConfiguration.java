package kernbeisser;

import kernbeisser.StartUp.DataSourceSelector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBConfiguration{
    private static Map<String,String> properties = new HashMap<>();
    static {
        File dir = new File("src/main/resources/DBConfiguration");
        if(!dir.exists()) dir.mkdir();
        File f = new File("src/main/resources/DBConfiguration/cfg.txt");
        if(f.exists()){
            try {
                for (String s : Files.readAllLines(f.toPath())) {
                    String[] sc = s.split(";");
                    properties.put(sc[0],sc[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                ArrayList<String> outProperties = new ArrayList<>();
                DataSourceSelector ds = new DataSourceSelector();
                Map<String,String> conf = ds.getConfiguration();
                ds.dispose();
                conf.forEach((e1,e2)->outProperties.add(e1+";"+e2));
                properties=conf;
                f.createNewFile();
                Files.write(f.toPath(),outProperties);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Map<String,String> getConfig(){
        return properties;
    }
}
