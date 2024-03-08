package kernbeisser.CustomComponents.Verifier;

import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;
import org.intellij.lang.annotations.Language;

public class RegexVerifier extends TextComponentVerifier {
  private final Pattern regex;

  public RegexVerifier(@Language("RegExp") String regex) {
    this.regex = Pattern.compile(regex);
  }

  @Override
  public boolean verify(JTextComponent component) {
    return regex.matcher(component.getText()).matches();
  }
}
