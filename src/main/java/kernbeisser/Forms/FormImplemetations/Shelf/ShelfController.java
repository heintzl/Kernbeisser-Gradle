package kernbeisser.Forms.FormImplemetations.Shelf;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import lombok.Cleanup;

public class ShelfController extends FormController<ShelfView, ShelfModel, Shelf> {

  public ShelfController() throws PermissionKeyRequiredException {
    super(new ShelfModel());
  }

  public static Source<Article> articlesNotInPriceLists(
      Supplier<Collection<PriceList>> priceListSupplier) {

    return () -> {
      Collection<PriceList> ignoredPriceLists = priceListSupplier.get();
      @Cleanup EntityManager em = DBConnection.getEntityManager();
      @Cleanup("commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      return em.createQuery("select a from Article a order by a.name", Article.class)
          .getResultStream()
          .filter(a -> ignoredPriceLists == null || !ignoredPriceLists.contains(a.getPriceList()))
          .collect(Collectors.toList());
    };
  }

  @Override
  public void fillView(ShelfView shelfView) {}

  @Override
  public void addPermission() {}

  @Override
  public void editPermission() {}

  @Override
  public void removePermission() {}

  @Override
  public ObjectForm<Shelf> getObjectContainer() {
    return getView().getObjectForm();
  }

  @Override
  public Supplier<Shelf> defaultFactory() {
    return () -> {
      Shelf shelf = new Shelf();
      shelf.setShelfNo(Shelf.createShelfNo());
      return shelf;
    };
  }
}
