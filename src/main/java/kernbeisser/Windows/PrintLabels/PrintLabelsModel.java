package kernbeisser.Windows.PrintLabels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Article;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Reports.ArticleLabel;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IModel;

public class PrintLabelsModel implements IModel<PrintLabelsController> {

  public static CollectionController<Article> getArticleSource() {
    return new CollectionController<>(
        new ArrayList<>(),
        Source.empty(),
        Column.create("Lieferant", a -> a.getSupplier().getName()),
        Column.create("Name", Article::getName).withStandardFilter(),
        Column.create("Ladennummer", Article::getKbNumber),
        Column.create("Lieferantennummer", Article::getSuppliersItemNumber),
        Column.create("Barcode", Article::getBarcode),
        Column.create("Preisliste", Article::getPriceList));
  }

  public Collection<Article> getAllArticles() {
    return Article.getAll(null);
  }

  void print(Collection<Article> articles) {
    new ArticleLabel(articles.stream().distinct().collect(Collectors.toList()))
        .sendToPrinter("Drucke Ladenschilder", Tools::showUnexpectedErrorWarning);
  }
}
