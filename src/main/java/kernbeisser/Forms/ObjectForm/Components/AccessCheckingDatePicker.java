package kernbeisser.Forms.ObjectForm.Components;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kernbeisser.Enums.Colors;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.BoundedWriteProperty;
import kernbeisser.Forms.ObjectForm.Properties.PredictableModifiable;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import kernbeisser.Useful.Date;

public class AccessCheckingDatePicker<P> extends DatePicker
    implements ObjectFormComponent<P>,
        BoundedReadProperty<P, Instant>,
        BoundedWriteProperty<P, Instant>,
        PredictableModifiable<P>,
        DocumentListener {

  private final FocusListener noReadPermissionMaker =
      new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          e.getComponent().setForeground(UIManager.getColor("Label.foreground"));
          getComponentDateTextField().setText("");
          e.getComponent().removeFocusListener(this);
        }
      };

  private final Color foregroundDefault = getForeground();
  private final Color backgroundDefault = getBackground();
  private boolean inputChanged = false;

  private final Setter<P, Instant> setter;
  private final Getter<P, Instant> getter;

  private final boolean atStartOfDay;

  public AccessCheckingDatePicker(
      Getter<P, Instant> getter, Setter<P, Instant> setter, boolean atStartOfDay) {
    super(new DatePickerSettings(Locale.GERMANY));
    getComponentDateTextField().getDocument().addDocumentListener(this);
    getComponentDateTextField().setEditable(true);
    this.getter = getter;
    this.setter = setter;
    this.atStartOfDay = atStartOfDay;
  }

  private static Properties convertToProperties(ResourceBundle resource) {
    Properties properties = new Properties();
    Enumeration<String> keys = resource.getKeys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      properties.put(key, resource.getString(key));
    }
    return properties;
  }

  @Override
  public void setPropertyModifiable(boolean v) {
    getComponentDateTextField().getParent().setEnabled(v);
  }

  @Override
  public void setInvalidInput() {
    if (getComponentDateTextField().getText().equals("")) setBackground(new Color(0xFF9999));
    else getComponentDateTextField().setForeground(Color.RED);
  }

  @Override
  public Instant get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyModifiable(P parent) {
    return Access.hasPermission(setter, parent);
  }

  @Override
  public Instant getData() throws CannotParseException {
    return Date.atStartOrEndOfDay(getDate(), atStartOfDay);
  }

  @Override
  public void set(P p, Instant t) throws PermissionKeyRequiredException {
    if (inputChanged) setter.set(p, t);
  }

  @Override
  public void setData(Instant v) {
    getComponentDateTextField().setText(Date.INSTANT_DATE.format(v));
    inputChanged = false;
  }

  @Override
  public void setReadable(boolean v) {
    if (!v) {
      getComponentDateTextField().setText("Keine Leseberechtigung");
      getComponentDateTextField().setForeground(Color.RED);
      getComponentDateTextField().addFocusListener(noReadPermissionMaker);
    } else {
      if (getComponentDateTextField().getText().equals("Keine Leseberechtigung")) {
        getComponentDateTextField().setText("");
      }
      getComponentDateTextField().setForeground(Colors.LABEL_FOREGROUND.getColor());
      getComponentDateTextField().removeFocusListener(noReadPermissionMaker);
    }
  }

  void removeInvalidInputMark() {
    getComponentDateTextField().setForeground(foregroundDefault);
    getComponentDateTextField().setBackground(backgroundDefault);
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    inputChanged = true;
    removeInvalidInputMark();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    inputChanged = true;
    removeInvalidInputMark();
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    inputChanged = true;
    removeInvalidInputMark();
  }
}
