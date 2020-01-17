package kernbeisser.CustomComponents.TextFields;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.function.Function;

public class FilterField extends JTextField {
    FilterField(Function<String,Boolean> check){
        ((AbstractDocument)getDocument()).setDocumentFilter(new DocumentFilter(){
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0,fb.getDocument().getLength()));
                sb.insert(offset,text);
                if(check.apply(sb.toString()))
                fb.replace(offset,length,text,attrs);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0,fb.getDocument().getLength()));
                sb.delete(offset,offset);
                if(check.apply(sb.toString()))
                    fb.remove(offset,length);
            }
        });
    }
}
