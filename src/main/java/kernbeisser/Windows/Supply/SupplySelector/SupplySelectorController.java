package kernbeisser.Windows.Supply.SupplySelector;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.UnsupportedLookAndFeelException;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Main;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Windows.MVC.Controller;
import lombok.SneakyThrows;

public class SupplySelectorController extends Controller<SupplySelectorView, SupplySelectorModel> {

  public SupplySelectorController(Consumer<Collection<ShoppingItem>> consumer)
      throws PermissionKeyRequiredException {
    super(new SupplySelectorModel(consumer));
  }

  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    Main.buildEnvironment();
    Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
    new SupplySelectorController((e) -> {}).openTab();
  }

  @SneakyThrows
  @Override
  public void fillView(SupplySelectorView supplySelectorView) {
    getView().setFilterOptions(Arrays.asList(ResolveStatus.values()));
    loadSettingsDir();
  }

  public void loadSettingsDir() {
    File directory = new File(Setting.KK_SUPPLY_DIR.getStringValue());
    if (!directory.isDirectory()) {
      requestDirectoryChange(Setting.KK_SUPPLY_DIR.getStringValue());
      return;
    }
    getView()
        .setSupplies(
            Supply.extractSupplies(
                directory.listFiles(),
                Setting.KK_SUPPLY_FROM_TIME.getIntValue(),
                Setting.KK_SUPPLY_TO_TIME.getIntValue(),
                Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class)));
  }

  public void requestDirectoryChange() {
    requestDirectoryChange(Setting.KK_SUPPLY_DIR.getValue());
  }

  public void requestDirectoryChange(String pathBefore) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File(pathBefore));
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.addActionListener(
        e -> {
          File selected = fileChooser.getSelectedFile();
          if (selected == null || !selected.isDirectory()) {
            getView().back();
            return;
          }
          Setting.KK_SUPPLY_DIR.changeValue(selected.getAbsolutePath());
          loadSettingsDir();
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
    loadSettingsDir();
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
}
