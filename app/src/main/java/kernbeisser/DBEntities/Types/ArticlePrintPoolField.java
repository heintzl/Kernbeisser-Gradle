package kernbeisser.DBEntities.Types;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticlePrintPool;

public class ArticlePrintPoolField {
	public static final FieldIdentifier<ArticlePrintPool, Integer> id = new FieldIdentifier<>(ArticlePrintPool.class, "id");
	public static final FieldIdentifier<ArticlePrintPool, Article> article = new FieldIdentifier<>(ArticlePrintPool.class, "article");
	public static final FieldIdentifier<ArticlePrintPool, Integer> number = new FieldIdentifier<>(ArticlePrintPool.class, "number");
}
