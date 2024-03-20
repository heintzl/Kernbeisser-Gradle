package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class PostField {
public static FieldIdentifier<kernbeisser.DBEntities.Post,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.Post.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.Post,kernbeisser.Enums.PostContext> context = new FieldIdentifier<>(kernbeisser.DBEntities.Post.class, kernbeisser.Enums.PostContext.class, "context");
public static FieldIdentifier<kernbeisser.DBEntities.Post,java.lang.String> htmlContent = new FieldIdentifier<>(kernbeisser.DBEntities.Post.class, java.lang.String.class, "htmlContent");
public static FieldIdentifier<kernbeisser.DBEntities.Post,java.lang.Boolean> active = new FieldIdentifier<>(kernbeisser.DBEntities.Post.class, java.lang.Boolean.class, "active");

}