package kernbeisser.Tasks.Catalog;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogDataSource;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.CannotParseException;

public class Catalog {

  public static Charset DEFAULT_ENCODING = Charset.forName("CP850");

  public static Pattern NUMBER_FILTER = Pattern.compile("\\d*[,.]?\\d*");
  public static Pattern CHARACTER_FILTER = Pattern.compile("[a-zA-z ]*");
  public static Pattern DIM3AMOUNT = Pattern.compile("\\d*[,.]?\\d* x \\d*[,.]?\\d*");

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

  public static Article parseArticle(
      Article base, HashMap<Integer, Double> deposit, Supplier kkSupplier, String[] source)
      throws CannotParseException {
    if (source.length < 42) {
      throw new CannotParseException(
          "Article doesn't have the minimum of 42 columns:\n" + Arrays.toString(source));
    }
    CatalogDataSource catalogDataSource = CatalogDataSource.parseRow(source);
    base.setSuppliersItemNumber(catalogDataSource.getArtikelNr());
    base.setName(catalogDataSource.getBezeichnung());
    base.setWeighable(catalogDataSource.getGewichtsartikel().equals("J"));
    base.setSingleDeposit(
        !catalogDataSource.getPfandNrLadeneinheit().equals("")
            ? deposit.get(Integer.parseInt(catalogDataSource.getPfandNrLadeneinheit()))
            : 0);
    base.setContainerDeposit(
        !catalogDataSource.getPfandNrBestelleinheit().equals("")
            ? deposit.get(Integer.parseInt(catalogDataSource.getPfandNrBestelleinheit()))
            : 0);
    base.setBarcode(catalogDataSource.getEANladen());
    base.setProducer(catalogDataSource.getHersteller());
    base.setVerified(false);
    base.setInfo(catalogDataSource.getBezeichnung2() + "\n" + catalogDataSource.getBezeichnung3());
    base.setContainerSize(catalogDataSource.getBestelleinheitsMenge());
    base.setVat(catalogDataSource.getMwstKennung() == 2 ? VAT.HIGH : VAT.LOW);
    base.setSupplier(kkSupplier);
    base.setMetricUnits(extractUnit(catalogDataSource.getLadeneinheit()));
    base.setNetPrice(catalogDataSource.getPreis());
    try {
      if (DIM3AMOUNT.matcher(catalogDataSource.getLadeneinheit()).matches()) {
        String[] parts = catalogDataSource.getLadeneinheit().split("x");
        double x = filterDouble(parts[0]);
        double y = filterDouble(parts[1]);
        extractAmount(base, x * y);
      } else {
        double parsedAmount =
            Double.parseDouble(
                CHARACTER_FILTER
                    .matcher(catalogDataSource.getLadeneinheit())
                    .replaceAll("")
                    .replace(",", "."));
        extractAmount(base, parsedAmount);
      }
    } catch (NumberFormatException e) {
      double parsedAmount = base.getContainerSize();
      extractAmount(base, parsedAmount);
      base.setContainerSize(1);
      base.setWeighable(true);
    }
    return base;
  }

  private static MetricUnits extractUnit(String source) {
    source = source.replace("x", " x ");
    int lastSep = source.lastIndexOf(" ");
    source = (lastSep == -1 ? source : source.substring(lastSep)).toUpperCase().replace(" ", "");
    switch (source) {
      case "ML":
        return MetricUnits.MILLILITER;
      case "L":
        return MetricUnits.LITER;
      case "KG":
        return MetricUnits.KILOGRAM;
      case "G":
        return MetricUnits.GRAM;
      case "":
        return MetricUnits.NONE;
      case "PLAKAT":
      case "DISPLAY":
      case "PROSPEKT":
      case "STK":
      case "ST":
      case "ST.":
      case "STÜCK":
      case "ROLLEN":
      case "TÜTEN":
      case "STÄNDER":
      case "PAKET":
      case "SET":
      case "PAAR":
      case "BÖGEN":
      case "BOGEN":
      case "PACK":
      case "WAFF":
      case "GLSER":
      case "LEIHGEBHR":
      case "KARTEN":
      case "METER":
      case "STERNE":
      case "KASTEN":
      case "BEUTEL":
      case "BL.":
      case "BL":
      case "BUCH":
      case "M":
      case "KARTON":
      case "SETS":
      case "CL":
      case "ROLLE":
      case "KISTE":
      case "ABROLLER":
      case "KANISTER":
      case "FLYER":
      case "STCK":
      case "CM":
      case "SATZ":
        return MetricUnits.PIECE;
      default:
        String filtered = source.replaceAll("[^A-Z]", "");
        if (filtered.equals(source)) {
          System.out.println(source + " : " + filtered);
          return MetricUnits.NONE;
        } else return extractUnit(filtered);
    }
  }

  private static double filterDouble(String s) {
    return Double.parseDouble(CHARACTER_FILTER.matcher(s).replaceAll("").replace(",", "."));
  }

  private static void extractAmount(Article base, double parsedAmount) {
    switch (base.getMetricUnits()) {
      case KILOGRAM:
        base.setAmount(
            (int) Math.round(MetricUnits.KILOGRAM.inUnit(MetricUnits.GRAM, parsedAmount)));
        base.setMetricUnits(MetricUnits.GRAM);
        break;
      case LITER:
        base.setAmount(
            (int) Math.round(MetricUnits.LITER.inUnit(MetricUnits.MILLILITER, parsedAmount)));
        base.setMetricUnits(MetricUnits.MILLILITER);
        break;
      default:
        base.setAmount((int) Math.round(parsedAmount));
        break;
    }
  }
}
