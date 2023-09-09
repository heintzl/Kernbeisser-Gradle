package kernbeisser.Windows.PostPanel;

import kernbeisser.Enums.PostContext;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class PostPanelController extends Controller<PostPanelView, PostPanelModel> {

  boolean editing = false;

  public PostPanelController(PostContext postContext) {
    super(new PostPanelModel(postContext));
  }

  @Override
  public @NotNull PostPanelModel getModel() {
    return model;
  }

  @Override
  public void fillView(PostPanelView postPanelView) {
    postPanelView.setHtmlContent(model.getHtmlContent());
    postPanelView.setActive(model.getActive());
    boolean userMayEdit = model.isEditable();
    postPanelView.setEditable(userMayEdit);
    postPanelView.setActiveVisible(userMayEdit);
  }

  public void setActive(boolean active) {
    model.setPostActive(active);
  }

  public void toggleEditing() {
    if (editing) {
      getView().stopEditSession();
    } else {
      getView().startEditSession();
    }
    editing = !editing;
  }

  public void saveContent(String htmlContent) {
    model.saveContent(htmlContent);
  }
}
