package kernbeisser.Windows.EditArticle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;
import javax.persistence.NoResultException;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.AccessChecking.SilentParseException;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;

public class EditArticleController extends Controller<EditArticleView, EditArticleModel> {

  private BarcodeCapture capture;

  public EditArticleController(Article article, Mode mode) {
    super(new EditArticleModel(article != null ? article : new Article(), mode));
    if (article != null && mode == Mode.REMOVE) {
      Tools.delete(article);
    }
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
  public PermissionKey[] getRequiredKeys() {
    switch (getModel().getMode()) {
      case ADD:
        return new PermissionKey[] {PermissionKey.ADD_ARTICLE};
      case EDIT:
        return new PermissionKey[] {PermissionKey.EDIT_ARTICLE};
      case REMOVE:
        return new PermissionKey[] {PermissionKey.REMOVE_ARTICLE};
    }
    throw new UnsupportedOperationException("undefined mode");
  }

  @Override
  public void fillView(EditArticleView editArticleView) {
    switch (getModel().getMode()) {
      case ADD:
        editArticleView.setActionTitle("Als neuen Artikel aufnehmen");
        editArticleView.setActionIcon(
            IconFontSwing.buildIcon(FontAwesome.PLUS, 20, new Color(0x00EE00)));
        break;
      case EDIT:
        editArticleView.setActionTitle("Änderungen übernehmen");
        getView()
            .setActionIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(0x0000BB)));
        break;
    }

    editArticleView.setPriceLists(model.getAllPriceLists());
    editArticleView.setSuppliers(model.getAllSuppliers());
    editArticleView.setUnits(model.getAllUnits());
    editArticleView.setVATs(model.getAllVATs());
    editArticleView.setSurchargeGroup(model.getAllSurchargeGroups());
    editArticleView.setShopRanges(Arrays.asList(ShopRange.values()));
    // after
    editArticleView.getArticleObjectForm().setSource(model.getSource());
    editArticleView.getArticleObjectForm().setObjectValidator(this::validateArticle);
  }

  int validateSuppliersItemNumber(String suppliersItemNumberRaw) throws CannotParseException {
    try {
      int suppliersItemNumber = Integer.parseInt(suppliersItemNumberRaw);
      try {
        Article article = Article.getBySuppliersItemNumber(getView().getSelectedSupplier(),suppliersItemNumber);
        if(getModel().getMode() == Mode.EDIT && article.getId() == getView().getArticleObjectForm().getOriginal().getId())
          return suppliersItemNumber;
        getView().suppliersItemNumberNotAvailable();
        throw new CannotParseException();
      }catch (NoResultException n){
        return suppliersItemNumber;
      }
    }catch (NumberFormatException e){
      throw new CannotParseException();
    }
  }

  private Article validateArticle(Article article) throws CannotParseException {
    if(model.getMode() == Mode.ADD) {
      Optional<Article> nearestOpt = model.findNearestArticle(article);
      if (!nearestOpt.isPresent())
        return article;
      Article nearest = nearestOpt.get();
      int distance = Tools.calculate(article.getName(), nearest.getName());
      if (distance < Setting.WARN_ARTICLE_DIFFERENCE.getIntValue()) {
        if (!getView().isSameArticle(nearest)) {
          throw new SilentParseException();
        }
      }
    }
    return article;
  }




  String validateName(String name) throws CannotParseException {
    var view = getView();
    switch (model.getMode()) {
      case EDIT:
        if (name.equals(model.getSource().getName())) return name;
      case ADD:
        if (EditArticleModel.nameExists(name)) {
          view.nameAlreadyExists();
          throw new CannotParseException("Name already taken");
        } else return name;
      default:
        throw new CannotParseException("No mode is selected");
    }
  }

  int validateKBNumber(String input) throws CannotParseException {
    var view = getView();
    try {
      int number = Integer.parseInt(input);
      switch (model.getMode()) {
        case EDIT:
          if (model.getSource().getKbNumber() == number) return number;
        case ADD:
          if (!(model.kbNumberExists(number) > -1)) return number;
          else if (view.kbNumberAlreadyExists()) {
            int next = model.nextUnusedArticleNumber(number);
            view.setKbNumber(next);
            return next;
          } else {
            throw new CannotParseException("Number is already taken");
          }
        default:
          throw new CannotParseException("No mode is selected");
      }
    } catch (NumberFormatException e) {
      throw new CannotParseException(input + " is not a number");
    }
  }

  Long validateBarcode(String input) throws CannotParseException {
    var view = getView();
    if (input.replace("null", "").equals("")) return null;
    try {
      long barcode = Long.parseLong(input);
      switch (model.getMode()) {
        case EDIT:
          if (model.getSource().getBarcode() == barcode) return barcode;
        case ADD:
          if (!(model.barcodeExists(barcode) > -1)) return barcode;
          else {
            view.barcodeAlreadyExists();
            throw new CannotParseException("Barcode is already taken");
          }
        default:
          throw new CannotParseException("No mode is selected");
      }
    } catch (NumberFormatException e) {
      throw new CannotParseException(input + " is not a barcode");
    }
  }

  void doAction() {
    var view = getView();
    if (view.getArticleObjectForm().applyMode(model.getMode())) view.back();
  }

  public int validateAmount(String s) {
    var view = getView();
    MetricUnits unit = view.getMetricUnits();
    if (unit != null) {
      return (int)
          (Double.parseDouble(s.replace(",", ".")) / view.getMetricUnits().getBaseFactor());
    } else throw new NullPointerException();
  }

  public String displayAmount(int amount) {
    var view = getView();
    MetricUnits units =
        view.getMetricUnits() != null ? view.getMetricUnits() : model.getSource().getMetricUnits();
    return amount * units.getBaseFactor() + "";
  }

  public void loadSurchargeGroupsFor(Supplier supplier) {
    getView().setSurchargeGroup(model.getAllSurchargeGroupsFor(supplier));
  }
}
