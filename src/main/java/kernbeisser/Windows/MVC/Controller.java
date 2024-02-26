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
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CloseEvent;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.Windows.ViewContainer;
import lombok.Getter;
import org.objenesis.ObjenesisStd;

public abstract class Controller<
    V extends IView<? extends Controller<? extends V, ? extends M>>,
    M extends IModel<? extends Controller<? extends V, ? extends M>>> {

  private static final ObjenesisStd VIEW_FACTORY_PROVIDER = new ObjenesisStd();

  @Getter(lazy = true)
  private final Collection<Controller<?, ?>> subControllers = findSubControllers();

  @Getter private ViewContainer container;

  private V view;

  @Getter protected final M model;

  public Controller(M model) throws PermissionKeyRequiredException {
    this.model = model;
  }

  private final Collection<CloseEvent> closeEvents = new ArrayList<>();

  static {
    activateKeyboardListener();
  }

  private static void activateKeyboardListener() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(
            new KeyEventDispatcher() {
              private transient boolean isAlreadyOpened = false;

              @Override
              public boolean dispatchKeyEvent(KeyEvent e) {
                try {
                  ControllerReference.traceBack(
                      e.getComponent(),
                      controllerReference ->
                          controllerReference.getController().processKeyboardInput(e));
                  return true;
                } catch (UnsupportedOperationException ignored) {
                  if (isAlreadyOpened) {
                    Tools.beep();
                    return false;
                  }
                  if (DBConnection.isInitialized()
                      && e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getKeyEventValue()
                      && e.getID() == KeyEvent.KEY_RELEASED) {
                    isAlreadyOpened = true;
                    JOptionPane.showMessageDialog(
                        e.getComponent(), "In diesem Fenster ist keine Barcode-Eingabe möglich");
                    isAlreadyOpened = false;
                  }
                  return false;
                }
              }
            });
  }

  public abstract void fillView(V v);

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
    openIn(TabbedPaneModel.getMainPanel().createTabViewContainer());
  }

  protected void closed() {}
  ;

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
        .filter(e -> e != null && !e.equals(this))
        .forEach(Controller::notifyClosed);
    closeEvents.forEach(CloseEvent::closed);
    closed();
  }

  protected void closeModel() {
    model.viewClosed();
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
    Class<V> viewClass = getViewClass((Class<? extends Controller<V, M>>) getClass());
    view = (V) VIEW_FACTORY_PROVIDER.getInstantiatorOf(viewClass).newInstance();
    linkViewControllerFields(view, this);
    callSetupUiMethod(view);
    callInitializeMethod(view, this);
    ControllerReference.putOn(view.getContent(), this);
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
    return false;
  }

  protected final boolean isInViewInitialize() {
    return inViewInitialize;
  }

  public static <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>>
      Class<V> getViewClass(Class<? extends Controller<V, M>> controllerClass) {
    for (Class<?> c = controllerClass; !c.equals(Object.class); c = c.getSuperclass()) {
      Type type = c.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
        for (Type actualTypeArgument : (((ParameterizedType) type).getActualTypeArguments())) {
          Class<V> viewClass;
          if (actualTypeArgument instanceof ParameterizedType) {
            viewClass = (Class<V>) ((ParameterizedType) actualTypeArgument).getRawType();
          } else viewClass = (Class<V>) actualTypeArgument;
          if (isViewClassOf(controllerClass, viewClass)) return viewClass;
        }
      }
    }
    throw new RuntimeException("cannot find view class in controller class");
  }

  public static <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>>
      boolean isViewClassOf(
          Class<? extends Controller<V, M>> controllerClass, Class<? extends V> viewClass) {
    for (Method method : viewClass.getDeclaredMethods()) {
      if (method.getName().equals("initialize")
          && method.getParameterTypes().length == 1
          && method.getParameterTypes()[0].isAssignableFrom(controllerClass)) {
        return true;
      }
    }
    return false;
  }
}
