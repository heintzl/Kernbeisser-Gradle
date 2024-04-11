package kernbeisser.Tasks.Catalog.Merge;

import kernbeisser.DBEntities.Article;
import kernbeisser.Useful.Tools;
import rs.groump.AccessDeniedException;

public enum MappedDifference implements Difference<Article, Object> {
  PRICE("Preis") {
    @Override
    public void set(Article article, Object t) throws AccessDeniedException {
      article.setNetPrice((Double) t);
    }

    @Override
    public Double get(Article article) throws AccessDeniedException {
      return article.getNetPrice();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((Double) b / (Double) a) - 1;
    }
  },
  CONTAINER_SIZE("Gebinde-Größe") {
    @Override
    public void set(Article article, Object t) throws AccessDeniedException {
      article.setContainerSize((Double) t);
    }

    @Override
    public Double get(Article article) throws AccessDeniedException {
      return article.getContainerSize();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((Double) b / (Double) a) - 1;
    }
  },
  AMOUNT("Menge") {
    @Override
    public void set(Article article, Object t) throws AccessDeniedException {
      article.setAmount((Integer) t);
    }

    @Override
    public Integer get(Article article) throws AccessDeniedException {
      return article.getAmount();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((double) (Integer) b / (double) (Integer) a) - 1;
    }
  },
  NAME("Name") {
    @Override
    public void set(Article article, Object t) throws AccessDeniedException {
      article.setName((String) t);
    }

    @Override
    public Object get(Article article) throws AccessDeniedException {
      return article.getName();
    }

    @Override
    public double distance(Object a, Object b) {
      return ((double)
              Tools.calculateStringDifference(
                  ((String) a).replaceAll(" ", ""), ((String) b).replaceAll(" ", ""))
          / ((String) b).replaceAll(" ", "").length());
    }
  };

  private final String name;

  MappedDifference(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
