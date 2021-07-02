package kernbeisser.Windows.Supply;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.persistence.NoResultException;
import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PrintLabels.PrintLabelsController;
import kernbeisser.Windows.Supply.SupplySelector.SupplySelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;

public class SupplyController extends Controller<SupplyView, SupplyModel> {

  @Key(PermissionKey.ACTION_OPEN_SUPPLY)
  public SupplyController() {
    super(new SupplyModel());
  }

  @Override
  public void fillView(SupplyView supplyView) {
    var view = getView();
    view.setSuppliers(model.getAllSuppliers());
    JButton printButton = PrintLabelsController.getLaunchButton(view.traceViewContainer());
    printButton.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            model.print();
            getView().repaintTable();
          }
        });
    view.getPrintButtonPanel().add(printButton);
  }

  private int last;

  void searchShoppingItem(Supplier supplier, int supNr) {
    if (supNr == 0 || last == supNr) return;
    try {
      getView()
          .getObjectForm()
          .setSource(
              model.findBySuppliersItemNumber(supplier, supNr).orElseThrow(NoResultException::new));
      last = supNr;
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  private void checkInput() throws CannotParseException {
    if (!model.articleExists(getView().getSuppliersItemNumber())) {
      throw new CannotParseException();
    }
  }

  public ShoppingItem addItem(double amount) throws CannotParseException {
    checkInput();
    Article article = getView().getObjectForm().getData(null);
    ShoppingItem item = new ShoppingItem(article, 0, true);
    item.setItemMultiplier((int) Math.round(amount * item.getContainerSize()));
    model.getShoppingItems().add(item);
    model.togglePrint(article);
    getView().getObjectForm().applyMode(Mode.EDIT);
    return item;
  }

  void commit() {
    model.commit();
    model.getShoppingItems().clear();
    getView().back();
  }

  @Override
  protected boolean commitClose() {
    if (model.isPrintSelected() && getView().shouldPrintLabels()) model.print();
    return model.getShoppingItems().size() == 0 || getView().commitClose();
  }

  public void remove(ShoppingItem selectedObject) {
    model.getShoppingItems().remove(selectedObject);
  }

  public void togglePrint(ShoppingItem t) {
    model.togglePrint(t.extractArticle());
    getView().repaintTable();
  }

  public boolean becomePrinted(ShoppingItem e) {
    return model.becomePrinted(e.extractArticle());
  }

  public void openImportSupplyFile() {
    new SupplySelectorController(
            e -> {
              for (ShoppingItem item : e) {
                model.getShoppingItems().add(item);
                model.togglePrint(item.extractArticle());
              }
              getView().setShoppingItems(model.getShoppingItems());
            })
        .openIn(new SubWindow(getView().traceViewContainer()));
  }
}
