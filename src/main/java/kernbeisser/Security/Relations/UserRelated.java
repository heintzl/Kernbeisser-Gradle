package kernbeisser.Security.Relations;

import kernbeisser.DBEntities.User;
import org.jetbrains.annotations.NotNull;

public interface UserRelated {

  boolean isInRelation(@NotNull User user);
}
