package kernbeisser.Windows.TabbedPanel;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DefaultTab {
    private JLabel tabTitle;
    private JLabel icon;
    private JPanel main;
    private JButton close;

    DefaultTab(Icon icon, String title, Runnable closeOperation, Runnable click){
        this.icon.setIcon(icon);
        this.tabTitle.setText(title);
        System.out.println();
        main.setPreferredSize(new Dimension(tabTitle.getFontMetrics(tabTitle.getFont()).stringWidth(title)+90,20));
        close.setIcon(IconFontSwing.buildIcon(FontAwesome.TIMES,20, Color.GRAY));
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeOperation.run();
            }
        });
        close.setRolloverIcon(IconFontSwing.buildIcon(FontAwesome.TIMES_CIRCLE,20, Color.RED));
        close.setMargin(new Insets(-3,0,0,0));
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                click.run();
            }
        };
        this.icon.addMouseListener(adapter);
        tabTitle.addMouseListener(adapter);
        tabTitle.setBorder(new EmptyBorder(-3,0,0,0));
        main.addMouseListener(adapter);
    }

    public JPanel getMain() {
        return main;
    }

    private void createUIComponents() {
        main = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {

            }
        };
    }
}
