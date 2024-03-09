package kernbeisser.StartUp.LogIn;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.*;
import kernbeisser.DataImport.GenericCSVImport;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Windows.MVC.Controller;
import org.apache.commons.collections.KeyValue;
import org.jetbrains.annotations.NotNull;

public class DBLogInController extends Controller<DBLogInView, DBLogInModel> {

  private boolean startUp = false;

  public DBLogInController() throws ClassIsSingletonException {
    super(new DBLogInModel());
  }

  public static DBLogInController openDBLogInController(boolean startUp)
      throws ClassIsSingletonException {
    DBLogInController dbLogInController = new DBLogInController();
    dbLogInController.startUp = startUp;
    return dbLogInController;
  }

  public boolean isStartUp() {
    return startUp;
  }

  @Override
  public @NotNull DBLogInModel getModel() {
    return model;
  }

  @Override
  public void fillView(DBLogInView dbLogInView) {}

  @Override
  protected void closed() {
    model.close();
  }

  void logIn() {
    model.saveService(getView().getDBAccess());
    getView().connectionRefused();
  }

  public void readFile(String path) {
    DBLogInView view = getView();
    Path filePath = Paths.get(path);
    if (path.isEmpty() || !Files.exists(filePath)) {
      view.messagePathNotFound(path);
      return;
    }
    if (view.confirmCSVImport()) {

      view.clearLogMessages();
      SwingWorker importWorker =
          new SwingWorker<Void, KeyValue>() {
            @Override
            protected Void doInBackground() throws Exception {
              new GenericCSVImport(filePath, this::publish);
              return null;
            }

            @Override
            protected void process(List<KeyValue> logMessages) {
              view.showLogMessages(logMessages);
            }

            @Override
            protected void done() {
              try {
                get();
              } catch (Exception e) {
                UnexpectedExceptionHandler.showUnexpectedErrorWarning(e.getCause());
              }
            }
          };

      importWorker.execute();
    }
  }
}
