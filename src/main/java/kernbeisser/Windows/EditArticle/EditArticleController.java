package kernbeisser.Windows.EditArticle;

import java.awt.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;

public class EditArticleController extends Controller<EditArticleView, EditArticleModel> {

  public EditArticleController(Article article, Mode mode) {
    super(new EditArticleModel(article != null ? article : new Article(), mode));
    if (article != null && mode == Mode.REMOVE) {
      Tools.delete(article);
    }
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
    // after
    editArticleView.getArticleObjectForm().setSource(model.getSource());
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
            return model.nextUnusedArticleNumber(number);
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

  public void loadSurchargeGroupsFor() {
    getView().setSurchargeGroup(model.getAllSurchargeGroups());
  }
}
