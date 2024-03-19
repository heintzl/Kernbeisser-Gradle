package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;

public class CatalogEntryField {
  public static FieldIdentifier<CatalogEntry, String> artikelNr =
      new FieldIdentifier<>(CatalogEntry.class, "artikelNr");
  public static FieldIdentifier<CatalogEntry, String> aenderungskennung =
      new FieldIdentifier<>(CatalogEntry.class, "aenderungskennung");
  public static FieldIdentifier<CatalogEntry, Instant> aenderungsDatum =
      new FieldIdentifier<>(CatalogEntry.class, "aenderungsDatum");
  public static FieldIdentifier<CatalogEntry, Instant> aenderungsZeit =
      new FieldIdentifier<>(CatalogEntry.class, "aenderungsZeit");
  public static FieldIdentifier<CatalogEntry, Long> eanLadenEinheit =
      new FieldIdentifier<>(CatalogEntry.class, "eanLadenEinheit");
  public static FieldIdentifier<CatalogEntry, Long> eanBestellEinheit =
      new FieldIdentifier<>(CatalogEntry.class, "eanBestellEinheit");
  public static FieldIdentifier<CatalogEntry, String> bezeichnung =
      new FieldIdentifier<>(CatalogEntry.class, "bezeichnung");
  public static FieldIdentifier<CatalogEntry, String> bezeichnung2 =
      new FieldIdentifier<>(CatalogEntry.class, "bezeichnung2");
  public static FieldIdentifier<CatalogEntry, String> bezeichnung3 =
      new FieldIdentifier<>(CatalogEntry.class, "bezeichnung3");
  public static FieldIdentifier<CatalogEntry, String> handelsklasse =
      new FieldIdentifier<>(CatalogEntry.class, "handelsklasse");
  public static FieldIdentifier<CatalogEntry, String> marke =
      new FieldIdentifier<>(CatalogEntry.class, "marke");
  public static FieldIdentifier<CatalogEntry, String> hersteller =
      new FieldIdentifier<>(CatalogEntry.class, "hersteller");
  public static FieldIdentifier<CatalogEntry, String> herkunft =
      new FieldIdentifier<>(CatalogEntry.class, "herkunft");
  public static FieldIdentifier<CatalogEntry, String> qualitaet =
      new FieldIdentifier<>(CatalogEntry.class, "qualitaet");
  public static FieldIdentifier<CatalogEntry, String> kontrollstelle =
      new FieldIdentifier<>(CatalogEntry.class, "kontrollstelle");
  public static FieldIdentifier<CatalogEntry, Integer> mHDRestlaufzeit =
      new FieldIdentifier<>(CatalogEntry.class, "mHDRestlaufzeit");
  public static FieldIdentifier<CatalogEntry, Integer> wgBnn =
      new FieldIdentifier<>(CatalogEntry.class, "wgBnn");
  public static FieldIdentifier<CatalogEntry, Integer> wgIfh =
      new FieldIdentifier<>(CatalogEntry.class, "wgIfh");
  public static FieldIdentifier<CatalogEntry, Integer> wgGh =
      new FieldIdentifier<>(CatalogEntry.class, "wgGh");
  public static FieldIdentifier<CatalogEntry, String> ersatzArtikelNr =
      new FieldIdentifier<>(CatalogEntry.class, "ersatzArtikelNr");
  public static FieldIdentifier<CatalogEntry, Double> minBestellMenge =
      new FieldIdentifier<>(CatalogEntry.class, "minBestellMenge");
  public static FieldIdentifier<CatalogEntry, String> bestelleinheit =
      new FieldIdentifier<>(CatalogEntry.class, "bestelleinheit");
  public static FieldIdentifier<CatalogEntry, Double> bestelleinheitsMenge =
      new FieldIdentifier<>(CatalogEntry.class, "bestelleinheitsMenge");
  public static FieldIdentifier<CatalogEntry, String> ladeneinheit =
      new FieldIdentifier<>(CatalogEntry.class, "ladeneinheit");
  public static FieldIdentifier<CatalogEntry, Double> mengenfaktor =
      new FieldIdentifier<>(CatalogEntry.class, "mengenfaktor");
  public static FieldIdentifier<CatalogEntry, Boolean> gewichtsartikel =
      new FieldIdentifier<>(CatalogEntry.class, "gewichtsartikel");
  public static FieldIdentifier<CatalogEntry, String> pfandNrLadeneinheit =
      new FieldIdentifier<>(CatalogEntry.class, "pfandNrLadeneinheit");
  public static FieldIdentifier<CatalogEntry, String> pfandNrBestelleinheit =
      new FieldIdentifier<>(CatalogEntry.class, "pfandNrBestelleinheit");
  public static FieldIdentifier<CatalogEntry, Double> gewichtLadeneinheit =
      new FieldIdentifier<>(CatalogEntry.class, "gewichtLadeneinheit");
  public static FieldIdentifier<CatalogEntry, Double> gewichtBestelleinheit =
      new FieldIdentifier<>(CatalogEntry.class, "gewichtBestelleinheit");
  public static FieldIdentifier<CatalogEntry, Integer> breite =
      new FieldIdentifier<>(CatalogEntry.class, "breite");
  public static FieldIdentifier<CatalogEntry, Integer> hoehe =
      new FieldIdentifier<>(CatalogEntry.class, "hoehe");
  public static FieldIdentifier<CatalogEntry, Integer> tiefe =
      new FieldIdentifier<>(CatalogEntry.class, "tiefe");
  public static FieldIdentifier<CatalogEntry, VAT> mwstKennung =
      new FieldIdentifier<>(CatalogEntry.class, "mwstKennung");
  public static FieldIdentifier<CatalogEntry, Double> vkFestpreis =
      new FieldIdentifier<>(CatalogEntry.class, "vkFestpreis");
  public static FieldIdentifier<CatalogEntry, Double> empfVk =
      new FieldIdentifier<>(CatalogEntry.class, "empfVk");
  public static FieldIdentifier<CatalogEntry, Double> empfVkGH =
      new FieldIdentifier<>(CatalogEntry.class, "empfVkGH");
  public static FieldIdentifier<CatalogEntry, Double> preis =
      new FieldIdentifier<>(CatalogEntry.class, "preis");
  public static FieldIdentifier<CatalogEntry, Boolean> rabattfaehig =
      new FieldIdentifier<>(CatalogEntry.class, "rabattfaehig");
  public static FieldIdentifier<CatalogEntry, Boolean> skontierfaehig =
      new FieldIdentifier<>(CatalogEntry.class, "skontierfaehig");
  public static FieldIdentifier<CatalogEntry, Double> staffelMenge1 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelMenge1");
  public static FieldIdentifier<CatalogEntry, Double> staffelPreis1 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelPreis1");
  public static FieldIdentifier<CatalogEntry, Boolean> rabattfaehig1 =
      new FieldIdentifier<>(CatalogEntry.class, "rabattfaehig1");
  public static FieldIdentifier<CatalogEntry, Boolean> skontierfaehig1 =
      new FieldIdentifier<>(CatalogEntry.class, "skontierfaehig1");
  public static FieldIdentifier<CatalogEntry, Double> staffelMenge2 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelMenge2");
  public static FieldIdentifier<CatalogEntry, Double> staffelPreis2 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelPreis2");
  public static FieldIdentifier<CatalogEntry, Boolean> rabattfaehig2 =
      new FieldIdentifier<>(CatalogEntry.class, "rabattfaehig2");
  public static FieldIdentifier<CatalogEntry, Boolean> skontierfaehig2 =
      new FieldIdentifier<>(CatalogEntry.class, "skontierfaehig2");
  public static FieldIdentifier<CatalogEntry, Double> staffelMenge3 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelMenge3");
  public static FieldIdentifier<CatalogEntry, Double> staffelPreis3 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelPreis3");
  public static FieldIdentifier<CatalogEntry, Boolean> rabattfaehig3 =
      new FieldIdentifier<>(CatalogEntry.class, "rabattfaehig3");
  public static FieldIdentifier<CatalogEntry, Boolean> skontierfaehig3 =
      new FieldIdentifier<>(CatalogEntry.class, "skontierfaehig3");
  public static FieldIdentifier<CatalogEntry, Double> staffelMenge4 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelMenge4");
  public static FieldIdentifier<CatalogEntry, Double> staffelPreis4 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelPreis4");
  public static FieldIdentifier<CatalogEntry, Boolean> rabattfaehig4 =
      new FieldIdentifier<>(CatalogEntry.class, "rabattfaehig4");
  public static FieldIdentifier<CatalogEntry, Boolean> skontierfaehig4 =
      new FieldIdentifier<>(CatalogEntry.class, "skontierfaehig4");
  public static FieldIdentifier<CatalogEntry, Double> staffelMenge5 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelMenge5");
  public static FieldIdentifier<CatalogEntry, Double> staffelPreis5 =
      new FieldIdentifier<>(CatalogEntry.class, "staffelPreis5");
  public static FieldIdentifier<CatalogEntry, Boolean> rabattfaehig5 =
      new FieldIdentifier<>(CatalogEntry.class, "rabattfaehig5");
  public static FieldIdentifier<CatalogEntry, Boolean> skontierfaehig5 =
      new FieldIdentifier<>(CatalogEntry.class, "skontierfaehig5");
  public static FieldIdentifier<CatalogEntry, String> artikelart =
      new FieldIdentifier<>(CatalogEntry.class, "artikelart");
  public static FieldIdentifier<CatalogEntry, Boolean> aktionspreis =
      new FieldIdentifier<>(CatalogEntry.class, "aktionspreis");
  public static FieldIdentifier<CatalogEntry, Instant> aktionspreisGueltigAb =
      new FieldIdentifier<>(CatalogEntry.class, "aktionspreisGueltigAb");
  public static FieldIdentifier<CatalogEntry, Instant> aktionspreisGueltigBis =
      new FieldIdentifier<>(CatalogEntry.class, "aktionspreisGueltigBis");
  public static FieldIdentifier<CatalogEntry, Double> empfVkAktion =
      new FieldIdentifier<>(CatalogEntry.class, "empfVkAktion");
  public static FieldIdentifier<CatalogEntry, MetricUnits> grundpreisEinheit =
      new FieldIdentifier<>(CatalogEntry.class, "grundpreisEinheit");
  public static FieldIdentifier<CatalogEntry, Double> grundpreisFaktor =
      new FieldIdentifier<>(CatalogEntry.class, "grundpreisFaktor");
  public static FieldIdentifier<CatalogEntry, Instant> lieferbarAb =
      new FieldIdentifier<>(CatalogEntry.class, "lieferbarAb");
  public static FieldIdentifier<CatalogEntry, Instant> lieferbarBis =
      new FieldIdentifier<>(CatalogEntry.class, "lieferbarBis");
  public static FieldIdentifier<CatalogEntry, String> artikelBioId =
      new FieldIdentifier<>(CatalogEntry.class, "artikelBioId");
  public static FieldIdentifier<CatalogEntry, Integer> artikelVariant =
      new FieldIdentifier<>(CatalogEntry.class, "artikelVariant");
  public static FieldIdentifier<CatalogEntry, String> markenId =
      new FieldIdentifier<>(CatalogEntry.class, "markenId");
  public static FieldIdentifier<CatalogEntry, String> herstellerId =
      new FieldIdentifier<>(CatalogEntry.class, "herstellerId");
  public static FieldIdentifier<CatalogEntry, Instant> katalogGueltigBis =
      new FieldIdentifier<>(CatalogEntry.class, "katalogGueltigBis");
  public static FieldIdentifier<CatalogEntry, Double> einzelPfand =
      new FieldIdentifier<>(CatalogEntry.class, "einzelPfand");
  public static FieldIdentifier<CatalogEntry, Double> gebindePfand =
      new FieldIdentifier<>(CatalogEntry.class, "gebindePfand");
  public static FieldIdentifier<CatalogEntry, Long> id =
      new FieldIdentifier<>(CatalogEntry.class, "id");
}
