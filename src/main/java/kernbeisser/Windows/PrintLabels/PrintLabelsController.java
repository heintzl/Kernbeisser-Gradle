package kernbeisser.Windows.PrintLabels;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ViewContainer;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;

public class PrintLabelsController extends Controller<PrintLabelsView, PrintLabelsModel> {

  @Linked private final CollectionController<Article> articles;

  private final JButton printButton = new JButton();
  private final JLabel printSheetInfo = new JLabel();

  private boolean saved = false;

  private void onObjectsSelected(Collection<Article> articles, Boolean chosen) {
    articles.forEach(e -> e.setPrintPool(chosen ? 1 : 0));
  }

  @Key(PermissionKey.ACTION_OPEN_PRINT_LABELS)
  public PrintLabelsController() throws PermissionKeyRequiredException {
    super(new PrintLabelsModel());
    articles = PrintLabelsModel.getArticleSource();
    articles.addCollectionModifiedListener(this::refreshPrintButton);
    articles.addObjectsListener(this::onObjectsSelected);
    printButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PRINT, 20, Color.BLUE));
    printButton.addActionListener((e) -> print());

    articles
        .getView()
        .getChosen()
        .addColumnAtIndex(
            0,
            Columns.create("Anzahl", Article::getPrintPool)
                .withLeftClickConsumer(this::editPrintPool));
    ObjectTable<Article> available = articles.getView().getAvailable();
    available.addColumnAtIndex(
        0,
        Columns.create("Lieferant", PrintLabelsModel::getArticleSupplierName).withDefaultFilter());
    available.addColumn(Columns.create("Barcode", Article::getBarcode).withDefaultFilter());
    available.addColumn(Columns.create("Preisliste", Article::getPriceList).withDefaultFilter());
    articles.addControls(printButton, printSheetInfo);
    model.setPrintPoolBefore(Articles.getPrintPool());
  }

  private void editPrintPool(Article article) {
    String response = getView().inputNumber(article.getPrintPool(), false);
    boolean exit = false;
    do {
      if (response == null || response.equals("")) {
        return;
      } else {
        try {
          int alteredAmount = Integer.parseInt(response);
          if (alteredAmount > 0) {
            model.setPrintPool(article, alteredAmount);
            getView().refreshChosenArticle(article);
            refreshPrintButton();
            exit = true;
          } else {
            throw (new NumberFormatException());
          }
        } catch (NumberFormatException exception) {
          response = getView().inputNumber(article.getAmount(), true);
        }
      }
    } while (!exit);
  }

  private static void openMe(ViewContainer targetComponent) {
    new PrintLabelsController().openIn(new SubWindow(targetComponent));
  }

  public static void setLabelPrintText(JButton button) {
    boolean pendingLabels = Articles.getArticlePrintPoolSize() > 0;
    button.setText("Etiketten drucken" + (pendingLabels ? " *" : ""));
  }

  public static void refreshLaunchButton(JButton launchButton) {
    Executors.newSingleThreadScheduledExecutor()
        .schedule(() -> setLabelPrintText(launchButton), 1000, TimeUnit.MILLISECONDS);
  }

  public static JButton getLaunchButton(ViewContainer targetComponent) {
    JButton launchButton =
        new JButton() {
          @Override
          public void setEnabled(boolean b) {
            super.setEnabled(b);
          }
        };
    launchButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PRINT, 20, Color.BLUE));
    setLabelPrintText(launchButton);
    launchButton.setToolTipText("Öffnet das Fenster für den Etikettendruck");
    launchButton.addActionListener(
        e ->
            new PrintLabelsController()
                .withCloseEvent(() -> refreshLaunchButton(launchButton))
                .openIn(new SubWindow(targetComponent)));
    return launchButton;
  }

  public void refreshPrintButton() {
    int labelsPerPage = Setting.LABELS_PER_PAGE.getIntValue();
    int labelCount = articles.getModel().getLoaded().stream().mapToInt(Article::getPrintPool).sum();
    int pages = (labelCount - 1) / labelsPerPage + 1;
    int emptyLabels = pages * labelsPerPage - labelCount;
    String infoText =
        labelCount == 0
            ? "Keine Etiketten zu drucken"
            : pages
                + " Seite"
                + (pages == 1 ? " wird" : "n werden")
                + " benötigt, es ist noch Platz für "
                + emptyLabels
                + " Etikett"
                + (emptyLabels == 1 ? "" : "en");
    printButton.setEnabled(labelCount > 0);
    printButton.setText("Ausdruck starten (" + labelCount + ")");
    printSheetInfo.setFont(
        printSheetInfo.getFont().deriveFont(labelCount > 0 ? Font.BOLD : Font.PLAIN));
    printSheetInfo.setText(infoText);
  }

  private void print() {
    model.print(articles);
    if (getView().confirmPrintSuccess()) {
      articles.selectAllChosen();
      persistPrintPoolIfNecessary(false);
    }
  }

  private void persistPrintPoolIfNecessary(boolean confirm) {
    var newPrintPool = articles.getModel().getLoaded();
    if (newPrintPool.equals(model.getPrintPoolBefore())) return;
    if (confirm && !(articles.getModel().isSaveChanges() || getView().confirmChanges())) return;
    Articles.replacePrintPool(newPrintPool);
    model.setPrintPoolBefore(newPrintPool);
  }

  @Override
  public void fillView(PrintLabelsView printLabelsView) {
    articles.setLoadedAndSource(Articles.getPrintPool(), model::getAllArticles);
    articles.getView().addSearchbox(CollectionView.BOTH);
  }

  @Override
  protected void closed() {
    persistPrintPoolIfNecessary(true);
  }
}
