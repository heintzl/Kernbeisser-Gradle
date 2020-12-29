package kernbeisser.Windows.MVC;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.StaticMethodTransformer.RestrictedAccess;
import kernbeisser.Security.StaticMethodTransformer.StaticMethodTransformer;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CloseEvent;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.Windows.ViewContainer;
import lombok.Getter;

public abstract class Controller<
    V extends IView<? extends Controller<? extends V, ? extends M>>,
    M extends IModel<? extends Controller<? extends V, ? extends M>> > implements RestrictedAccess
{

  @Getter(lazy = true)
  private final Collection<Controller<?, ?>> subControllers = findSubControllers();

  @Getter private ViewContainer container;

  private V view;

  @Getter protected final M model;

  public Controller(M model) throws PermissionKeyRequiredException {
    this.model = model;
    if (!PermissionSet.MASTER.hasPermissions(getRequiredKeys())) {
      throw new PermissionKeyRequiredException("the PermissionSet doesn't contain the required keys: "+Arrays.toString(getRequiredKeys())+ " to open this controller: "+getClass().getCanonicalName());
    }
  }

  private final Collection<CloseEvent> closeEvents = new ArrayList<>();

  static {
    activateKeyboardListener();
  }

  private static void activateKeyboardListener() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(
            new KeyEventDispatcher() {
              @Override
              public boolean dispatchKeyEvent(KeyEvent e) {
                try {
                  return ControllerReference.traceBack(e.getComponent())
                      .getController()
                      .processKeyboardInput(e);
                } catch (UnsupportedOperationException ignored) {
                  return false;
                }
              }
            });
  }

  public abstract void fillView(V v);

  public static RestrictedAccess getRestrictedAccess(Class<? extends Controller<?,?>> clazz){
    return StaticMethodTransformer.createStaticInterface(RestrictedAccess.class,clazz);
  }

  public ViewContainer openIn(ViewContainer container) {
    this.container = container;
    container.loadController(this);
    return container;
  }

  public Controller<?, ?> withCloseEvent(CloseEvent closeEvent) {
    closeEvents.add(closeEvent);
    return this;
  }

  private boolean inViewInitialize = false;

  public V getView() {
    if (inViewInitialize) return view;
    if (view == null) {
      inViewInitialize = true;
      instantiateView();
      fillView(view);
      inViewInitialize = false;
    }
    return view;
  }

  public void openTab() {
    openIn(TabbedPaneModel.MAIN_PANEL.createTabViewContainer());
  }

  protected void closed() {};

  protected boolean commitClose() {
    return true;
  }

  public final boolean requestClose() {
    return getSubControllers().stream().allMatch(Controller::requestClose)
        && commitClose()
        && closeEvents.stream().allMatch(CloseEvent::shouldClose);
  }

  public final void notifyClosed() {
    getSubControllers().stream()
        .filter(e -> e != null && e.equals(this))
        .forEach(e -> notifyClosed());
    closeEvents.forEach(CloseEvent::closed);
    closed();
  }

  protected Collection<Controller<?, ?>> findSubControllers() {
    return Arrays.stream(getClass().getDeclaredFields())
        .filter(e -> Controller.class.isAssignableFrom(e.getType()))
        .map(this::getControllerOfField)
        .collect(Collectors.toCollection(ArrayList<Controller<?, ?>>::new));
  }

  private Controller<?, ?> getControllerOfField(Field field) {
    field.setAccessible(true);
    try {
      return (Controller<?, ?>) field.get(this);
    } catch (IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
      throw new RuntimeException(e);
    }
  }


  private void instantiateView() {
    Class<V> viewClass = getViewClass((Class<? extends Controller<V,M>>) getClass());
    view = Tools.createWithoutConstructor(viewClass);
    linkViewControllerFields(view, this);
    callSetupUiMethod(view);
    ControllerReference.putOn(view.getContent(), this);
    callInitializeMethod(view, this);
  }

  private static void linkViewControllerFields(IView<?> view, Controller<?, ?> controller) {
    Collection<Field> fields = Tools.getWithAnnotation(controller.getClass(), Linked.class);
    for (Field declaredField : Tools.getAllFields(view.getClass())) {
      if (declaredField.isAnnotationPresent(Linked.class)) {
        declaredField.setAccessible(true);
        if (declaredField.getType().isAssignableFrom(controller.getClass())) {
          try {
            declaredField.set(view, controller);
            continue;
          } catch (IllegalAccessException e) {
            Tools.showUnexpectedErrorWarning(e);
          }
        }
        for (Field field : fields) {
          if (field.getType().equals(declaredField.getType())) {
            try {
              declaredField.set(view, field.get(controller));
            } catch (IllegalAccessException e) {
              Tools.showUnexpectedErrorWarning(e);
            }
          }
        }
      }
    }
  }

  private static void callInitializeMethod(IView<?> view, Controller<?, ?> controller) {
    try {
      Method initMethod = view.getClass().getDeclaredMethod("initialize", Controller.class);
      initMethod.setAccessible(true);
      initMethod.invoke(view, controller);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
    } catch (InvocationTargetException e) {
      e.getCause().printStackTrace();
    }
  }

  private static void callSetupUiMethod(IView<?> view) {
    try {
      Method setUpUiComponents = view.getClass().getDeclaredMethod("$$$setupUI$$$");
      setUpUiComponents.setAccessible(true);
      setUpUiComponents.invoke(view);
    } catch (NoSuchMethodException ignored) {
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      Tools.showUnexpectedErrorWarning(e);
    } catch (InvocationTargetException e) {
      e.getCause().printStackTrace();
    }
  }

  public static void wrapAsController(Component component) {}

  public void addCloseEvent(CloseEvent closeEvent) {
    closeEvents.add(closeEvent);
  }

  protected boolean processKeyboardInput(KeyEvent e) {
    if (e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getKeyEventValue()) {
      JOptionPane.showMessageDialog(
          view.getTopComponent(), "In diesem Fenster ist keine Barcode-Eingabe möglich");
      return true;
    } else {
      return false;
    }
  }

  protected final boolean isInViewInitialize() {
    return inViewInitialize;
  }

  public static <V extends IView<? extends Controller<? extends V, ? extends M>>, M extends IModel<? extends Controller<? extends V, ? extends M>>> Class<V> getViewClass(Class<? extends Controller<V, M>> controllerClass) {
    Type type = controllerClass.getGenericSuperclass();

    while (!(type instanceof ParameterizedType)
        || ((ParameterizedType) type).getRawType() != Controller.class) {
      if (type instanceof ParameterizedType) {
        type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
      } else {
        type = ((Class<?>) type).getGenericSuperclass();
      }
    }
    Type out = ((ParameterizedType) type).getActualTypeArguments()[0];
    if (out instanceof ParameterizedType) {
      return (Class<V>) ((ParameterizedType) out).getRawType();
    } else return (Class<V>) out;
  }
}
