package kernbeisser.Windows.Supply.SupplySelector;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.CatalogEntry_;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Useful.Icons;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Supply.SupplyModel;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.Range;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.jetbrains.annotations.NotNull;

@Data
@Setter(AccessLevel.PUBLIC)
public class LineContent {
  private ResolveStatus resolveStatus;
  private String message;
  private int kkNumber; // 13
  private boolean plusSign; // 1
  private double containerMultiplier; // 7 div 1000
  private String name; // 30
  private double containerSize; // 7 div 1000
  private int amount;
  private MetricUnits unit; // 12
  private String producer; // 3
  private String origin; // 2
  private boolean weighableKk; // 2 white space
  private boolean weighableKb; // 2 white space
  private String identification; // 2
  // 1 white space
  // placeholder Nr. 1234567890123
  private double priceKk; // 3
  private double priceKb;
  private double discount;
  private boolean verified;
  private Article article;
  private PriceList estimatedPriceList;
  private SurchargeGroup estimatedSurchargeGroup;
  private Long barcode = null;
  private double singleDeposit = 0.0;
  private double containerDeposit = 0.0;
  private VAT vat;
  private ArticleComparedToCatalogEntry comparedToCatalog;
  private Integer userPreorderCount;
  private Integer labels;
  private String containerUnit;

  public static List<LineContent> parseContents(List<String> lines, String fileName, int offset) {
    List<LineContent> contents = new ArrayList<>(lines.size() - offset);
    List<String> kkNumbers = new ArrayList<>(lines.size() - offset);
    List<LineError> lineErrors = new ArrayList<>();
    for (int i = offset; i < lines.size(); i++) {
      String currentLine = lines.get(i);
      if (isComment(currentLine)) {
        if (contents.isEmpty()) continue;
        LineContent before = contents.getLast();
        before.setMessage(extractMessage(currentLine));
        continue;
      }
      try {
        LineContent content = singleLine(currentLine);
        contents.add(content);
        kkNumbers.add(Objects.toString(content.getKkNumber()));
      } catch (Exception e) {
        lineErrors.add(new LineError(i, currentLine, e));
        e.printStackTrace();
      }
    }
    if (!lineErrors.isEmpty()) {
      ObjectTable<LineError> errorTable =
          new ObjectTable<>(
              Columns.create("Zeile", LineError::getLineNumber)
                  .withSorter(Column.NUMBER_SORTER)
                  .withPreferredWidth(40),
              Columns.create("Inhalt", LineError::getLine).withPreferredWidth(700),
              Columns.create("Fehler", LineError::getException).withPreferredWidth(200),
              Columns.createIconColumn(
                  Icons.defaultIcon(FontAwesome.BUG, Color.RED),
                  e ->
                      UnexpectedExceptionHandler.showErrorWarning(
                          e.getException(), "Import-Fehler"),
                  e -> true));
      errorTable.addAll(lineErrors);
      JScrollPane errorPane = new JScrollPane(errorTable);
      errorPane.setPreferredSize(new Dimension(1000, Math.min(lineErrors.size() * 24 + 30, 800)));
      JOptionPane.showMessageDialog(
          null,
          errorPane,
          fileName + ": Fehlerhafte Zeilen wurden übersprungen:",
          JOptionPane.WARNING_MESSAGE);
    }
    var articleCondition = CatalogEntry_.artikelNr.in(kkNumbers);
    var actionCondition = CatalogEntry_.aktionspreis.eq(false);
    Map<String, CatalogEntry> catalogEntries =
        DBConnection.getConditioned(CatalogEntry.class, articleCondition, actionCondition).stream()
            .collect(Collectors.toMap(CatalogEntry::getArtikelNr, e -> e));
    Map<CatalogEntry, Integer> entryPreorders = SupplyModel.getUserPreorderEntryCount();

    for (LineContent content : contents) {
      if (content.containerMultiplier == 0) {
        content.resolveStatus = ResolveStatus.IGNORE;
      }
      CatalogEntry matchingEntry = catalogEntries.get(Objects.toString(content.kkNumber));
      if (content.getStatus() != ResolveStatus.PRODUCE) {
        if (matchingEntry == null) {
          if (!content.weighableKk) {
            content.comparedToCatalog = ArticleComparedToCatalogEntry.NO_CATALOG_ENTRY;
          }
          content.userPreorderCount = 0;
        } else {
          content.barcode = matchingEntry.getEanLadenEinheit();
          content.singleDeposit = matchingEntry.getEinzelPfand();
          content.containerDeposit = matchingEntry.getGebindePfand();
          content.vat = matchingEntry.getMwstKennung();
          Article articleToCompare = content.article;
          if (articleToCompare == null) {
            articleToCompare = new Article();
            articleToCompare.setSupplier(ArticleRepository.KK_SUPPLIER);
          }
          content.comparedToCatalog =
              new ArticleComparedToCatalogEntry(articleToCompare, matchingEntry);
          content.userPreorderCount = Tools.ifNull(entryPreorders.get(matchingEntry), 0);
          content.containerUnit = matchingEntry.getBestelleinheit();
        }
        content.labels = SupplyModel.getPrintNumberFromLineContent(content);
      } else {
        content.containerUnit = content.getContainerDescription();
      }
    }
    return contents;
  }

