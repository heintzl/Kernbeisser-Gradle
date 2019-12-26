package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Enums.Filters;
import kernbeisser.Useful.Tools;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FilteredField extends JTextField {

    FilteredField(Filters filters){
        ((AbstractDocument) getDocument()).setDocumentFilter(filters.documentFilter);
    }

    public int getIntValue(){
        try{
            return Integer.parseInt(getText());
        }catch (NumberFormatException e){
            Tools.ping(this);
            return Integer.MIN_VALUE;
        }
    }

    public long getLongValue(){
        try{
            return Long.parseLong(getText());
        }catch (NumberFormatException e){
            Tools.ping(this);
            return Long.MIN_VALUE;
        }
    }

}
