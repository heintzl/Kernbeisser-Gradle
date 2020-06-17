package kernbeisser.CustomComponents.Verifier;

import org.intellij.lang.annotations.Language;

import javax.swing.text.JTextComponent;
import java.util.regex.Pattern;

public class RegexVerifier extends TextComponentVerifier{
    private final Pattern regex;

    public RegexVerifier(@Language("RegExp") String regex) {
        this.regex = Pattern.compile(regex);
    }

    @Override
    public boolean verify(JTextComponent component) {
        return regex.matcher(component.getText()).matches();
    }
}
