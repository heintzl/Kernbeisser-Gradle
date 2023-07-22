package kernbeisser.Tasks.Catalog;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Useful.Tools;

public class Catalog {

  public static Charset DEFAULT_ENCODING = Charset.forName("CP850");

  public static Pattern NUMBER_FILTER = Pattern.compile("\\d*[,.]?\\d*");
  public static Pattern CHARACTER_FILTER = Pattern.compile("[a-zA-z ]*");
  public static Pattern DIM3AMOUNT = Pattern.compile("\\d*[,.]?\\d* x \\d*[,.]?\\d*");

  public static Article parseArticle(
      Article base, Map<Integer, Double> deposit, Supplier kkSupplier, CatalogEntry catalogEntry) {
    base.setSuppliersItemNumber(catalogEntry.getArtikelNrInt());
    base.setName(catalogEntry.getBezeichnung());
    base.setWeighable(catalogEntry.getGewichtsartikel());
    base.setSingleDeposit(
        !catalogEntry.getPfandNrLadeneinheit().equals("")
            ? deposit.get(Integer.parseInt(catalogEntry.getPfandNrLadeneinheit()))
            : 0);
    base.setContainerDeposit(
        !catalogEntry.getPfandNrBestelleinheit().equals("")
            ? deposit.get(Integer.parseInt(catalogEntry.getPfandNrBestelleinheit()))
            : 0);
    base.setBarcode(catalogEntry.getEanLadenEinheit());
    base.setProducer(catalogEntry.getHersteller());
    base.setVerified(false);
    base.setInfo(catalogEntry.getBezeichnung2() + "\n" + catalogEntry.getBezeichnung3());
    base.setContainerSize(catalogEntry.getBestelleinheitsMenge());
    base.setVat(catalogEntry.getMwstKennung());
    base.setSupplier(kkSupplier);
    base.setMetricUnits(extractUnit(catalogEntry.getLadeneinheit()));
    base.setNetPrice(catalogEntry.getPreis());
    double parsedAmount =
        Arrays.stream(Tools.allNumbers(catalogEntry.getLadeneinheit())).reduce(1, (a, b) -> a * b);
    extractAmount(base, parsedAmount);
    return base;
  }

  public static MetricUnits extractUnit(String source) {
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
      case "CAG":
      case "ERSET":
      case "BGEN":
      case "SCHACHTELN":
      case "TPFE":
      case "SACHETS":
      case "ERPACK":
      case "FLASCHEN":
      case "EN":
      case "TBL":
      case "KSTEN":
      case "DOSE":
      case "REGAL":
      case "MUSTER":
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
      case "BUND":
      case "KISTEDECK":
      case "SCHALEN":
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
