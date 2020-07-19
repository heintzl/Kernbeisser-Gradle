package kernbeisser.Windows;

import jiconfont.IconCode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.TabbedPanel.Tab;
import kernbeisser.Windows.TabbedPanel.TabbedPaneController;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Controller<V extends View<? extends Controller<? extends V,? extends M>>, M extends Model<? extends Controller<? extends V,? extends M>>> {

    @NotNull V getView();

    @NotNull M getModel();

    /**
     * return the view and initialized it
     *
     * @return the initialized view
     */
    default @NotNull V getInitializedView() {
        initView();
        return getView();
    }

    /**
     * fills the UI with data after the view and the controller already initialized
     */
    void fillUI();

    /**
     * @return the required keys that are necessary to open this View
     */
    PermissionKey[] getRequiredKeys();

    /**
     * get called if a window get closed
     *
     * @return true if the window close is allowed false when the window cannot get closed yet
     */
    default boolean commitClose() {
        return true;
    }


    /**
     * initialize the view of the controller by calling initialize function
     * then call the fillUi function in the controller to set values in the ui
     *
     * @see Controller#fillUI()
     * @see View#initialize(Controller)
     */
    default void initView() {
        try {
            Method method = getView().getClass().getDeclaredMethod("initialize", Controller.class);
            method.setAccessible(true);
            method.invoke(getView(), this);
        } catch (IllegalAccessException e) {
            Tools.showUnexpectedErrorWarning(e);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (NoSuchMethodException e) {
            Main.logger.error("failed to initialize view cannot find initialize(" + this.getClass() + ")");
        }
        fillUI();
    }

    /**
     * returns the controller with initialized view
     *
     * @return controller with initialized view
     */
    default Controller<V,M> withInitializedView() {
        initView();
        return this;
    }


    /**
     * sets default value for openAsWindow closeOld to true
     *
     * @return the result of openAsWindow(?,?,true)
     * @see Controller#openAsWindow(Window, Function, boolean)
     */
    default <W extends Window> W openAsWindow(Window parent, Function<Controller<V,M>,W> windowFactory) {
        return openAsWindow(parent, windowFactory, true);
    }

    /**
     * base function for open windowing window
     *
     * @param parent        current window which is the window which gets selected when this window gets closed
     * @param windowFactory creates a window from the controller
     * @param closeOld      if true the parent window will be set invisible until the new window gets closed
     * @param <W>           any class that implements window
     * @return a reference to the window which is now created and visible on the screen
     */
    default <W extends Window> W openAsWindow(Window parent, Function<Controller<V,M>,W> windowFactory,
                                              boolean closeOld) {
        W createWindow = windowFactory.apply(this);
        createWindow.getController().initView();
        createWindow.setContent(this);
        createWindow.setSize(getView().getSize());
        createWindow.setTitle(getView().getTitle());
        parent.openWindow(createWindow, closeOld);
        return createWindow;
    }

    /**
     * use for creating SubWindow of a existing window.
     * opens the controller as a window
     *
     * @param parent        the parent of the window
     * @param windowFactory calls a function that creates a window based on the owner and the controller
     * @param <W>           any class that implements Window
     * @return the created window
     */
    default <W extends Window> W openAsWindow(Window parent, BiFunction<Controller<V,M>,Window,W> windowFactory) {
        return openAsWindow(parent, (controller) -> windowFactory.apply(this, parent), false);
    }

    /**
     * wraps the controller with tab interface
     *
     * @param title the title of the created tab
     * @return the created Tab
     */
    default Tab asTab(String title) {
        initView();
        return new Tab() {
            @Override
            public IconCode getIcon() {
                return getController().getView().getTabIcon();
            }

            @Override
            public Controller<?,?> getController() {
                return Controller.this;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public boolean commitClose() {
                return Controller.this.commitAllClose();
            }
        };
    }

    /**
     * open tab on specific tabbedPane
     *
     * @param title                the title of the Tab
     * @param tabbedPaneController the tabbedPaneController
     * @return the Window of the TabbedPane
     * @see TabbedPaneController#openTab(String, TabbedPaneController)
     */

    default Window openTab(String title, TabbedPaneController tabbedPaneController) {
        tabbedPaneController.addTab(asTab(title));
        return tabbedPaneController.getView().getWindow();
    }

    /**
     * removes this Tab from selected TabbedPaneController
     *
     * @param tabbedPaneController the controller from the tab container
     * @see TabbedPaneController#closeTab(Tab)
     * @see Controller#asTab(String)
     */
    default void removeSelf(TabbedPaneController tabbedPaneController) {
        tabbedPaneController.closeTab(this.asTab(""));
    }

    /**
     * removes tab from this tab from DEFAULT_TABBED_PANE
     *
     * @see Controller#removeSelf(TabbedPaneController)
     */
    default void removeSelf() {
        removeSelf(TabbedPaneModel.DEFAULT_TABBED_PANE);
    }

    /**
     * open tab on default panel
     *
     * @param title the title of the Tab
     * @return the Window
     * @see TabbedPaneModel#DEFAULT_TABBED_PANE
     * @see Controller#openTab(String, TabbedPaneController)
     */
    default Window openTab(String title) {
        return openTab(title, TabbedPaneModel.DEFAULT_TABBED_PANE);
    }


    /**
     * calls commitCloseTree with this value
     *
     * @return function result of commitCloseTree
     * @see Controller#commitCloseTree(Controller)
     */
    default boolean commitAllClose() {
        return commitCloseTree(this);
    }


    /**
     * ask all components that are represented in the controller and all under controllers for closing
     *
     * @param controller the tree head
     * @return true if all components allow close
     * @see Controller#commitClose()
     */
    static boolean commitCloseTree(Controller<?,?> controller) {
        boolean b = true;
        for (Field field : controller.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //Filter lambda super reference
            if (field.getName().contains("$") && field.getName().contains("this")) {
                continue;
            }
            for (Class<?> anInterface : field.getType().getInterfaces()) {
                if (anInterface.equals(Controller.class)) {
                    field.setAccessible(true);
                    try {
                        b = b && commitCloseTree((Controller<?,?>) field.get(controller));
                    } catch (IllegalAccessException e) {
                        Tools.showUnexpectedErrorWarning(e);
                        return false;
                    }
                    break;
                }
            }
        }
        return b && controller.commitClose();
    }

    static Controller<?,?> createFakeController(JComponent content) {
        return new FakeController(content);
    }

    class FakeController implements Controller<FakeView,FakeModel> {


        private final FakeView fakeView;
        private final FakeModel model;

        FakeController(JComponent component) {
            this.fakeView = new FakeView(component);
            this.model = new FakeModel();
        }

        @NotNull
        @Override
        public FakeView getView() {
            return fakeView;
        }

        @NotNull
        @Override
        public FakeModel getModel() {
            return new FakeModel();
        }

        @Override
        public void fillUI() {

        }

        @Override
        public PermissionKey[] getRequiredKeys() {
            return new PermissionKey[0];
        }
    }

    class FakeView implements View<FakeController> {
        private final JComponent content;

        FakeView(JComponent content) {
            this.content = content;
        }

        @Override
        public void initialize(FakeController controller) {

        }

        @Override
        public @NotNull JComponent getContent() {
            return content;
        }
    }

    class FakeModel implements Model<FakeController> {

    }
}
