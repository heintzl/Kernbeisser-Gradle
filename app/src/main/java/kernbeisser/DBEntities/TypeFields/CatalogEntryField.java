package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class CatalogEntryField {
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> artikelNr =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "artikelNr");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      aenderungskennung =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.String.class,
              "aenderungskennung");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      aenderungsDatum =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.time.Instant.class,
              "aenderungsDatum");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      aenderungsZeit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.time.Instant.class, "aenderungsZeit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Long>
      eanLadenEinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Long.class, "eanLadenEinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Long>
      eanBestellEinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Long.class, "eanBestellEinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> bezeichnung =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "bezeichnung");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      bezeichnung2 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "bezeichnung2");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      bezeichnung3 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "bezeichnung3");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      handelsklasse =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "handelsklasse");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> marke =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "marke");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> hersteller =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "hersteller");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> herkunft =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "herkunft");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> qualitaet =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "qualitaet");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      kontrollstelle =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "kontrollstelle");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer>
      mHDRestlaufzeit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Integer.class,
              "mHDRestlaufzeit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer> wgBnn =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "wgBnn");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer> wgIfh =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "wgIfh");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer> wgGh =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "wgGh");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      ersatzArtikelNr =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "ersatzArtikelNr");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      minBestellMenge =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "minBestellMenge");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      bestelleinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "bestelleinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      bestelleinheitsMenge =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Double.class,
              "bestelleinheitsMenge");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      ladeneinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "ladeneinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      mengenfaktor =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "mengenfaktor");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      gewichtsartikel =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Boolean.class,
              "gewichtsartikel");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      pfandNrLadeneinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.String.class,
              "pfandNrLadeneinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      pfandNrBestelleinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.String.class,
              "pfandNrBestelleinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      gewichtLadeneinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Double.class,
              "gewichtLadeneinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      gewichtBestelleinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Double.class,
              "gewichtBestelleinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer> breite =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "breite");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer> hoehe =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "hoehe");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer> tiefe =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "tiefe");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, kernbeisser.Enums.VAT>
      mwstKennung =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              kernbeisser.Enums.VAT.class,
              "mwstKennung");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double> vkFestpreis =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "vkFestpreis");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double> empfVk =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "empfVk");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double> empfVkGH =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "empfVkGH");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double> preis =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "preis");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      rabattfaehig =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "rabattfaehig");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      skontierfaehig =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "skontierfaehig");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelMenge1 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelMenge1");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelPreis1 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelPreis1");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      rabattfaehig1 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "rabattfaehig1");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      skontierfaehig1 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Boolean.class,
              "skontierfaehig1");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelMenge2 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelMenge2");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelPreis2 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelPreis2");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      rabattfaehig2 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "rabattfaehig2");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      skontierfaehig2 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Boolean.class,
              "skontierfaehig2");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelMenge3 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelMenge3");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelPreis3 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelPreis3");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      rabattfaehig3 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "rabattfaehig3");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      skontierfaehig3 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Boolean.class,
              "skontierfaehig3");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelMenge4 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelMenge4");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelPreis4 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelPreis4");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      rabattfaehig4 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "rabattfaehig4");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      skontierfaehig4 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Boolean.class,
              "skontierfaehig4");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelMenge5 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelMenge5");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      staffelPreis5 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "staffelPreis5");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      rabattfaehig5 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "rabattfaehig5");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      skontierfaehig5 =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Boolean.class,
              "skontierfaehig5");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> artikelart =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "artikelart");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Boolean>
      aktionspreis =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Boolean.class, "aktionspreis");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      aktionspreisGueltigAb =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.time.Instant.class,
              "aktionspreisGueltigAb");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      aktionspreisGueltigBis =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.time.Instant.class,
              "aktionspreisGueltigBis");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      empfVkAktion =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Double.class, "empfVkAktion");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, kernbeisser.Enums.MetricUnits>
      grundpreisEinheit =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              kernbeisser.Enums.MetricUnits.class,
              "grundpreisEinheit");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Double>
      grundpreisFaktor =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.lang.Double.class,
              "grundpreisFaktor");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      lieferbarAb =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.time.Instant.class, "lieferbarAb");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      lieferbarBis =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.time.Instant.class, "lieferbarBis");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      artikelBioId =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "artikelBioId");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.Integer>
      artikelVariant =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.Integer.class, "artikelVariant");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String> markenId =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "markenId");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.lang.String>
      herstellerId =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class, java.lang.String.class, "herstellerId");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, java.time.Instant>
      katalogGueltigBis =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.CatalogEntry.class,
              java.time.Instant.class,
              "katalogGueltigBis");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, Double> einzelPfand =
      new FieldIdentifier<>(kernbeisser.DBEntities.CatalogEntry.class, Double.class, "einzelPfand");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, Double> gebindePfand =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.CatalogEntry.class, Double.class, "gebindePfand");
  public static FieldIdentifier<kernbeisser.DBEntities.CatalogEntry, Long> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.CatalogEntry.class, Long.class, "id");
}
