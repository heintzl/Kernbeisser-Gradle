package kernbeisser.Windows.Supply.SupplySelector;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.io.File;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.SupplyImportState;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Data
@Setter(AccessLevel.NONE)
public class Supply {
  private final Instant deliveryDate;
  private final Collection<SupplierFile> supplierFiles = new ArrayList<>();

  private static boolean isSupplyFile(File file) {
    return file.getName().matches(Setting.KK_SUPPLIER_FILE_REGEX_MATCH.getStringValue());
  }

  private static Supply findOrCreate(
      Collection<Supply> collection, Instant date, int maxSecondsDistance) {
    for (Supply supply : collection) {
      if (Duration.between(supply.deliveryDate, date).abs().get(ChronoUnit.SECONDS)
          < maxSecondsDistance) {
        return supply;
      }
    }
    Supply supply = new Supply(date);
    collection.add(supply);
    return supply;
  }

  public static Collection<Supply> extractSupplies(File[] files, int hFrom, int hTo) {
    ArrayList<Supply> supplies = new ArrayList<>();
    int maxSecDiff = Setting.KK_SUPPLY_MAX_FILE_TRANSFER_DURATION.getIntValue();
    for (File file : files) {
      if (!isSupplyFile(file)) continue;
      Instant date = Instant.ofEpochMilli(file.lastModified());
      ZonedDateTime dateTime = date.atZone(ZoneId.systemDefault());
      int h = dateTime.getHour();
      DayOfWeek d = dateTime.getDayOfWeek();
      if (h >= hFrom && h <= hTo) {
        SupplierFile supplierFile = SupplierFile.parse(file);
        FileHeader header = supplierFile.getHeader();
        if (header != null && header.getOrderType() == 8) {
          findOrCreate(supplies, date, maxSecDiff).supplierFiles.add(supplierFile);
        }
      }
    }
    return supplies;
  }

  public long getArticleCount() {
    return supplierFiles.stream().map(SupplierFile::getContents).mapToLong(Collection::size).sum();
  }

  public Collection<LineContent> getAllLineContents() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return supplierFiles.stream()
        .map(SupplierFile::getContents)
        .flatMap(Collection::stream)
        .peek(e -> e.getStatus(em))
        .sorted(Comparator.comparingInt(LineContent::getKkNumber))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public double getContentSum() {
    return getAllLineContents().stream()
        .mapToDouble(lineContent -> (1 - lineContent.getDiscount()) * lineContent.getTotalPrice())
        .sum();
  }

  // ALL if all are imported, NONE if none, SOME if some, but not all
  public SupplyImportState getImportState() {
    Boolean assumption = null;
    for (SupplierFile file : supplierFiles) {
      if (assumption == null) {
        assumption = file.isAlreadyImported();
      } else {
        if (file.isAlreadyImported() != assumption) {
          return SupplyImportState.SOME;
        }
      }
    }
    return (assumption ? SupplyImportState.ALL : SupplyImportState.NONE);
  }
}
