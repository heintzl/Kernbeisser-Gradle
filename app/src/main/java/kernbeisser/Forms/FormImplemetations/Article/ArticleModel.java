package kernbeisser.Forms.FormImplemetations.Article;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Article_;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.PriceList_;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.Supplier_;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.SurchargeGroup_;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class ArticleModel implements IModel<ArticleController> {

  boolean kbNumberExists(int kbNumber) {
    return QueryBuilder.propertyWithThatValueExists(Article_.kbNumber, kbNumber);
  }

  boolean barcodeExists(long barcode) {
    return QueryBuilder.propertyWithThatValueExists(Article_.barcode, barcode);
  }

  MetricUnits[] getAllUnits() {
    return MetricUnits.values();
  }

  VAT[] getAllVATs() {
    return VAT.values();
  }

  Collection<Supplier> getAllSuppliers() {
    return QueryBuilder.selectAll(Supplier.class).orderBy(Supplier_.name.asc()).getResultList();
  }

  Collection<PriceList> getAllPriceLists() {
    return QueryBuilder.selectAll(PriceList.class).orderBy(PriceList_.name.asc()).getResultList();
  }

  public int nextUnusedArticleNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return ArticleRepository.nextFreeKBNumber(em, kbNumber);
  }

  public static boolean nameExists(String name) {
    return QueryBuilder.propertyWithThatValueExists(Article_.name, name);
  }

  public Collection<SurchargeGroup> getAllSurchargeGroupsFor(Supplier s) {
    return QueryBuilder.selectAll(SurchargeGroup.class)
        .where(SurchargeGroup_.supplier.eq(s))
        .orderBy(SurchargeGroup_.name.asc())
        .getResultList();
  }

  public Optional<Article> findArticleWithMostIdenticalName(Article a) {
    return QueryBuilder.selectAll(Article.class)
        .where(Article_.supplier.eq(a.getSupplier()), Article_.id.eq(a.getId()).not())
        .getResultList()
        .stream()
        .min(
            Comparator.comparingInt(
                e -> Tools.calculateStringDifference(a.getName(), e.getName())));
  }

  public boolean suppliersItemNumberExists(Supplier supplier, int suppliersItemNumber) {
    return !QueryBuilder.selectAll(Article.class)
        .where(Article_.supplier.eq(supplier), Article_.suppliersItemNumber.eq(suppliersItemNumber))
        .getResultList()
        .isEmpty();
  }
}
