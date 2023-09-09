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
    postPanelView.setEditable(model.isEditable());
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
