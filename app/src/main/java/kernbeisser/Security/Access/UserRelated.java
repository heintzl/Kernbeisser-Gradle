package kernbeisser.Security.Access;

import kernbeisser.DBEntities.User;
import org.jetbrains.annotations.NotNull;

public interface UserRelated {

  boolean isInRelation(@NotNull User user);
}
