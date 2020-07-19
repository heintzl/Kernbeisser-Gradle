package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.ArticleKornkraft;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ArticleDifference<T> {
    private final ArticleBase kernbeisser, catalog;
    private final Function<ArticleBase,T> getValue;
    private final BiConsumer<ArticleBase,T> setValue;
    private final String name;


    public ArticleDifference(ArticleBase kernbeisser, ArticleBase catalog, Function<ArticleBase,T> getValue,
                             BiConsumer<ArticleBase,T> setValue, String name) {
        this.getValue = getValue;
        this.kernbeisser = kernbeisser;
        this.catalog = catalog;
        this.setValue = setValue;
        this.name = name;
    }


    void applyKernbeisser() {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        ArticleKornkraft articleKornkraft = em.find(ArticleKornkraft.class, catalog.getId());
        articleKornkraft.setSynchronised(true);
        em.persist(articleKornkraft);
        em.flush();
        et.commit();
        em.close();
    }

    void applyCatalog() {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Article article = em.find(Article.class, kernbeisser.getId());
        setValue.accept(article, getValue.apply(catalog));
        em.persist(article);
        em.flush();
        et.commit();
        em.close();
    }

    public ArticleBase getKernbeisserArticle() {
        return kernbeisser;
    }

    public ArticleBase getCatalogArticle() {
        return catalog;
    }

    public T getKernbeisserVersion() {
        return getValue.apply(kernbeisser);
    }

    public T getCatalogVersion() {
        return getValue.apply(catalog);
    }

    public String getDifferenceName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleDifference<?> that = (ArticleDifference<?>) o;
        return Objects.equals(kernbeisser, that.kernbeisser) &&
               Objects.equals(catalog, that.catalog) &&
               Objects.equals(getValue, that.getValue) &&
               Objects.equals(setValue, that.setValue) &&
               Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kernbeisser, catalog, getValue, setValue, name);
    }
}
