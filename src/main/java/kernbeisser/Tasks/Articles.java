package kernbeisser.Tasks;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.*;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;

public class Articles {

  public static Article parse(
      String[] rawArticleValues,
      HashSet<Long> barcodes,
      HashSet<String> names,
      HashMap<String, Supplier> suppliers,
      HashMap<String, PriceList> priceLists,
      SurchargeGroup surchargeGroup)
      throws CannotParseException {
    Article article = new Article();
    article.setName(rawArticleValues[1].replace("%", "Prozent"));
    if (names.contains(article.getName().toUpperCase().replace(" ", ""))) {
      Main.logger.warn("Ignored " + article.getName() + " because the name is already taken");
      throw new CannotParseException("Article name is already taken");
    } else {
      names.add(article.getName().toUpperCase().replace(" ", ""));
    }
    article.setKbNumber(Integer.parseInt(rawArticleValues[2]));
    article.setAmount(Integer.parseInt(rawArticleValues[3]));
    article.setNetPrice(Integer.parseInt(rawArticleValues[4]) / 100.);
    article.setSupplier(suppliers.get(rawArticleValues[5].replace("GRE", "GR")));
    try {
      Long ib = Long.parseLong(rawArticleValues[6]);
      if (!barcodes.contains(ib)) {
        article.setBarcode(ib);
        barcodes.add(ib);
      } else {
        article.setBarcode(null);
      }
    } catch (NumberFormatException e) {
      article.setBarcode(null);
    }
    // columns[7] look at line 311
    article.setVat(Boolean.parseBoolean(rawArticleValues[8]) ? VAT.LOW : VAT.HIGH);
    article.setObsoleteSurcharge(Integer.parseInt(rawArticleValues[9]) / 1000.);
    article.setSingleDeposit(Integer.parseInt(rawArticleValues[10]) / 100.);
    article.setContainerDeposit(Integer.parseInt(rawArticleValues[11]) / 100.);
    article.setMetricUnits(
        MetricUnits.valueOf(
            rawArticleValues[12].replace("WEIGHT", "GRAM").replace("STACK", "PIECE")));
    article.setPriceList(priceLists.get(rawArticleValues[13]));
    article.setContainerSize(Double.parseDouble(rawArticleValues[15].replaceAll(",", ".")));
    article.setSuppliersItemNumber(Integer.parseInt(rawArticleValues[16]));
    article.setWeighable(!Boolean.parseBoolean(rawArticleValues[17]));
    article.setShowInShop(!Boolean.parseBoolean(rawArticleValues[19]));
    article.setActive(!Boolean.parseBoolean(rawArticleValues[20]));
    article.setInfo(rawArticleValues[24]);
    // TODO: article.setInvShelf(Tools.extract(ArrayList::new, columns[28], "_",
    // Integer::parseInt));
    // TODO: article.setInvStock(Tools.extract(ArrayList::new, columns[29], "_",
    // Integer::parseInt));
    // TODO: article.setInvPrice(Integer.parseInt(columns[30])/100.);
    article.setVerified(Boolean.parseBoolean(rawArticleValues[36]));
    article.setSurchargeGroup(surchargeGroup);
    article.setShopRange(true);
    return article;
  }

  public static List<Offer> extractOffers(String[] raw) {
    return extractOffers(
        Tools.extract(Boolean.class, raw[26], "_", Boolean::parseBoolean),
        Integer.parseInt(raw[7]) / 100f);
  }

  private static List<Offer> extractOffers(Boolean[] months, double price) {
    int from = -1;
    ArrayList<Offer> out = new ArrayList<>();
    LocalDate today = LocalDate.now();
    for (int i = 1; i < months.length + 1; i++) {
      if (months[i - 1]) {
        if (from == -1) {
          from = i;
        }
        continue;
      }
      if (from == -1) {
        continue;
      }
      Offer offer = new Offer();
      offer.setSpecialNetPrice(price);
      offer.setFromDate(
          Instant.from(
              LocalDate.of(today.getYear(), from, 1).atStartOfDay(ZoneId.systemDefault())));
      offer.setToDate(
          Instant.from(
              LocalDate.of(today.getYear(), from + (i - 1 - from), 1)
                  .atStartOfDay(ZoneId.systemDefault())
                  .with(TemporalAdjusters.lastDayOfMonth())));
      offer.setRepeatMode(Repeat.EVERY_YEAR);
      out.add(offer);
      from = -1;
    }
    return out;
  }
}