  private void setNonWeighableAmount(double containerSize, String amountAndUnit) {
    setContainerSize(containerSize);
    char[] chars = amountAndUnit.toCharArray();
    String unitString = amountAndUnit.substring(Tools.lastIndexOfPartOfNumber(chars) + 1);
    double[] amounts = Tools.allNumbers(amountAndUnit.replace("ca.", ""));
    double product = 1;
    for (double amount : amounts) {
      product *= amount;
    }

    MetricUnits unit = Catalog.extractUnit(unitString.replace(" ", ""));
    if (isExactEnough(product)) {
      setUnit(unit);
      setAmount((int) Math.round(product));
    } else {
      trySplit(unit, product);
    }
  }

  public String getPriceDifference() {
    if (getStatus() != ResolveStatus.OK || article == null) {
      return "";
    }
    double articlePrice = article.getNetPrice();
    if (priceKb == 0.0d) {
      return articlePrice == 0.0d ? "" : "!";
    }
    return String.format("%.0f%%", 100 * (priceKb - articlePrice) / priceKb);
  }

  private void trySplit(@NotNull MetricUnits currentUnit, double amount) {
    double newAmount;
    MetricUnits newUnit;
    switch (currentUnit) {
      case LITER:
        newUnit = MetricUnits.MILLILITER;
        newAmount = MetricUnits.LITER.inUnit(MetricUnits.MILLILITER, amount);
        break;
      case KILOGRAM:
        newUnit = MetricUnits.GRAM;
        newAmount = MetricUnits.KILOGRAM.inUnit(MetricUnits.GRAM, amount);
        break;
      case GRAM:
        newUnit = MetricUnits.MILLIGRAM;
        newAmount = MetricUnits.GRAM.inUnit(MetricUnits.MILLIGRAM, amount);
        break;
      default:
        throw new RuntimeException("cannot split: " + currentUnit);
    }
    if (isExactEnough(newAmount)) {
      setAmount((int) Math.round(newAmount));
      setUnit(newUnit);
    } else {
      trySplit(newUnit, newAmount);
    }
  }

  private static boolean isExactEnough(double value) {
    return Math.abs(Math.round(value) - value) < 0.00001;
  }

  private void setWeighableAmount(double amount, String unitString) {
    setContainerSize(1);
    weighableKk = true;
    MetricUnits unit = Catalog.extractUnit(unitString);
    if (isExactEnough(amount)) {
      setUnit(unit);
      setAmount((int) Math.round(amount));
    } else {
      trySplit(unit, amount);
    }
  }

  private void parseAmount(double containerSizeOrAmount, String unitString) {
    if (unitString.startsWith("x")) setNonWeighableAmount(containerSizeOrAmount, unitString);
    else setWeighableAmount(containerSizeOrAmount, unitString);
  }

