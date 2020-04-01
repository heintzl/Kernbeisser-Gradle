package kernbeisser.Windows.SpecialPriceEditor;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionCheckBox;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.CustomComponents.TextFields.DateParseField;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Repeat;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;

public class SpecialPriceEditorView extends Window implements View {
    private ObjectTable<Offer> offers;

    private kernbeisser.CustomComponents.TextFields.DateParseField from;
    private kernbeisser.CustomComponents.TextFields.DateParseField to;
    private JComboBox<Repeat> repeat;
    private SearchBoxView<Article> searchBox;
    private DoubleParseField specialNetPrice;
    private JButton remove;
    private JButton add;
    private JButton edit;
    private JPanel main;
    private JButton finishButton;
    private JButton searchFrom;
    private JButton searchTo;
    private JLabel selectedArticle;
    private PermissionCheckBox filterActionArticle;

    private final SpecialPriceEditorController controller;

    public SpecialPriceEditorView(SpecialPriceEditorController controller, Window currentWindow, Key... required) {
        super(currentWindow, required);
        this.controller = controller;
        add(main);
        int ICON_SIZE = 15;
        add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, ICON_SIZE, Color.GREEN));
        remove.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH,ICON_SIZE,Color.RED));
        edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL,ICON_SIZE,new Color(0x757EFF)));
        offers.addSelectionListener(e -> controller.selectOffer());
        add.addActionListener(e -> controller.add());
        edit.addActionListener(e -> controller.edit());
        remove.addActionListener(e -> controller.remove());
        finishButton.addActionListener(e -> back());
        searchFrom.addActionListener(e -> controller.searchFrom());
        searchTo.addActionListener(e -> controller.searchTo());
        searchTo.setIcon(IconFontSwing.buildIcon(FontAwesome.CALENDAR,ICON_SIZE,Color.GRAY));
        searchFrom.setIcon(IconFontSwing.buildIcon(FontAwesome.CALENDAR,ICON_SIZE,Color.GRAY));
        filterActionArticle.addActionListener(e -> controller.refreshSearchSolutions());
        setSize(670,600);
        windowInitialized();
    }

    void fillRepeat(Repeat[] repeats){
        repeat.removeAllItems();
        for (Repeat r : repeats) {
            repeat.addItem(r);
        }
    }

    void setOffers(Collection<Offer> offers){
        this.offers.setObjects(offers);
    }


    void setFrom(LocalDate s){
        from.setValue(s);
    }

    void setTo(LocalDate s){
        to.setValue(s);
    }

    void setRepeat(Repeat r){
        repeat.setSelectedItem(r);
    }

    void setSpecialNetPrice(int p){
        specialNetPrice.setText(p  + "");
    }

    void setEditEnable(boolean b){
        edit.setEnabled(b);
    }

    void setRemoveEnable(boolean b){
        remove.setEnabled(b);
    }

    void setSelectedArticleIdentifier(String name){
        selectedArticle.setText(name == null ? "Kein Artikel ausgewählt" : name);
    }

    boolean filterOnlyActionArticle(){
        return filterActionArticle.isSelected();
    }

    private void createUIComponents() {
        searchBox = controller.getSearchBoxView();
        offers = new ObjectTable<>(Column.create("Von",Offer::getFromDate),Column.create("Bis",Offer::getToDate),Column.create("Aktionsnettopreis",Offer::getSpecialNetPrice),Column.create("Wiederholung",Offer::getRepeatMode));
        from = new DateParseField();
        to = new DateParseField();
    }

    Offer getSelectedOffer() {
        return offers.getSelectedObject();
    }

    public Date getFrom() throws IncorrectInput {
        return Date.valueOf(from.getUncheckedValue());
    }

    public Date getTo() throws IncorrectInput {
        return Date.valueOf(to.getUncheckedValue());
    }

    int getSpecialPrice() {
        return (int) (specialNetPrice.getSafeValue() * 100);
    }

    Repeat getRepeatMode() {
        return (Repeat) repeat.getSelectedItem();
    }

    public void setAddEnable(boolean b) {
        add.setEnabled(b);
    }

    public void cannotParseDateFormat() {
        JOptionPane.showMessageDialog(this,"Das angegebene Datum kann nicht eingelesen werden,\n bitte überprüfen sie ob das folgende Format eingehalten wurde:\n dd:mm:yyyy");
    }
}
