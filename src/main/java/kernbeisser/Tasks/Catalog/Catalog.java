package kernbeisser.Tasks.Catalog;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;

public class Catalog {

  public static Charset DEFAULT_ENCODING = Charset.forName("IBM850");

  public static void autoLinkAllUndefArticles(Supplier supplier) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Article> articleBases =
        em.createQuery("select a from Article a where a.supplier = :s", Article.class)
            .setParameter("s", supplier)
            .getResultList();
    CatalogDataInterpreter.autoLinkArticle(articleBases);
    articleBases.forEach(em::persist);
    et.commit();
    em.close();
  }

  public static Article parseArticle(Article base, String[] source)
      throws CannotParseException {
    try {
      if (source.length < 42) {
        throw new CannotParseException(
            "Article doesn't have the minimum of 42 columns:\n" + Arrays.toString(source));
      }
      base.setSuppliersItemNumber(Integer.parseInt(source[0]));
      try {
        base.setBarcode(Long.parseLong(source[4]));
      } catch (NumberFormatException e) {
        base.setBarcode(null);
      }
      base.setName(source[6]);
      base.setProducer(source[10]);
      base.setContainerSize(Double.parseDouble(source[22].replaceAll(",", ".")));
      base.setMetricUnits(MetricUnits.fromString(source[23].replaceAll("\\d", "")));
      base.setAmount(extractAmount(source[22].replaceAll("\\D", ""), base.getMetricUnits()));
      base.setVat(source[33].equals("1") ? VAT.LOW : VAT.HIGH);
      base.setNetPrice(Double.parseDouble(source[37].replace(",", ".")));
      base.setSupplier(Supplier.getKKSupplier());
      if (!source[26].equals("")) {
        base.setSingleDeposit(Integer.parseInt(source[26]));
      }
      if (!source[27].equals("")) {
        base.setContainerDeposit(Integer.parseInt(source[27]));
      }
    } catch (NumberFormatException e) {
      throw new CannotParseException(e.getMessage());
    }
    return base;
  }

  private static int extractAmount(String s, MetricUnits u) throws CannotParseException {
    try {
      double d = Double.parseDouble(s.replaceAll(",", "."));
      switch (u) {
        case LITER:
        case KILOGRAM:
          return (int) (d * 1000);
        case PIECE:
        case GRAM:
        case MILLILITER:
        default:
          return (int) d;
      }
    } catch (NumberFormatException n) {
      throw new CannotParseException("Can't parse amount from " + u + s);
    }
  }

  public static void setDeposit(Collection<Article> catalog) {
    HashMap<Integer, Double> priceBySuppliersNumber = new HashMap<>();
    catalog.forEach((a) -> priceBySuppliersNumber.put(a.getSuppliersItemNumber(), a.getNetPrice()));
    catalog.forEach(
        e -> {
          Double singleDeposit = priceBySuppliersNumber.get((int) e.getSingleDeposit());
          e.setSingleDeposit(singleDeposit == null ? 0 : singleDeposit);
          Double crateDeposit = priceBySuppliersNumber.get((int) e.getContainerDeposit());
          e.setContainerDeposit(crateDeposit == null ? 0 : crateDeposit);
        });
  }
}
