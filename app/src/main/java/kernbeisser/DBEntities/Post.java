package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.util.List;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PostContext;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import rs.groump.AccessDeniedException;

@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class Post {
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  private int id;

  @Column(unique = true)
  private PostContext context;

  @Column(columnDefinition = "TEXT")
  @Getter
  private String htmlContent;

  @Column @Getter private Boolean active = false;
  @Column @Getter private Integer popupHeight;
  @Column @Getter private Integer popupWidth;

  private Post(PostContext context) {
    this.context = context;
  }

  public Post setContent(String content, int width, int height) throws AccessDeniedException {
    if (!context.isWriteable()) {
      throw new AccessDeniedException("missing Permission for " + context);
    }
    Post post = Tools.ifNull(get(), new Post(context));
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    post.htmlContent = content;
    post.popupHeight = height;
    post.popupWidth = width;
    em.merge(post);
    return post;
  }

  public Post setActive(boolean active) throws AccessDeniedException {
    if (!context.isWriteable()) {
      throw new AccessDeniedException("missing Permission for " + context);
    }
    ;
    Post post = Tools.ifNull(get(), new Post(context));
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    post.active = active;
    em.merge(post);
    return post;
  }

  public static Post getByContext(PostContext postContext) {
    List<Post> posts = DBConnection.getConditioned(Post.class, Post_.context.eq(postContext));
    if (posts.isEmpty()) {
      return new Post(postContext);
    } else {
      return posts.getFirst();
    }
  }

  private Post get() {
    return getByContext(context);
  }

  public boolean isWriteable() {
    return context.isWriteable();
  }

  public String getTitle() {
    return context.getTitle();
  }
}
