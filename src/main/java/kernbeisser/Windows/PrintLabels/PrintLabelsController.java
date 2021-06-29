package kernbeisser.Windows.PrintLabels;

import java.awt.*;
import java.util.Collection;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ViewContainer;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;

public class PrintLabelsController extends Controller<PrintLabelsView, PrintLabelsModel> {

  @Linked private final CollectionController<Article> articles;
  private boolean onlyCashier = false;

  private final JButton printButton = new JButton();
  private final JLabel printSheetInfo = new JLabel();

  @Key(PermissionKey.ACTION_OPEN_PERMISSION_ASSIGNMENT)
  public PrintLabelsController() throws PermissionKeyRequiredException {
    super(new PrintLabelsModel());
    articles = PrintLabelsModel.getArticleSource();
    articles.addSearchbox(Article::defaultSearch);
    printButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PRINT, 20, Color.BLUE));
    printButton.addActionListener(e -> getModel().print(articles.getModel().getLoaded()));
    articles.addControls(printButton, printSheetInfo);
  }

  private static void openMe(ViewContainer targetComponent, Collection<Article> preselection) {
    var controller = new PrintLabelsController().openIn(new SubWindow(targetComponent));
    if (!preselection.isEmpty()) {}
  }

  public static JButton getLaunchButton(
      ViewContainer targetComponent, Collection<Article> preselection) {
    int printPoolSize = Article.getPrintPool().size();
    JButton launchButton =
        new JButton("Etiketten drucken" + (printPoolSize > 0 ? "(" + printPoolSize + ")" : ""));
    launchButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PRINT, 20, Color.BLUE));
    launchButton.setToolTipText("Öffnet das Fenster für den Etikettendruck");
    launchButton.addActionListener(e -> openMe(targetComponent, preselection));
    return launchButton;
  }

  public void refreshPrintButton() {
    int labelsPerPage = Setting.LABELS_PER_PAGE.getIntValue();
    int labelCount = articles.getModel().getLoaded().size();
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

  @Override
  public void fillView(PrintLabelsView printLabelsView) {
    articles.addSelectionListener(this::refreshPrintButton);
    articles.setLoadedAndSource(Article.getPrintPool(), model::getAllArticles);
  }
}
