package kernbeisser.Tasks.DTO;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Property {
  String shortname;
  PropertyValue value;

  public static TypeAdapter<Property[]> getTypeAdapter() {
    return new TypeAdapter<Property[]>() {
      final Gson gson = new Gson();

      @Override
      public void write(JsonWriter out, Property[] value) throws IOException {
        out.beginObject();
        for (Property producer : value) {
          out.name(producer.shortname);
          gson.toJson(gson.toJsonTree(producer.value), out);
        }
        out.endArray();
      }

      @Override
      public Property[] read(JsonReader in) throws IOException {
        ArrayList<Property> properties = new ArrayList<>();
        in.beginObject();
        while (in.hasNext()) {
          Property property = new Property();
          property.shortname = in.nextName();
          property.value = gson.fromJson(in.nextString(), PropertyValue.class);
          properties.add(property);
        }
        in.endObject();
        return properties.toArray(new Property[0]);
      }
    };
  }
}
