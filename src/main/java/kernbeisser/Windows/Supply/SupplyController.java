package kernbeisser.Windows.Supply;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.swing.*;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.*;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PrintLabels.PrintLabelsController;
import kernbeisser.Windows.PrintLabels.PrintLabelsModel;
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
  public final KeyCapture keyCapture;
  public final BarcodeCapture barcodeCapture;

  @Key(PermissionKey.ACTION_OPEN_SUPPLY)
  public SupplyController() {
    super(new SupplyModel());
    model.setPrintPoolBefore(ArticlePrintPool.getPrintPoolAsMap());
    keyCapture = new KeyCapture();
    barcodeCapture = new BarcodeCapture(this::processBarcode);
  }

  public void editPrintPool(ShoppingItem item) {
    Integer newValue =
        Tools.integerInputDialog(getView().getContent(), model.getPrintNumber(item), i -> i >= 0);
    if (newValue == null) {
      return;
    }
    model.setPrintNumber(item, newValue);
    getView().refreshRow(item);
  }

  public void editItemMultiplier(ShoppingItem item) {
    Integer newValue =
        Tools.integerInputDialog(
            getView().getContent(), -(int) Math.round(item.getContainerCount()));
    if (newValue == null) {
      return;
    }
    model.setContainerMultiplier(item, newValue);
    model.setPrintNumber(item, newValue);
    getView().refreshRow(item);
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
    keyCapture.addF2ToF8NumberActions(getView()::setAmount);
  }

  void searchShoppingItem(Supplier supplier, int supNr) {
    if (supNr == 0) {
      getView().noArticleFound();
      return;
    }
    try {
      getView()
          .getObjectForm()
          .setSource(
              model.findBySuppliersItemNumber(supplier, supNr).orElseThrow(NoResultException::new));
      getView().setAddAvailable(true);
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  public void processBarcode(String s) {
    try {
      getView().getObjectForm().setSource(PrintLabelsModel.getByBarcode(s));
      getView().addItem();
    } catch (NoResultException e) {
      Tools.noArticleFoundForBarcodeWarning(getView().getContent(), s);
    }
  }

  private void checkInput() throws CannotParseException {
    if (!model.articleExists(getView().getSelected(), getView().getSuppliersItemNumber())) {
      throw new NoResultException();
    }
  }

  public void addItem(double amount) throws CannotParseException {
    checkInput();
    Article article = getView().getObjectForm().getData(null);
    if (!article.equals(getView().getObjectForm().getOriginal())) {
      if (!getView().confirmChanges()) {
        getView().getObjectForm().setSource(getView().getObjectForm().getOriginal());
        return;
      }
      getView().getObjectForm().setShowSuccessDialog(false);
      getView().getObjectForm().applyMode(Mode.EDIT);
    }
    ShoppingItem item = new ShoppingItem(article, 0, false);
    model.setContainerMultiplier(item, amount);
    model.addShoppingItem(item);
    setPrintNumber(item);
    getView().noArticleFound();
    recalculateTotal();
    getView().setShoppingItems(model.getShoppingItems());
  }

  void commit() {
    model.commit();
    model.clearShoppingItems();
    recalculateTotal();
    getView().back();
  }

  @Override
  protected boolean commitClose() {
    if (model.isPrintSelected() && getView().shouldPrintLabels()) model.print();
    return model.getShoppingItems().size() == 0 || getView().commitClose();
  }

  public void remove(ShoppingItem selectedObject) {
    model.removeShoppingItem(selectedObject);
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

  public void setPrintNumber(ShoppingItem item) {
    model.setPrintNumber(item, SupplyModel.getPrintNumberFromItem(item));
    getView().repaintTable();
  }

  public void increaseItemPrintNumber(ShoppingItem item) {
    model.setPrintNumber(item, model.getPrintNumber(item) + 1);
    getView().refreshRow(item);
  }

  public int getPrintNumber(ShoppingItem item) {
    return model.getPrintNumber(item);
  }

  public void openImportSupplyFile() {
    Dimension viewSize = getView().getSize();
    Dimension size =
        new Dimension(
            (int) viewSize.getWidth(),
            (int) (viewSize.getHeight() * Setting.SUBWINDOW_SIZE_FACTOR.getFloatValue()));
    new SupplySelectorController(
            (supply, shoppingItems) -> {
              for (ShoppingItem item : shoppingItems) {
                model.addShoppingItem(item);
                setPrintNumber(item);
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
        .openIn(new SubWindow(getView().traceViewContainer()).withSize(size));
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
    SupplyModel.checkFractionalItemMultiplier(rawItemMultiplier, content.getKkNumber());
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
    article.setNetPrice(content.getPrice());
    article.setMetricUnits(content.getUnit());
    article.setAmount(content.getAmount());
    article.setWeighable(content.isWeighableKb());
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

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return keyCapture.processKeyEvent(e) || barcodeCapture.processKeyEvent(e);
  }
}
