package kernbeisser.Windows.Supply.SupplySelector;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.Article.ArticleController;
import kernbeisser.Reports.ProducePriceList;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Supply.SupplyController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.SneakyThrows;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class SupplySelectorController extends Controller<SupplySelectorView, SupplySelectorModel> {

  private final Object LOAD_LOCK = new Object();

  private final BarcodeCapture capture;

  public SupplySelectorController(BiConsumer<Supply, Collection<ShoppingItem>> consumer)
      throws PermissionKeyRequiredException {
    super(new SupplySelectorModel(consumer));
    this.capture = new BarcodeCapture(this::processBarcode);
  }

  @SneakyThrows
  @Override
  public void fillView(SupplySelectorView supplySelectorView) {
    getView().setFilterOptions(Arrays.asList(ResolveStatus.values()));
    loadDefaultDir();
  }

  private void openArticleWindow(LineContent lineContent, Article article, boolean confirmMerge) {
    FormEditorController.create(article, new ArticleController(), Mode.EDIT)
        .withCloseEvent(
            () -> {
              refreshLineContent(
                  lineContent,
                  Articles.getByKkItemNumber(article.getSuppliersItemNumber()).get(),
                  confirmMerge);
            })
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  public void editArticle(LineContent lineContent) {
    Supplier kkSupplier = Supplier.getKKSupplier();
    switch (lineContent.getStatus()) {
      case OK:
        if (!getView().messageConfirmLineMerge()) {
          return;
        }
      case ADDED:
        try {
          Article article = SupplyController.findOrCreateArticle(kkSupplier, lineContent, false);
          openArticleWindow(lineContent, article, false);
        } catch (NoSuchElementException e) {
          Tools.showUnexpectedErrorWarning(e);
        }
        break;
      default:
    }
  }

  public void openArticle(LineContent lineContent) {
    Article article = lineContent.getArticle();
    if (article == null) {
      return;
    }
    openArticleWindow(lineContent, article, true);
  }

  public void applyFilter(ResolveStatus status) {
    if (status == null) {
      getView().setLineContentFilter(l -> true);
    } else if (status == ResolveStatus.NO_PRODUCE) {
      getView()
          .setLineContentFilter(
              l -> l.getStatus() != ResolveStatus.PRODUCE && l.getStatus() != ResolveStatus.IGNORE);
    } else {
      getView().setLineContentFilter(l -> l.getStatus() == status);
    }
  }

  private void refreshLineContent(LineContent lineContent, Article article, Boolean confirmMerge) {
    if (confirmMerge && !getView().messageConfirmArticleMerge()) {
      return;
    }
    lineContent.refreshFromArticle(article);
    getView().refreshTable();
  }

  public void loadDefaultDir() {
    File defaultDir = Config.getConfig().getDefaultKornkraftInboxDir();
    if (!defaultDir.isDirectory()) {
      getView().messageDefaultDirNotFound();
      openOtherDir(defaultDir.getAbsolutePath());
      return;
    }
    loadDirAsync(defaultDir);
  }

  public void loadDir(@NotNull File dir) {
    if (!dir.isDirectory()) throw new IllegalArgumentException("Not a directory");
    synchronized (LOAD_LOCK) {
      getView()
          .setSupplies(
              Supply.extractSupplies(
                  Objects.requireNonNull(dir.listFiles()),
                  Setting.KK_SUPPLY_FROM_TIME.getIntValue(),
                  Setting.KK_SUPPLY_TO_TIME.getIntValue()));
    }
  }

  public void loadDirAsync(@NotNull File dir) {
    new Thread(
            () -> {
              try {
                getView().setLoadingIndicatorVisible(true);
                loadDir(dir);
                getView().setLoadingIndicatorVisible(false);
              } catch (Exception e) {
                Tools.showUnexpectedErrorWarning(e);
              }
            })
        .start();
  }

  public void openOtherDir(String pathBefore) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File(pathBefore));
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setDialogTitle("Kornkraft Ordner auswählen");
    fileChooser.addActionListener(
        e -> {
          File selected = fileChooser.getSelectedFile();
          if (selected == null || !selected.isDirectory()) {
            getView().back();
            return;
          }
          loadDirAsync(selected);
        });
    fileChooser.showOpenDialog(getView().getContent());
  }

  private void processBarcode(String barcode) {
    SupplySelectorView view = getView();
    Optional<LineContent> selectedLineContent = view.getSelectedLineContent();
    if (!selectedLineContent.isPresent()) {
      return;
    }
    LineContent lineContent = selectedLineContent.get();
    if (lineContent.getStatus() != ResolveStatus.ADDED) {
      return;
    }
    try {
      long longBarcode = Long.parseLong(barcode);
      if (view.messageConfirmBarcode(lineContent, barcode)) {
        lineContent.setBarcode(longBarcode);
      }
    } catch (NumberFormatException e) {
      view.messageInvalidBarcode(barcode);
    }
  }

  public void deleteCurrentSupply(ActionEvent actionEvent) {
    getView()
        .getSelectedSupply()
        .ifPresent(
            e -> {
              getView().messageConfirmDelete();
              for (SupplierFile supplierFile : e.getSupplierFiles()) {
                supplierFile.getOrigin().delete();
              }
            });
    loadDefaultDir();
  }

  public void exportShoppingItems() {
    Optional<Supply> selected = getView().getSelectedSupply();
    if (!selected.isPresent()) {
      getView().messageSelectSupplyFirst();
      return;
    }
    Supply supply = selected.get();
    model
        .getConsumer()
        .accept(
            supply,
            supply.getSupplierFiles().stream()
                .map(f -> f.collectShoppingItems(getView().getContent()))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new)));
    getView().back();
  }

  public void printProduce() {
    Optional<Supply> selection = getView().getSelectedSupply();
    if (!selection.isPresent()) {
      getView().messageSelectSupplyFirst();
      return;
    }
    Supply supply = selection.get();
    new ProducePriceList(
            supply.getAllLineContents(),
            "Kornkraft Obst und Gemüse Verkaufspreise vom " + supply.getDeliveryDate())
        .sendToPrinter("Drucke Obst und Gemüse Verkaufspreise", Tools::showUnexpectedErrorWarning);
  }

  public void viewOrders(ActionEvent actionEvent) {
    getView()
        .getSelectedSupply()
        .ifPresent(
            supply -> {
              ObjectTable<SupplierFile> ot =
                  new ObjectTable<>(
                      Columns.create("Auftragsnummern", e -> e.getHeader().getOrderNr()),
                      Columns.create(
                          "Auftragssumme",
                          e ->
                              String.format(
                                  "%.2f€",
                                  e.getContents().stream()
                                      .mapToDouble(LineContent::getTotalPrice)
                                      .sum())));
              ot.setObjects(supply.getSupplierFiles());
              SubWindow sw = new SubWindow(getView().traceViewContainer());
              var cc = new ComponentController(new JScrollPane(ot));
              sw.setTitle(
                  "Aufträge der Lierung vom " + Date.INSTANT_DATE.format(supply.getDeliveryDate()));
              cc.openIn(sw);
            });
  }

  public static String formatDisplayPrice(LineContent lineContent) {
    double priceKk = lineContent.getPriceKk();
    ResolveStatus status = lineContent.getStatus();
    String displayPrice = String.format("%.2f€", priceKk);
    if (status == ResolveStatus.OK || status == ResolveStatus.ADDED) {
      double priceKb = lineContent.getPriceKb();
      if (priceKk != priceKb) {
        displayPrice += String.format(" (%.2f€)", priceKb);
      }
    }
    return displayPrice;
  }

  public void toggleWeighable(LineContent lineContent) {
    ResolveStatus status = lineContent.getStatus();
    if ((status != ResolveStatus.ADDED && status != ResolveStatus.OK)
        || !getView()
            .confirmDialog(
                "Soll der Auswiegestatus des Artikels wirklich geändert werden?",
                "Auswiegestatus ändern")) {
      return;
    }
    lineContent.setWeighableKb(!lineContent.isWeighableKb());
    lineContent.setPriceKb(lineContent.calculatePriceKb());
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return capture.processKeyEvent(e);
  }
}
