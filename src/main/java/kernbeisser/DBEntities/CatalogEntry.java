package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Tasks.Catalog.BoolValues;
import lombok.*;

@Data
@Table(
    uniqueConstraints = {
      @UniqueConstraint(
          name = "UX_artikelNr_aktionspreis",
          columnNames = {"artikelNr", "aktionspreis"})
    })
@Entity
@Setter(AccessLevel.NONE)
@Getter(AccessLevel.PUBLIC)
public class CatalogEntry {
  private String artikelNr;

  // CatalogChange.identifier
  private String aenderungskennung;
  private Instant aenderungsDatum;
  private Instant aenderungsZeit;
  private Long eanLadenEinheit;
  private Long eanBestellEinheit;
  private String bezeichnung;
  private String bezeichnung2;
  private String bezeichnung3;

  // roman Number
  private String handelsklasse;

  // BNN-Markenkürzel
  private String marke;

  // deprecated Herstellerkürzel
  private String hersteller;

  // car plate country code
  private String herkunft;

  // according to BNN-IK list
  private String qualitaet;
  private String kontrollstelle;

  // days
  private Integer mHDRestlaufzeit;

  // wg* trade group properties
  private Integer wgBnn;
  private Integer wgIfh;
  private Integer wgGh;

  // is delivered if article is not on stock
  private String ersatzArtikelNr;
  private Double minBestellMenge;
  private String bestelleinheit;
  private Double bestelleinheitsMenge;
  private String ladeneinheit;
  private Double mengenfaktor;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean gewichtsartikel;

  private String pfandNrLadeneinheit;
  private String pfandNrBestelleinheit;
  private Double gewichtLadeneinheit;
  private Double gewichtBestelleinheit;

  // size in cm
  private Integer breite;
  private Integer hoehe;
  private Integer tiefe;
  private VAT mwstKennung;
  private Double vkFestpreis;
  private Double empfVk;
  private Double empfVkGH;
  private Double preis;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean rabattfaehig;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean skontierfaehig;

  private Double staffelMenge1;
  private Double staffelPreis1;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean rabattfaehig1;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean skontierfaehig1;

  private Double staffelMenge2;
  private Double staffelPreis2;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean rabattfaehig2;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean skontierfaehig2;

  private Double staffelMenge3;
  private Double staffelPreis3;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean rabattfaehig3;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean skontierfaehig3;

  private Double staffelMenge4;
  private Double staffelPreis4;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean rabattfaehig4;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean skontierfaehig4;

  private Double staffelMenge5;
  private Double staffelPreis5;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean rabattfaehig5;

  @BoolValues(trueValue = "J", falseValue = "N")
  private Boolean skontierfaehig5;

  private String artikelart;

  @BoolValues(trueValue = "A")
  private Boolean aktionspreis;

  private Instant aktionspreisGueltigAb;
  private Instant aktionspreisGueltigBis;
  private Double empfVkAktion;

  // should be but isn't in bnn
  private MetricUnits grundpreisEinheit;
  private Double grundpreisFaktor;
  private Instant lieferbarAb;
  private Instant lieferbarBis;
  private String artikelBioId;
  private Integer artikelVariant;
  private String markenId;
  private String herstellerId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Setter(AccessLevel.PUBLIC)
  private long id;

  public CatalogEntry() {}

  public int getArtikelNrInt() throws NumberFormatException {
    return Integer.parseInt(artikelNr);
  }

  public String getUXString() {
    String result = artikelNr;
    if (aktionspreis != null && aktionspreis) result += "A";
    return result;
  }

  public static List<CatalogEntry> getCatalog() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("SELECT c FROM CatalogEntry c", CatalogEntry.class).getResultList();
  }

  public static void clearCatalog() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createQuery("DELETE FROM CatalogEntry").executeUpdate();
  }

  public static Collection<CatalogEntry> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Long n;
    try {
      n = Long.parseLong(s);
    } catch (NumberFormatException e) {
      n = -9999999L;
    }
    return em.createQuery(
            "select c from CatalogEntry c where c.bezeichnung like :sn or artikelNr like :s or eanLadenEinheit = :n",
            CatalogEntry.class)
        .setParameter("sn", "%" + s + "%")
        .setParameter("s", s + "%")
        .setParameter("n", n)
        .setMaxResults(max)
        .getResultList();
  }
}
