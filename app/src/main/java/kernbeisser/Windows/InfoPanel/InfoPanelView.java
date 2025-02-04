package kernbeisser.Windows.InfoPanel;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;
import kernbeisser.Config.Config;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class InfoPanelView implements IView<InfoPanelController> {

  private JPanel main;
  private JTextPane infoText;

  @Linked private InfoPanelController controller;

  private String getBuildDate() {
    try {
      File jarFile =
          new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
      return Date.INSTANT_DATE.format(Files.getLastModifiedTime(jarFile.toPath()).toInstant());
    } catch (IOException | URISyntaxException e) {
      return "(nicht gefunden)";
    }
  }

  @Override
  public void initialize(InfoPanelController controller) {
    infoText.setEditorKit(new HTMLEditorKit());
    infoText.setEditable(false);
    infoText.setText(
        "<HTML><BODY>"
            + "<table border=\"0\">"
            + "<tr><td colspan=\"2\"><h1>"
            + Setting.STORE_NAME.getStringValue()
            + " Ladenprogramm</h1></td></tr>"
            + "<tr><td valign=\"top\"><i>Beschreibung:</i></td>"
            + "<td>Dieses Programm wurde für den Ladenbetrieb der "
            + Setting.STORE_NAME.getStringValue()
            + " Verbraucher-Erzeuger-Genossenschaft"
            + " in Braunschweig (https://www.kernbeisser.de) entwickelt. "
            + "Es wurde in Java als quelloffene Software implementiert.</td></tr>"
            + "<tr><td><i>Sourcecode:</i></td><td><a href=\"https://github.com/heintzl/Kernbeisser-Gradle\">"
            + "https://github.com/heintzl/Kernbeisser-Gradle</a></td></tr>"
            + "<tr><td><div><i>Erstellt am:</i></td><td>"
            + getBuildDate()
            + "</td></tr>"
            + "<tr><td><i>Datenbank:</i></td>"
            + Config.getConfig().getDBAccessData().getUrl()
            + "<tr><td colspan=\"2\"><h1>Du bist angemeldet als "
            + LogInModel.getLoggedIn().getFullName()
            + "</h1></td></tr>"
            + "</table>"
            + "<BODY></HTML>");
    infoText.addHyperlinkListener(
        e -> {
          if (e.getEventType() == EventType.ACTIVATED) {
            try {
              Desktop.getDesktop().browse(e.getURL().toURI());
            } catch (IOException | URISyntaxException f) {
              UnexpectedExceptionHandler.showUnexpectedErrorWarning(f);
            }
          }
        });
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(500, 500);
  }

  @Override
  public String getTitle() {
    return "Softwareinformationen";
  }

  // @spotless:off

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /** Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    main = new JPanel();
    main.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    infoText = new JTextPane();
    infoText.putClientProperty("charset", "");
    main.add(infoText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }

  // @spotless:on
}
