package kernbeisser.Forms.FormImplemetations.CatalogEntry;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CatalogEntryView implements IView<CatalogEntryController> {

  private AccessCheckingField<CatalogEntry, String> designation;
  private JPanel main;
  private AccessCheckingField<CatalogEntry, String> name;
  private AccessCheckingField<CatalogEntry, String> articleNo;
  private JPanel formContent;

  private ObjectForm<CatalogEntry> form;

  private Setter<CatalogEntry, String> dummy = (e, s) -> e.toString();

  @Linked private CatalogEntryController CatalogEntryController;

  @Override
  public void initialize(CatalogEntryController CatalogEntryController) {

    Set<String> ignoredFields =
        new HashSet<>(
            Arrays.asList("artikelNr", "bezeichnung", "id", "aenderungsZeit", "hersteller"));
    Field[] catalogFields = CatalogEntry.class.getDeclaredFields();
    Map<Field, ObjectFormComponent<CatalogEntry>> fieldComponents = new HashMap<>();
    Method[] catalogMethods = CatalogEntry.class.getDeclaredMethods();
    EmptyBorder componentBorder = new EmptyBorder(5, 15, 5, 15);
    LineBorder fieldBorder = new LineBorder(Color.gray, 1);

    for (Field field : catalogFields) {
      String fieldName = field.getName();
      if (ignoredFields.contains(fieldName)) continue;
      fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      Type fieldType = field.getType();
      boolean isBoolean = fieldType.equals(boolean.class);
      String getterName = (isBoolean ? "is" : "get") + fieldName;
      Optional<Method> getterMethod =
          Arrays.stream(catalogMethods).filter(m -> m.getName().equals(getterName)).findAny();
      String readableFieldname = "";
      for (int i = 0; i < fieldName.length(); i++) {
        String c = fieldName.substring(i, i + 1);
        if (c.equals(c.toUpperCase())) {
          readableFieldname += " ";
        }
        readableFieldname += c;
      }
      if (getterMethod.isPresent()) {
        Getter<CatalogEntry, String> getter;
        switch (fieldType.toString()) {
          case "class java.lang.Boolean":
            getter =
                p -> {
                  try {
                    return (Boolean) getterMethod.get().invoke(p) ? "ja" : "nein";
                  } catch (NullPointerException
                      | InvocationTargetException
                      | IllegalAccessException e) {
                    return "";
                  }
                };
            break;
          case "class java.time.Instant":
            getter =
                p -> {
                  try {
                    Instant date = (Instant) getterMethod.get().invoke(p);
                    return Date.INSTANT_DATE.format(date);
                  } catch (NullPointerException
                      | InvocationTargetException
                      | IllegalAccessException
                      | DateTimeParseException e) {
                    return "";
                  }
                };
            break;
          default:
            getter =
                p -> {
                  try {
                    return String.valueOf(getterMethod.get().invoke(p));
                  } catch (InvocationTargetException | IllegalAccessException e) {
                    return "";
                  }
                };
        }
        AccessCheckingField<CatalogEntry, String> formComponent =
            new AccessCheckingField<>(getter, dummy, AccessCheckingField.NONE);
        formComponent.setEditable(false);
        formComponent.setBorder(fieldBorder);
        JLabel formLabel = new JLabel(readableFieldname + ":");
        formLabel.setBorder(componentBorder);
        formContent.add(formLabel);
        formContent.add(formComponent);
        fieldComponents.put(field, formComponent);
      }
    }
    List<ObjectFormComponent<CatalogEntry>> formComponents = new ArrayList<>();
    formComponents.add(articleNo);
    formComponents.add(name);
    formComponents.addAll(fieldComponents.values());
    form = new ObjectForm<CatalogEntry>(formComponents);
    form.setObjectDistinction("Der Katalog-Artikel");
  }

  private void createUIComponents() {
    articleNo =
        new AccessCheckingField<>(CatalogEntry::getArtikelNr, dummy, AccessCheckingField.NONE);
    name = new AccessCheckingField<>(CatalogEntry::getBezeichnung, dummy, AccessCheckingField.NONE);
    formContent = new JPanel(new GridLayout(0, 6));
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public ObjectForm<CatalogEntry> getForm() {
    return form;
  }
}
