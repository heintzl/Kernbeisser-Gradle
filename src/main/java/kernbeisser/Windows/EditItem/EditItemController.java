package kernbeisser.Windows.EditItem;

import java.awt.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

public class EditItemController implements Controller<EditItemView, EditItemModel> {

  private EditItemView view;
  private final EditItemModel model;

  public EditItemController(Article article, Mode mode) {
    model = new EditItemModel(article != null ? article : new Article(), mode);
    if (mode == Mode.REMOVE) {
      model.doAction(article);
      return;
    } else {
      this.view = new EditItemView(this);
    }
    switch (mode) {
      case ADD:
        view.setActionTitle("Als neuen Artikel aufnehmen");
        view.setActionIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, new Color(0x00EE00)));
        break;
      case EDIT:
        view.setActionTitle("Änderungen übernehmen");
        view.setActionIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(0x0000BB)));
        break;
    }
  }

  @Override
  public @NotNull EditItemView getView() {
    return view;
  }

  @Override
  public @NotNull EditItemModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setPriceLists(model.getAllPriceLists());
    view.setSuppliers(model.getAllSuppliers());
    view.setUnits(model.getAllUnits());
    view.setContainerDefinitions(model.getAllContainerDefinitions());
    view.setVATs(model.getAllVATs());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  String validateName(String name) throws CannotParseException{
    switch (model.getMode()){
      case EDIT:
        if(name.equals(model.getSource().getName()))return name;
      case ADD:
        if (EditItemModel.nameExists(name)) {
          view.nameAlreadyExists();
          throw new CannotParseException("Name already taken");
        }else return name;
      default:
        throw new CannotParseException("No mode is selected");
    }
  }

  int validateKBNumber(String input)throws CannotParseException{
    try {
      int number = Integer.parseInt(input);
      switch (model.getMode()){
        case EDIT:
          if(model.getSource().getKbNumber()==number)return number;
        case ADD:
          if(!(model.kbNumberExists(number) > -1 ))
            return number;
          else if(view.kbNumberAlreadyExists()){
            return model.nextUnusedArticleNumber(number);
          }else {
            throw new CannotParseException("Number is already taken");
          }
        default:
          throw new CannotParseException("No mode is selected");
      }
    }catch (NumberFormatException e){
      throw new CannotParseException(input+" is not a number");
    }
  }

  Long validateBarcode(String input) throws CannotParseException{
    if(input.replace("null","").equals(""))
      return null;
    try {
      long barcode = Long.parseLong(input);
      switch (model.getMode()){
        case EDIT:
          if(model.getSource().getBarcode()==barcode)return barcode;
        case ADD:
          if(!(model.barcodeExists(barcode) > -1 ))
            return barcode;
          else {
            view.barcodeAlreadyExists();
            throw new CannotParseException("Barcode is already taken");
          }
        default:
          throw new CannotParseException("No mode is selected");
      }
    }catch (NumberFormatException e){
      throw new CannotParseException(input+" is not a barcode");
    }
  }

  void doAction() {
    view.getArticleObjectForm().applyMode(model.getMode());
  }

  public int validateAmount(String s) {
    MetricUnits unit = view.getMetricUnits();
    if(unit != null){
      return (int)(Double.parseDouble(s.replace(",",".")) / view.getMetricUnits().getBaseFactor());
    }else throw new NullPointerException();
  }

  public String displayAmount(int amount){

    return amount * (view.getMetricUnits() !=null ? view.getMetricUnits().getBaseFactor() : model.getSource().getMetricUnits().getBaseFactor())+"";
  }
}
