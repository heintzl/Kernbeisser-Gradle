package kernbeisser.Forms.FormImplemetations.Shelf;

import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.DBEntities.TypeFields.ArticleField;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import rs.groump.AccessDeniedException;

public class ShelfController extends FormController<ShelfView, ShelfModel, Shelf> {

  private BarcodeCapture barcodeCapture;

  public ShelfController() throws AccessDeniedException {
    super(new ShelfModel());
  }

  public static Source<Article> getAllArticleSource() {
    return () ->
        QueryBuilder.selectAll(Article.class).orderBy(ArticleField.name.asc()).getResultList();
  }

  @Override
  public void fillView(ShelfView shelfView) {
    barcodeCapture = new BarcodeCapture(this::processBarcode);
  }

  private void processBarcode(String s) {
    try {
      long barcode = Long.parseLong(s);
      Shelf shelf = getView().getObjectForm().getData(Mode.EDIT);
      Optional<Article> article = ArticleRepository.getByBarcode(barcode);
      if (!article.isPresent()) {
        return;
      }
      Set<Article> extraArticles = shelf.getArticles();
      if (extraArticles.contains(article.get())) {
        return;
      }
      extraArticles.add(article.get());
      shelf.setArticles(extraArticles);
    } catch (NumberFormatException | CannotParseException ignored) {
      return;
    }
    getView().refreshExtraArticleTable();
  }

  public boolean isShelfNoUniqe(int shelNo) {
    return model.shelfNoExists(shelNo);
  }

  public boolean isLocationUniqe(String location) {
    return model.locationExists(location);
  }

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

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return barcodeCapture.processKeyEvent(e);
  }
}
