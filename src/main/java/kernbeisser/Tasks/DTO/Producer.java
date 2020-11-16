package kernbeisser.Tasks.DTO;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import lombok.Data;

@Data
public class Producer {
  private String name;
  private String shortName;

  public static TypeAdapter<Producer[]> getTypeAdapter() {
    return new TypeAdapter<Producer[]>() {
      @Override
      public void write(JsonWriter out, Producer[] value) throws IOException {
        out.beginObject();
        for (Producer producer : value) {
          out.name(producer.shortName);
          out.value(producer.name);
        }
        out.endArray();
      }

      @Override
      public Producer[] read(JsonReader in) throws IOException {
        ArrayList<Producer> producers = new ArrayList<>();
        in.beginObject();
        while (in.hasNext()) {
          Producer p = new Producer();
          p.shortName = in.nextName();
          p.name = in.nextString();
          producers.add(p);
        }
        in.endObject();
        return producers.toArray(new Producer[0]);
      }
    };
  }
}
