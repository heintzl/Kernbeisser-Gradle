package kernbeisser.Tasks.Catalog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kernbeisser.DBEntities.CatalogDataSource;
import kernbeisser.Exeptions.UnknownFileFormatException;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import lombok.Getter;

public class CatalogImporter {

  private Charset charset;
  @Getter private String address;
  private String scope; // V - complete list, T - partial list, S - special list
  @Getter private String description;
  private String currency;
  @Getter private Instant validFrom;
  @Getter private Instant validTo;
  @Getter private Instant createdDate;
  @Getter private Instant createdTime;
  private int fileSeqNo;
  private int followUpSeqNo;

  private static final String DELIMITER = ";";
  private HashMap<Integer, List<Exception>> readErrors = new HashMap<>();
  private List<CatalogDataSource> catalog = new ArrayList<>();

  private CatalogDataSource readCatalogEntry(String line, List<Exception> rowLog) {
    CatalogDataSource out = CatalogDataSource.parseRowWithLog(line.split(DELIMITER), rowLog);
    return out;
  }

  private void parseHeader(String[] parts) throws UnknownFileFormatException {
    DateTimeFormatter dateFormatter = Date.INSTANT_CATALOG_DATE;
    DateTimeFormatter timeFormatter = Date.INSTANT_CATALOG_TIME;
    String fileFormat = parts[0];
    String formatVersion = parts[1];
    if (!(fileFormat.equals("BNN") && formatVersion.equals("3"))) {
      throw new UnknownFileFormatException(fileFormat + " V. " + formatVersion);
    }
    String encoding = parts[2];
    switch (encoding) {
      case "0":
        charset = Charset.forName("CP850");
        break;
      case "1":
        charset = Charset.forName("CP1252");
        break;
      default:
        throw new UnknownFileFormatException("Enc. " + encoding);
    }
    address = parts[3];
    scope = parts[4];
    description = parts[5];
    currency = parts[6];
    if (!currency.equals("EUR")) {
      throw new UnknownFileFormatException("Währung " + currency);
    }
    validFrom = Date.parseInstantDate(parts[7], dateFormatter);
    validTo = Date.parseInstantDate(parts[8], dateFormatter);
    createdDate = Date.parseInstantDate(parts[9], dateFormatter);
    createdTime = Date.parseInstantTime(parts[10], timeFormatter);
    fileSeqNo = Integer.parseInt(parts[11]);
  }

  public CatalogImporter(Path bnnFile) {
    try {
      List<String> catalogSource = Files.readAllLines(bnnFile, Catalog.DEFAULT_ENCODING);
      String[] headerParts = catalogSource.get(0).split(DELIMITER);
      parseHeader(headerParts);
      followUpSeqNo = Integer.parseInt(catalogSource.get(catalogSource.size()).split(DELIMITER)[2]);
      if (fileSeqNo != 1 || followUpSeqNo != 99) {
        throw new UnknownFileFormatException("Kann keine mehrteiligen Katalogdateien verarbeiten!");
      }
      for (int i = 1; i < catalogSource.size() - 1; i++) {
        List<Exception> rowLog = new ArrayList<>();
        catalog.add(readCatalogEntry(catalogSource.get(i), rowLog));
        if (!rowLog.isEmpty()) {
          readErrors.put(i, rowLog);
        }
      }
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }
}
