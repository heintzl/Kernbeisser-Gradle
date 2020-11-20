package kernbeisser.Tasks.Catalog;

import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Tasks.DTO.Catalog;
import kernbeisser.Tasks.DTO.KornkraftArticle;
import kernbeisser.Tasks.DTO.KornkraftGroup;
import kernbeisser.Useful.Tree;
import lombok.Getter;

public class CatalogDataInterpreter {

  private final Catalog catalog;

  @Getter(lazy = true)
  private final HashMap<Long, KornkraftGroup> numberGroupRefMap = createNumberRefMap();

  @Getter(lazy = true)
  private final Tree<KornkraftGroup> groupTree = extractGroupsTree();

  public CatalogDataInterpreter(Catalog catalog) {
    this.catalog = catalog;
  }

  private HashMap<Long, KornkraftGroup> createNumberRefMap() {
    KornkraftArticle[] data = catalog.getData();
    HashMap<Long, KornkraftGroup> out = new HashMap<>(data.length);
    for (KornkraftArticle article : data) {
      KornkraftGroup[] groups = catalog.getBreadcrums().get(article.getInetwg());
      out.put(article.getArtnr(), groups[groups.length - 1]);
    }
    return out;
  }

  public Tree<KornkraftGroup> extractGroupsTree() {
    Tree<KornkraftGroup> tree = new Tree<>();
    for (KornkraftGroup[] value : catalog.getBreadcrums().values()) {
      tree.put(value);
    }
    return tree;
  }

  public void linkArticlesAndPersistSurchargeGroups(
      List<ArticleBase> articleBases, EntityManager em, boolean persistKornkraftGroup) {
    Supplier kk = Supplier.getKKSupplier();
    HashMap<KornkraftGroup, SurchargeGroup> surchargeGroupHashMap = new HashMap<>();
    getGroupTree()
        .overAll(
            new KornkraftGroup(),
            (parent, value) -> {
              SurchargeGroup sg = new SurchargeGroup();
              sg.setName(value.getItext());
              sg.setSupplier(kk);
              sg.setParent(surchargeGroupHashMap.get(parent));
              if (persistKornkraftGroup) em.persist(sg);
              surchargeGroupHashMap.put(value, sg);
            });
    em.flush();
    for (ArticleBase articleBase : articleBases) {
      SurchargeGroup sg =
          surchargeGroupHashMap.get(
              CatalogDataInterpreter.this
                  .getNumberGroupRefMap()
                  .get((long) articleBase.getSuppliersItemNumber()));

      if (sg != null) {
        articleBase.setSurchargeGroup(sg);
      }
    }
    ArticleBase last = null;
    ArticleBase next = null;
    for (int i = 0; i < articleBases.size(); i++) {
      ArticleBase articleBase = articleBases.get(i);
      if (articleBase.getSurchargeGroup() != null) {
        last = articleBase;
        continue;
      }
      if (next == null || next.getSuppliersItemNumber() <= articleBase.getSuppliersItemNumber()) {
        next = last;
        for (int offset = i + 1; offset < articleBases.size(); offset++) {
          SurchargeGroup sg =
              surchargeGroupHashMap.get(
                  getNumberGroupRefMap()
                      .get((long) articleBases.get(offset).getSuppliersItemNumber()));
          if (sg != null) {
            next = articleBases.get(offset);
            break;
          }
        }
      }
      if (last == null) {
        last = next;
      }
      if (Math.abs(last.getSuppliersItemNumber() - articleBase.getSuppliersItemNumber())
          < Math.abs(next.getSuppliersItemNumber() - articleBase.getSuppliersItemNumber())) {
        articleBase.setSurchargeGroup(last.getSurchargeGroup());
      } else {
        articleBase.setSurchargeGroup(next.getSurchargeGroup());
      }
    }
  }
}
