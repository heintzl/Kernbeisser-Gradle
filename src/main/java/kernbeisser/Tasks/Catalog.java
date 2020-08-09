package kernbeisser.Tasks;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import kernbeisser.Useful.Tools;

public class Catalog {

  public static String CATALOG_URL =
      "https://shop.kornkraft.com/files/cms_download/PL_2005_20200424.BNN";

  public static String updateCatalogFromWeb() throws IOException {
    URL url;
    url = new URL(CATALOG_URL);
    InputStream in = url.openConnection().getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    ArrayList<String[]> articleRawData = new ArrayList<>();
    String infoLine = reader.readLine();
    reader.lines().forEach(e -> articleRawData.add(e.split(";")));
    updateCatalog(articleRawData);
    return infoLine;
  }

  public static String getInfoLineFromWeb() throws IOException {
    BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(new URL(CATALOG_URL).openConnection().getInputStream()));
    String out = bufferedReader.readLine();
    bufferedReader.close();
    return out;
  }

  public static void updateCatalog(Iterable<String[]> s) {
    Collection<ArticleKornkraft> newCatalog = new ArrayList<>(1000);
    for (String[] value : s) {
      try {
        newCatalog.add(parseArticle(value));
      } catch (CannotParseException e) {
        Main.logger.warn("Ignored ArticleKornKraft because " + e.getMessage());
      }
    }
    clearCatalog();
    setDeposit(newCatalog);
    persistCatalog(newCatalog);
    // setDepositByReference();
  }

  public static void updateCatalogFromKornKraftDefault(File file) {
    new Thread(
            () -> {
              Collection<ArticleKornkraft> newCatalog = new ArrayList<>(10000);
              boolean infoLineSkipped = false;
              try {
                Collection<String> lines =
                    Files.readAllLines(Paths.get(file.getPath()), Charset.forName("IBM850"));
                ProgressMonitor pm =
                    new ProgressMonitor(
                        null,
                        "Aktualiesiere Katalog",
                        "interpretiere Aktikel ",
                        0,
                        2 * lines.size() + 3);
                int p = 0;
                for (String line : lines) {
                  if (!infoLineSkipped) {
                    infoLineSkipped = true;
                    continue;
                  }
                  String[] parts = line.split(";");
                  try {
                    newCatalog.add(parseArticle(parts));
                  } catch (CannotParseException e) {
                    Main.logger.warn("Ignored ArticleKornKraft because " + e.getMessage());
                  }
                  pm.setProgress(++p);
                  pm.setNote("Interpretiere Artikel " + p);
                }
                pm.setNote("Alter Katalog wird gelöscht");
                pm.setProgress(++p);
                clearCatalog();
                pm.setNote("Pfand wird gesetzt");
                pm.setProgress(++p);
                setDeposit(newCatalog);
                pm.setNote("Neuer Katalog wird gespeichert");
                pm.setProgress(++p);
                persistCatalog(newCatalog, pm, p);
                pm.close();
                // setDepositByReference();
              } catch (IOException e) {
                Tools.showUnexpectedErrorWarning(e);
              }
            })
        .start();
  }

  public static void persistCatalog(Iterable<ArticleKornkraft> articles) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    int c = 0;
    for (ArticleKornkraft article : articles) {
      em.persist(article);
      c++;
      if (c % 200 == 0) {
        em.flush();
        em.clear();
      }
    }
    em.flush();
    et.commit();
    em.close();
  }

  public static void persistCatalog(
      Iterable<ArticleKornkraft> articles, ProgressMonitor pm, int before) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    int c = 0;
    for (ArticleKornkraft article : articles) {
      pm.setProgress(++before);
      pm.setNote("Artikel " + c + " wird auf der Datenbank gespeichert");
      em.persist(article);
      c++;
      if (c % 200 == 0) {
        em.flush();
        em.clear();
      }
    }
    em.flush();
    et.commit();
    em.close();
  }

  public static void clearCatalog() {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (ArticleKornkraft articleKornkraft :
        em.createQuery("select c from ArticleKornkraft c", ArticleKornkraft.class)
            .getResultList()) {
      em.detach(articleKornkraft);
    }
    em.flush();
    et.commit();
    em.close();
  }

  public static ArticleKornkraft parseArticle(String[] source) throws CannotParseException {
    ArticleKornkraft item = new ArticleKornkraft();
    try {
      if (source.length < 42) {
        throw new CannotParseException(
            "Article doesn't has at least 42 columns:\n" + Arrays.toString(source));
      }
      item.setSuppliersItemNumber(Integer.parseInt(source[0]));
      try {
        item.setBarcode(Long.parseLong(source[4]));
      } catch (NumberFormatException e) {
        item.setBarcode(null);
      }
      item.setName(source[6]);
      item.setProducer(source[10]);
      item.setContainerSize(Double.parseDouble(source[22].replaceAll(",", ".")));
      item.setMetricUnits(MetricUnits.fromString(source[23].replaceAll("\\d", "")));
      item.setAmount(extractAmount(source[22].replaceAll("\\D", ""), item.getMetricUnits()));
      item.setVat(source[33].equals("1") ? VAT.LOW : VAT.HIGH);
      item.setNetPrice(Double.parseDouble(source[37].replace(",", ".")));
      item.setSupplier(Supplier.getKKSupplier());
      if (!source[26].equals("")) {
        item.setSingleDeposit(Integer.parseInt(source[26]));
      }
      if (!source[27].equals("")) {
        item.setContainerDeposit(Integer.parseInt(source[27]));
      }
    } catch (NumberFormatException e) {
      throw new CannotParseException(e.getMessage());
    }
    return item;
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

  public static void setDepositByReference() {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createQuery(
            "update ArticleKornkraft c set c.singleDeposit = (select a.netPrice from ArticleKornkraft a where a.suppliersItemNumber = c.singleDeposit), c.containerDeposit = (select a.netPrice from ArticleKornkraft a where a.suppliersItemNumber = c.containerDeposit)")
        .executeUpdate();
    et.commit();
    em.close();
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
