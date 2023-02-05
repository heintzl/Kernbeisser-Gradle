package kernbeisser.Windows.Supply.SupplySelector;

import com.sun.istack.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.Range;
import org.eclipse.jdt.core.compiler.InvalidInputException;

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
  private String qualitySign; // 2
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

  public static List<LineContent> parseContents(List<String> lines, int offset) {
    List<LineContent> contents = new ArrayList<>(lines.size() - offset);
    for (int i = offset; i < lines.size(); i++) {
      String currentLine = lines.get(i);
      if (isComment(currentLine)) {
        if (contents.size() == 0) continue;
        LineContent before = contents.get(contents.size() - 1);
        before.setMessage(extractMessage(currentLine));
        continue;
      }
      LineContent content = singleLine(currentLine);
      contents.add(content);
    }
    for (LineContent content : contents) {
      if (content.containerMultiplier == 0) content.resolveStatus = ResolveStatus.IGNORE;
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
    double price = getPriceKb();
    double articlePrice = article.getNetPrice();
    if (price == 0.0d) {
      return articlePrice == 0.0d ? "" : "!";
    }
    return String.format("%.0f%%", 100 * (price - articlePrice) / price);
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
    content.origin = line.substring(73, 77).replace(" ", "");
    content.qualitySign = line.substring(77, 80).replace(" ", "");
    content.discount = Integer.parseInt(line.substring(133, 136)) / 10000.;
    content.priceKk = Integer.parseInt(line.substring(93, 100).replace(" ", "")) / 1000.;
    Optional<Article> matchedArticle = Articles.getByKkItemNumber(content.kkNumber);
    Article pattern;
    if (matchedArticle.isPresent()) {
      content.article = matchedArticle.get();
      pattern = content.article;
      content.weighableKb = content.article.isWeighable();
      content.calculatePriceKb();
    } else {
      pattern = Articles.nextArticleTo(em, content.kkNumber, Supplier.getKKSupplier());
      content.weighableKb = false;
    }
    content.estimatedPriceList = pattern.getPriceList();
    content.estimatedSurchargeGroup = pattern.getSurchargeGroup();
    return content;
  }

  public void calculatePriceKb() {
    if (weighableKb == weighableKk || amount == 0) {
      priceKb = priceKk;
    } else { // handle difference in priceunit if weighability is not the same
      priceKb = priceKk * (weighableKk ? (amount) : 1000.0 / amount);
    }
  }

  private static boolean isComment(String line) {
    return line.length() < 13 || line.startsWith("        ", 13) || line.startsWith("1    ");
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
        Articles.getBySuppliersItemNumber(Supplier.getKKSupplier(), kkNumber, em)
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
        Articles.calculateRetailPrice(
            price, VAT.LOW, Supplier.getProduceSupplier().getDefaultSurcharge(), 0, false);
    return Math.round(retailPrice * 10) * 0.1;
  }

  public void verify(boolean v) {
    verified = v;
  }

  public String toString() {
    return name + getTotalPrice() + "€";
  }
}
