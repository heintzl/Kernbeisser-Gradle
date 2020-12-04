package kernbeisser.Windows.SynchronizeArticles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class SynchronizeArticleController
    extends Controller<SynchronizeArticleView, SynchronizeArticleModel> {

  public SynchronizeArticleController() {
    super(new SynchronizeArticleModel());
  }

  @Override
  public void fillView(SynchronizeArticleView synchronizeArticleView) {
    getView().setAllDiffs(MappedDifferences.values());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }


  public void useKernbeisser() {
    apply(true);
  }

  public void useKernbeisserAndIgnore(){
    for (ArticleDifference<?> selectedObject : getView().getSelectedObjects()) {
      getView().remove(selectedObject);
      model.resolveAndIgnoreDifference(selectedObject);
    }
  }

  public void useKornkraft() {
    apply(false);
  }


  private void apply(boolean useCurrent){
    for (ArticleDifference<?> selectedObject : getView().getSelectedObjects()) {
      getView().remove(selectedObject);
      model.resolveDifference(selectedObject,useCurrent);
    }
  }
  public void setProductGroups(){
    try {
      model.setProductGroups(Files.lines(getView().requestInputFile("json","JSON").toPath()));
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  public void importCatalog() {
    try {
      model.load(Files.lines(getView().requestInputFile("csv","BNN","bnn","txt","TXT").toPath(),
          Catalog.DEFAULT_ENCODING));

      getView().setDifferences(model.getAllDiffs());
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  @Override
  protected boolean commitClose() {
    if (model.isCatalogLoaded()) {
      try {
        model.pushToDB();
      }catch (UnsupportedOperationException e){
        getView().mergeDiffsFirst();
        return false;
      }
    }
    return true;
  }
}
