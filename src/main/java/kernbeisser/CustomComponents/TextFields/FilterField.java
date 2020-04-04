package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Enums.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.util.function.Function;

public class FilterField extends PermissionField {
    private String lastCorrect = "";
    FilterField(Function<String,Boolean> check) {
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                fb.replace(offset, length, text, attrs);
                check(check);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                fb.remove(offset, length);
                check(check);
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if(!check.apply(FilterField.this.getText())){
                    Tools.ping(FilterField.this);
                    requestFocus();
                }
            }
        });
    }

    @Override
    public String getText() {
        return lastCorrect;
    }

    private void check(Function<String,Boolean> check){
        if(check.apply(FilterField.super.getText())){
            lastCorrect = FilterField.super.getText();
            setForeground(Color.DARK_GRAY);
        }else {
            setForeground(Color.RED);
        }
    }

}
