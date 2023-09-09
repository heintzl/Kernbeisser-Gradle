package kernbeisser.Windows.PostPanel;

import kernbeisser.DBEntities.Post;
import kernbeisser.Enums.PostContext;
import kernbeisser.Windows.MVC.IModel;

public class PostPanelModel implements IModel<PostPanelController> {

  private final Post post;

  public PostPanelModel(PostContext postContext) {
    this.post = Post.getByContext(postContext);
  }

  public boolean isEditable() {
    return post.isWriteable();
  }

  public String getHtmlContent() {
    return post.getHtmlContent();
  }

  public String getTitle() {
    return post.getTitle();
  }

  public void saveContent(String htmlContent) {
    post.setContent(htmlContent);
  }
}
