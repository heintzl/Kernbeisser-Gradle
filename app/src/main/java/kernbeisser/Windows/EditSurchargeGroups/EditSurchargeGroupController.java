package kernbeisser.Windows.EditSurchargeGroups;

import com.google.gson.GsonBuilder;
import jakarta.persistence.PersistenceException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import kernbeisser.Config.Config;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.hibernate.exception.ConstraintViolationException;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class EditSurchargeGroupController
    extends Controller<EditSurchargeGroupView, EditSurchargeGroupModel> {

  @Key(PermissionKey.ACTION_OPEN_EDIT_SURCHARGE_GROUPS)
  public EditSurchargeGroupController() {
    super(new EditSurchargeGroupModel());
  }

  @Override
  public void fillView(EditSurchargeGroupView editSurchargeGroupView) {
    editSurchargeGroupView.setSuppliers(
        model.getAllSuppliers().stream()
            .sorted(Comparator.comparing(Supplier::getName))
            .collect(Collectors.toList()));
    editSurchargeGroupView.setSupplier(Supplier.KK_SUPPLIER);
    loadForCurrentSupplier();
    getView().setEditAvailable(false);
  }

  public void validate(SurchargeGroup surchargeGroup, Mode mode) throws CannotParseException {
    if (surchargeGroup.equals(surchargeGroup.getParent())) {
      JOptionPane.showMessageDialog(
          getView().getTopComponent(),
          "Eine Zuschlagstabelle darf sich nicht selbst als Übergruppe haben,\nbitte wähle eine andere aus.");
      throw new CannotParseException("cannot have SurchargeGroup with itself as a parent");
    }
  }

  void loadForCurrentSupplier() {
    setSurchargeGroupsFor(getView().getSelectedSupplier());
  }

  void surchargeGroupSelected(Node<SurchargeGroup> surchargeGroupNode) {
    SurchargeGroup surchargeGroup = surchargeGroupNode.getValue();
    if (!surchargeGroup.getSupplier().equals(getView().getSelectedSupplier()))
      setSurchargeGroupsFor(surchargeGroup.getSupplier());
    getView().getObjectForm().setSource(surchargeGroup);
    getView().setEditAvailable(true);
  }

  void setSurchargeGroupsFor(Supplier s) {
    getView().setSurchargeGroups(model.getSurchargeGroupTree(s));
    getView().setAllSuperGroups(model.getAllFromSupplier(s));
    getView().getObjectForm().setSource(defaultSurchargeGroupFor(s));
  }

  private SurchargeGroup defaultSurchargeGroupFor(Supplier s) {
    return s.getOrPersistDefaultSurchargeGroup();
  }

  void editSurchargeGroup() {
    applyMode(Mode.EDIT);
  }

  void calculateSurchargeValues() {
    Map<SurchargeGroup, Map<Double, List<String>>> calcResult = Tools.productSurchargeToGroup();
    Setting.IS_DEFAULT_SURCHARGES.changeValue("false");
    try {
      File resultFile =
          Config.getConfig()
              .getReports()
              .getOutputDirectory()
              .toPath()
              .resolve("AufschlagsBerechnung.json")
              .toAbsolutePath()
              .toFile();
      FileWriter fileWriter = new FileWriter(resultFile);
      fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(calcResult));
      fileWriter.flush();
      fileWriter.close();
    } catch (IOException e) {
      UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }

  void removeSurchargeGroup() {
    removeSurchargeGroup(false);
  }

  void removeSurchargeGroup(boolean alreadyVerified) {
    try {
      if (alreadyVerified || getView().shouldDelete()) applyMode(Mode.REMOVE);
    } catch (PersistenceException persistenceException) {
      try {
        throw persistenceException.getCause();
      } catch (ConstraintViolationException exception) {
        getView().constraintViolationException();
        if (!alreadyVerified && getView().shouldBecomeAutoLinked()) {
          try {
            model.autoLinkAllInSurchargeGroup(getView().getObjectForm().getOriginal().getId());
            removeSurchargeGroup(true);
          } catch (UnsupportedOperationException e) {
            UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
          }
        }
      } catch (Throwable throwable) {
        UnexpectedExceptionHandler.showUnexpectedErrorWarning(throwable);
      }
    }
  }

  void addSurchargeGroup() {
    applyMode(Mode.ADD);
  }

  private void applyMode(Mode mode) {
    getView().getObjectForm().applyMode(mode);
    loadForCurrentSupplier();
  }

  public Collection<SurchargeGroup> getSurchargeGroups() {
    return model.getAllFromSupplier(getView().getSelectedSupplier());
  }

  public Collection<Supplier> getSuppliers() {
    return model.getAllSuppliers();
  }

  public void surchargeGroupChanged() {
    getView().setEditAvailable(true);
  }
}
