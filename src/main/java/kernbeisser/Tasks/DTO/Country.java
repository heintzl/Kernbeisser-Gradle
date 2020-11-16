package kernbeisser.Tasks.DTO;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Country {
  private String name;
  private String shortName;

  public static TypeAdapter<Country[]> getTypeAdapter() {
    return new TypeAdapter<Country[]>() {
      @Override
      public void write(JsonWriter out, Country[] value) throws IOException {
        out.beginObject();
        for (Country producer : value) {
          out.name(producer.shortName);
          out.value(producer.name);
        }
        out.endArray();
      }

      @Override
      public Country[] read(JsonReader in) throws IOException {
        ArrayList<Country> producers = new ArrayList<>();
        in.beginObject();
        while (in.hasNext()) {
          Country p = new Country();
          p.shortName = in.nextName();
          p.name = in.nextString();
          producers.add(p);
        }
        in.endObject();
        return producers.toArray(new Country[0]);
      }
    };
  }
}
