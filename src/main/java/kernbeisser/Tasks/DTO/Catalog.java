package kernbeisser.Tasks.DTO;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class Catalog {
  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private KornkraftArticle[] data;
  private HashMap<String, String> land;
  private HashMap<String, String> qualitaet;
  private HashMap<String, KornkraftGroup[]> breadcrums;
  private HashMap<String, Property> merkmale;

  public static Catalog read(String s) {
    return new GsonBuilder().create().fromJson(s, Catalog.class);
  }

  public static Catalog read(Path path) throws IOException {
    return read(Files.lines(path).collect(Collectors.joining("\n")));
  }
}
