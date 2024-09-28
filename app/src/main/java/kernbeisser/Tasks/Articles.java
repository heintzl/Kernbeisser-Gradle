package kernbeisser.Tasks;

import java.util.HashMap;
import java.util.HashSet;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.*;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Articles {

  public static Article parse(
      String[] rawArticleValues,
      HashSet<Long> barcodes,
      HashSet<String> names,
      HashMap<String, Supplier> suppliers,
      HashMap<Supplier, SurchargeGroup> defaultGroup,
      HashMap<String, PriceList> priceLists)
      throws CannotParseException {
    Article article = new Article();
    article.setName(rawArticleValues[1].replace("%", "Prozent"));
    if (names.contains(article.getName().toUpperCase().replace(" ", ""))) {
      log.warn("Ignored " + article.getName() + " because the name is already taken");
      throw new CannotParseException("Article name is already taken");
    } else {
      names.add(article.getName().toUpperCase().replace(" ", ""));
    }
    article.setKbNumber(Integer.parseInt(rawArticleValues[2]));
    article.setAmount(Integer.parseInt(rawArticleValues[3]));
    article.setNetPrice(Integer.parseInt(rawArticleValues[4]) / 100.);
    String supplierValue = rawArticleValues[5];
    if (supplierValue.equals("GRE")) {
      article.setSupplier(suppliers.get("GR"));
      article.setSurchargeGroup(defaultGroup.get(suppliers.get("GRE")));
    } else {
      article.setSupplier(suppliers.get(supplierValue));
      article.setSurchargeGroup(defaultGroup.get(article.getSupplier()));
    }
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
    article.setShopRange(ShopRange.IN_RANGE);
    return article;
  }
}
