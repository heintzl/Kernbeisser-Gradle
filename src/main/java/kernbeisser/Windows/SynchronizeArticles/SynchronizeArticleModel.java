package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SynchronizeArticleModel implements Model<SynchronizeArticleController> {


    void setSynchronized(ArticleBase articleBase){

    }


    Collection<ArticleDifference<?>> getAllDifferences(){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        HashMap<Integer,ArticleKornkraft> kornkraftHashMap = new HashMap<>();
        ArticleKornkraft.getAll("where synchronised = false").forEach(e -> kornkraftHashMap.put(e.getSuppliersItemNumber(),e));
        ArrayList<ArticleBase> a = new ArrayList<>();
        ArrayList<ArticleBase> b = new ArrayList<>();
        em.createQuery("select a from Article a where a.supplier.shortName = 'KK'",Article.class).getResultList().forEach(e -> {
            ArticleKornkraft kornkraft = kornkraftHashMap.get(e.getSuppliersItemNumber());
            if(kornkraft!=null){
                a.add(e);
                b.add(kornkraft);
            }
        });
        ArrayList<ArticleDifference<?>> out = new ArrayList<>();
        out.addAll(createDifference("Preis",ArticleBase::getNetPrice,ArticleBase::setNetPrice,a,b,15));
        out.addAll(createDifference("Gebindegröße",ArticleBase::getContainerSize,ArticleBase::setContainerSize,a,b,0));
        out.addAll(createDifference("Einzelpfand",ArticleBase::getSingleDeposit,ArticleBase::setSingleDeposit,a,b,0));
        out.addAll(createDifference("Kistenpfand",ArticleBase::getCrateDeposit,ArticleBase::setCrateDeposit,a,b,0));
        out.addAll(createDifference("Packungsmenge",e -> e.getAmount()*1.,(e,n) -> e.setAmount((int)Math.round(n)),a,b,0));
        return out;
    }

    private Collection<ArticleDifference<Double>> createDifference(String name, Function<ArticleBase,Double> getValue,
                                                                  BiConsumer<ArticleBase,Double> setValue, List<ArticleBase> a, List<ArticleBase> b,double allowedDiv){
        ArrayList<ArticleDifference<Double>> out = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            if(!getValue.apply(a.get(i)).equals(getValue.apply(b.get(i)))){
                if (Math.abs(getValue.apply(a.get(i)) - getValue.apply(b.get(i))) > (getValue.apply(a.get(i)) * allowedDiv)) {
                    out.add(new ArticleDifference<>(a.get(i),b.get(i),getValue,setValue,name));
                }
            }
        }
        return out;
    }

}
