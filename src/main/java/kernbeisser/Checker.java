package kernbeisser;

import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Class to check if Text Component is abel to cast into a set Type
 */
public class Checker {
    /**
     * checks if the component is castable to an Integer
     * @param component
     * @return the Value of the component if the value is castable to an Integer
     * @throws IncorrectInput if the Input isn't castable
     */
    public int checkInteger(JTextComponent component) throws IncorrectInput {
        return checkInteger(component,Integer.MIN_VALUE,Integer.MAX_VALUE);
    }

    /**
     * @param min sets the min required value
     * @param max sets the max required value
     */
    public int checkInteger(JTextComponent component,int min,int max) throws IncorrectInput{
        try {
            int i = Integer.parseInt(component.getText());
            if(i < min||i > max)throw new IncorrectInput(component,Integer.class);
            else return i;
        }catch (NumberFormatException e){
            throw new IncorrectInput(component,Integer.class);
        }
    }
    /**
     * checks if the component is castable to a Integer(Price is saved as an Integer)
     * @param component
     * @return the Value of the component if the value is castable to a Price
     * @throws IncorrectInput if the Input isn't castable
     */
    public int checkPrice(JTextComponent component) throws IncorrectInput {
        return checkPrice(component,Integer.MIN_VALUE,Integer.MAX_VALUE);
    }
    /**
     * @param min sets the min required value
     * @param max sets the max required value
     */
    public int checkPrice(JTextComponent component, int min, int max) throws IncorrectInput {
        try {
            String[] parts = component.getText().split("[.,]");
            int euros = Integer.parseInt(parts[0]);
            int price = 0;
            if (parts.length == 2) {
                int cents = Integer.parseInt(parts[1].substring(0, Math.max(parts[1].length(), 2)));
                price = euros * 100 + cents;
            } else price = euros * 100;
            if(price < min|| price > max)throw new IncorrectInput(component,Double.class);
            else return price;
        }catch (NumberFormatException e){
            throw new IncorrectInput(component,Double.class);
        }
    }

    /**
     * checks if the component is castable to a Double
     * @param component
     * @return the Value of the component if the value is castable to an Double
     * @throws IncorrectInput if the Input isn't castable
     */
    public double checkDouble(JTextComponent component) throws IncorrectInput {
        return checkDouble(component,Double.MIN_VALUE,Double.MAX_VALUE);
    }
    /**
     * @param min sets the min required value
     * @param max sets the max required value
     */
    public double checkDouble(JTextComponent component,double min,double max) throws IncorrectInput {
        try {
            double i = Double.parseDouble(component.getText());
            if(i < min||i > max)throw new IncorrectInput(component,Double.class);
            else return i;
        }catch (NumberFormatException e){
            new Thread(() -> {
                component.setForeground(Color.RED);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                component.setForeground(Color.BLACK);
            }).start();
            throw new IncorrectInput(component,Double.class);
        }
    }
    /**
     * checks if a component value is castable into a Long
     */
    public Long checkLong(JTextComponent component) throws IncorrectInput {
        try {
            return Long.parseLong(component.getText());
        }catch (NumberFormatException e){
            throw new IncorrectInput(component,Long.class);
        }
    }
}
