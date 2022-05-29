package kernbeisser.Windows.ManagePriceLists;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.Article.ArticleController;
import kernbeisser.Security.Key;
import kernbeisser.Windows.EditPriceList.EditPriceListController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class ManagePriceListsController
    extends Controller<ManagePriceListsView, ManagePriceListsModel> implements ActionListener {

  @Key(PermissionKey.ACTION_OPEN_MANAGE_PRICE_LISTS)
  public ManagePriceListsController() {
    super(new ManagePriceListsModel());
  }

  @Override
  public @NotNull ManagePriceListsModel getModel() {
    return model;
  }

  public Node<PriceList> getNode() {
    return PriceList.getPriceListsAsNode();
  }

  private void move() {
    if (getView().getSelectedNode() == null) getView().messageSelectionRequired();
    else getView().requestPriceListSelection(this::move);
  }

  private void move(Node<PriceList> target) {
    PriceList selected = getView().getSelectedNode().getValue();
    if (target.getValue().getId() == selected.getId()) {
      getView().cannotMoveIntoSelf();
      return;
    }
    if (getView().commitMovement(selected, target.getValue())) {
      model.setSuperPriceList(selected, target.getValue());
      getView().requestRepaint();
    }
  }

  private void rename() {
    String name = getView().requestName();
    if (name.isEmpty()) {
      return;
    }
    try {
      model.renamePriceList(getView().getSelectedNode().getValue(), name);
      getView().requestRepaint();
    } catch (PersistenceException e) {
      getView().nameAlreadyExists(name);
      rename();
    }
  }

  private void add() {
    if (getView().getSelectedNode() == null) {
      getView().messageSelectionRequired();
      return;
    }
    String name = getView().requestName();
    if (name.isEmpty()) {
      return;
    }
    try {
      model.add(getView().getSelectedNode(), name);
      getView().requestRepaint();
    } catch (PersistenceException e) {
      getView().nameAlreadyExists(name);
    }
  }

  private void remove() {
    try {
      model.deletePriceList(getView().getSelectedNode().getValue());
      getView().requestRepaint();
    } catch (PersistenceException e) {
      getView().cannotDelete();
    }
  }

  private void moveItems(Node<PriceList> target) {
    ManagePriceListsView view = getView();
    Collection<Article> articles = view.getSelectedArticles();
    if (articles.isEmpty()) {
      view.warningNoArticlesSelected();
      return;
    }
    if (view.commitItemMovement(view.getSelectedNode().getValue(), target.getValue())) {
      model.moveItems(articles, target.getValue());
      view.refreshNode();
    }
  }

  private void moveItems() {
    if (getView().getSelectedNode() == null) {
      getView().messageSelectionRequired();
      return;
    }
    getView().requiresPriceListLeaf(this::moveItems);
    getView().refreshNode();
  }

  private void edit() {
    PriceList selection = getView().getSelectedNode().getValue();
    new EditPriceListController(selection).openIn(new SubWindow(getView().traceViewContainer()));
    getView().refreshNode();
  }

  private void print() {
    ManagePriceListsView view = getView();
    if (view.getSelectedNode() == null) {
      view.messageSelectionRequired();
    } else {
      PriceList selectedList = view.getSelectedNode().getValue();
      List<Article> articles = getAllArticles(selectedList);
      if (articles.size() == 0) {
        view.messageSelectionRequired();
      } else {
        model.print(selectedList);
      }
    }
  }

  List<Article> getAllArticles(PriceList priceList) {
    return priceList.getAllArticles();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand().toUpperCase()) {
      case "ADD":
        add();
        break;
      case "RENAME":
        rename();
        break;
      case "MOVE":
        move();
        break;
      case "DELETE":
        remove();
        break;
      case "MOVE_ITEMS":
        moveItems();
        break;
      case "PRINT":
        print();
        break;
      case "EDIT":
        edit();
        break;
      default:
        throw new UnsupportedOperationException(e.getActionCommand() + " is not a valid command");
    }
  }

  @Override
  public void fillView(ManagePriceListsView managePriceListsView) {}

  public void editSelectedArticle(ActionEvent actionEvent) {
    editArticle(
        getView().getSelectedArticles().stream()
            .findFirst()
            .orElseThrow(NoSuchElementException::new));
  }

  public void editArticle(Article article) {
    FormEditorController.create(article, new ArticleController(), Mode.EDIT)
        .withCloseEvent(getView()::refreshNode)
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  public void addArticle(ActionEvent event) {
    Article article = new Article();
    article.setPriceList(getView().getSelectedNode().getValue());
    FormEditorController.create(article, new ArticleController(), Mode.ADD)
        .withCloseEvent(getView()::refreshNode)
        .openIn(new SubWindow(getView().traceViewContainer()));
  }
}
