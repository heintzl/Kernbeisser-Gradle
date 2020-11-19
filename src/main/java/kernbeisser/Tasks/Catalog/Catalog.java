package kernbeisser.Tasks.Catalog;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;

public class Catalog {

  public static Charset DEFAULT_ENCODING = Charset.forName("IBM850");

  public static void updateCatalog(Stream<String> lines, Runnable onSuccess) {
    Main.logger.info("Updating catalog");
    new Thread(
            () -> {
              EntityManager em = DBConnection.getEntityManager();
              EntityTransaction et = em.getTransaction();
              et.begin();
              HashMap<Integer, ArticleKornkraft> before = new HashMap<>();
              em.createQuery("select a from ArticleKornkraft a", ArticleKornkraft.class)
                  .getResultStream()
                  .forEach(e -> before.put(e.getSuppliersItemNumber(), e));
              em.flush();
              ArrayList<ArticleKornkraft> newCatalog = new ArrayList<>(before.size());
              lines
                  .skip(1)
                  .forEach(
                      row -> {
                        String[] parts = row.split(";");
                        try {
                          ArticleKornkraft base = before.get(Integer.parseInt(parts[0]));
                          newCatalog.add(
                              parseArticle(base == null ? new ArticleKornkraft() : base, parts));
                        } catch (CannotParseException | NumberFormatException e) {
                          Main.logger.warn("Ignored ArticleKornKraft because " + e.getMessage());
                        }
                      });
              setDeposit(newCatalog);
              int c = 0;
              for (ArticleKornkraft articleKornkraft : newCatalog) {
                em.persist(articleKornkraft);
                if (c % 500 == 0) em.flush();
                c++;
              }
              em.flush();
              et.commit();
              em.close();
              onSuccess.run();
            })
        .start();
  }

  public static ArticleKornkraft parseArticle(ArticleKornkraft base, String[] source)
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

  public static void setDeposit(Collection<ArticleKornkraft> catalog) {
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
