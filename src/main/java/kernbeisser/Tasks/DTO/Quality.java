package kernbeisser.Tasks.DTO;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Quality {
  private String name;
  private String shortName;

  public static TypeAdapter<Quality[]> getTypeAdapter() {
    return new TypeAdapter<Quality[]>() {
      @Override
      public void write(JsonWriter out, Quality[] value) throws IOException {
        out.beginObject();
        for (Quality producer : value) {
          out.name(producer.shortName);
          out.value(producer.name);
        }
        out.endArray();
      }

      @Override
      public Quality[] read(JsonReader in) throws IOException {
        ArrayList<Quality> producers = new ArrayList<>();
        in.beginObject();
        while (in.hasNext()) {
          Quality p = new Quality();
          p.shortName = in.nextName();
          p.name = in.nextString();
          producers.add(p);
        }
        in.endObject();
        return producers.toArray(new Quality[0]);
      }
    };
  }
}
