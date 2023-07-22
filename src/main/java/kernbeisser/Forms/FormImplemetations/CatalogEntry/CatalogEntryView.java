package kernbeisser.Forms.FormImplemetations.CatalogEntry;

import javax.swing.*;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Utils.Setter;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CatalogEntryView implements IView<CatalogEntryController> {

  private AccessCheckingField<CatalogEntry, String> designation;
  private JPanel main;
  private AccessCheckingField<CatalogEntry, String> articleNo;

  private ObjectForm<CatalogEntry> form;

  private Setter<CatalogEntry, String> dummy = (e, s) -> e.toString();

  @Linked private CatalogEntryController CatalogEntryController;

  @Override
  public void initialize(CatalogEntryController CatalogEntryController) {
    form = new ObjectForm<>(designation, articleNo);
    form.setObjectDistinction("Der Katalog-Artikel");
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    designation =
        new AccessCheckingField<>(
            CatalogEntry::getBezeichnung, dummy, AccessCheckingField.NOT_NULL);
    articleNo =
        new AccessCheckingField<>(CatalogEntry::getArtikelNr, dummy, AccessCheckingField.NONE);
  }

  public ObjectForm<CatalogEntry> getForm() {
    return form;
  }
}
