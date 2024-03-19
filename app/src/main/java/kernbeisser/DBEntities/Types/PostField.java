package kernbeisser.DBEntities.Types;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.Post;
import kernbeisser.Enums.PostContext;

public class PostField {
  public static FieldIdentifier<Post, Integer> id = new FieldIdentifier(Post.class, "id");
  public static FieldIdentifier<Post, PostContext> context =
      new FieldIdentifier(Post.class, "context");
  public static FieldIdentifier<Post, String> htmlContent =
      new FieldIdentifier(Post.class, "htmlContent");
  public static FieldIdentifier<Post, Boolean> active = new FieldIdentifier(Post.class, "active");
}
