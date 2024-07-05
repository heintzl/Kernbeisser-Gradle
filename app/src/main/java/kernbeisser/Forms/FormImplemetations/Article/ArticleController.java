package kernbeisser.Forms.FormImplemetations.Article;

import java.awt.event.KeyEvent;
import java.util.*;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.Exceptions.SilentParseException;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.EditArticles.EditArticlesModel;
import kernbeisser.Windows.EditArticles.EditArticlesView;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class ArticleController extends FormController<ArticleView, ArticleModel, Article> {

  private BarcodeCapture capture;

  public ArticleController() {
    super(new ArticleModel());
    capture = new BarcodeCapture(this::pasteBarcode);
  }

  private void pasteBarcode(String s) {
    getView().setBarcode(s);
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return capture.processKeyEvent(e);
  }

  @Override
  public void fillView(ArticleView articleView) {}

  void validateArticle(Article article, Mode mode) throws CannotParseException {
    if (mode == Mode.ADD) {
      Optional<Article> nearestOpt = model.findArticleWithMostIdenticalName(article);
      if (!nearestOpt.isPresent()) return;
      Article nearest = nearestOpt.get();
      int distance = Tools.calculateStringDifference(article.getName(), nearest.getName());
      if (distance < Setting.WARN_ARTICLE_DIFFERENCE.getIntValue()) {
        if (!getView().isSameArticle(nearest)) {
          throw new SilentParseException();
        }
      }
    }
  }

  @Override
  public void remove(Article article) {
    ArticleView view = getView();
    var preparedArticles = ArticleRepository.prepareRemoval(List.of(article));
    if (EditArticlesView.confirmDelete(view.getContent(), preparedArticles)) {
      EditArticlesModel.remove(preparedArticles);
    }
    ;
  }

  public int parseAmount(String s) throws SilentParseException {
    ArticleView view = getView();
    MetricUnits unit = view.getMetricUnits();
    if (unit != null) {
      return (int)
          (Double.parseDouble(s.replace(",", ".")) / view.getMetricUnits().getBaseFactor());
    } else {
      throw new SilentParseException();
    }
  }

  public String displayAmount(int amount) {
    ArticleView view = getView();
    MetricUnits units = view.getMetricUnits() != null ? view.getMetricUnits() : MetricUnits.GRAM;
    return amount * units.getBaseFactor() + "";
  }

  public void loadSurchargeGroupsFor(Supplier supplier) {
    getView().setSurchargeGroup(model.getAllSurchargeGroupsFor(supplier));
  }

  public Collection<SurchargeGroup> getAllForSuppler(Supplier s) {
    return model.getAllSurchargeGroupsFor(s);
  }

  @Override
  @Key(PermissionKey.ADD_ARTICLE)
  public void addPermission() {}

  @Override
  @Key(PermissionKey.EDIT_ARTICLE)
  public void editPermission() {}

  @Override
  @Key(PermissionKey.REMOVE_ARTICLE)
  public void removePermission() {}

  @Override
  public ObjectForm<Article> getObjectContainer() {
    return getView().getArticleObjectForm();
  }

  public boolean barcodeExists(Long barcode) {
    return barcode != null && model.barcodeExists(barcode);
  }

  public boolean nameExists(String t) {
    return ArticleModel.nameExists(t);
  }

  public void validateKbNumber(Article original, Article article, Mode mode)
      throws CannotParseException {
    switch (mode) {
      case EDIT:
        if (article.getKbNumber() == original.getKbNumber()) return;
      case ADD:
        if (model.kbNumberExists(article.getKbNumber())) {
          if (getView().kbNumberAlreadyExists()) {
            int nextUnused = model.nextUnusedArticleNumber(article.getKbNumber());
            getView().setKbNumber(nextUnused);
            article.setKbNumber(nextUnused);
          } else throw new CannotParseException();
        }
    }
  }

  public Long parseLong(String s) throws CannotParseException {
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException n) {
      if (s.equals("null") | s.equals("")) return null;
      else throw new CannotParseException();
    }
  }

  @Override
  public java.util.function.Supplier<Article> defaultFactory() {
    return Article::new;
  }

  public void checkSuppliersItemNumber(Article original, Article t, Mode mode)
      throws CannotParseException {
    switch (mode) {
      case EDIT:
        if (t.getSuppliersItemNumber() == original.getSuppliersItemNumber()) return;
      case ADD:
        if (model.suppliersItemNumberExists(t.getSupplier(), t.getSuppliersItemNumber())) {
          getView().messageSuppliersItemNumberAlreadyTaken();
          throw new CannotParseException();
        }
    }

    ;
  }

  public Collection<Supplier> getSuppliers() {
    return model.getAllSuppliers();
  }

  public Collection<PriceList> getPriceLists() {
    return model.getAllPriceLists();
  }

  public Collection<MetricUnits> getMetricUnits() {
    return Arrays.asList(model.getAllUnits());
  }

  public Collection<VAT> getVats() {
    return Arrays.asList(model.getAllVATs());
  }

  public Collection<ShopRange> getAllShopRages() {
    return Arrays.asList(ShopRange.values().clone());
  }
}
