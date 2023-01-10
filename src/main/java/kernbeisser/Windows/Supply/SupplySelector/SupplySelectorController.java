package kernbeisser.Windows.Supply.SupplySelector;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.Article.ArticleController;
import kernbeisser.Reports.PriceListReport;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;
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

  public SupplySelectorController(BiConsumer<Supply, Collection<ShoppingItem>> consumer)
      throws PermissionKeyRequiredException {
    super(new SupplySelectorModel(consumer));
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
          Article article = SupplyController.findOrCreateArticle(kkSupplier, lineContent);
          openArticleWindow(lineContent, article, false);
        } catch (NoSuchElementException e) {
          Tools.showUnexpectedErrorWarning(e);
        }
        break;
      default:
    }
  }

  public void openArticle(LineContent lineContent) {
    if (lineContent.getStatus() != ResolveStatus.OK) {
      return;
    }
    try {
      Article article = Articles.getByKkItemNumber(lineContent.getKkNumber()).get();
      openArticleWindow(lineContent, article, true);
    } catch (NoSuchElementException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private void refreshLineContent(LineContent lineContent, Article article, Boolean confirmMerge) {
    if (confirmMerge && !getView().messageConfirmArticleMerge()) {
      return;
    }
    lineContent.refreshFromArticle(article);
    getView().verifyLine(lineContent);
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
                  Setting.KK_SUPPLY_TO_TIME.getIntValue(),
                  Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class)));
    }
  }

  public void loadDirAsync(@NotNull File dir) {
    new Thread(
            () -> {
              getView().setLoadingIndicatorVisible(true);
              loadDir(dir);
              getView().setLoadingIndicatorVisible(false);
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
                .map(SupplierFile::collectShoppingItems)
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
    new PriceListReport(
            supply.getAllLineContents().stream()
                .filter(e -> e.getStatus() == ResolveStatus.PRODUCE)
                .map(SupplySelectorController::wrapToPrint)
                .collect(Collectors.toList()),
            "Kornkraft Obst und Gemüse Verkaufspreise vom " + supply.getDeliveryDate())
        .sendToPrinter("Drucke Obst und Gemüse Verkaufspreise", Tools::showUnexpectedErrorWarning);
  }

  public static PriceListReportArticle wrapToPrint(LineContent content) {
    PriceListReportArticle article = new PriceListReportArticle();
    article.setSuppliersItemNumber(content.getKkNumber());
    article.setMetricUnits(content.getUnit().getName());
    article.setSuppliersShortName("KK");
    article.setShortBarcode("");
    article.setWeighAble(true);
    article.setName(content.getName());
    article.setKbNumber(0);
    article.setItemRetailPrice(calcPrice(content.getPrice()));
    return article;
  }

  public static double calcPrice(double priceBefore) {
    double calcPrice =
        priceBefore * (Setting.SURCHARGE_PRODUCE.getDoubleValue() + 1) * (VAT.LOW.getValue() + 1);
    return roundForCents(calcPrice, calcPrice < 2 ? 0.05 : calcPrice < 5 ? 0.1 : 0.2);
  }

  private static double roundForCents(double price, double cent) {
    double dis = price % cent;
    return dis < cent / 2. ? price - dis : price + (cent - dis);
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
}
