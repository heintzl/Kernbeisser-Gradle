package kernbeisser.Tasks.DTO;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public class KornkraftTag {
  private String name;
  private KornkraftGroup[] parents;

  public static TypeAdapter<KornkraftTag[]> getTypeAdapter() {
    return new TypeAdapter<KornkraftTag[]>() {
      Gson gson;

      @Override
      public void write(JsonWriter out, KornkraftTag[] value) throws IOException {
        out.beginObject();
        for (KornkraftTag producer : value) {
          out.name(producer.name);
          out.beginArray();
          for (KornkraftGroup parent : producer.parents) {
            gson.toJson(gson.toJsonTree(parent), out);
          }
          out.endArray();
        }
        out.endArray();
      }

      @Override
      public KornkraftTag[] read(JsonReader in) throws IOException {
        ArrayList<KornkraftTag> tags = new ArrayList<>();
        in.beginObject();
        while (in.hasNext()) {
          KornkraftTag p = new KornkraftTag();
          p.name = in.nextName();
          in.beginArray();
          while (in.hasNext()) {
            ArrayList<KornkraftGroup> parents = new ArrayList<>();
            parents.add(gson.fromJson(in.nextString(), KornkraftGroup.class));
            p.parents = (KornkraftGroup[]) parents.toArray();
          }
          tags.add(p);
        }
        in.endObject();
        return tags.toArray(new KornkraftTag[0]);
      }
    };
  }
}
