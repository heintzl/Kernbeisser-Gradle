package kernbeisser.Windows.Supply.SupplySelector;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.UnsupportedLookAndFeelException;
import kernbeisser.Config.Config;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Main;
import kernbeisser.Reports.PriceListReport;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class SupplySelectorController extends Controller<SupplySelectorView, SupplySelectorModel> {

  public SupplySelectorController(Consumer<Collection<ShoppingItem>> consumer)
      throws PermissionKeyRequiredException {
    super(new SupplySelectorModel(consumer));
  }

  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    Main.buildEnvironment();
    Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
    new SupplySelectorController(e -> {}).openTab();
  }

  @SneakyThrows
  @Override
  public void fillView(SupplySelectorView supplySelectorView) {
    getView().setFilterOptions(Arrays.asList(ResolveStatus.values()));
    loadDefaultDir();
  }

  public void loadDefaultDir() {
    File defaultDir = Config.getConfig().getDefaultKornkraftInboxDir();
    if (!defaultDir.isDirectory()) {
      getView().messageDefaultDirNotFound();
      openOtherDir(defaultDir.getAbsolutePath());
      return;
    }
    loadDir(defaultDir);
  }

  public void loadDir(@NotNull File dir) {
    if (!dir.isDirectory()) throw new IllegalArgumentException("Not a directory");
    getView()
        .setSupplies(
            Supply.extractSupplies(
                Objects.requireNonNull(dir.listFiles()),
                Setting.KK_SUPPLY_FROM_TIME.getIntValue(),
                Setting.KK_SUPPLY_TO_TIME.getIntValue(),
                Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class)));
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
          loadDir(selected);
        });
    fileChooser.showOpenDialog(getView().getContent());
  }

  public void deleteCurrentSupply(ActionEvent actionEvent) {
    getView()
        .getSelectedSupply()
        .ifPresent(
            e -> {
              getView().messageCommitDelete();
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
}
