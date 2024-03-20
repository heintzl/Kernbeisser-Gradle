package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PostContext;

public class PostField {
public static FieldIdentifier<Post,Integer> id = new FieldIdentifier<>(Post.class, Integer.class, "id");
public static FieldIdentifier<Post, PostContext> context = new FieldIdentifier<>(Post.class, PostContext.class, "context");
public static FieldIdentifier<Post,String> htmlContent = new FieldIdentifier<>(Post.class, String.class, "htmlContent");
public static FieldIdentifier<Post,Boolean> active = new FieldIdentifier<>(Post.class, Boolean.class, "active");

}