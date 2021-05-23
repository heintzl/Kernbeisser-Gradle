package kernbeisser.Forms.ObjectForm.Components;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.Instant;
import java.time.LocalDate;
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
import org.jdatepicker.DateModel;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilCalendarModel;

public class AccessCheckingDatePicker<P> extends JDatePickerImpl
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
          getJFormattedTextField().setText("");
          e.getComponent().removeFocusListener(this);
        }
      };

  private final Color foregroundDefault = getForeground();
  private final Color backgroundDefault = getBackground();
  private boolean inputChanged = false;

  private final Setter<P, Instant> setter;
  private final Getter<P, Instant> getter;

  private final boolean atStartOfDay;

  private static final Properties properties = getI18nStrings(Locale.getDefault());

  public AccessCheckingDatePicker(
      Getter<P, Instant> getter, Setter<P, Instant> setter, boolean atStartOfDay) {
    super(new JDatePanelImpl(createDateModel(), properties), new DateComponentFormatter());
    getJFormattedTextField().getDocument().addDocumentListener(this);
    getJFormattedTextField().setEditable(true);
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

  private static Properties getI18nStrings(Locale locale) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("org.jdatepicker.i18n.Text", locale);
    return convertToProperties(resourceBundle);
  }

  public static DateModel<?> createDateModel() {
    return new UtilCalendarModel();
  }

  @Override
  public void setPropertyModifiable(boolean v) {
    getJFormattedTextField().getParent().setEnabled(v);
  }

  @Override
  public void setInvalidInput() {
    if (getJFormattedTextField().getText().equals("")) setBackground(new Color(0xFF9999));
    else getJFormattedTextField().setForeground(Color.RED);
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
    DateModel<?> dateModel = getModel();
    return Date.atStartOrEndOfDay(
        LocalDate.of(dateModel.getYear(), dateModel.getMonth() + 1, dateModel.getDay()),
        atStartOfDay);
  }

  @Override
  public void set(P p, Instant t) throws PermissionKeyRequiredException {
    if (inputChanged) setter.set(p, t);
  }

  @Override
  public void setData(Instant v) {
    getJFormattedTextField().setText(Date.INSTANT_DATE.format(v));
    inputChanged = false;
  }

  @Override
  public void setReadable(boolean v) {
    if (!v) {
      getJFormattedTextField().setText("Keine Leseberechtigung");
      getJFormattedTextField().setForeground(Color.RED);
      getJFormattedTextField().addFocusListener(noReadPermissionMaker);
    } else {
      if (getJFormattedTextField().getText().equals("Keine Leseberechtigung")) {
        getJFormattedTextField().setText("");
      }
      getJFormattedTextField().setForeground(Colors.LABEL_FOREGROUND.getColor());
      getJFormattedTextField().removeFocusListener(noReadPermissionMaker);
    }
  }

  void removeInvalidInputMark() {
    getJFormattedTextField().setForeground(foregroundDefault);
    getJFormattedTextField().setBackground(backgroundDefault);
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
