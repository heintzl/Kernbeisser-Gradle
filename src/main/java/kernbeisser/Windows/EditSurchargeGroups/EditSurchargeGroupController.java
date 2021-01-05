package kernbeisser.Windows.EditSurchargeGroups;

import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.hibernate.exception.ConstraintViolationException;

public class EditSurchargeGroupController
    extends Controller<EditSurchargeGroupView, EditSurchargeGroupModel> {

  public EditSurchargeGroupController() {
    super(new EditSurchargeGroupModel());
  }

  @Override
  public void fillView(EditSurchargeGroupView editSurchargeGroupView) {
    getView().getObjectForm().setObjectValidator(this::validate);
    editSurchargeGroupView.setSuppliers(model.getAllSuppliers());
    loadForCurrentSupplier();
  }

  private SurchargeGroup validate(SurchargeGroup surchargeGroup) throws CannotParseException {
    if (surchargeGroup.equals(surchargeGroup.getParent())) {
      JOptionPane.showMessageDialog(
          getView().getTopComponent(),
          "Eine Zuschlagstabelle darf sich nicht selbst als Übergruppe haben,\nbitte wähle eine andere aus.");
      throw new CannotParseException("cannot have SurchargeGroup with itself as a parent");
    }
    return surchargeGroup;
  }

  void loadForCurrentSupplier() {
    setSurchargeGroupsFor(getView().getSelectedSupplier());
  }

  void surchargeGroupSelected(Node<SurchargeGroup> surchargeGroupNode) {
    SurchargeGroup surchargeGroup = surchargeGroupNode.getValue();
    if (!surchargeGroup.getSupplier().equals(getView().getSelectedSupplier()))
      setSurchargeGroupsFor(surchargeGroup.getSupplier());
    getView().getObjectForm().setSource(surchargeGroup);
  }

  void setSurchargeGroupsFor(Supplier s) {
    getView().setSurchargeGroups(model.getSurchargeGroupTree(s));
    getView().setAllSuperGroups(model.getAllFromSupplier(s));
    getView().getObjectForm().setSource(defaultSurchargeGroupFor(s));
  }

  private SurchargeGroup defaultSurchargeGroupFor(Supplier s) {
    SurchargeGroup surchargeGroup = new SurchargeGroup();
    surchargeGroup.setSupplier(s);
    return surchargeGroup;
  }

  void editSurchargeGroup() {
    applyMode(Mode.EDIT);
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
          model.autoLinkAllInSurchargeGroup(getView().getObjectForm().getOriginal().getId());
          removeSurchargeGroup(true);
        }
      } catch (Throwable throwable) {
        Tools.showUnexpectedErrorWarning(throwable);
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
}
