package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

@Data
@Table
@Entity
@Setter(AccessLevel.NONE)
@Getter(AccessLevel.PUBLIC)
public class CatalogDataSource {
  private String artikelNr;

  // CatalogChange.identifier
  @Required private String aenderungskennung;
  @Required private Instant aenderungsDatum;
  private Instant aenderungsZeit;
  private Long eanLadenEinheit;
  private Long eanBestellEinheit;
  @Required private String bezeichnung;
  private String bezeichnung2;
  private String bezeichnung3;

  // roman Number
  private String handelsklasse;

  // BNN-Markenkürzel
  @Required private String marke;

  // deprecated Herstellerkürzel
  private String hersteller;

  // car plate country code
  @Required private String herkunft;

  // according to BNN-IK list
  @Required private String qualitaet;
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
  @Required private String bestelleinheit;
  @Required private Double bestelleinheitsMenge;
  @Required private String ladeneinheit;
  @Required private Double mengenfaktor;
  private Boolean gewichtsartikel;
  @Required private String pfandNrLadeneinheit;
  private String pfandNrBestelleinheit;
  private Double gewichtLadeneinheit;
  @Required private Double gewichtBestelleinheit;

  // size in cm
  private Integer breite;
  private Integer hoehe;
  private Integer tiefe;
  @Required private VAT mwstKennung;
  private Double vkFestpreis;
  private Double empfVk;
  private Double empfVkGH;
  @Required private Double preis;
  private Boolean rabattfaehig;
  private Boolean skontierfaehig;
  private Double staffelMenge1;
  private Double staffelPreis1;
  private Boolean rabattfaehig1;
  private Boolean skontierfaehig1;
  private Double staffelMenge2;
  private Double staffelPreis2;
  private Boolean rabattfaehig2;
  private Boolean skontierfaehig2;
  private Double staffelMenge3;
  private Double staffelPreis3;
  private Boolean rabattfaehig3;
  private Boolean skontierfaehig3;
  private Double staffelMenge4;
  private Double staffelPreis4;
  private Boolean rabattfaehig4;
  private Boolean skontierfaehig4;
  private Double staffelMenge5;
  private Double staffelPreis5;
  private Boolean rabattfaehig5;
  private Boolean skontierfaehig5;
  private String artikelart;
  private Boolean aktionspreis;
  private Instant aktionspreisGueltigAb;
  private Instant aktionspreisGueltigBis;
  private Double empfVkAktion;

  // should be @Required but isn't in bnn
  private MetricUnits grundpreisEinheit;
  @Required private Double grundpreisFaktor;
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

  public CatalogDataSource() {}

  public int getArtikelNrInt() throws NumberFormatException {
    return Integer.parseInt(artikelNr);
  }

  public static List<CatalogDataSource> getCatalog() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("SELECT c FROM CatalogDataSource c", CatalogDataSource.class)
        .getResultList();
  }
}
