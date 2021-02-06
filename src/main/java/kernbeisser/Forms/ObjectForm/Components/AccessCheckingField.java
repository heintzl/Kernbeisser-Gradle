package kernbeisser.Forms.ObjectForm.Components;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kernbeisser.Enums.Colors;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.BoundedWriteProperty;
import kernbeisser.Forms.ObjectForm.Properties.Predictable;
import kernbeisser.Security.Proxy;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;

public class AccessCheckingField<P, V> extends JTextField
    implements ObjectFormComponent<P>,
        BoundedReadProperty<P, V>,
        BoundedWriteProperty<P, V>,
        Predictable<P>,
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
  public void setPropertyEditable(boolean v) {
    setEnabled(v);
  }

  @Override
  public void setInvalidInput() {
    if (getText().equals("")) setBackground(new Color(0xFF9999));
    else setForeground(Color.RED);
  }

  @Override
  public V get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyReadable(P parent) {
    return Proxy.hasPermission(getter, parent);
  }

  @Override
  public boolean isPropertyWriteable(P parent) {
    return Proxy.hasPermission(setter, parent);
  }

  @Override
  public V getData() throws CannotParseException {
    return stringTransformer.fromString(getText());
  }

  @Override
  public void set(P p, V t) throws PermissionKeyRequiredException {
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

  public interface StringTransformer<V> {
    default String toString(V v) {
      return String.valueOf(v);
    }

    V fromString(String s) throws CannotParseException;
  }

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

  @Override
  public void setText(String t) {
    inputChanged = true;
    super.setText(t);
  }

  public static final StringTransformer<Double> DOUBLE_FORMER =
      new StringTransformer<Double>() {
        @Override
        public String toString(Double integer) {
          return integer.toString();
        }

        @Override
        public Double fromString(String s) throws CannotParseException {
          try {
            return Double.parseDouble(s);
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
          if (!s.matches(
              "(?:[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
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
          if (s.replace(" ", "").equals("")) {
            throw new CannotParseException(this + " doesn't contain a value");
          }
          return s;
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
}
