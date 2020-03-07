package kernbeisser.Exeptions;

import java.io.File;
import java.nio.charset.Charset;

public class FileReadException extends Exception {
    private File file;
    private Charset charset;

    public FileReadException(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    @Override
    public String getMessage() {
        return "Cannot read data from " + file + " in " + charset;
    }
}
