package kernbeisser.Windows.PostPanel;

import java.util.function.Consumer;
import kernbeisser.Enums.PostContext;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class PostPanelController extends Controller<PostPanelView, PostPanelModel> {

  boolean editing = false;
  boolean confirmation = false;
  Consumer<Boolean> confirmationConsumer;

  public PostPanelController(PostContext postContext) {
    super(new PostPanelModel(postContext));
  }

  @Override
  public @NotNull PostPanelModel getModel() {
    return model;
  }

  @Override
  public void fillView(PostPanelView postPanelView) {
    resetContent();
    postPanelView.setActive(model.getActive());
    boolean userMayEdit = model.isEditable();
    postPanelView.setEditable(userMayEdit);
    postPanelView.setActiveVisible(userMayEdit);
  }

  public void resetContent() {
    getView().setHtmlContent(model.getHtmlContent());
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

  public void back(boolean confirmed) {
    if (confirmation) {
      confirmationConsumer.accept(confirmed);
    }
    getView().back();
  }

  public PostPanelController withConfirmation(Consumer<Boolean> confirmationConsumer) {
    getView().activateConfirmation();
    confirmation = true;
    this.confirmationConsumer = confirmationConsumer;
    return this;
  }
}
