package kernbeisser.Forms.ObjectForm.Components;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kernbeisser.Enums.Colors;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.BoundedWriteProperty;
import kernbeisser.Forms.ObjectForm.Properties.PredictableModifiable;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import kernbeisser.Useful.Date;
import rs.groump.Access;
import rs.groump.AccessDeniedException;

public class AccessCheckingField<P, V> extends JTextField
    implements ObjectFormComponent<P>,
        BoundedReadProperty<P, V>,
        BoundedWriteProperty<P, V>,
        PredictableModifiable<P>,
        DocumentListener {

  private final FocusListener noReadPermissionMaker =
      new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          e.getComponent().setForeground(UIManager.getColor("Label.foreground"));
          AccessCheckingField.super.setText("");
          e.getComponent().removeFocusListener(this);
        }
      };

  private final Color foregroundDefault = getForeground();
  private final Color backgroundDefault = getBackground();
  private boolean inputChanged = false;

  private boolean enabled = true;

  private final Setter<P, V> setter;
  private final Getter<P, V> getter;

  private final StringTransformer<V> stringTransformer;

  public AccessCheckingField(
      Getter<P, V> getter, Setter<P, V> setter, StringTransformer<V> stringTransformer) {
    this.getter = getter;
    this.setter = setter;
    this.stringTransformer = stringTransformer;
    getDocument().addDocumentListener(this);
  }

  @Override
  public void setPropertyModifiable(boolean v) {
    super.setEnabled(enabled && v);
  }

  @Override
  public void setInvalidInput() {
    if (getText().equals("")) setBackground(new Color(0xFF9999));
    else setForeground(Color.RED);
  }

  @Override
  public V get(P p) throws AccessDeniedException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyModifiable(P parent) {
    return Access.hasPermission(setter, parent);
  }

  @Override
  public V getData() throws CannotParseException {
    return stringTransformer.fromString(getText());
  }

  @Override
  public void set(P p, V t) throws AccessDeniedException {
    if (inputChanged) setter.set(p, t);
  }

  @Override
  public void setData(V v) {
    super.setText(stringTransformer.toString(v));
    inputChanged = false;
  }

  @Override
  public void setReadable(boolean v) {
    if (!v) {
      super.setText("Keine Leseberechtigung");
      setForeground(Color.RED);
      addFocusListener(noReadPermissionMaker);
    } else {
      if (getText().equals("Keine Leseberechtigung")) {
        super.setText("");
      }
      setForeground(Colors.LABEL_FOREGROUND.getColor());
      removeFocusListener(noReadPermissionMaker);
    }
  }

  void removeInvalidInputMark() {
    setForeground(foregroundDefault);
    setBackground(backgroundDefault);
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

  @Override
  public void setText(String t) {
    inputChanged = true;
    super.setText(t);
  }

  public interface StringTransformer<V> {
    default String toString(V v) {
      return String.valueOf(v);
    }

    V fromString(String s) throws CannotParseException;
  }

  public static final StringTransformer<Integer> UNSIGNED_INT_FORMER =
      new StringTransformer<Integer>() {
        @Override
        public Integer fromString(String s) throws CannotParseException {
          Integer result = INT_FORMER.fromString(s);
          if (result < 0) throw new CannotParseException("Number should be positive");
          return result;
        }

        @Override
        public String toString(Integer integer) {
          return INT_FORMER.toString(integer);
        }
      };

  public static final StringTransformer<Double> UNSIGNED_DOUBLE_FORMER =
      new StringTransformer<Double>() {
        @Override
        public Double fromString(String s) throws CannotParseException {
          Double result = DOUBLE_FORMER.fromString(s);
          if (result < 0) throw new CannotParseException("Number should be positive");
          return result;
        }

        @Override
        public String toString(Double doubleValue) {
          return DOUBLE_FORMER.toString(doubleValue);
        }
      };

  public static final StringTransformer<Double> UNSIGNED_CURRENCY_FORMER =
      new StringTransformer<Double>() {
        @Override
        public Double fromString(String s) throws CannotParseException {
          return UNSIGNED_DOUBLE_FORMER.fromString(s);
        }

        @Override
        public String toString(Double doubleValue) {
          return String.format("%.2f", doubleValue);
        }
      };

  public static final StringTransformer<Integer> INT_FORMER =
      new StringTransformer<Integer>() {
        @Override
        public String toString(Integer integer) {
          return integer.toString();
        }

        @Override
        public Integer fromString(String s) throws CannotParseException {
          try {
            return Integer.parseInt(s);
          } catch (NumberFormatException e) {
            throw new CannotParseException();
          }
        }
      };

  public static final StringTransformer<Double> DOUBLE_FORMER =
      new StringTransformer<Double>() {
        @Override
        public String toString(Double doubleValue) {
          DecimalFormat df = new DecimalFormat("0");
          df.setMaximumFractionDigits(340);
          return df.format(doubleValue);
        }

        @Override
        public Double fromString(String s) throws CannotParseException {
          try {
            return Double.parseDouble(s.replace(",", "."));
          } catch (NumberFormatException e) {
            throw new CannotParseException();
          }
        }
      };

  public static final StringTransformer<String> NONE =
      new StringTransformer<String>() {
        @Override
        public String toString(String s) {
          return s;
        }

        @Override
        public String fromString(String s) {
          return s;
        }
      };

  public static final StringTransformer<Long> LONG_FORMER =
      new StringTransformer<Long>() {
        @Override
        public String toString(Long aLong) {
          if (aLong == null) {
            return "";
          }
          return aLong.toString();
        }

        @Override
        public Long fromString(String s) throws CannotParseException {
          try {
            return Long.parseLong(s);
          } catch (NumberFormatException e) {
            throw new CannotParseException();
          }
        }
      };

  public static final StringTransformer<String> EMAIL_FORMER =
      new StringTransformer<String>() {
        @Override
        public String toString(String s) {
          return s;
        }

        @Override
        public String fromString(String s) throws CannotParseException {
          if (!s.isEmpty() && !s.matches("[^ ]+@[^ ]+\\.[^ ]+")) {
            throw new CannotParseException();
          }
          return s;
        }
      };

  public static final StringTransformer<String> NOT_NULL =
      new StringTransformer<String>() {
        @Override
        public String toString(String s) {
          return s;
        }

        @Override
        public String fromString(String s) throws CannotParseException {
          if (s.replace(" ", "").isEmpty()) {
            throw new CannotParseException(this + " doesn't contain a value");
          }
          return s;
        }
      };

  public static final StringTransformer<Instant> DAY_DATE_BEGIN_FORMER =
      new StringTransformer<Instant>() {
        @Override
        public Instant fromString(String s) throws CannotParseException {
          try {
            return Date.atStartOrEndOfDay(LocalDate.from(Date.INSTANT_DATE.parse(s)), true);
          } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                null,
                "Das angegebene Datum kann nicht eingelesen werden,\n bitte überprüfe, ob das folgende Format eingehalten wurde:\n dd:mm:yyyy");
            throw new CannotParseException();
          }
        }

        @Override
        public String toString(Instant instant) {
          return Date.INSTANT_DATE.format(instant);
        }
      };

  public static final StringTransformer<Instant> DAY_DATE_END_FORMER =
      new StringTransformer<Instant>() {
        @Override
        public Instant fromString(String s) throws CannotParseException {
          try {
            return Date.atStartOrEndOfDay(LocalDate.from(Date.INSTANT_DATE.parse(s)), false);
          } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                null,
                "Das angegebene Datum kann nicht eingelesen werden,\n bitte überprüfe, ob das folgende Format eingehalten wurde:\n dd:mm:yyyy");
            throw new CannotParseException();
          }
        }

        @Override
        public String toString(Instant instant) {
          return Date.INSTANT_DATE.format(instant);
        }
      };

  public interface StringParser<T> {
    T fromString(String s) throws CannotParseException;
  }

  public static <T> StringTransformer<T> combine(
      Function<T, String> toString, StringParser<T> fromString) {
    return new StringTransformer<T>() {
      @Override
      public String toString(T t) {
        return toString.apply(t);
      }

      @Override
      public T fromString(String s) throws CannotParseException {
        return fromString.fromString(s);
      }
    };
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
