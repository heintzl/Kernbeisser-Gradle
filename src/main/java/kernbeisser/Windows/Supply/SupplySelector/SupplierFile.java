package kernbeisser.Windows.Supply.SupplySelector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Windows.Supply.SupplyController;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;

@AllArgsConstructor
@Data
@Setter(AccessLevel.NONE)
public class SupplierFile {
  private final FileHeader header;
  private final List<LineContent> contents;
  private final Instant createDate;
  private final File origin;

  @SneakyThrows
  public static SupplierFile parse(File file) {
    List<String> lines = Files.readAllLines(file.toPath(), Catalog.DEFAULT_ENCODING);
    return new SupplierFile(
        FileHeader.parseLine(lines.get(0)),
        LineContent.parseContents(lines, 1),
        Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toInstant(),
        file);
  }

  public Collection<LineContent> getNotInCatalog() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Supplier kkSupplier = Supplier.getKKSupplier();
    return contents.stream()
        .filter(e -> Articles.getBySuppliersItemNumber(kkSupplier, e.getKkNumber()).isPresent())
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public Collection<ShoppingItem> collectShoppingItems() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Supplier kkSupplier = Supplier.getKKSupplier();
    return contents.stream()
        .filter(SupplyController::shouldBecomeShoppingItem)
        .map(e -> SupplyController.createShoppingItem(kkSupplier, e))
        .collect(Collectors.toList());
  }

  public int hashCode() {
    return header.getId().hashCode();
  }
}
