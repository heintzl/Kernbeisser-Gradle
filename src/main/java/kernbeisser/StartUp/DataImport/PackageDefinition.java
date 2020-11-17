package kernbeisser.StartUp.DataImport;

import lombok.Getter;

@Getter
public class PackageDefinition {
  public static final String TYPE_MARK = "DATA_SOURCE_DEFINITION";
  private String type = TYPE_MARK;
  private String jobs = "jobs.csv";
  private String user = "user.csv";
  private String priceLists = "priceLists.csv";
  private String suppliers = "suppliers.csv";
  private String articles = "article.csv";
}
