package kernbeisser.Windows.PostPanel;

import java.awt.*;
import kernbeisser.DBEntities.Post;
import kernbeisser.Enums.PostContext;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import rs.groump.AccessDeniedException;

public class PostPanelModel implements IModel<PostPanelController> {

  private Post post;

  public PostPanelModel(PostContext postContext) {
    this.post = Post.getByContext(postContext);
  }

  public boolean isEditable() {
    return post.isWriteable();
  }

  public String getHtmlContent() {
    return post.getHtmlContent();
  }

  public boolean getActive() {
    return post.getActive();
  }

  public String getTitle() {
    return post.getTitle();
  }

  public void setPostActive(boolean active) {
    try {
      post = post.setActive(active);
    } catch (AccessDeniedException e) {
      UnexpectedExceptionHandler.showErrorWarning(e, "Fehlende Berechtigung");
    }
  }

  public void saveContent(String htmlContent, int width, int height) {
    post = post.setContent(htmlContent, width, height);
  }

  public int getPopupWidth() {
    return Tools.ifNull(
        post.getPopupWidth(),
        Math.round(Setting.SUBWINDOW_SIZE_FACTOR.getFloatValue())
            * Toolkit.getDefaultToolkit().getScreenSize().width);
  }

  public int getPopupHeight() {
    return Tools.ifNull(
        post.getPopupHeight(),
        Math.round(Setting.SUBWINDOW_SIZE_FACTOR.getFloatValue())
            * Toolkit.getDefaultToolkit().getScreenSize().height);
  }
}