  private static LineContent singleLine(String line) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    LineContent content = new LineContent();
    content.kkNumber = Integer.parseInt(line.substring(0, 13).replace(" ", ""));
    content.plusSign = line.charAt(13) == '+';
    content.containerMultiplier = Integer.parseInt(line.substring(14, 21).replace(" ", "")) / 1000.;
    content.name = line.substring(21, 51);
    content.parseAmount(
        Integer.parseInt(line.substring(51, 58).replace(" ", "")) / 1000.,
        line.substring(58, 70).replace(" ", ""));
    content.producer = line.substring(70, 73).replace(" ", "");
    content.identification = line.substring(73, 77).replace(" ", "");
    content.origin = line.substring(77, 80).replace(" ", "");
    content.discount = Integer.parseInt(line.substring(133, 136)) / 10000.;
    content.priceKk = Integer.parseInt(line.substring(93, 100).replace(" ", "")) / 1000.;
    Optional<Article> matchedArticle = ArticleRepository.getByKkItemNumber(content.kkNumber);
    Article pattern;
    if (matchedArticle.isPresent()) {
      content.article = matchedArticle.get();
      pattern = content.article;
      content.weighableKb = content.article.isWeighable();
      content.priceKb = content.calculatePriceKb();
    } else {
      pattern = ArticleRepository.nextArticleTo(em, content.kkNumber, Supplier.getKKSupplier());
      content.weighableKb = false;
      content.priceKb = content.priceKk;
    }
    content.estimatedPriceList = ArticleRepository.getValidPriceList(em, pattern);
    content.estimatedSurchargeGroup = pattern.getSurchargeGroup();
    return content;
  }

  public double calculatePriceKb() {
    double priceKb = priceKk * article.getCatalogPriceFactor();
    return priceKb;
  }

  private static boolean isComment(String line) {
    return line.length() < 13
        || line.startsWith("        ", 13)
        || line.startsWith("1    ")
        || line.startsWith("A#");
  }

  private static String extractMessage(String line) {
    return line.length() > 21 ? line.substring(21) : "";
  }

  public double getTotalPrice() {
    if (weighableKk) return containerMultiplier * unit.getBaseFactor() * amount * priceKk;
    else return containerSize * containerMultiplier * priceKk;
  }

  public String getContainerDescription() {
    return getAmount() + (getUnit() == null ? "" : getUnit().getShortName());
  }

  public ResolveStatus getStatus() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getStatus(em);
  }

  public ResolveStatus getStatus(EntityManager em) {
    if (resolveStatus != null) return resolveStatus;
    Range<Integer> produceRange =
        Setting.KK_SUPPLY_PRODUCE_SUPPLIER_ITEM_NUMBER_RANGE.getIntRange();
    if (produceRange.contains(kkNumber)) {
      resolveStatus = ResolveStatus.PRODUCE;
      return ResolveStatus.PRODUCE;
    }
    resolveStatus =
        ArticleRepository.getBySuppliersItemNumber(Supplier.getKKSupplier(), kkNumber, em)
            .map(e -> ResolveStatus.OK)
            .orElse(ResolveStatus.ADDED);
    return resolveStatus;
  }

  public void refreshFromArticle(Article article) {
    this.setPriceKb(article.getNetPrice()); // different to article generation
    this.setPriceKk(article.getNetPrice()); // different to article generation
    this.setUnit(article.getMetricUnits());
    this.setAmount(article.getAmount());
    this.setContainerSize(article.getContainerSize());
    this.setWeighableKb(article.isWeighable());
    this.setWeighableKk(article.isWeighable());
    this.setEstimatedPriceList(article.getPriceList());
    this.setEstimatedSurchargeGroup(article.getSurchargeGroup());
  }

  public double getProduceRetailPrice() throws InvalidInputException {
    if (this.getStatus() != ResolveStatus.PRODUCE) {
      throw new InvalidInputException("expected PRODUCE, got " + getStatus().name());
    }
    double retailPrice =
        ArticleRepository.calculateRetailPrice(
            priceKk, VAT.LOW, Supplier.getProduceSupplier().getDefaultSurcharge(), 0, false);
    return Math.round(retailPrice * 10) * 0.1;
  }

  public void verify(boolean v) {
    verified = v;
  }

  public String toString() {
    return name + getTotalPrice() + "€";
  }
}
