package kernbeisser.Config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.IOException;

public class FileTypeAdapter extends TypeAdapter<File> {
  @Override
  public void write(JsonWriter out, File value) throws IOException {
    out.beginObject();
    out.name("path");
    out.value(value.getPath());
    out.endObject();
  }

  @Override
  public File read(JsonReader in) throws IOException {
    in.beginObject();
    in.nextName();
    File file = new File(in.nextString());
    in.endObject();
    return file;
  }
}
