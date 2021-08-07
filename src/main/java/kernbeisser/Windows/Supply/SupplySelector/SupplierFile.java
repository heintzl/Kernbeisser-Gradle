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
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Main;
import kernbeisser.Tasks.Catalog.Catalog;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

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

  public void checkFractionalItemMultiplier(double itemMultiplier, int kkNumber) {
    if (itemMultiplier % 1 == 0) {
      Main.logger.warn(
          String.format(
              "fractional item multiplier while reading KKSupplierFile content Article[%s]",
              kkNumber));
    }
  }

  public Collection<ShoppingItem> collectShoppingItems() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    ArrayList<ShoppingItem> items = new ArrayList<>();

    Supplier kkSupplier = Supplier.getKKSupplier();
    for (LineContent content : contents) {
      if (content.getStatus() == ResolveStatus.IGNORE
          || content.getStatus() == ResolveStatus.PRODUCE) continue;
      Article article =
          Articles.getBySuppliersItemNumber(kkSupplier, content.getKkNumber())
              .orElseGet(() -> createArticle(content));
      // create shopping item
      ShoppingItem shoppingItem = new ShoppingItem(article, 0, false);
      checkFractionalItemMultiplier(
          content.getContainerMultiplier() * content.getContainerSize(), content.getKkNumber());
      shoppingItem.setItemMultiplier(
          (shoppingItem.isWeighAble()
                  ? getAsItemMultiplierAmount(content)
                  : (int) Math.round(content.getContainerMultiplier() * content.getContainerSize()))
              * -1);
      items.add(shoppingItem);
    }
    return items;
  }

  private int getAsItemMultiplierAmount(LineContent content) {
    return (int)
        Math.round(
            content
                .getUnit()
                .inUnit(
                    MetricUnits.GRAM,
                    content.getContainerMultiplier()
                        * content.getContainerSize()
                        * content.getAmount()));
  }

  private @NotNull Article createArticle(LineContent content) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Supplier kkSupplier = Supplier.getKKSupplier();
    Article pattern = Articles.nextArticleTo(em, content.getKkNumber(), kkSupplier);
    Article article = new Article();
    article.setSupplier(kkSupplier);
    article.setName(content.getName());
    article.setNetPrice(content.getPrice() / content.getContainerSize());
    article.setMetricUnits(content.getUnit());
    article.setAmount(content.getAmount());
    article.setWeighable(content.getContainerSize() == 1);
    article.setContainerSize(content.getContainerSize());
    article.setShopRange(ShopRange.NOT_IN_RANGE);
    article.setSurchargeGroup(pattern.getSurchargeGroup());
    article.setPriceList(pattern.getPriceList());
    article.setVerified(false);
    article.setKbNumber(Articles.nextFreeKBNumber(em));
    article.setSuppliersItemNumber(content.getKkNumber());
    em.persist(article);
    em.flush();
    return article;
  }

  public int hashCode() {
    return header.getId().hashCode();
  }
}
