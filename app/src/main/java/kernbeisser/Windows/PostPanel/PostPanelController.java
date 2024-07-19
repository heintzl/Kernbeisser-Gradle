package kernbeisser.Windows.PostPanel;

import java.awt.*;
import java.util.function.Consumer;
import kernbeisser.Enums.PostContext;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class PostPanelController extends Controller<PostPanelView, PostPanelModel> {

  private boolean editing = false;
  private boolean confirmation = false;
  private Consumer<Boolean> confirmationConsumer;

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
    postPanelView.setPopupSize(new Dimension(model.getPopupWidth(), model.getPopupHeight()));
  }

  public void openIn(SubWindow container) {
    super.openIn(container.withSize(getView().getPopupSize()));
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

  public void saveContent(String htmlContent, Dimension popupSize) {
    model.saveContent(htmlContent, popupSize.width, popupSize.height);
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
