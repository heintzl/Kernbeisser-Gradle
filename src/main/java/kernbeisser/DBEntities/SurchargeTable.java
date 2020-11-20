package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.InconsistentSurchargeTable;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class SurchargeTable implements Serializable, Cloneable {

  public static final SurchargeTable DEFAULT;

  static {
    SurchargeTable standard =
        new SurchargeTable() {
          @Override
          public double getSurcharge() {
            return Setting.SURCHARGE_DEFAULT.getDoubleValue();
          }
        };
    standard.from_number = -1;
    standard.to_number = -1;
    standard.description = "DEFAULT";
    standard.supplier = null;
    DEFAULT = standard;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_WRITE)})
  private double surcharge;

  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_FROM_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_FROM_WRITE)})
  private int from_number;

  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_TO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_TO_WRITE)})
  private int to_number;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_NAME_WRITE)})
  private String description;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private Supplier supplier;

  public SurchargeTable() {}

  public static List<SurchargeTable> getAll(String condition) {
    return Tools.getAll(SurchargeTable.class, condition);
  }

  public static Collection<SurchargeTable> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<SurchargeTable> out =
        em.createQuery(
                "select s from SurchargeTable s where s.description like :search or s.supplier.name like :search or s.supplier.shortName like :search",
                SurchargeTable.class)
            .setParameter("search", s + "%")
            .setMaxResults(max)
            .getResultList();
    em.close();
    return out;
  }

  public static Map<SurchargeTable, String> checkSurchargeTableConsistency() {
    Map<SurchargeTable, String> result = new HashMap<>();
    EntityManager em = DBConnection.getEntityManager();
    List<SurchargeTable> sortedSurcharges =
        em.createQuery(
                "select st from SurchargeTable st order by st.supplier, st.from_number",
                SurchargeTable.class)
            .getResultList();
    SurchargeTable old_row = null;
    for (SurchargeTable st : sortedSurcharges) {
      if (st.to_number < st.from_number) {
        result.put(st, "Endnummer ist kleiner als Startnummer");
      }
      if (old_row != null
          && old_row != null
          && st.supplier.equals(old_row.supplier)
          && st.from_number <= old_row.to_number) {
        result.put(st, "Startnummer ist nicht größer als die Endnummer des vorherigen Eintrags");
        result.put(
            old_row, "Endnummer ist nicht kleiner als die Startnummer des folgenden Eintrags");
      }
      old_row = st;
    }
    return result;
  }

  public static void checkSurchargeTableRowConsistency(SurchargeTable surchargeTable)
      throws InconsistentSurchargeTable {
    Map<SurchargeTable, String> checkResult = checkSurchargeTableConsistency();
    if (checkResult.containsKey(surchargeTable)) {
      throw new InconsistentSurchargeTable(checkResult.get(surchargeTable));
    }
  }
}
