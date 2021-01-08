package kernbeisser.Tasks.Catalog.Merge;

import kernbeisser.DBEntities.Article;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.Tools;

public enum MappedDifferences implements Difference<Article, Object> {
  PRICE("Preis") {
    @Override
    public void set(Article article, Object t) throws PermissionKeyRequiredException {
      article.setNetPrice((Double) t);
    }

    @Override
    public Double get(Article article) throws PermissionKeyRequiredException {
      return article.getNetPrice();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((Double) b / (Double) a);
    }
  },
  CONTAINER_SIZE("Gebinde-Größe") {
    @Override
    public void set(Article article, Object t) throws PermissionKeyRequiredException {
      article.setContainerSize((Double) t);
    }

    @Override
    public Double get(Article article) throws PermissionKeyRequiredException {
      return article.getContainerSize();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((Double) b / (Double) a);
    }
  },
  DEPOSIT("Pfand") {
    @Override
    public void set(Article article, Object t) throws PermissionKeyRequiredException {
      article.setSingleDeposit((Double) t);
    }

    @Override
    public Double get(Article article) throws PermissionKeyRequiredException {
      return article.getSingleDeposit();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((Double) b / (Double) a);
    }
  },
  CONTAINER_DOPSIT("Kisten-Pfand") {
    @Override
    public void set(Article article, Object t) throws PermissionKeyRequiredException {
      article.setContainerDeposit((Double) t);
    }

    @Override
    public Double get(Article article) throws PermissionKeyRequiredException {
      return article.getContainerDeposit();
    }

    @Override
    public String getName() {
      return "Kistenpfand";
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((Double) b / (Double) a);
    }
  },
  AMOUNT("Menge") {
    @Override
    public void set(Article article, Object t) throws PermissionKeyRequiredException {
      article.setAmount((Integer) t);
    }

    @Override
    public Integer get(Article article) throws PermissionKeyRequiredException {
      return article.getAmount();
    }

    @Override
    public double distance(Object a, Object b) {
      return Math.abs((double) (Integer) b / (double) (Integer) a);
    }
  },
  NAME("Name") {
    @Override
    public void set(Article article, Object t) throws PermissionKeyRequiredException {
      article.setName((String) t);
    }

    @Override
    public Object get(Article article) throws PermissionKeyRequiredException {
      return article.getName();
    }

    @Override
    public double distance(Object a, Object b) {
      return ((double)
              Tools.calculate(((String) a).replaceAll(" ", ""), ((String) b).replaceAll(" ", ""))
          / ((String) b).replaceAll(" ", "").length()
          * 100.);
    }
  };

  private final String name;

  MappedDifferences(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
