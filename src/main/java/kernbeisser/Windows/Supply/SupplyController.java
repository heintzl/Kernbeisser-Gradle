package kernbeisser.Windows.Supply;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Security.Key;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PrintLabels.PrintLabelsController;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
import kernbeisser.Windows.Supply.SupplySelector.ResolveStatus;
import kernbeisser.Windows.Supply.SupplySelector.SupplySelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.Cleanup;
import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SupplyController extends Controller<SupplyView, SupplyModel> {

  public static final Logger logger = LogManager.getLogger(SupplyController.class);

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
      getView().setAddAvailable(true);
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  private void checkInput() throws CannotParseException {
    if (!model.articleExists(getView().getSelected(),getView().getSuppliersItemNumber())) {
      throw new NoResultException();
    }
  }

  public ShoppingItem addItem(double amount) throws CannotParseException {
    checkInput();
    Article article = getView().getObjectForm().getData(null);
    ShoppingItem item = new ShoppingItem(article, 0, false);
    double rawItemMultiplier =
        (item.isWeighAble()
                ? item.getMetricUnits().inUnit(MetricUnits.GRAM, item.getAmount() * amount)
                : amount * item.getContainerSize())
            * -1;
    checkFractionalItemMultiplier(rawItemMultiplier, item.getSuppliersItemNumber());
    item.setItemMultiplier((int) Math.round(rawItemMultiplier));
    model.addToPrint(article);
    model.getShoppingItems().add(item);
    getView().getObjectForm().setShowSuccessDialog(false);
    getView().getObjectForm().applyMode(Mode.EDIT);
    getView().noArticleFound();
    recalculateTotal();
    return item;
  }

  void commit() {
    model.commit();
    model.getShoppingItems().clear();
    recalculateTotal();
    getView().back();
  }

  @Override
  protected boolean commitClose() {
    if (model.isPrintSelected() && getView().shouldPrintLabels()) model.print();
    return model.getShoppingItems().size() == 0 || getView().commitClose();
  }

  public void remove(ShoppingItem selectedObject) {
    model.getShoppingItems().remove(selectedObject);
    recalculateTotal();
  }

  public void recalculateTotal() {

    double items =
        model.getShoppingItems().stream()
            .mapToDouble(
                e ->
                    Math.abs(
                        e.getItemNetPrice()
                            * (e.isWeighAble()
                                ? (e.getItemMultiplier() / 1000.)
                                : e.getItemMultiplier())))
            .sum();
    double produce = model.getAppendedProducePrice();

    getView().setTotal(produce + items);
    getView().setProduce(produce);
  }

  public void togglePrint(ShoppingItem t) {
    model.togglePrint(t.getArticleNow().get());
    getView().repaintTable();
  }

  public boolean becomePrinted(ShoppingItem e) {
    return model.becomePrinted(e.getArticleNow().get());
  }

  public void openImportSupplyFile() {
    new SupplySelectorController(
            (supply, shoppingItems) -> {
              for (ShoppingItem item : shoppingItems) {
                model.getShoppingItems().add(item);
                model.togglePrint(item.getArticleNow().get());
              }
              getView().setShoppingItems(model.getShoppingItems());
              getModel()
                  .setAppendedProducePrice(
                      supply.getAllLineContents().stream()
                          .filter(e -> e.getStatus() == ResolveStatus.PRODUCE)
                          .mapToDouble(LineContent::getTotalPrice)
                          .sum());
              recalculateTotal();
            })
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  public static boolean shouldBecomeShoppingItem(LineContent content) {
    return !(content.getStatus() == ResolveStatus.IGNORE
        || content.getStatus() == ResolveStatus.PRODUCE);
  }

  public static Article findOrCreateArticle(Supplier kkSupplier, LineContent content) {
    Optional<Article> articleInDb =
        Articles.getBySuppliersItemNumber(kkSupplier, content.getKkNumber());
    if (articleInDb.isPresent()) {
      Article article = articleInDb.get();
      if (article.getNetPrice() != content.getPrice()) {
        article.setNetPrice(content.getPrice());
        logger.info(
            "Article price change ["
                + article.getSuppliersItemNumber()
                + "]: "
                + article.getNetPrice()
                + " -> "
                + content.getPrice());
        changePrice(kkSupplier, article.getSuppliersItemNumber(), content.getPrice());
      }
    }

    return articleInDb.orElseGet(() -> createArticle(content));
  }

  public static void changePrice(Supplier supplier, int suppliersItemNumber, double newPrice) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Article article =
        em.createQuery(
                "select a from Article a where a.supplier = :s and a.suppliersItemNumber = :sn",
                Article.class)
            .setParameter("s", supplier)
            .setParameter("sn", suppliersItemNumber)
            .getSingleResult();
    article.setNetPrice(newPrice);
    em.persist(article);
    em.flush();
  }

  public static ShoppingItem createShoppingItem(Supplier kkSupplier, LineContent content) {
    ShoppingItem shoppingItem =
        new ShoppingItem(findOrCreateArticle(kkSupplier, content), 0, false);
    double rawItemMultiplier =
        (shoppingItem.isWeighAble()
                ? getAsItemMultiplierAmount(content)
                : content.getContainerMultiplier() * content.getContainerSize())
            * -1;
    checkFractionalItemMultiplier(rawItemMultiplier, content.getKkNumber());
    shoppingItem.setItemMultiplier((int) Math.round(rawItemMultiplier));
    return shoppingItem;
  }

  private static double getAsItemMultiplierAmount(LineContent content) {
    return content
        .getUnit()
        .inUnit(
            MetricUnits.GRAM,
            content.getContainerMultiplier() * content.getContainerSize() * content.getAmount());
  }

  public static void checkFractionalItemMultiplier(double itemMultiplier, int kkNumber) {
    if (itemMultiplier % 1 != 0) {
      Main.logger.warn(
          String.format(
              "fractional item multiplier while reading KKSupplierFile content Article[%s] itemmultiplier: [%f]",
              kkNumber, itemMultiplier));
    }
  }

  private static @NotNull Article createArticle(LineContent content) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Supplier kkSupplier = Supplier.getKKSupplier();
    Article pattern = Articles.nextArticleTo(em, content.getKkNumber(), kkSupplier);
    Article article = new Article();
    article.setSupplier(kkSupplier);
    article.setName(content.getName());
    article.setNetPrice(content.getPrice() / content.getContainerSize());
    article.setMetricUnits(content.getUnit());
    article.setAmount(content.getAmount());
    article.setWeighable(content.getContainerSize() == 1);
    article.setContainerSize(content.getContainerSize());
    article.setShopRange(ShopRange.NOT_IN_RANGE);
    article.setSurchargeGroup(pattern.getSurchargeGroup());
    article.setVat(pattern.getVat());
    article.setPriceList(pattern.getPriceList());
    article.setVerified(false);
    article.setKbNumber(Articles.nextFreeKBNumber(em));
    article.setSuppliersItemNumber(content.getKkNumber());
    em.persist(article);
    em.flush();
    return article;
  }
}
