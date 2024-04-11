package kernbeisser.Forms.FormImplemetations.Article;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.TypeFields.ArticleField;
import kernbeisser.DBEntities.TypeFields.PriceListField;
import kernbeisser.DBEntities.TypeFields.SupplierField;
import kernbeisser.DBEntities.TypeFields.SurchargeGroupField;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class ArticleModel implements IModel<ArticleController> {

  boolean kbNumberExists(int kbNumber) {
    return QueryBuilder.propertyWithThatValueExists(ArticleField.kbNumber, kbNumber);
  }

  boolean barcodeExists(long barcode) {
    return QueryBuilder.propertyWithThatValueExists(ArticleField.barcode, barcode);
  }

  MetricUnits[] getAllUnits() {
    return MetricUnits.values();
  }

  VAT[] getAllVATs() {
    return VAT.values();
  }

  Collection<Supplier> getAllSuppliers() {
    return QueryBuilder.selectAll(Supplier.class).orderBy(SupplierField.name.asc()).getResultList();
  }

  Collection<PriceList> getAllPriceLists() {
    return QueryBuilder.selectAll(PriceList.class)
        .orderBy(PriceListField.name.asc())
        .getResultList();
  }

  public int nextUnusedArticleNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return ArticleRepository.nextFreeKBNumber(em, kbNumber);
  }

  public static boolean nameExists(String name) {
    return QueryBuilder.propertyWithThatValueExists(ArticleField.name, name);
  }

  public Collection<SurchargeGroup> getAllSurchargeGroupsFor(Supplier s) {
    return QueryBuilder.selectAll(SurchargeGroup.class)
        .where(SurchargeGroupField.surcharge.eq(s))
        .orderBy(SurchargeGroupField.name.asc())
        .getResultList();
  }

  public Optional<Article> findArticleWithMostIdenticalName(Article a) {
    return QueryBuilder.selectAll(Article.class)
        .where(ArticleField.supplier.eq(a.getSupplier()), ArticleField.id.eq(a.getId()).not())
        .getResultList()
        .stream()
        .min(
            Comparator.comparingInt(
                e -> Tools.calculateStringDifference(a.getName(), e.getName())));
  }

  public boolean suppliersItemNumberExists(Supplier supplier, int suppliersItemNumber) {
    return !QueryBuilder.selectAll(Article.class)
        .where(
            ArticleField.supplier.eq(supplier),
            ArticleField.suppliersItemNumber.eq(suppliersItemNumber))
        .getResultList()
        .isEmpty();
  }
}
